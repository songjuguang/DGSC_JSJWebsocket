package com.mkoteam.configuration;

import com.mkoteam.controller.JSJDataRecevier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * 获得调用接口服务连接的工具类
 * Created by song on 12/1/2017.
 */

@Configuration
public class JSJDataRecevierConfig {

    @Value("${spring.data.url}")
    public String url;

    @Bean
    public JSJDataRecevier jsjDataRecevier() {
        JSJDataRecevier jsjDataRecevier = null;
        try {
            jsjDataRecevier = new JSJDataRecevier(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsjDataRecevier;
    }

}
