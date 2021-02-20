package com.hellozjf.project.shadowsocks.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hellozjf.project.shadowsocks.config.UserPortConfig;
import com.hellozjf.project.shadowsocks.dao.entity.User;
import com.hellozjf.project.shadowsocks.dao.mapper.UserMapper;
import com.hellozjf.project.shadowsocks.exception.ApiException;
import com.hellozjf.project.shadowsocks.request.UserAddReq;
import com.hellozjf.project.shadowsocks.response.UserListResp;
import com.hellozjf.project.shadowsocks.service.NettyService;
import com.hellozjf.project.shadowsocks.service.UserService;
import com.hellozjf.project.shadowsocks.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private UserPortConfig userPortConfig;

    @Autowired
    private NettyService nettyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(UserAddReq userAddReq) {

        // 检查参数
        checkSaveParams(userAddReq);

        // 新建用户
        User user = new User();
        BeanUtil.copyProperties(userAddReq, user);
        user.setId(snowflake.nextId());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDel("N");
        // 用户的端口不能和已存在的用户端口冲突
        while (true) {
            int port = RandomUtil.randomInt(userPortConfig.getMin(), userPortConfig.getMax());
            if (checkPortExist(port)) {
                continue;
            }
            user.setPort(port);
            break;
        }

        boolean ret = save(user);
        if (ret) {
            try {
                nettyService.createPort(user.getPort(), user.getPassword(), "aes-256-gcm");
            } catch (InterruptedException e) {
                throw new ApiException("启动端口失败，用户添加失败");
            }
        }
        return ret;
    }

    @Override
    public List<UserVO> listAll() {
        List<User> list = list();
        List<UserVO> collect = list.stream().map(entity -> {
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(entity, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 检查入参是否合法
     */
    private void checkSaveParams(UserAddReq userAddReq) {
        if (userAddReq == null) {
            throw new RuntimeException("req不能为空");
        }
        // 检查数据库有没有重复的用户名
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", userAddReq.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("用户名重复");
        }
        // 检查数据库有没有重复的邮箱
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userAddReq.getEmail());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("邮箱重复");
        }
    }

    /**
     * 检查端口是否有冲突
     * @param port
     * @return
     */
    private boolean checkPortExist(int port) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("port", port);
        if (userMapper.selectCount(queryWrapper) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
