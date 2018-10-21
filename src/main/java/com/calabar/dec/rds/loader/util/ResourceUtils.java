package com.calabar.dec.rds.loader.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 获取资源文件
 * Created by zz on 2017/5/17.
 */
public class ResourceUtils implements Serializable {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);
    /**
     * 获取资源文件的三种方式 (通过jdk提供的java.util.Properties类)
     * <p>
     * "log4j.properties"   是去当前这个class文件同一个目录下找（即当前这个java文件同一个目录下必须有这个properties文件，才能复制到对应的class目录）
     * "/log4j.properties"  是去整个项目的classes目录下去找，也即是上面提到过的target/classes
     * <p>
     * 注意：
     * Class.getResourceAsStream(String path)
     * -- path不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由 ClassLoader获取资源
     * <p>
     * Class.getClassLoader.getResourceAsStream(String path)、 ClassLoader.getSystemResourceAsStream
     * -- 默认则是从ClassPath根下获取，path不能以’/'开头，最终是由ClassLoader获取资源。
     *
     * @param name 路径名
     * @return Properties
     */
    public static Properties getProperties(String name) throws Exception {
        Properties props = new Properties();
        if (StringUtils.isNotEmpty(name)) {
            InputStream in = null;
            try {
                if (name.startsWith("/")) {
                    in = ResourceUtils.class.getResourceAsStream(name);
                } else {
                    in = ResourceUtils.class.getClassLoader().getResourceAsStream(name);
                    // in = ClassLoader.getSystemResourceAsStream(name);
                }
                props.load(in);
                LOGGER.info("加载 {} 配置文件内容 = = = > {}", name, props);
            } catch (Exception e) {
                throw new Exception("can not find resource file：" + name, e);
            } finally {
                if (null != in) {
                    in.close();
                }
            }
        }

        return props;
    }


    /**
     * 获取resources资源文件下的properties文件 (通过java.util.ResourceBundle类读取)
     * <p>
     * 只能加载 classes 下面的资源文件
     * 只能读取 .properties文件
     *
     * @param name properties文件
     * @return ResourceBundle
     */
    public static ResourceBundle getResource(String name) {
        ResourceBundle resource = null;
        if (StringUtils.isNotEmpty(name)) {
            // 这种方式来获取properties属性文件不需要加.properties后缀名，只需要文件名即可
            resource = ResourceBundle.getBundle(name);
        }

        return resource;
    }

    /**
     * 获取资源文件绝对路径的三种方式
     *
     * @param name 路径名
     * @return absolutePath
     */
    public static String getAbsolutePath(String name) throws Exception {
        String absolutePath = null;
        if (StringUtils.isNotEmpty(name)) {
            try {
                if (name.startsWith("/")) {
                    absolutePath = ResourceUtils.class.getResource(name).getPath();
                } else {
                    absolutePath = ResourceUtils.class.getClassLoader().getResource(name).getPath();
                    // absolutePath = ClassLoader.getSystemResource(name).getPath();
                }
            } catch (Exception e) {
                throw new Exception("can not find resource file：" + name, e);
            }
        }

        return absolutePath;
    }


    public static void main(String[] args) {
        try {
            String path = ResourceUtils.getAbsolutePath("log4j.properties");
            System.out.println(path);

            Properties props = ResourceUtils.getProperties("/log4j.properties");
            System.out.println(props.get("log4j.rootCategory"));

            ResourceBundle resource = ResourceUtils.getResource("log4j");
            System.out.println(resource.getString("log4j.appender.console"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
