package com.hellozjf.project.shadowsocks.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.request.UserAddReq;
import com.hellozjf.project.shadowsocks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "")
    public R addUser(@Valid @RequestBody UserAddReq userAddReq) {
        boolean ret = userService.save(userAddReq);
        return R.ok(ret);
    }

    @GetMapping(path = "")
    public R listUsers() {
        List<User> list = userService.list();
        return R.ok(list);
    }
}
