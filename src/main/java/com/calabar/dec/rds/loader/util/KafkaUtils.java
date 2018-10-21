package com.calabar.dec.rds.loader.util;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p/>
 * <li>Description:KafkaUtils</li>
 * <li>@author: liukailong <kailong.liu@cdcalabar.com> </li>
 * <li>Date: 18-3-14 上午11:21</li>
 * <li>@version: 2.0.0 </li>
 * <li>@since JDK 1.8 </li>
 */
public class KafkaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);

    public static final String KAFKA_CONFIG_PREFIX = "dec.ac.kafka.";
    public static final String BOOTSTRAP_SERVERS_CONFIG = "dec.ac.kafka.bootstrap.servers";
    public static final String GROUP_ID_CONFIG = "dec.ac.kafka.group.id";
    public static final String KEY_DESERIALIZER_CLASS_CONFIG = "dec.ac.kafka.key.deserializer";
    public static final String VALUE_DESERIALIZER_CLASS_CONFIG = "dec.ac.kafka.value.deserializer";
    public static final String HEARTBEAT_INTERVAL_MS_CONFIG = "dec.ac.kafka.heartbeat.interval.ms";
    public static final String SESSION_TIMEOUT_MS_CONFIG = "dec.ac.kafka.session.timeout.ms";
    public static final String ENABLE_AUTO_COMMIT_CONFIG = "dec.ac.kafka.enable.auto.commit";
    public static final String AUTO_COMMIT_INTERVAL_MS_CONFIG = "dec.ac.kafka.auto.commit.interval.ms";
    public static final String AUTO_OFFSET_RESET_CONFIG = "dec.ac.kafka.auto.offset.reset";
    public static final String CONNECTIONS_MAX_IDLE_MS_CONFIG = "dec.ac.kafka.connections.max.idle.ms";
    public static final String MAX_POLL_RECORDS_CONFIG = "dec.ac.kafka.max.poll.records";
    public static final String CLIENT_ID_CONFIG = "dec.ac.kafka.client.id";
    public static final String ACKS_CONFIG = "dec.ac.kafka.acks";
    public static final String RETRIES_CONFIG = "dec.ac.kafka.retries";
    public static final String LINGER_MS_CONFIG = "dec.ac.kafka.linger.ms";
    public static final String KEY_SERIALIZER_CLASS_CONFIG = "dec.ac.kafka.key.serializer";
    public static final String VALUE_SERIALIZER_CLASS_CONFIG = "dec.ac.kafka.value.serializer";
    public static final String MAX_BLOCK_MS_CONFIG = "dec.ac.kafka.max.block.ms";
    public static final String BUFFER_MEMORY_CONFIG = "dec.ac.kafka.buffer.memory";
    public static final String COMPRESSION_TYPE_CONFIG = "dec.ac.kafka.compression.type";
    public static final String BATCH_SIZE_CONFIG = "dec.ac.kafka.batch.size";
    public static final String MAXRATEPERPARTITION_CONFIG = "dec.ac.kafka.maxRatePerPartition";
    public static final String SEND_BUFFER_CONFIG = "dec.ac.kafka.send.buffer.bytes";
    public static final String MAX_REQUEST_SIZE_CONFIG = "dec.ac.kafka.max.request.size";

    public static final String KEY_KKS_TOPIC = "dec.ac.kafka.kks.topic";
    public static final String KEY_FFT_TOPIC = "dec.ac.kafka.fft.topic";
    public static final String KEY_STREAM_ANALYSIS_OUT_TOPIC = "dec.ac.kafka.stream.analysis.out.topic";
    public static final String KEY_STREAM_TREND_ANALYSIS_OUT_TOPIC = "dec.ac.kafka.stream.trend.analysis.out.topic";
    /**
     * 流程输出的数据组织 json 字符串
     */
    @Getter
    private Producer<String, String> producer;

    @Getter
    private KafkaConsumer<String, String> consumer;

    @Getter
    /** KafkaProducer */
    private static KafkaUtils INSTANCE;

    /**
     * instance 的getter方法
     *
     * @return 返回 instance
     */
    public static synchronized KafkaUtils getInstance() throws Exception {
        if (null == INSTANCE) {
            INSTANCE = new KafkaUtils();
            LOGGER.info("初始化 kafka producer...");
        }
        return INSTANCE;
    }

    public void initProducer(Map<String, Object> props) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.get(BOOTSTRAP_SERVERS_CONFIG));
        config.put(ProducerConfig.ACKS_CONFIG, props.get(ACKS_CONFIG));
        config.put(ProducerConfig.RETRIES_CONFIG, props.get(RETRIES_CONFIG));
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, props.get(BATCH_SIZE_CONFIG));
        config.put(ProducerConfig.LINGER_MS_CONFIG, props.get(LINGER_MS_CONFIG));
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, props.get(BUFFER_MEMORY_CONFIG));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, props.get(KEY_SERIALIZER_CLASS_CONFIG));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, props.get(VALUE_SERIALIZER_CLASS_CONFIG));
        config.put(ProducerConfig.SEND_BUFFER_CONFIG, props.get(SEND_BUFFER_CONFIG));
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, props.get(MAX_REQUEST_SIZE_CONFIG));
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-" + System.currentTimeMillis());
        producer = new KafkaProducer<>(config);
    }


    /**
     * kafka consumer 初始化配置
     *
     * @return
     */
    public KafkaConsumer initConsumer(Map<Object, Object> props) {
        Properties config = new Properties();
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, props.get(AUTO_OFFSET_RESET_CONFIG));
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, props.get(MAX_POLL_RECORDS_CONFIG));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, props.get(GROUP_ID_CONFIG));
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.get(BOOTSTRAP_SERVERS_CONFIG));
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, props.get(SESSION_TIMEOUT_MS_CONFIG));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, props.get(VALUE_DESERIALIZER_CLASS_CONFIG));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, props.get(KEY_DESERIALIZER_CLASS_CONFIG));
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, props.get(AUTO_COMMIT_INTERVAL_MS_CONFIG));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, props.get(ENABLE_AUTO_COMMIT_CONFIG));
        consumer = new KafkaConsumer<>(config);
        return consumer;
    }

    public void sendMsg(String msg, String topic) {
        try {
            sendMsg(topic, "", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param key key
     * @param msg msg
     * @param msg msg
     * @return true:发送成功，false:发送失败
     * @throws Exception Exception
     */
    public boolean sendMsg(String topic, String key, String msg)/* throws Exception */ {
        boolean isDone = false;
        if (StringUtils.isNotEmpty(msg)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, msg);
            Future<RecordMetadata> fu = this.producer.send(record);
            try {
                RecordMetadata rm = fu.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return isDone;
    }

}
