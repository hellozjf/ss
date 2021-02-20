package com.hellozjf.project.shadowsocks.response;

import com.hellozjf.project.shadowsocks.vo.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("列出用户响应")
@Data
public class UserListResp {
    @ApiModelProperty(value = "用户列表")
    private List<UserVO> userList;
}
