package com.hellozjf.project.shadowsocks.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;
    private Date createTime;
    private Date updateTime;
    private String isDel;
    private String username;
    private String password;
    private String email;
    private Integer port;
}
