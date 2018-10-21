package com.calabar.dec.rds.loader.util;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p/>
 * <li>Description: http 重试机制</li>
 * <li>@author: zhongzhi</li>
 * <li>Date: 2018/5/8 10:44</li>
 */
public class HttpRetryingUtils implements Serializable {
    /** 日志记录 */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRetryingUtils.class);

    private static final String SUCCESS_FLAG = "2";

    /**
     * 重试发送数据到 tsdb
     *
     * @param url  url
     * @param json tring
     * @throws Exception 连接异常
     */
    public static Boolean retrying(String url, String json) throws Exception {
        boolean result;
        // 异常或者返回null都继续重试、每3秒重试一次、最多重试5次
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(100, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .build();

        try {
            result = retryer.call(() -> {
                int responseStatusCode = HttpClientPost.post(url, json);
                return StringUtils.startsWith(String.valueOf(responseStatusCode), SUCCESS_FLAG);
            });
        } catch (Exception e) {
            LOGGER.error("多次重试发送数据到 OpenTSDB 失败！", e);
            throw new Exception("多次重试发送数据到 OpenTSDB 失败！", e);
        }

        return result;
    }
}
