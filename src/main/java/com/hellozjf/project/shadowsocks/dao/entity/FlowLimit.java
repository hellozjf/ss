package com.hellozjf.project.shadowsocks.dao.entity;

import lombok.Data;

import java.util.Date;

/**
 * 流量限速信息
 */
@Data
public class FlowLimit {
    private String id;
    private Date createTime;
    private Date updateTime;
    private Date validStartTime;
    private Date validEndTime;
    private String userId;
    private String userType;
}
