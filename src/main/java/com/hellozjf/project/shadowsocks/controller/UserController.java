package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.vo.UserAddVO;
import com.hellozjf.project.shadowsocks.service.UserService;
import com.hellozjf.project.shadowsocks.vo.UserUpdateVO;
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
    public R<Boolean> addUser(@Valid @RequestBody UserAddVO userAddVO) {
        return success(userService.save(userAddVO));
    }

    @ApiOperation(value = "修改一个用户")
    @PutMapping(path = "")
    public R<Boolean> modifyUser(@Valid @RequestBody UserUpdateVO userUpdateVO) {
        // todo
        return null;
    }

    @ApiOperation(value = "查看所有用户")
    @GetMapping(path = "")
    public R<List<UserVO>> listUsers() {
        return success(userService.listAll());
    }
}
