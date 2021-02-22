CREATE TABLE user
(
	id VARCHAR(32) NOT NULL COMMENT '主键ID',
	create_time DATETIME NOT NULL COMMENT '创建时间',
	update_time DATETIME NOT NULL COMMENT '修改时间',
	is_del CHAR(1) NOT NULL COMMENT '是否删除，Y已删除，N未删除',
	username VARCHAR(30) NOT NULL COMMENT '用户名',
	password VARCHAR(30) NOT NULL COMMENT '密码',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    port INT NULL DEFAULT NULL COMMENT '端口',
	PRIMARY KEY (id)
);
CREATE INDEX index_user ON user(port, is_del);

CREATE TABLE flow
(
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
	create_time DATETIME NOT NULL COMMENT '创建时间',
	update_time DATETIME NOT NULL COMMENT '修改时间',
	host VARCHAR(128) NOT NULL COMMENT '代理服务器名称',
	port INT NOT NULL COMMENT '代理服务器端口',
	type CHAR(1) NOT NULL COMMENT '流量类型，D下载(sslocal->ssserver)，U上传(ssserver->sslocal)',
	size INT NOT NULL COMMENT '流量大小',
    PRIMARY KEY (id)
);
CREATE INDEX index_flow ON flow(host, port, create_time);

CREATE TABLE flow_sum
(
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
	create_time DATETIME NOT NULL COMMENT '创建时间',
	update_time DATETIME NOT NULL COMMENT '修改时间',
	sum_time DATETIME NOT NULL COMMENT '做汇总的时间，按10秒取整',
	host VARCHAR(128) NOT NULL COMMENT '代理服务器名称',
	port INT NOT NULL COMMENT '代理服务器端口',
	download_size INT NOT NULL COMMENT '下载流量大小',
	upload_size INT NOT NULL COMMENT '上传流量大小',
	PRIMARY KEY (id)
);
CREATE INDEX index_flow_sum ON flow_sum(host, port, create_time);

CREATE TABLE flow_limit
(
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '修改时间',
    valid_start_time DATETIME NOT NULL COMMENT '有效开始时间',
    valid_end_time DATETIME NOT NULL COMMENT '有效结束时间',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    user_type CHAR(1) NOT NULL COMMENT '用户类型，0.普通用户，1.VIP用户，2.SVIP用户'
);
CREATE INDEX index_flow_limit ON flow_limit(user_id, valid_start_time, valid_end_time);