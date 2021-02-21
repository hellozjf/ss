package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.config.SSConfig;
import com.hellozjf.project.shadowsocks.dao.entity.FlowSum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试流量汇总
 */
@Slf4j
public class FlowSumServiceTest extends BaseTest {

    @Autowired
    private FlowSumService flowSumService;

    @Autowired
    private SSConfig ssConfig;

    @Test
    public void test() {
        FlowSum flowSum = new FlowSum();
        flowSum.setId(IdUtil.simpleUUID());
        flowSum.setCreateTime(new Date());
        flowSum.setUpdateTime(new Date());
        DateTime dateTime = DateUtil.date();
        dateTime.setField(DateField.SECOND, dateTime.getField(DateField.SECOND) / 10 * 10);
        dateTime.setField(DateField.MILLISECOND, 0);
        flowSum.setSumTime(dateTime);
        flowSum.setHost(ssConfig.getHost());
        flowSum.setPort(8388);
        flowSum.setDownloadSize(10240);
        flowSum.setUploadSize(5231);
        flowSumService.save(flowSum);

        List<FlowSum> list = flowSumService.list();
        log.info("list = {}", list);
    }
}