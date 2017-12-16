package com.mkoteam.controller;


import com.alibaba.fastjson.JSON;
import com.mkoteam.entity.JSJConfig;
import com.mkoteam.listener.ListenerManager;
import com.mkoteam.repository.AlarmRepository;
import com.mkoteam.repository.JSJConfigRepository;
import com.mkoteam.until.DateUtils;
import com.mkoteam.until.MKOResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;


@RestController
@RequestMapping("/")
public class MainController extends BaseController {

    @Autowired
    JSJConfigRepository jsjConfigRepository;
    @Autowired
    AlarmRepository alarmRepository;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    /**
     * 根据cid查询设备报警信息  参数为 cid
     */
    @GetMapping("findAlarmInfo")
    public MKOResponse findAlarmInfo(@RequestParam(value = "cid") String cid) {
        if (StringUtils.isEmpty(cid)) {
            return this.makeParamsLackResponse("缺少cid");
        }
//        查询出指定cid的数量
        Integer total = alarmRepository.findAlarmInfoCount(cid);
//        查询报警的首次时间
        Date first = alarmRepository.findAlarmFirstTime(cid);
        String firstTime = DateUtils.datetimeFormat.format(first);
//        查询报警的最近时间
        Date recent = alarmRepository.findAlarmRecentTime(cid);
        String recentTime = DateUtils.datetimeFormat.format(recent);
        Map<String, Object> map = new HashMap<>();
        map.put("alarmCount", total);
        map.put("firstTime", firstTime);
        map.put("recentTime", recentTime);
        List<Object> lists = alarmRepository.findAlarmInfo(cid);
        List<Object> li = new ArrayList<>();
        for (Object object : lists) {
            String str = JSON.toJSONString(object);
            String[] res = str.split(",");
            System.out.println("结果：" + res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "," + res[5] + "," + res[6] + "," + res[7] + "," + res[8]);
            Map<String, Object> maps = new HashMap<>();
            maps.put("cid", res[0].replace("[\"", "").replace("\"", ""));
            maps.put("jzName", res[1].replace("\"", ""));
            maps.put("jzLevel", res[2].replace("\"", ""));
            maps.put("jzPosition", res[3].replace("\"", ""));
            maps.put("unitName", res[4].replace("\"", ""));
            maps.put("alarmType", res[5].replace("\"", ""));
            long timeLong = Long.parseLong(res[6]);
            String timeStr = DateUtils.datetimeFormat.format(new Date(timeLong));
            maps.put("alarmTime", timeStr);
            if (Integer.valueOf(res[7]) == 1) {
                maps.put("deviceStatus", "待处理");
            }
            if (Integer.valueOf(res[7]) == 2) {
                maps.put("deviceStatus", "已通知");
            }
            maps.put("picture", res[8].replace("]", "").replace("\"", ""));
            li.add(maps);
        }
        li.add(map);
        return this.makeSuccessResponse(li);
    }


    /**
     * 查询设备报警列表  参数为GroupId
     */
    @GetMapping("findAlarmInfoList")
    public MKOResponse findAlarmList(@RequestParam(value = "groupId") String groupId) {

        if (StringUtils.isEmpty(groupId)) {
            return this.makeParamsLackResponse("缺少groupId");
        }
//        查询出摄像头位置和类型数据和单个报警次数
        List<Object> lists = alarmRepository.findAlarmData(groupId);
        List<Object> li = new ArrayList<>();
        Map<String, Object> mapTtotal = new HashMap<>();
        for (Object object : lists) {
            String str = JSON.toJSONString(object);
            String[] res = str.split(",");
            Map<String, Object> map = new HashMap<>();
            String cid = res[0].replace("[\"", "").replace("\"", "");
            map.put("cid", cid);
            map.put("count", Integer.valueOf(res[1]));
            map.put("jzName", res[2].replace("\"", ""));
            map.put("jzLevel", res[3].replace("\"", ""));
            map.put("jzPosition", res[4].replace("\"", ""));
            map.put("unitName", res[5].replace("\"", ""));
            String status = alarmRepository.findAlarmStatus(cid);
            if (StringUtils.isEmpty(status)) {
                map.put("status", "待处理");
            } else {
                map.put("status", "已通知");
            }
            li.add(map);
        }
        mapTtotal.put("total", li.size());
        li.add(mapTtotal);

        return this.makeSuccessResponse(li);
    }

    /**
     * 根据cid改变处理状态  传入type参数为0时代表状态改为已通知  type为1时代表状态改为已确认正常
     */
    @GetMapping("updateCidStatusBycid")
    public MKOResponse updateCidStatus(@RequestParam(value = "cid") String cid, @RequestParam(value = "type") String type) {
        if (StringUtils.isEmpty(cid)) {
            return this.makeParamsLackResponse("缺少cid");
        }
        if (StringUtils.isEmpty(type)) {
            return this.makeParamsLackResponse("缺少type");
        }
        if (!type.equals("0") && !type.equals("1")) {
            return this.makeParamsLackResponse("type传入参数格式不对");
        }
        String date = DateUtils.datetimeFormat.format(new Date());
        Date da = null;
        try {
            da = DateUtils.datetimeFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (type.equals("0")) {
            alarmRepository.updateStatusNoticeBycid(da, cid);
        }
        if (type.equals("1")) {
            alarmRepository.updateStatusNormalBycid(da, cid);
        }
        return this.makeSuccessResponse(null);
    }

    /**
     * 根据groupId 显示当前用户下实时监控列表
     */
    @GetMapping("findVideoSurveillanceList")
    public MKOResponse findVideoSurveillanceList(@RequestParam(value = "groupId") String groupId) {

        if (StringUtils.isEmpty(groupId)) {
            return this.makeParamsLackResponse("缺少groupId");
        }
        List<Object> lists = alarmRepository.findVideoSurveillanceList(groupId);
        List<Object> li = new ArrayList<>();
        Map<String, Object> mapTtotal = new HashMap<>();
        for (Object object : lists) {
            String str = JSON.toJSONString(object);
            String[] res = str.split(",");
            Map<String, Object> map = new HashMap<>();
            String cid = res[0].replace("[\"", "").replace("\"", "");
            map.put("cid", cid);
            map.put("jzName", res[1].replace("\"", ""));
            map.put("jzLevel", res[2].replace("\"", ""));
            map.put("jzPosition", res[3].replace("\"", ""));
            Integer in = Integer.valueOf(res[4].replace("]", "").replace("\"", ""));
            if (in == 1) {
                map.put("deviceStatus", "正常");
            }
            if (in == 4) {
                map.put("deviceStatus", "停用");
            }
//            map.put("picture", res[5].replace("]", "").replace("\"", ""));
            map.put("picture", stringRedisTemplate.opsForValue().get(cid));
            li.add(map);
        }
        mapTtotal.put("total", li.size());
        li.add(mapTtotal);
        return this.makeSuccessResponse(li);
    }


    /**
     * 根据cId 显示当前摄像头设备的信息
     */
    @GetMapping("findVideoSurveillanceByCid")
    public MKOResponse findVideoSurveillanceByCid(@RequestParam(value = "cid") String cid) {

        if (StringUtils.isEmpty(cid)) {
            return this.makeParamsLackResponse("缺少cid");
        }
        List<Object> lists = alarmRepository.findVideoSurveillanceByCid(cid);
        List<Object> li = new ArrayList<>();
        for (Object object : lists) {
            String str = JSON.toJSONString(object);
            String[] res = str.split(",");
            System.out.println("结果：" + res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "," + res[5] + "," + res[6] + "," + res[7] + "," + res[8] + "," + res[9] + "," + res[10] + "," + res[11] + "," + res[12]);
            Map<String, Object> map = new HashMap<>();
            map.put("cid", res[0].replace("[\"", "").replace("\"", ""));
            if (Integer.valueOf(res[1]) == 1) {
                map.put("system", "视频监控系统");
            }
            map.put("deviceName", res[2].replace("\"", ""));
            map.put("company", res[3].replace("\"", ""));
            map.put("installTime", res[4].replace("\"", ""));
            map.put("alarmType", res[5].replace("\"", ""));
            map.put("brand", res[6].replace("\"", ""));
            map.put("model", res[7].replace("\"", ""));
            map.put("jzName", res[8].replace("\"", ""));
            map.put("jzLevel", res[9].replace("\"", ""));
            map.put("jzPosition", res[10].replace("\"", ""));
            if (Integer.valueOf(res[11]) == 1) {
                map.put("deviceStatus", "正常");
            }
            if (Integer.valueOf(res[11]) == 4) {
                map.put("deviceStatus", "停用");
            }
            map.put("video_addr", res[12].replace("]", "").replace("\"", ""));
            li.add(map);
        }
        return this.makeSuccessResponse(li);
    }

    /**
     * 实时监控摄像头的开启和停用  参数cid type 0代表停用 1代表开启
     */
    @GetMapping("webcamIsOpenOrClose")
    public MKOResponse webcamIsOpenOrClose(@RequestParam(value = "cid") String cid, @RequestParam(value = "type") String type) {
        if (StringUtils.isEmpty(cid)) {
            return this.makeParamsLackResponse("缺少cid");
        }
        if (StringUtils.isEmpty(type)) {
            return this.makeParamsLackResponse("缺少type");
        }
        if (!type.equals("0") && !type.equals("1")) {
            return this.makeParamsLackResponse("type格式不正确");
        }
        if (type.equals("0")) {
            Integer line = alarmRepository.webcamIsClose(cid);
            if (line == 1) {
                return this.makeSuccessResponse("摄像头已停用");
            }
        }
        if (type.equals("1")) {
            Integer line = alarmRepository.webcamIsOpen(cid);
            if (line == 1) {
                return this.makeSuccessResponse("摄像头已开启");
            }
        }
        return this.makeSuccessResponse(null);
    }

    /**
     * 监控摄像头的实时报警和历史记录展示
     */
    @GetMapping("findDeviceAlarmInfoList")
    public MKOResponse findDeviceAlarmInfoList(@RequestParam(value = "cid") String cid) {
        if (StringUtils.isEmpty(cid)) {
            return this.makeParamsLackResponse("缺少cid");
        }
        List<Object> lists = alarmRepository.findDeviceAlarmInfoList(cid);
        List<Object> li = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (Object object : lists) {
            String str = JSON.toJSONString(object);
            String[] res = str.split(",");
            System.out.println("结果：" + res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "," + res[5] + "," + res[6] + "," + res[7] + "," + res[8]);
            Map<String, Object> maps = new HashMap<>();
            maps.put("cid", res[0].replace("[\"", "").replace("\"", ""));
            maps.put("count", res[1]);
            maps.put("jzName", res[2].replace("\"", ""));
            maps.put("jzLevel", res[3].replace("\"", ""));
            maps.put("jzPosition", res[4].replace("\"", ""));
            maps.put("unitName", res[5].replace("\"", ""));
            long timeLong = Long.parseLong(res[6]);
            String timeStr = DateUtils.datetimeFormat.format(new Date(timeLong));
            maps.put("alarmTime", timeStr);
            if (Integer.valueOf(res[7]) == 1) {
                maps.put("status", "待处理");
            }
            if (Integer.valueOf(res[7]) == 2) {
                maps.put("status", "已通知");
            }
            if (Integer.valueOf(res[7]) == 3) {
                maps.put("status", "已确认正常");
            }
            maps.put("videoAddr", res[8].replace("\"", "").replace("]", ""));
            li.add(maps);
        }
        if (li.size() == 3) {
            Map<String, Object> m1 = (Map<String, Object>) li.get(0);
            Map<String, Object> m2 = (Map<String, Object>) li.get(1);
            if (m1.get("status").equals("已通知") && m2.get("status").equals("待处理")) {
                int totalNumber = Integer.valueOf(m1.get("count").toString()) + Integer.valueOf(m2.get("count").toString());
                m1.put("count", totalNumber);
                li.remove(1);
            }
        }
        int total = li.size();
        map.put("total", total);
        li.add(map);
        return this.makeSuccessResponse(li);
    }


    /**
     * 增加调第三方接口的参数持久化
     */
    @GetMapping("saveJSJConfig")
    public MKOResponse saveJSJConfig(JSJConfig jsjConfig) {
        if (StringUtils.isEmpty(jsjConfig.getAppkey())) {
            return this.makeParamsLackResponse("缺少appkey");
        }
        if (StringUtils.isEmpty(jsjConfig.getAppsecret())) {
            return this.makeParamsLackResponse("缺少appsecret");
        }
        if (StringUtils.isEmpty(jsjConfig.getCids())) {
            return this.makeParamsLackResponse("缺少cids");
        }
        if (StringUtils.isEmpty(jsjConfig.getRoute())) {
            return this.makeParamsLackResponse("缺少route");
        }
        if (StringUtils.isEmpty(jsjConfig.getGroupId())) {
            return this.makeParamsLackResponse("缺少GroupId");
        }
//        先判断数据库有没该groupId的参数信息
        JSJConfig js = jsjConfigRepository.findByGroupId(jsjConfig.getGroupId());
        if (js == null) {
            try {
                jsjConfigRepository.save(jsjConfig);
                ListenerManager.getSingle().saveMessage("增加参数", jsjConfig.getGroupId(), jsjConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.makeSuccessResponse(null);
    }

    /**
     * 移除调第三方接口的参数持久化
     */
    @GetMapping("deleteJSJConfig")
    public MKOResponse deleteJSJConfig(@RequestParam(value = "groupId") String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            return this.makeParamsLackResponse("缺少GroupId");
        }
//        先判断数据库有没该groupId的参数信息
        JSJConfig js = jsjConfigRepository.findByGroupId(groupId);
        if (js != null) {
            try {
                jsjConfigRepository.delete(js);
                ListenerManager.getSingle().saveMessage("移除参数", groupId, js);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.makeSuccessResponse(null);
    }
}
