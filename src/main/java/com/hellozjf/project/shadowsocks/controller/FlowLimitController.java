package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.service.FlowLimitService;
import com.hellozjf.project.shadowsocks.vo.FlowLimitAddVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitFinishVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitQueryVO;
import com.hellozjf.project.shadowsocks.vo.FlowLimitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/flowLimit")
@Api(tags = "流量限速管理")
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

    @ApiOperation(value = "查询流量限速列表")
    @GetMapping(path = "")
    public R<List<FlowLimitVO>> getFlowLimitList(FlowLimitQueryVO flowLimitQueryVO) {
        return success(flowLimitService.getAll(flowLimitQueryVO));
    }
}
