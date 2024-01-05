create table sys_business_sn
(
    id              bigint auto_increment comment '主键id'
        primary key,
    tenant_id       bigint   default 0                 not null comment '租户id',
    business_type   int                                not null comment '业务类型',
    business_date   date                               not null comment '业务日期',
    business_sn     int                                not null comment '业务编号 (一般根据 业务类型+业务日期 唯一)',
    row_create_time datetime default CURRENT_TIMESTAMP not null comment '数据创建时间',
    row_update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '数据更新时间',
    constraint uniq_tenant_type_date unique (tenant_id, business_type, business_date)
) comment '系统业务编号表';