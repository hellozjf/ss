package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.vo.UserAddVO;
import com.hellozjf.project.shadowsocks.vo.UserDeleteVO;
import com.hellozjf.project.shadowsocks.vo.UserUpdateVO;
import com.hellozjf.project.shadowsocks.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    boolean save(UserAddVO userAddVO);

    boolean update(UserUpdateVO userUpdateVO);

    boolean delete(UserDeleteVO userDeleteVO);

    List<UserVO> listAll();

    UserVO getUser(String id);

    UserVO getUserByPort(Integer port);
}
