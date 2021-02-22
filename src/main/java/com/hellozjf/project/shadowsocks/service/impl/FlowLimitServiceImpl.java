package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.constant.UserTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowLimitMapper;
import com.hellozjf.project.shadowsocks.service.FlowLimitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowLimitServiceImpl extends ServiceImpl<FlowLimitMapper, FlowLimit>
        implements FlowLimitService {

    @Override
    public String getCurrentUserType(String userId) {
        QueryWrapper<FlowLimit> queryWrapper = new QueryWrapper<>();
        DateTime now = DateTime.now();
        queryWrapper.eq("user_id", userId)
                .lt("valid_start_time", now)
                .gt("valid_end_time", now);
        List<FlowLimit> list = list(queryWrapper);
        if (list.size() == 0) {
            return UserTypeConstant.NORMAL;
        } else {
            String userType = UserTypeConstant.NORMAL;
            for (FlowLimit flowLimit : list) {
                if (Integer.parseInt(flowLimit.getUserType()) > Integer.parseInt(userType)) {
                    userType = flowLimit.getUserType();
                }
            }
            return userType;
        }
    }
}
