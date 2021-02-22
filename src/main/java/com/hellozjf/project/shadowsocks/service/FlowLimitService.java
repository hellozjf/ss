package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitFinishVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitVO;

import java.util.List;

public interface FlowLimitService extends IService<FlowLimit> {

    /**
     * 获取当前的用户类型
     * @return
     */
    String getCurrentUserType(String userId);

    /**
     * 添加流量限速
     * @param flowLimitAddVO
     * @return
     */
    boolean addFlowLimit(FlowLimitAddVO flowLimitAddVO);

    /**
     * 结束流量限速
     * @param flowLimitFinishVO
     * @return
     */
    boolean finishFlowLimit(FlowLimitFinishVO flowLimitFinishVO);

    /**
     * 获取所有流量限速信息
     * @return
     */
    List<FlowLimitVO> getAll(FlowLimitQueryVO flowLimitQueryVO);
}
