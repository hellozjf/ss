package com.hellozjf.project.shadowsocks.service;

import com.hellozjf.project.shadowsocks.handler.ClientInHandler;
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
     * 删除端口
     * @param port
     */
    void deletePort(int port);

    /**
     * 连接目标服务器
     * @param address
     * @param port
     * @return
     */
    void connectTarget(String address, int port, Channel clientHandler, ClientInHandler clientInHandler, long threadId) throws InterruptedException;

}
