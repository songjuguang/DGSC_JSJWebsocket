package com.mkoteam.configuration;

import com.mkoteam.ControllerApplication;
import com.mkoteam.controller.JSJDataRecevier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;


@Configuration
public class JSJDataRecevierConfig {

    /*@Value("${spring.data.url}")
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
    }*/

   /* @Bean
    public ControllerApplication controllerApplication(){
        return new ControllerApplication();
    }*/

}
