package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.vo.UserAddVO;
import com.hellozjf.project.shadowsocks.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    boolean save(UserAddVO req);

    List<UserVO> listAll();
}
