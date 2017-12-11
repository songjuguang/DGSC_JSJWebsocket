package com.mkoteam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Accessors(chain = true)
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "SC_JSJ_Config")
public class JSJConfig {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)*/
    @Id
    @Column(nullable = false)
    private String groupId;
    private String appkey;
    private String appsecret;
    private String cids;
    private String route;
}
