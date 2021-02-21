package com.hellozjf.project.shadowsocks.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hellozjf.project.shadowsocks.config.SSConfig;
import com.hellozjf.project.shadowsocks.constant.FlowTypeConstant;
import com.hellozjf.project.shadowsocks.dao.entity.Flow;
import com.hellozjf.project.shadowsocks.service.FlowService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 流出流量监控
 */
@Slf4j
public class FlowOutRecordHandler extends ChannelOutboundHandlerAdapter {

    private int port;
    private long threadId;
    private FlowService flowService;
    private SSConfig ssConfig;

    public FlowOutRecordHandler(int port, long threadId) {
        this.port = port;
        this.threadId = threadId;
        this.flowService = SpringUtil.getBean(FlowService.class);
        this.ssConfig = SpringUtil.getBean(SSConfig.class);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (ctx == null || msg == null || !(msg instanceof ByteBuf)) {
            return;
        }
        ByteBuf byteBuf = (ByteBuf) msg;
        log.trace("threadId:{} {}流出流量:{}", threadId, port, byteBuf.readableBytes());
        recordFlow(byteBuf.readableBytes());
        super.write(ctx, msg, promise);
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
        flow.setType(FlowTypeConstant.UPLOAD);
        flow.setSize(size);
        flowService.save(flow);
    }
}
