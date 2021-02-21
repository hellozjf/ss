package com.hellozjf.project.shadowsocks.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel("流量汇总查询信息")
@Data
public class FlowSumQueryVO {

    @ApiModelProperty(value = "代理服务器地址")
    private String host;

    @ApiModelProperty(value = "代理服务器端口", example = "0")
    private Integer port;

    @ApiModelProperty(value = "流量汇总开始时间（包含）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "流量汇总结束时间（不包含）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}
