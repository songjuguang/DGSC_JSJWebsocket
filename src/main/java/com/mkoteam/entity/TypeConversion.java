package com.mkoteam.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Accessors(chain = true)
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "SC_JSJ_TypeConversion")
public class TypeConversion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer pid;
    @Column(nullable = false)
    private String name;
    @Column(name = "alarmType")
    private String type;
    @Column(name = "alarmPath")
    private Integer path;
    private String group;
}
