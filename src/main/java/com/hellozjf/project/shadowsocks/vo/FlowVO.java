package com.hellozjf.project.shadowsocks.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel("流量信息")
@Data
public class FlowVO {

    @ApiModelProperty(value = "流量ID")
    private String id;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "代理服务器地址")
    private String host;

    @ApiModelProperty(value = "代理服务器端口")
    private Integer port;

    @ApiModelProperty(value = "流量类型，D下载(sslocal->ssserver)，U上传(ssserver->sslocal)")
    private String type;

    @ApiModelProperty(value = "流量大小")
    private Integer size;
}
