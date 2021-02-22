package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.constant.UserTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitFinishVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

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

    @Test
    public void FlowLimitTest() {

        log.info("userType = {}", flowLimitService.getCurrentUserType("1"));

        FlowLimitAddVO flowLimitAddVO = new FlowLimitAddVO();
        flowLimitAddVO.setUserId("1");
        flowLimitAddVO.setUserType(UserTypeConstant.SVIP);
        DateTime validStartTime = DateUtil.parseDateTime("2021-02-22 00:00:00");
        DateTime validEndTime = DateUtil.parseDateTime("2021-02-23 00:00:00");
        flowLimitAddVO.setValidStartTime(validStartTime);
        flowLimitAddVO.setValidEndTime(validEndTime);
        flowLimitService.addFlowLimit(flowLimitAddVO);

        log.info("userType = {}", flowLimitService.getCurrentUserType("1"));

        FlowLimitQueryVO flowLimitQueryVO = new FlowLimitQueryVO();
        flowLimitQueryVO.setUserId("1");
        List<FlowLimitVO> flowLimitVOList = flowLimitService.getAll(flowLimitQueryVO);
        FlowLimitVO flowLimitVO = flowLimitVOList.get(0);

        FlowLimitFinishVO flowLimitFinishVO = new FlowLimitFinishVO();
        flowLimitFinishVO.setId(flowLimitVO.getId());
        flowLimitService.finishFlowLimit(flowLimitFinishVO);

        log.info("userType = {}", flowLimitService.getCurrentUserType("1"));
    }
}