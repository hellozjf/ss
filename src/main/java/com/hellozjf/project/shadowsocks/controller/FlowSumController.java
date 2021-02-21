package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.service.FlowSumService;
import com.hellozjf.project.shadowsocks.vo.FlowQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowSumQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowSumVO;
import com.hellozjf.project.shadowsocks.vo.FlowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/flowSum")
@Api(tags = "流量汇总管理")
public class FlowSumController extends ApiController {

    @Autowired
    private FlowSumService flowSumService;

    @ApiOperation(value = "查看流量汇总信息")
    @GetMapping(path = "")
    public R<List<FlowSumVO>> listFlowSums(FlowSumQueryVO flowSumQueryVO) {
        return success(flowSumService.list(flowSumQueryVO));
    }
}
