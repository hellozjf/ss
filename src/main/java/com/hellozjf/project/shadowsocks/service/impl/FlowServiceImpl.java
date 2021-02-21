package com.hellozjf.project.shadowsocks.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowMapper;
import com.hellozjf.project.shadowsocks.service.FlowService;
import org.springframework.stereotype.Service;

/**
 * 流量服务
 */
@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, Flow>
        implements FlowService {
}
