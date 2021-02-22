package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.constant.UserTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.FlowLimit;
import com.hellozjf.project.shadowsocks.dao.mapper.FlowLimitMapper;
import com.hellozjf.project.shadowsocks.service.FlowLimitService;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitFinishVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public boolean addFlowLimit(FlowLimitAddVO flowLimitAddVO) {
        FlowLimit flowLimit = new FlowLimit();
        BeanUtil.copyProperties(flowLimitAddVO, flowLimit);
        flowLimit.setId(IdUtil.simpleUUID());
        flowLimit.setCreateTime(new Date());
        flowLimit.setUpdateTime(new Date());
        return save(flowLimit);
    }

    @Override
    public boolean finishFlowLimit(FlowLimitFinishVO flowLimitFinishVO) {
        UpdateWrapper<FlowLimit> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("valid_end_time", DateTime.now())
                .eq("id", flowLimitFinishVO.getId());
        return update(updateWrapper);
    }

    @Override
    public List<FlowLimitVO> getAll(FlowLimitQueryVO flowLimitQueryVO) {
        QueryWrapper<FlowLimit> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(flowLimitQueryVO.getUserId())) {
            queryWrapper.eq("user_id", flowLimitQueryVO.getUserId());
        }
        queryWrapper.orderByDesc("create_time");
        List<FlowLimit> list = list(queryWrapper);
        List<FlowLimitVO> collect = list.stream().map(flowLimit -> {
            FlowLimitVO flowLimitVO = new FlowLimitVO();
            BeanUtil.copyProperties(flowLimit, flowLimitVO);
            return flowLimitVO;
        }).collect(Collectors.toList());
        return collect;
    }
}
