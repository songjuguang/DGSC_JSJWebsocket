package com.mkoteam.controller;

import com.alibaba.fastjson.JSONObject;
import com.mkoteam.entity.JSJConfig;
import com.mkoteam.listener.ListenerManager;
import com.mkoteam.until.SignUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;


/**
 *
 * Created by song on 12/1/2017.
 */
@Component
public class JSJDataRecevier extends WebSocketClient {


    private JSJConfig jsjConfig;

    public JSJDataRecevier(URI serverURI) {
        super(serverURI);
    }

    public JSJDataRecevier(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public JSJDataRecevier() throws URISyntaxException {
        super(new URI(""));
        //super();
    }

    public JSJDataRecevier(JSJConfig config, String uri) throws URISyntaxException{
        super(new URI(uri));
        this.jsjConfig = config;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        JSONObject heartObj = new JSONObject();
        heartObj.put("route", "heart_beat");

//      此线程为发送心跳包 检测连接

        final JSJDataRecevier self = this;
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(30000);
                            self.send(heartObj.toJSONString());
                        } catch (Exception e) {
                            System.out.println("心跳包发送异常");
                            e.printStackTrace();
                        }
                        if (self.getConnection().isClosed() == true){
                            break;
                        }
                    }
                }
        }).start();

//      经算法生成签名
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        long time = (long) Integer.valueOf(timestamp.substring(0, length - 3));
        String first = String.format("%s%s", jsjConfig.getAppsecret(), time);
        String second = DigestUtils.md5Hex(first);
        String signstr = SignUtil.encode(second).substring(5, 15);

        JSONObject data = new JSONObject();
        data.put("route", jsjConfig.getRoute());
        data.put("cids", jsjConfig.getCids());
        data.put("appid", jsjConfig.getAppkey());
        data.put("time_stamp", time);
        data.put("sign", signstr);
        this.send(JSONObject.toJSONString(data));

    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);

//   如有返回报警数据对其进行保存处理
//        message="{\"info\":\"\",\"alert_type\":\"cloud\",\"pic3\":\"\",\"cid\":\"100961\",\"route\":\"alarm\",\"video_addr\":\"http:\\/\\/extremevision-hz-stream.oss-cn-hangzhou.aliyuncs.com\\/IPC-100961\\/1513152819474.ts\",\"pic1\":\"http:\\/\\/img-alert.extremevision.com.cn\\/alert_20171213161400_385a2ce383db4042bfbd4ce398e86be0.jpg\",\"pic2\":\"\",\"appid\":\"sUser131\",\"time_stamp\":\"1513152840\",\"datetime\":\"2017-12-13 16:13:49\",\"type\":\"17000000\",\"device_id\":\"100961\"}";
        if (message != null && !message.trim().equals("") && !message.equals("{\"route\":\"heart_beat\"}") && !message.equals("{\"status\":200,\"info\":\"success\"}")) {

            ListenerManager.getSingle().saveMessage(message,jsjConfig.getGroupId(),null);

        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        this.connect();
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("连接异常");
        this.connect();
        ex.printStackTrace();
    }
}