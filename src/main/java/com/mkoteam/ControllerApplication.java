package com.mkoteam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mkoteam.controller.JSJDataRecevier;
import com.mkoteam.entity.AlarmData;
import com.mkoteam.entity.JSJConfig;
import com.mkoteam.entity.TypeConversion;
import com.mkoteam.listener.DListener;
import com.mkoteam.listener.JSJDataEvent;
import com.mkoteam.listener.ListenerManager;
import com.mkoteam.repository.AlarmRepository;
import com.mkoteam.repository.JSJConfigRepository;
import com.mkoteam.repository.TypeRepository;
import com.mkoteam.until.HttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

@SpringBootApplication
public class ControllerApplication implements CommandLineRunner, DListener {

    @Autowired
    JSJConfigRepository jsjConfigRepository;
    @Autowired
    AlarmRepository alarmRepository;
    @Autowired
    TypeRepository typeRepository;

    ListenerManager listenerManager;


    @Value("${spring.data.url}")
    String webSocketURI;
    List<JSJDataRecevier> connections;

    @Override
    public void run(String... args) {
        listenerManager = ListenerManager.getSingle();
        listenerManager.addDoorListener(this);


//        connections = new ArrayList<>();
        //从数据库获得JSJConfig参数
        List<JSJConfig> jsjConfigs = jsjConfigRepository.findAll();
        for (JSJConfig jsjConfig : jsjConfigs) {
            try {
                JSJDataRecevier dataRecevier = new JSJDataRecevier(jsjConfig, webSocketURI);
//                connections.add(dataRecevier);
                dataRecevier.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(ControllerApplication.class, args);
    }

    @Override
    public void doorEvent(JSJDataEvent event, String groupId) {
        String message = (String) event.getSource();
        JSONArray jsons = (JSONArray) JSONArray.parse(message);
        for (Object obj : jsons) {
            JSONObject jo = (JSONObject) obj;
            String cid = jo.getString("cid");
            String route = jo.getString("route");
            String video_addr = jo.getString("video_addr");
            String pic1 = jo.getString("pic1");
            String appid = jo.getString("appid");
            String time_stamp = jo.getString("time_stamp");
            String datetime = jo.getString("datetime");
            String type = jo.getString("type");
            String device_id = jo.getString("device_id");

            if (!cid.equals("") && !route.equals("") && !appid.equals("") && !type.equals("") && !device_id.equals("") && !type.equals("") && !video_addr.equals("") && video_addr.startsWith("http:") && video_addr.endsWith(".ts") && !pic1.equals("") && pic1.startsWith("http:") && !time_stamp.equals("") && time_stamp.length() == 10) {
                String s = JSONObject.toJSONString(obj);
                AlarmData alarmData = JSONObject.parseObject(s, AlarmData.class);
                alarmData.setGroupId(groupId);
//                保存报警数据前先对设备是否停用作判断，只有在开启的状态下才能保存数据
                Integer status = alarmRepository.findSbstatusByCid(cid);
                if (status == 1) {
                    alarmRepository.save(alarmData);
                }
            }
        }
    }


}
