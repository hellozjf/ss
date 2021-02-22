package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.service.FlowLimitService;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitFinishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class FlowLimitController extends ApiController {

    @Autowired
    private FlowLimitService flowLimitService;

    @ApiOperation(value = "添加一个流量限速")
    @PostMapping(path = "")
    public R<Boolean> addFlowLimit(@Valid @RequestBody FlowLimitAddVO flowLimitAddVO) {
        return success(flowLimitService.addFlowLimit(flowLimitAddVO));
    }

    @ApiOperation(value = "结束一个流量限速")
    @DeleteMapping(path = "")
    public R<Boolean> finishFlowLimit(@Valid @RequestBody FlowLimitFinishVO flowLimitFinishVO) {
        return success(flowLimitService.finishFlowLimit(flowLimitFinishVO));
    }
}
