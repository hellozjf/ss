package com.hellozjf.project.shadowsocks.service;

import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.vo.UserAddVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 测试service
 */
@Slf4j
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        List<User> userList = userService.list();
        log.info("userList = {}", userList);
    }

    @Test
    public void add() {
        UserAddVO userAddVO = new UserAddVO();
        userAddVO.setUsername("zjf");
        userAddVO.setPassword("123456");
        userAddVO.setEmail("zhoujingfeng0338@gmail.com");
        boolean save = userService.save(userAddVO);
        log.info("save = {}", save);


        test();
    }
}