package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowMapper;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.vo.FlowQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流量服务
 */
@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, Flow>
        implements FlowService {

    @Override
    public List<FlowVO> list(String host, Integer port, Date startTimeInclude, Date endTimeExclusive) {
        QueryWrapper<Flow> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(host)) {
            queryWrapper.eq("host", host);
        }
        if (port != null && port.intValue() != 0) {
            queryWrapper.eq("port", port);
        }
        if (startTimeInclude != null) {
            queryWrapper.ge("create_time", startTimeInclude);
        }
        if (endTimeExclusive != null) {
            queryWrapper.lt("create_time", endTimeExclusive);
        }
        List<Flow> flowList = list(queryWrapper);
        return flowList.stream()
                .map(flow -> {
                    FlowVO flowVO = new FlowVO();
                    BeanUtil.copyProperties(flow, flowVO);
                    return flowVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowVO> list(FlowQueryVO flowQueryVO) {
        return list(flowQueryVO.getHost(),
                flowQueryVO.getPort(),
                flowQueryVO.getStartTime(),
                flowQueryVO.getEndTime());
    }
}
