-- docker run --name mysql-mybatis-generator-example -v E:\OneDrive\Workspace\Docker\mysql-mybatis-generator-example\data:/var/lib/mysql -v E:\OneDrive\Workspace\Docker\mysql-mybatis-generator-example\conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD='root' -p 3306:3306 -d mysql:latest
create database mybatis_generator_example character set utf8 collate utf8_general_ci;
use mybatis_generator_example;

create table user
(
    id int not null auto_increment primary key comment '主键',
    email varchar(30) not null comment '邮箱',
    name varchar(50) null comment '名称',
    status tinyint null comment '状态
0：无效
1：有效',
    column_name_name_name_name_a varchar(50) null comment '测试长度',
    column_name_name_name_name_b varchar(50) null comment '测试长度',
    column_name_name_name_name_c varchar(50) null comment '测试长度',
    column_name_name_name_name_d varchar(50) null comment '测试长度',
    column_name_name_name_name_e varchar(50) null comment '测试长度',
    column_name_name_name_name_f varchar(50) null comment '测试长度',
    column_name_name_name_name_g varchar(50) null comment '测试长度',
    column_name_name_name_name_h varchar(50) null comment '测试长度',
    column_name_name_name_name_i varchar(50) null comment '测试长度',
    column_name_name_name_name_j varchar(50) null comment '测试长度',
    column_name_name_name_name_k varchar(50) null comment '测试长度',
    column_name_name_name_name_l varchar(50) null comment '测试长度',
    column_name_name_name_name_m varchar(50) null comment '测试长度',
    column_name_name_name_name_n varchar(50) null comment '测试长度',
    column_name_name_name_name_o varchar(50) null comment '测试长度',
    column_name_name_name_name_p varchar(50) null comment '测试长度',
    create_on datetime null comment '创建时间',
    update_on datetime null comment '更新时间'
) comment '用户表';

create table product
(
    id int not null auto_increment primary key comment '主键',
    name varchar(50) null comment '名称',
    status tinyint null comment '状态
0：无效
1：有效',
    column_a varchar(50) null comment '测试多列',
    column_b varchar(50) null comment '测试多列',
    column_c varchar(50) null comment '测试多列',
    column_d varchar(50) null comment '测试多列',
    column_e varchar(50) null comment '测试多列',
    column_f varchar(50) null comment '测试多列',
    column_g varchar(50) null comment '测试多列',
    column_h varchar(50) null comment '测试多列',
    column_i varchar(50) null comment '测试多列',
    column_j varchar(50) null comment '测试多列',
    column_k varchar(50) null comment '测试多列',
    column_l varchar(50) null comment '测试多列',
    column_m varchar(50) null comment '测试多列',
    column_n varchar(50) null comment '测试多列',
    column_o varchar(50) null comment '测试多列',
    column_p varchar(50) null comment '测试多列',
    column_q varchar(50) null comment '测试多列',
    column_r varchar(50) null comment '测试多列',
    column_s varchar(50) null comment '测试多列',
    column_t varchar(50) null comment '测试多列',
    column_u varchar(50) null comment '测试多列',
    column_v varchar(50) null comment '测试多列',
    column_x varchar(50) null comment '测试多列',
    create_on datetime null comment '创建时间',
    update_on datetime null comment '更新时间'
) comment 'auth_user';


create table article
(
    id int not null auto_increment primary key comment '主键',
    name varchar(50) null comment '名称',
    status tinyint null comment '状态
0：无效
1：有效',
    blob_column blob comment 'test blob',
    text_column text comment 'test blob',
    create_on datetime null comment '创建时间',
    update_on datetime null comment '更新时间'
) comment 'article';