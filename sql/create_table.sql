# 建表脚本
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists bi;

-- 切换库
use bi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    gender       char(2)                                null comment '性别',
    age          int                                    null comment '年龄',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint auto_increment comment 'id' primary key,
    'name'       varchar(256)                           null comment '图表名称',
    goal  		 text                                   null comment '分析目标',
    chartData    text                                   null comment '图标数据',
    chartType    varchar(128)                           null comment '图表类型',
    genChart     text                                   null comment '生成的图表数据',
    genResult    text           				        null comment '生成的分析结果',
    chartStatus  varchar(128)                           not null default 'wait' comment 'wait,running,succeed,failed',
    errorMessage text                                   null comment '错误信息',
    userId       bigInt           				        null comment '创建用户 ID',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '图表信息表' collate = utf8mb4_unicode_ci;

-- 聊天表
create table if not exists chat
(
    id           bigint auto_increment comment 'id' primary key,
    'name'      varchar(255)                            null comment '问题名称',
    question     text                                   null comment '问题',
    genResult    text                                   null comment '生成结果',
    chatType    varchar(128)                           null comment '问题类型',
    chatStatus  varchar(128)                           not null default 'wait' comment 'wait,running,succeed,failed',
    errorMessage text                                   null comment '错误信息',
    userId       bigInt           				        null comment '创建用户 ID',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
    ) comment '聊天信息表' collate = utf8mb4_unicode_ci;
