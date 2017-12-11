package com.mkoteam.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 报警数据的实体类
 * Created by Song on 12/3/17.
 */

@Accessors(chain = true)
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "SC_JSJ_AlarmData")
public class AlarmData {
//    private Integer id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String groupId;
    private String cid;
    private String route;
    @Column(name = "videoAddr")
    private String video_addr;
    private String pic1;
    @Column(name = "appId")
    private String appid;
    private Long time_stamp;
    private Date datetime;
    private String type;
    @Column(name = "deviceId")
    private String device_id;

}
