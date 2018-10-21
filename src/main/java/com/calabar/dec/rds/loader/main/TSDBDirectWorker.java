package com.calabar.dec.rds.loader.main;

import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;

public class TSDBDirectWorker extends Thread {
    private LinkedList<Deferred<Object>> entries;

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(TSDBDirectWorker.class);

    TSDBDirectWorker(LinkedList<Deferred<Object>> entries) {
        this.entries = entries;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            Deferred.groupInOrder(entries)
                    .addCallback(new succBack(start))
                    .addErrback(new errBack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class errBack implements Callback<String, Exception> {
        public String call(final Exception e) throws Exception {
            String message = ">>>>>>>>>>>Failure!>>>>>>>>>>>";
            LOGGER.info(message + " " + e.getMessage());
            e.printStackTrace();
            return message;
        }
    };

    class succBack implements Callback<Object, ArrayList<Object>> {
        long start;
        public succBack(long start){
            this.start= start;
        }
        public Object call(final ArrayList<Object> results) {
            LOGGER.info("开始时间 ：" + start+
                    " 当前时间："+System.currentTimeMillis()+
                    " 当前线程 ："+Thread.currentThread().getName()+
                    " Successfully wrote " + results.size() + " data points," +
                    " tsdb存储耗时："+(System.currentTimeMillis()-start));
            return null;
        }
    };
}