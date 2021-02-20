package com.hellozjf.project.shadowsocks.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel("用户删除请求")
@Data
public class UserDeleteVO {
    @ApiModelProperty(value = "用户ID列表")
    @NotEmpty(message = "")
    private List<String> idList;
}
