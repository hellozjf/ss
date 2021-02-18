package com.hellozjf.project.shadowsocks.service;

import io.netty.channel.Channel;

/**
 * netty服务
 */
public interface NettyService {

    /**
     * 根据数据库中当前的用户信息，初始化相关的shadowsocks端口
     */
    void init();

    /**
     * 根据端口、密码、加密方式，获取一个通道
     * @param port
     * @param password
     * @param method
     * @return
     */
    Channel createPort(int port, String password, String method) throws InterruptedException;

    /**
     * 连接目标服务器
     * @param address
     * @param port
     * @return
     */
    Channel connectTarget(String address, int port, Channel clientHandler, long threadId) throws InterruptedException;

}
