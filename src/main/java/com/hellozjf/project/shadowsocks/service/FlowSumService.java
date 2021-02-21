package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.dao.entity.FlowSum;
import com.hellozjf.project.shadowsocks.vo.FlowQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowSumQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowSumVO;

import java.util.List;

/**
 * 流量服务
 */
public interface FlowSumService extends IService<FlowSum> {
    List<FlowSumVO> list(FlowSumQueryVO flowQueryVO);
}
