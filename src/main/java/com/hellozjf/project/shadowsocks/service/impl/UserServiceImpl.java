package com.hellozjf.project.shadowsocks.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.dao.mapper.UserMapper;
import com.hellozjf.project.shadowsocks.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
}
