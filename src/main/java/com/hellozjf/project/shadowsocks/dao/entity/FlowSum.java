package com.hellozjf.project.shadowsocks.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 流量汇总
 */
@Data
public class FlowSum {
    private String id;
    private Date createTime;
    private Date updateTime;
    private Date sumTime;
    private String host;
    private Integer port;
    private Integer downloadSize;
    private Integer uploadSize;
}
