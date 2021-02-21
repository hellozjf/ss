package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.dao.entity.FlowSum;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowMapper;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowSumMapper;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.service.FlowSumService;
import com.hellozjf.project.shadowsocks.vo.FlowSumQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowSumVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流量汇总服务
 */
@Service
public class FlowSumServiceImpl extends ServiceImpl<FlowSumMapper, FlowSum>
        implements FlowSumService {

    @Override
    public List<FlowSumVO> list(FlowSumQueryVO flowSumQueryVO) {
        QueryWrapper<FlowSum> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(flowSumQueryVO.getHost())) {
            queryWrapper.eq("host", flowSumQueryVO.getHost());
        }
        if (flowSumQueryVO.getPort() != null && flowSumQueryVO.getPort() != 0) {
            queryWrapper.eq("port", flowSumQueryVO.getPort());
        }
        if (flowSumQueryVO.getStartTime() != null) {
            queryWrapper.ge("create_time", flowSumQueryVO.getStartTime());
        }
        if (flowSumQueryVO.getEndTime() != null) {
            queryWrapper.lt("create_time", flowSumQueryVO.getEndTime());
        }
        List<FlowSum> flowSumList = list(queryWrapper);

        return flowSumList.stream()
                .map(flowSum -> {
                    FlowSumVO flowSumVO = new FlowSumVO();
                    BeanUtil.copyProperties(flowSum, flowSumVO);
                    return flowSumVO;
                })
                .collect(Collectors.toList());
    }
}
