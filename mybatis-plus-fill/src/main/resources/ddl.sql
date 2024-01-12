CREATE TABLE `user`
(
    id BIGINT NOT NULL COMMENT '主键ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age INT NULL DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    create_time datetime not null comment '创建时间',
    create_by_id bigint not null comment '创建人',
    update_time datetime NULL comment '更新时间',
    PRIMARY KEY (id)
);