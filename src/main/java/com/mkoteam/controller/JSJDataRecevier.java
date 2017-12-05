package com.mkoteam.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mkoteam.entity.WarningData;
import com.mkoteam.repository.WarningRepository;
import com.mkoteam.until.SignUtil;
import com.mkoteam.until.WebSocketClientss;
import org.apache.commons.codec.digest.DigestUtils;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
import java.util.List;


/**
 * 由于java自带的WebSocketClient该类无提供无参构造，不能使用注解，故自定义WebSocketClientss类
 * Created by song on 12/1/2017.
 */
@Component
public class JSJDataRecevier extends WebSocketClientss {

    @Autowired
    WarningRepository warningRepository;


    @Value("${spring.data.appkey}")
    private String appkeystr;
    @Value("${spring.data.appsecret}")
    private String appsecretstr;
    @Value("${spring.data.cids}")
    private String cidsstr;
    @Value("${spring.data.route}")
    private String routestr;

    public JSJDataRecevier(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public JSJDataRecevier(URI serverURI) {
        super(serverURI);
    }

    public JSJDataRecevier() {

    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        JSONObject heartObj = new JSONObject();
        heartObj.put("route", "heart_beat");

//      此线程为发送心跳包 检测连接
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30000);
                        JSJDataRecevier.this.send(heartObj.toJSONString());
                    } catch (Exception e) {
                        System.out.println("心跳包发送异常");
                        JSJDataRecevier.this.connect();
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//      经算法生成签名
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        long time = (long) Integer.valueOf(timestamp.substring(0, length - 3));
        String first = String.format("%s%s", appsecretstr, time);
        String second = DigestUtils.md5Hex(first);
        String signstr = SignUtil.encode(second).substring(5, 15);

        JSONObject data = new JSONObject();
        data.put("route", routestr);
        data.put("cids", cidsstr);
        data.put("appid", appkeystr);
        data.put("time_stamp", time);
        data.put("sign", signstr);
        JSJDataRecevier.this.send(JSONObject.toJSONString(data));

    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);

//   如有返回报警数据对其进行保存处理
        message="[\n"+
                "    {\n"+
                "        \"cid\": \"100755\",\n"+
                "        \"route\": \"alarm\",\n"+
                "        \"video_addr\": \"http://extremevision-hz-open.oss-cn-hangzhou.aliyuncs.com/IPC-100124/1470726132243.ts\",\n"+
                "        \"pic1\": \"http://img-alert.extremevision.com.cn/shot_20170218193601_e6cb222c530a415cb07138410e43a70c.jpg\",\n"+
                "        \"appid\": \"sUser131\",\n"+
                "        \"time_stamp\": \"1512376711\",\n"+
                "        \"datetime\": \"2017-12-04 16:38:31\",\n"+
                "        \"type\": \"23000000\",\n"+
                "        \"device_id\": \"100755\"\n"+
                "    },\n"+
                "    {\n"+
                "        \"cid\": \"100756\",\n"+
                "        \"route\": \"alarm\",\n"+
                "        \"video_addr\": \"http://extremevision-hz-open.oss-cn-hangzhou.aliyuncs.com/IPC-100124/1470726132243.ts\",\n"+
                "        \"pic1\": \"http://img-alert.extremevision.com.cn/shot_20170218193601_e6cb222c530a415cb07138410e43a70c.jpg\",\n"+
                "        \"appid\": \"sUser131\",\n"+
                "        \"time_stamp\": \"1512376711\",\n"+
                "        \"datetime\": \"2017-12-04 16:38:31\",\n"+
                "        \"type\": \"23000000\",\n"+
                "        \"device_id\": \"100756\"\n"+
                "    }\n"+
                "]";


        if (message != null && !message.trim().equals("") && !message.equals("{\"route\":\"heart_beat\"}") && !message.equals("{\"status\":200,\"info\":\"success\"}")) {
            List<WarningData> datas = JSONArray.parseArray(message, WarningData.class);
            warningRepository.save(datas);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("连接异常");
        JSJDataRecevier.this.connect();
        ex.printStackTrace();
    }
}