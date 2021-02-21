package com.hellozjf.project.shadowsocks.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.dao.entity.FlowSum;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowMapper;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowSumMapper;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.service.FlowSumService;
import org.springframework.stereotype.Service;

/**
 * 流量汇总服务
 */
@Service
public class FlowSumServiceImpl extends ServiceImpl<FlowSumMapper, FlowSum>
        implements FlowSumService {
}
