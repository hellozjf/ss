package com.hellozjf.project.shadowsocks.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("流量限速新增请求")
@Data
public class FlowLimitAddVO {
    @ApiModelProperty(value = "用户ID")
    @NotEmpty(message = "用户ID不能为空")
    private String userId;
    @ApiModelProperty(value = "用户类型，0普通用户，1VIP，2SVIP")
    @NotEmpty(message = "用户类型不能为空")
    private String userType;
    @ApiModelProperty(value = "有效开始时间")
    @NotNull(message = "有效开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validStartTime;
    @ApiModelProperty(value = "有效结束时间")
    @NotNull(message = "有效结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validEndTime;
}
