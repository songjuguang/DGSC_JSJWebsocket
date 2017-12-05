package com.mkoteam.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "SC_JSJ_WariningData")
public class WarningData {
    @Id
    @Column(nullable = false)
    private String cid;
    private String route;
    private String video_addr;
    private String pic1;
    private String appid;
    private Long time_stamp;
    private Date datetime;
    private String type;
    private String device_id;
}
