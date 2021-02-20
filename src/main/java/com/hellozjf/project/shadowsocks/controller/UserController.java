package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.request.UserAddReq;
import com.hellozjf.project.shadowsocks.service.UserService;
import com.hellozjf.project.shadowsocks.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
@Api(tags = "用户管理")
public class UserController extends ApiController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "添加一个用户")
    @PostMapping(path = "")
    public R<Boolean> addUser(@Valid @RequestBody UserAddReq userAddReq) {
        return success(userService.save(userAddReq));
    }

    @ApiOperation(value = "查看所有用户")
    @GetMapping(path = "")
    public R<List<UserVO>> listUsers() {
        return success(userService.listAll());
    }
}
