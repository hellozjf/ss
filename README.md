# shadowsocks协议详解

根据本人调试shadowsocks的经验，写出本文，以便让自己不会忘记shadowsocks协议的实现细节。

## 整体流程



![](https://hellozjf-oss.oss-cn-hangzhou.aliyuncs.com/uploads/2021/2/19/ss架构.jpg)

1. 浏览器配置好socks5代理，浏览器访问目标服务器的时候，请求就会转发到sslocal
2. sslocal收到socks5请求，解析出请求对象，将请求对象最前面加上ss头部（见详细说明->ss头部），再生成一个32字节的盐（也称为IV），再用aes-256-gcm进行加密，将盐和加密后的数据一起发送给ssserver
3. ssserver收到sslocal传输的数据，先读取出32字节的盐，再用aes-256-gcm进行解密，再从解密完的数据中取出ss头部，根据ss头部的指示连接目标服务器，接着把剩余的数据发送给目标服务器
4. 目标服务器处理请求，将结果返回给ssserver
5. ssserver收到结果，生成一个32字节的盐，再用aes-256-gcm进行加密，将盐和加密后的数据一起发送给sslocal
6. sslocal收到数据，取出32字节的盐，再用aes-256-gcm进行解密，将解密好的数据通过socks5协议返回给浏览器
7. 浏览器收到数据，展示页面结果

## 详细说明

### 基本概念

#### password

shadowsocks的密码，这个在配置shadowsocks连接的时候会用到

#### method

aes-256-gcm，这是加密方式，此外还有aes-128-gcm、aes-192-gcm、chacha20-ietf-poly1305、xchacha20-ietf-poly1305，因为时间有限我只研究了aes-256-gcm

#### key

密钥，根据password和method生成

method决定key的长度，例如aes-256-gcm就是32字节

第一个16字节 = md5(password)

第二个16字节 = md5(md5(password) + password)

参考代码：`com.hellozjf.project.shadowsocks.service.impl.CryptServiceImpl#getKey`

#### salt

盐，也称为IV，在aes-256-gcm中为32字节

#### subkey

子密钥，根据key和salt生成，aes-256-gcm是32字节

具体怎么生成的，我也不是特别清楚，好像跟HKDF(SHA1)有关

参考代码：`com.hellozjf.project.shadowsocks.service.impl.CryptServiceImpl#genSubkey`

#### tag

标签，aes-256-gcm中每次加密都会生成一个16字节的tag，具体干嘛用的我也不知道

#### nonce

不知道怎么称呼它，在aes-256-gcm中它是12字节byte数组，每次加密都会使最前面的byte加1，前面的byte满了会让下一个byte加1

### aes-256-gcm加密

每一次请求，都先会产生一个随机的32字节salt

根据password和method，可以产生key

根据key和salt，可以产生subkey

根据method、nonce、subkey，可以将明文变成密文

下面是具体加密流程

数据按最大0x3fff字节分块，每一块将长度明文加密，生成一个tag，nonce递增，数据内容明文加密，生成一个tag，nonce递增。一直这么处理，直到数据全部加密完毕

最后的结构如下所示：

![](https://hellozjf-oss.oss-cn-hangzhou.aliyuncs.com/uploads/2021/2/19/aes-256-gcm加密.jpg)

**注意：每一次请求只会有一个salt，不管本次请求数据有多长salt都是不会变的。不同的请求salt是不同的。**

### aes-256-gcm解密

每一次请求，在aes-256-gcm中都先取出头部32字节salt

根据password和method，可以产生key

根据key和salt，可以产生subkey

根据method、nonce、subkey，可以将密文变为明文

具体解密流程，就是先取2+16字节，解密解析出长度，再取长度+16字节，解密解析出内容

将所有内容拼起来就是解密后的结果

### ss头部

ss头部与加密解密无关，因为正常的数据中只有请求数据，而没有请求服务器的地址，所以在请求数据的头部加了1+n+2个字节表示请求服务器的地址

ss头部格式如下图所示

![](https://hellozjf-oss.oss-cn-hangzhou.aliyuncs.com/uploads/2021/2/19/ss头部.jpg)

ss头部与正常请求数据结合在一起形成明文，和加密解密中的salt一样，ss头部每次请求也只有一个

## 总结

一图胜万言

![](https://hellozjf-oss.oss-cn-hangzhou.aliyuncs.com/uploads/2021/2/20/ss总结.jpg)

## 代码地址

[https://gitee.com/nbda1121440/shadowsocks](https://gitee.com/nbda1121440/shadowsocks)

## 鸣谢

[https://github.com/shadowsocks/shadowsocks](https://github.com/shadowsocks/shadowsocks)
[https://github.com/TongxiJi/shadowsocks-java](https://github.com/TongxiJi/shadowsocks-java)

# 代码说明

## 下载说明

下载完代码之后，需要执行

```
git update-index --assume-unchanged db/ss.mv.db
git update-index --assume-unchanged db/ss.trace.db
```

将db/ss.mv.db和ss.trace.db的修改记录去掉

## 后续工作

- [x] 实现最基本的aes-256-gcm加密翻墙功能
- [ ] 实现用户增删改查接口
- [ ] 实现登录注册功能
- [ ] 实现接口权限功能
- [ ] 实现流量限速功能
- [ ] 实现更多的加密协议
- [ ] 实现v2ray协议

## 工作记录

### 2021-02-22

- [ ] ~~数据库表自动生成~~
- [ ] 解决开着shadowsocks-windows后无法打开hk.hellozjf.com:8080/swagger-ui.html页面的问题
- [x] 增加用户类型，区分普通用户、VIP、SVIP