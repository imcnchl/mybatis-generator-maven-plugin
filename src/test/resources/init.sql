-- docker run --name mysql-mybatis-generator-example -v E:\OneDrive\Workspace\Docker\mysql-mybatis-generator-example\data:/var/lib/mysql -v E:\OneDrive\Workspace\Docker\mysql-mybatis-generator-example\conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD='caohongliang' -p 3306:3306 -d mysql:5.7
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
    create_on datetime null comment '创建时间',
    update_on datetime null comment '更新时间'
) comment 'auth_user';