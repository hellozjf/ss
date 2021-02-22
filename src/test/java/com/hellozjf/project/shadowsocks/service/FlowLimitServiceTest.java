package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.constant.UserTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FlowLimitServiceTest extends BaseTest {

    @Autowired
    private FlowLimitService flowLimitService;

    /**
     *
     */
    @Test
    public void test() {

        // 获取一下当前的用户类型
        String currentUserType = flowLimitService.getCurrentUserType("1");
        log.info("currentUserType = {}", currentUserType);

        FlowLimit flowLimit = new FlowLimit();
        flowLimit.setId(IdUtil.simpleUUID());
        flowLimit.setCreateTime(new Date());
        flowLimit.setUpdateTime(new Date());

        DateTime validStartTime = DateUtil.parseDateTime("2021-02-01 00:00:00");
        DateTime validEndTime = DateUtil.offset(validStartTime, DateField.MONTH, 1);
        flowLimit.setValidStartTime(validStartTime);
        flowLimit.setValidEndTime(validEndTime);
        flowLimit.setUserId("1");
        flowLimit.setUserType(UserTypeConstant.VIP);
        flowLimitService.save(flowLimit);

        // 再获取一下当前的用户类型
        currentUserType = flowLimitService.getCurrentUserType("1");
        log.info("currentUserType = {}", currentUserType);



        flowLimit = new FlowLimit();
        flowLimit.setId(IdUtil.simpleUUID());
        flowLimit.setCreateTime(new Date());
        flowLimit.setUpdateTime(new Date());

        validStartTime = DateUtil.parseDateTime("2021-02-22 21:00:00");
        validEndTime = DateUtil.parseDateTime("2021-02-22 22:00:00");
        flowLimit.setValidStartTime(validStartTime);
        flowLimit.setValidEndTime(validEndTime);
        flowLimit.setUserId("1");
        flowLimit.setUserType(UserTypeConstant.SVIP);
        flowLimitService.save(flowLimit);



        List<FlowLimit> list = flowLimitService.list();
        log.info("list = {}", list);

        // 再获取一下当前的用户类型
        currentUserType = flowLimitService.getCurrentUserType("1");
        log.info("currentUserType = {}", currentUserType);
    }
}