package com.calabar.dec.rds.loader.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class HttpClientPost {

    /** 日志记录 */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientPost.class);

    /** 客户端 */
    private static CloseableHttpClient CLIENT;

    /**
     * 获取客户端
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getClient() {
        if (null == CLIENT) {
            CLIENT = HttpClients.createDefault();
        }
        return CLIENT;
    }

    /**
     * 关闭客户端
     */
    public static void closeClient() {
        try {
            CLIENT.close();
        } catch (IOException e) {
            LOGGER.error("CloseableHttpClient关闭失败");
        }
    }

    /**
     * post请求
     *
     * @param url 请求url
     * @param data json类型字符串数据
     * @return 响应状态码
     */
    public static Integer post(String url, String data) throws Exception {
        // 创建客户端
        CloseableHttpClient client = getClient();
        HttpPost post = new HttpPost(url);

        // 构造请求数据
        StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        // HTTP响应状态码
        Integer statusCode;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            statusCode = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            InputStream in = response.getEntity().getContent();
            System.out.println(IOUtils.toString(in, "utf-8"));
            LOGGER.error("客户端执行 post 时出现异常！", e);
            throw new Exception("客户端执行 post 时出现异常！", e);
        } finally {
            closeResponse(response);
        }
        // 返回 code 码异常
        if (StringUtils.startsWith(String.valueOf(statusCode), "5")) {
            InputStream in = response.getEntity().getContent();
            System.out.println(IOUtils.toString(in, "utf-8"));
            throw new Exception("服务器在尝试请求处理时发生内部错误！",new Exception(statusCode+""));
        } else if (StringUtils.startsWith(String.valueOf(statusCode), "4")) {
            InputStream in = response.getEntity().getContent();
            System.out.println(IOUtils.toString(in, "utf-8"));
            throw new Exception("请求出错了！",new Exception(statusCode+""));
        }
        return statusCode;
    }

    /**
     * 关闭response
     *
     * @param response CloseableHttpResponse
     */
    private static void closeResponse(CloseableHttpResponse response) {
        try {
            if (null != response) {
                response.close();
            }
        } catch (Exception e) {
            LOGGER.error("响应关闭失败");
        }
    }
}
