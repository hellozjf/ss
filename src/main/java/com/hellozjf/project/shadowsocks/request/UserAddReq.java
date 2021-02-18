package com.hellozjf.project.shadowsocks.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 添加用户的请求
 */
@Data
public class UserAddReq {
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "邮箱不能为空")
    private String email;
}
