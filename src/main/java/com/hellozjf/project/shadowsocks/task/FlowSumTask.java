package com.hellozjf.project.shadowsocks.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hellozjf.project.shadowsocks.config.SSConfig;
import com.hellozjf.project.shadowsocks.dao.entity.FlowSum;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.service.FlowSumService;
import com.hellozjf.project.shadowsocks.service.UserService;
import com.hellozjf.project.shadowsocks.vo.FlowQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class FlowSumTask {

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowSumService flowSumService;

    @Autowired
    private UserService userService;

    @Autowired
    private SSConfig ssConfig;

    /**
     * 每分钟进行一次流量汇总
     */
    @Scheduled(cron = "0 * * * * ?")
    public void doSum() {

        DateTime nowTime = DateTime.now();
        DateTime startTime = nowTime
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        DateTime endTime = DateUtil.offset(startTime, DateField.MINUTE, 1);

        List<User> userList = userService.list();
        for (User user : userList) {

            // 统计出这个用户这个10秒的流量信息
            FlowQueryVO flowQueryVO = new FlowQueryVO();
            flowQueryVO.setHost(ssConfig.getHost());
            flowQueryVO.setPort(user.getPort());
            flowQueryVO.setStartTime(startTime);
            flowQueryVO.setEndTime(endTime);
            List<FlowVO> flowVOList = flowService.list(flowQueryVO);

            // 计算出汇总数据，写入数据库中
            FlowSum flowSum = new FlowSum();
            flowSum.setId(IdUtil.simpleUUID());
            flowSum.setCreateTime(new Date());
            flowSum.setUpdateTime(new Date());
            flowSum.setSumTime(startTime);
            flowSum.setHost(ssConfig.getHost());
            flowSum.setPort(user.getPort());
            int downloadSize = 0;
            int uploadSize = 0;
            for (FlowVO flowVO : flowVOList) {
                downloadSize += flowVO.getSize();
                uploadSize += flowVO.getSize();
            }
            flowSum.setDownloadSize(downloadSize);
            flowSum.setUploadSize(uploadSize);
            flowSumService.save(flowSum);
        }
    }
}
