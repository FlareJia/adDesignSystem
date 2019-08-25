# advertisingDesignAndImplementation
spring boot and spring cloud implement advertising design
# adDesignSystem

条件数据--(请求)-->广告系统--(匹配)-->响应

广告投放系统 广告检索系统 广告计费系统 报表系统 曝光检测系统...

java 1.8 
mysql 8.0
spring cloud Finchley(Eureka Zuul Feign ...)
Kafka 2.1.0
------------------------------------------------------------------------------------------------------------------------
# 广告系统实现的功能：
## 1.广告主的广告投放
  ### 1.1 推广计划
  ### 1.2 推广单元

## 2.媒体方的曝光
  ### 计费方式：
  ### 2.1 CPM cost per million
  ### 2.2 CPT cost per time
  ### 2.3 CPC cost per click
  
## 3.广告系统的扩展
  ### 3.1 采用更多维度
  ### 3.2 用户画像
  ### 3.3 AI
  

------------------------------------------------------------------------------------------------------------------------
# SpringCloud
## 1.eureka
  ### 1.1 服务注册和服务发现
  ### 1.2 CS架构 一个服务器 多个客户端
  ### 1.3 eureka server是一个服务注册中心 其他微服务来注册 且维持一个心跳连接
  ### 1.4 多实例的eureka启动：
    启动命令：
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server1
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server2
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server3
    实现Eureka Server的高可用
  ### 1.5 Eureka的角色组成：
    Eureka Server 提供服务注册与发现
    Service Provider 服务提供方，将自身服务注册到Eureka Server上，从而让Eureka Server持有服务的元信息，让其他的服务消费方能够找到当前服务
    Service Consumer 服务消费方，从Eureka Server上获取注册服务列表，从而能够消费服务
    Service Provider/Consumer 相对于Server，都叫Eureka client

## 2.微服务
  ### 2.1 微服务的架构及其应用场景
    点对点的方式：服务直接直接调用，每个微服务都开放REST API，并调用其他微服务的接口（简单系统的时候可以用）
    API-Gateway方式：业务接口通过API网关暴露，是所有客户端接口的唯一入口。微服务之间的通信也通过API网关（Zuul）
  ### 2.2 Zuul作为网关
    Zuul的生命周期：filters{Pre Routing Post Error Costom}
    网关可以用来做权限校验
 
## 3.ad-common模块
  ### 3.1 通用代码定义、配置定义
  ### 3.2 统一的响应处理
    advice包用于控制器拦截,Advice在spring中就是对xx功能增强
    @RestControllerAdvice(ControllerAdvice)就是对控制器增强，之所以使用RestControllerAdvice，是因为我们对外提供的都是Rest接口(json)
    我们需要对Controller返回内容做一些额外的工作，即功能增强，就需要利用到这个注解。
    如果对Controller的处理过程比较复杂，根据处理的分类，可以制定多个ResponseBodyAdvice，并使用@Order制定处理顺序
  ### 3.3 统一的异常处理
  
## 4.Spring boot
  ### 4.1 IOC
   1)读取Bean配置信息： XML(Bean)、Java类@Configuration、注解@Autowire（定义bean之间的依赖关系，同时也是配置信息）
   2)根据bean注册表去实例化bean：bean1 @Component、bean2 @Service、bean3 @Repository
   3)将bean实例放到容器中
   4)应用程序使用
  ### 4.2 SpringMVC
   1)client发送http请求给DispatchServlet（servlet.xml）
   2)DispatchServlet寻找处理器，到了HandlerMapping
   3)DispatchServlet调用处理器，到了Controller
   4)Controller调用业务处理服务，到了Service
   5)得到处理结果，从ModelAndView传给DispatchServlet
   6)同时处理视图映射，到了ViewResolver
   7)处理结果从DisoatchServlet传到Model进行渲染
   8)之后Model将模型数据传到View
   9)最后View将Http响应返回给client
 
## 5.广告投放系统数据表设计
  ### 5.1 四个概念：用户账户、推广计划<-- 一对多 -->推广单元<-- 多对多 -->创意
  ### 5.2 推广单元：关键词限制、地域限制、兴趣限制
  
-------------------------------------------------------------------------------------------------------------------------------------------------
# .加载全量索引
## .广告主投放的广告数据，导出放到文件里面
## .最好是实现一个子服务用来导出，现在用sponsor里的test来导出，以一种简单方式导出

## 1.ad-common/src/main/java/com.wlarein.ad/dump DConstant 下放置了那些需要导出数据的表的常量
## 2.ad-common/src/main/java/com.wlarein.ad/dump/table 下放置了那些导出数据的表中的属性
  

-------------------------------------------------------------------------------------------------------------------------------------------------
# MySQL Binlog
## 1.Binlog：
Binlog即二进制日志，记录对数据发生或潜在发生更改的SQL语句，并以二进制的形式保存在磁盘中。
一般mysql默认不开启Binlog，但在增量备份时必须打开。

## 2.Binlog的作用是：
有两个主要的作用：复制、恢复和审计

## 3.Binlog相关的变量：
log_bin: Binlog开关 / 查看变量：show variables like 'log_bin';
set global log_bin = ON

binlog_format: Binlog日志格式 / 查看变量： show variables like 'binlog_format';

## 4.Binlog日志的三种格式：
ROW: 仅保存记录被修改的细节，不记录sql语句上下文相关信息
STATEMENT: 每一条会修改数据的sql都会记录在Binlog中
MIXED: 以上两种level的混合使用

## 5.管理Binlog相关的SQL语句:
show master logs; 查看所有Binlog的日志列表
show master status; 查看最后一个Binlog日志的编号名称，及最后一个事件结束的位置(pos)
flush logs; 刷新Binlog，此刻开始产生一个新编号的Binlog日志文件
reset master; 情况所有的Binlog日志

## 6.查看Binlog相关的SQL语句：
(show binlog events [IN 'log_name'] [FROME pos] [LIMIT [offset,] row_count])

show binlog events; 查看第一个Binlog日志
show binlog events in 'binlog.000030'; 查看指定的Binlog日志
show binlog events in 'binlog.000030' from 931; 从指定的位置开始，查看指定的Binlog日志
show binlog events in 'binlog.000030' from 931 limit 2; 从指定的位置开始，查看指定的Binlog日志，限制查询的条数
show binlog events in 'binlog.000030' from 931 limit 1,2; 从指定的位置开始，带有偏移，查看指定的Binlog日志，限制查询的条数

## 7.Binlog中的Event_type:
7.1 QUERY_EVENT: 与数据无关的操作，begin、drop table、truncate table等
7.2 TABLE_MAP_EVENT: 记录下一个操作所对应的表信息，存储了数据库名和表名
7.3 XID_EVENT: 标记事务提交
7.4 WRITE_ROWS_EVENT: 插入数据，即insert操作
7.5 UPDATE_ROWS_EVENT: 更新数据，即update操作
7.6 DELETE_ROWS_EVENT: 删除数据，即delete操作