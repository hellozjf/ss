package com.hellozjf.project.shadowsocks.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel("流量限速结束请求")
@Data
public class FlowLimitFinishVO {
    @ApiModelProperty(value = "ID")
    @NotEmpty(message = "")
    private String id;
}
