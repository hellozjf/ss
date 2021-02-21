package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.util.IdUtil;
import com.hellozjf.project.shadowsocks.BaseTest;
import com.hellozjf.project.shadowsocks.config.SSConfig;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.IdGenerator;

import java.util.Date;
import java.util.List;

@Slf4j
public class FlowServiceTest extends BaseTest {

    @Autowired
    private FlowService flowService;

    @Autowired
    private SSConfig ssConfig;

    @Test
    public void insertTest() {
        Flow flow = new Flow();
        flow.setId(IdUtil.simpleUUID());
        flow.setCreateTime(new Date());
        flow.setUpdateTime(new Date());
        flow.setHost(ssConfig.getHost());
        flow.setPort(8388);
        flow.setType("U");
        flow.setSize(10240);
        flowService.save(flow);

        // 读取所有的流量
        List<Flow> list = flowService.list();
        log.info("list = {}", list);
    }
}