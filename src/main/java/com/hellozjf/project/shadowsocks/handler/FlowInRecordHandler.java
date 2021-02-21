package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hellozjf.project.shadowsocks.config.SSConfig;
import com.hellozjf.project.shadowsocks.constant.FlowTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.service.FlowService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 流入流量监控
 */
@Slf4j
public class FlowInRecordHandler extends ChannelInboundHandlerAdapter {

    private int port;
    private long threadId;
    private FlowService flowService;
    private SSConfig ssConfig;

    public FlowInRecordHandler(int port, long threadId) {
        this.port = port;
        this.threadId = threadId;
        this.flowService = SpringUtil.getBean(FlowService.class);
        this.ssConfig = SpringUtil.getBean(SSConfig.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }
        ByteBuf byteBuf = (ByteBuf) msg;
        log.trace("threadId:{} {}流入流量:{}",  threadId, port, byteBuf.readableBytes());
        recordFlow(byteBuf.readableBytes());
        super.channelRead(ctx, msg);
    }

    /**
     * 记录流量
     */
    private void recordFlow(int size) {
        Flow flow = new Flow();
        flow.setId(IdUtil.simpleUUID());
        flow.setCreateTime(new Date());
        flow.setUpdateTime(new Date());
        flow.setHost(ssConfig.getHost());
        flow.setPort(port);
        flow.setType(FlowTypeConstant.DOWNLOAD);
        flow.setSize(size);
        flowService.save(flow);
    }
}
