package com.hellozjf.project.shadowsocks.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.vo.FlowQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowVO;

import java.util.Date;
import java.util.List;

/**
 * 流量服务
 */
public interface FlowService extends IService<Flow> {

    /**
     * 获取host:port，在startTime ~ endTime之间的所有流量信息
     * @param host
     * @param port
     * @param startTimeInclude
     * @param endTimeExclusive
     * @return
     */
    List<FlowVO> list(String host, Integer port, Date startTimeInclude, Date endTimeExclusive);

    /**
     * 获取流量信息
     * @param flowQueryVO
     * @return
     */
    List<FlowVO> list(FlowQueryVO flowQueryVO);
}
