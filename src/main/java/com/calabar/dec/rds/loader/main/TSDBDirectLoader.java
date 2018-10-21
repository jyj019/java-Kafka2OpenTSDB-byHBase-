package com.calabar.dec.rds.loader.main;

import com.calabar.dec.rds.loader.constant.LoaderConstant;
import com.calabar.dec.rds.loader.entry.KKSEntity;
import com.calabar.dec.rds.loader.entry.PacketData;
import com.calabar.dec.rds.loader.util.KafkaUtils;
import com.calabar.dec.rds.loader.util.ResourceUtils;
import com.google.gson.Gson;
import com.stumbleupon.async.Deferred;

import net.opentsdb.core.TSDB;
import net.opentsdb.utils.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class TSDBDirectLoader {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(TSDBDirectLoader.class);

    static ExecutorService service = new ThreadPoolExecutor(8, 8, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    static Gson gson = new Gson();
    static BlockingQueue<KKSEntity> queue = new LinkedBlockingQueue<KKSEntity>(1);
    static Map<Object, Object> kafkaConfig;
    /**
     * 配置文件Properties
     */
    private static Properties PROPS;

    public static void loadConfig() throws Exception {
            kafkaConfig=new HashMap<>();

            PROPS = ResourceUtils.getProperties("conf.properties");
            LOGGER.info("dec-esc.properties\n" + PROPS);
            Iterator<Map.Entry<Object, Object>> it = PROPS.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<Object, Object> tmp = it.next();
                String key = String.valueOf(tmp.getKey());
                Object value = tmp.getValue();
                if (key.startsWith(KafkaUtils.KAFKA_CONFIG_PREFIX)) {
                    kafkaConfig.put(key, value);
                }
            }
    }

    public static void main(String[] args) throws Exception {
        loadConfig();

        final Config config = new Config(ResourceUtils.getAbsolutePath("pro/opentsdb.conf"));
        final TSDB tsdb = new TSDB(config);

        KafkaConsumer<String, String> consumer = KafkaUtils.getInstance().initConsumer(kafkaConfig);
        consumer.subscribe(Arrays.asList(kafkaConfig.get("dec.ac.kafka.rev.topic").toString()));

        // 拉数据
        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
            LinkedList<LinkedList<Deferred<Object>>> openTsdbEntries = handler(consumerRecords,tsdb);
            for (int i = 0; i < openTsdbEntries.size(); i++) {
                Future<?> res = service.submit(new TSDBDirectWorker(openTsdbEntries.get(i)));
            }
        }
    }

    private static LinkedList<LinkedList<Deferred<Object>>> handler(ConsumerRecords<String, String> consumerRecords,TSDB tsdb) throws Exception {
        LinkedList<LinkedList<Deferred<Object>>> list = new LinkedList<LinkedList<Deferred<Object>>>();
        for (ConsumerRecord<String, String> record : consumerRecords) {
            String value = record.value();
            LOGGER.info(System.currentTimeMillis()+"当前偏移量：" + record.offset());
            if (StringUtils.isNotBlank(value)) {
                KKSEntity kksEntity= gson.fromJson(value, KKSEntity.class);

                if (kksEntity == null) {
                    return new LinkedList<LinkedList<Deferred<Object>>>();
                }
                String power_type = kksEntity.getPower_type();
                String power_code = kksEntity.getPower_code();
                String power_type_code = power_type + power_code;
                String power_unit_code = kksEntity.getPower_unit_code();

                // 装opentsdb对象
                LinkedList<Deferred<Object>> deferreds = new LinkedList<Deferred<Object>>();
                Map<String, String> tags = new HashMap<>();
                tags.put(LoaderConstant.PLT_CODE, power_type_code);
                tags.put(LoaderConstant.SET_CODE, power_unit_code);

                Map<String, PacketData> packeted_data = kksEntity.getPacketed_data();
                PacketData[] arr = packeted_data.values().toArray(new PacketData[]{});

                for (int i = 0; i < arr.length; i++) {
                    PacketData data = arr[i];
                    String substd_code = data.getSubstd_code();
                    // 校准时间
                    long std_time = System.currentTimeMillis();
                    Double src_value = data.getSrc_value();
                    int src_data_quality = data.getSrc_data_quality();
                    Double std_value = data.getStd_value();
                    int std_data_quality = data.getStd_data_quality();

                    // tsdb
                    if (deferreds.size() == 1000) {
                        list.add(deferreds);
                        deferreds = new LinkedList<Deferred<Object>>();
                    }

                    deferreds.add(tsdb.addPoint("test_"+substd_code + LoaderConstant.SRC_VALUE_SUFFIX, std_time, src_value, tags));
                    deferreds.add(tsdb.addPoint("test_"+substd_code + LoaderConstant.SRC_DATA_QUALITY_SUFFIX, std_time, src_data_quality, tags));
                    deferreds.add(tsdb.addPoint("test_"+substd_code + LoaderConstant.STD_VALUE_SUFFIX, std_time, std_value, tags));
                    deferreds.add(tsdb.addPoint("test_"+substd_code + LoaderConstant.STD_DATA_QUALITY_SUFFIX, std_time, std_data_quality, tags));
                }
                list.add(deferreds);
                }
            }

        // 多线程处理数据


        return list;
    }
}
