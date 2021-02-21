package com.hellozjf.project.shadowsocks.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 流量
 */
@Data
public class Flow {
    private String id;
    private Date createTime;
    private Date updateTime;
    private String host;
    private Integer port;
    private String type;
    private Integer size;
}
