package com.hellozjf.project.shadowsocks.controller;

import com.hellozjf.project.shadowsocks.api.ApiController;
import com.hellozjf.project.shadowsocks.api.R;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.service.FlowService;
import com.hellozjf.project.shadowsocks.service.UserService;
import com.hellozjf.project.shadowsocks.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/flow")
@Api(tags = "流量管理")
public class FlowController extends ApiController {

    @Autowired
    private FlowService flowService;

    @ApiOperation(value = "查看流量信息")
    @GetMapping(path = "")
    public R<List<FlowVO>> listFlowsByCondition(FlowQueryVO flowQueryVO) {
        return success(flowService.list(flowQueryVO));
    }
}
