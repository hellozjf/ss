package com.hellozjf.project.shadowsocks.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流量限速信息
 */
@Data
public class FlowLimitVO {
    private String id;
    private Date validStartTime;
    private Date validEndTime;
    private String userId;
    private String username;
    private String userType;
}
