package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.constant.UserTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import com.hellozjf.project.shadowsocks.service.FlowLimitService;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FlowLimitServiceImplTest extends BaseTest {

    @Autowired
    private FlowLimitService flowLimitService;
}