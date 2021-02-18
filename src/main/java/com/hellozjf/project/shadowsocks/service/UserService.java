package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.request.UserAddReq;

public interface UserService extends IService<User> {
    boolean save(UserAddReq req);
}
