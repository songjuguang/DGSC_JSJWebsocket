package com.mkoteam.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;
import java.util.Date;


@Setter
@Getter
@ToString
public class ResultData implements Serializable {
    private String cid;
    private String jzName;
    private String jzLevel;
    private String jzPosition;
    private String unitName;
    private Integer status;

    private String alarmType;
    private String alarmTime;
    private String pic;
}
