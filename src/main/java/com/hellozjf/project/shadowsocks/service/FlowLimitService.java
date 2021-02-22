package com.hellozjf.project.shadowsocks.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;

public interface FlowLimitService extends IService<FlowLimit> {

    /**
     * 获取当前的用户类型
     * @return
     */
    String getCurrentUserType(String userId);
}
