package com.hellozjf.project.shadowsocks.dao.mapper;

import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class UserMapperTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        List<User> userList = userMapper.selectList(null);
        log.info("userList = {}", userList);
    }
}