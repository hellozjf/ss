package com.hellozjf.project.shadowsocks.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 添加用户的请求
 */
@ApiModel("用户新增请求")
@Data
public class UserAddVO {
    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @ApiModelProperty(value = "密码")
    @NotEmpty(message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "邮箱")
    @NotEmpty(message = "邮箱不能为空")
    private String email;
}
