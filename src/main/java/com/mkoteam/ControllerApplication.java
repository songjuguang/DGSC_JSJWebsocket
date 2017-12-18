package com.mkoteam;

import com.alibaba.fastjson.JSONObject;
import com.mkoteam.controller.JSJDataRecevier;
import com.mkoteam.entity.AlarmData;
import com.mkoteam.entity.JSJConfig;
import com.mkoteam.listener.DListener;
import com.mkoteam.listener.JSJDataEvent;
import com.mkoteam.listener.ListenerManager;
import com.mkoteam.repository.AlarmRepository;
import com.mkoteam.repository.JSJConfigRepository;
import com.mkoteam.repository.TypeRepository;
import com.mkoteam.until.RandomUtil;
import com.mkoteam.until.UpdatePictureUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.URISyntaxException;
import java.util.List;

@SpringBootApplication
public class ControllerApplication implements CommandLineRunner, DListener {

    @Autowired
    JSJConfigRepository jsjConfigRepository;
    @Autowired
    AlarmRepository alarmRepository;
    @Autowired
    TypeRepository typeRepository;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ListenerManager listenerManager;

    @Value("${cbs.urladdress}")
    private String urladdress;

    @Value("${cbs.ffmpegPath}")
    private String ffmpegPath;

    @Value("${cbs.picturepath}")
    private String picturepath;

    @Value("${spring.data.url}")
    String webSocketURI;
//    List<JSJDataRecevier> connections;

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
//      首先先从数据库查出的直播流地址
        String[] addrs = alarmRepository.findAllVideoaddr();

//      此处线程负责实时视频定时更新截图
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(120000);
                        for (String str : addrs) {
                            if (!StringUtils.isEmpty(str.replace(",",""))) {
                                String[] cidStr = str.split(",");
                                String cidInfo = cidStr[0];
                                String videoInfo = cidStr[1];
                                String picAddr = RandomUtil.getRandomString(24) + ".jpeg";
                                String commandStr = ffmpegPath + " -i " + videoInfo + " -f image2 -ss 5 -vframes 1 -s 300*300 "+ picturepath + picAddr;
                                UpdatePictureUtil.exeCmd(commandStr);
//                             更新数据库该监控摄像头的图片信息
                                alarmRepository.updatePic(cidInfo, ""+urladdress+picAddr);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/

//     将视频实时截图保存至redis服务器内 stringRedisTemplate
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(120000);
                        for (String str : addrs) {
                            if (!StringUtils.isEmpty(str.replace(",",""))) {
                                String[] cidStr = str.split(",");
                                String cidInfo = cidStr[0];
                                String videoInfo = cidStr[1];
                                String picAddr = RandomUtil.getRandomString(24) + ".jpeg";
                                String commandStr = ffmpegPath + " -i " + videoInfo + " -f image2 -ss 5 -vframes 1 -s 300*300 "+ picturepath + picAddr;
//                                String commandStr = /usr/local/ffmpeg/bin/ffmpeg -i http://61.177.139.216:8891/hls/100964/index.m3u8 -f image2 -ss 5 -vframes 1 -s 300*300 /usr/test/image1.jpeg;
//                                String commandStr = ffmpeg -i http://61.177.139.216:8891/hls/100964/index.m3u8 -f image2 -ss 5 -vframes 1 -s 300*300 D:/pictures/image6.jpeg;
                                UpdatePictureUtil.exeCmd(commandStr);
//                             将图片保存至redis数据库
//                                alarmRepository.updatePic(cidInfo, ""+urladdress+picAddr);
//                                redisTemplate.opsForHash().put("videoPicture",cidInfo,""+urladdress+picAddr);
                                stringRedisTemplate.opsForValue().set(cidInfo,""+urladdress+picAddr);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        SpringApplication.run(ControllerApplication.class, args);
    }

    @Override
    public void doorEvent(JSJDataEvent event, String groupId, JSJConfig jsg) {
        String message = (String) event.getSource();
        if (message.equals("增加参数")) {
                /*try {
                    JSJDataRecevier dataRecevier = new JSJDataRecevier(jsg, webSocketURI);
                    connections.add(dataRecevier);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/
            this.run();
        }
        if (message.equals("移除参数")) {
                /*try {
                    JSJDataRecevier dataRecevier = new JSJDataRecevier(jsg, webSocketURI);
                    connections.remove(dataRecevier);
                   int i = connections.size();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/
            this.run();
        }
        if (!message.equals("增加参数") && !message.equals("移除参数")) {
            AlarmData alarmData = JSONObject.parseObject(message, AlarmData.class);
            alarmData.setGroupId(groupId);
            String cid = alarmData.getCid();
            String route = alarmData.getRoute();
            String video_addr = alarmData.getVideo_addr();
            String pic1 = alarmData.getPic1();
            String appid = alarmData.getAppid();
            String time_stamp = alarmData.getTime_stamp().toString();
            String datetime = alarmData.getDatetime().toString();
            String type = alarmData.getType();
            String device_id = alarmData.getDevice_id();

            if (!cid.equals("") && !route.equals("") && !appid.equals("") && !type.equals("") && !device_id.equals("") && !type.equals("") && !video_addr.equals("") && video_addr.startsWith("http:") && video_addr.endsWith(".ts") && !pic1.equals("") && pic1.startsWith("http:") && !time_stamp.equals("") && time_stamp.length() == 10) {
//                保存报警数据前先对设备是否停用作判断，只有在开启的状态下才能保存数据
                Integer status = alarmRepository.findSbstatusByCid(cid);
                if (status == 1) {
                    alarmRepository.save(alarmData);
                }
            }
        }
    }


}
