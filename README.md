# advertisingDesignAndImplementation
    spring boot and spring cloud implement advertising design

    条件数据--(多条请求)-->广告系统--(匹配)-->响应
    广告投放系统 广告检索系统 广告计费系统 报表系统 曝光检测系统...

## 环境
    java 1.8 
    mysql 8.0
    spring cloud Finchley(Eureka Zuul Feign ...)
    Kafka 2.1.0

## 结构：
               (Eureka Server<--->Eureka Server<--->Eureka Server)
                                        ^️
                                        |
                                        |
                                        |
                         ---------->(Gateway)<----------
                         |                             |
                         |                             |
                         |                             |
               |(广告投放系统Sponsor)|                |(广告检索系统Search)|
               |                    |               |                   |
               |      (MySQL)       |               |     广告检索服务    |
               |    广告数据写入      |               |         |         |
               |         |          |               |         |         |
               |         |          |               |       索引查询     |
               |      数据导出       |               |         |         |
               |         |          |               |         |         |
               |         ⬇️         |                |（全量索引、增量索引）|               
               |【推广计划】【推广单元】|-----数据---->|         ^         |
               |                     |               |         |         |               
               |【维度限制】【创   意】 |              |    Binlog Data    |
                                                           |        |
                                                           |        |
                                                         kafka     ....
                                                              (Binlog多维度分发)  
 
## 架构：
    广告主 的计费方式有 CPM、CPC、CPT等
    广告主 投放广告 到广告投放系统（推广计划、推广单元、推广限制、广告创意）
    广告投放系统的数据 存储到 MySQL数据库
    
    媒体方 通过检索 广告检索系统（广告数据索引（全量索引（广告主一开始投放的广告）、增量索引（伪装为slave））、广告检索服务）得到服务

## 代码架构：
    spring-cloud:
        eureka
        gateway
        adservice
            common
            dashboard
            search
            sponsor
            
###
# 广告系统实现的功能：
## 1.广告主的广告投放
    1.1 推广计划
    1.2 推广单元

## 2.媒体方的曝光
    计费方式：
    2.1 CPM cost per million
    2.2 CPT cost per time
    2.3 CPC cost per click
  
## 3.广告系统的扩展
    3.1 采用更多维度
    3.2 用户画像
    3.3 AI
## 4.真实广告系统
    广告投放系统
    广告检索系统
    曝光检测系统
    扣费系统
    报表系统
    等等。。。
  
###
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
    HTTP Request 发送给Pre filters -> Routing filters -> Post filters
    Routing filters 和 Service交互   
    网关是整个系统的入口，实现流量(路由)分发的功能，所以不可以做耗时的功能在里面
    网关可以用来做权限校验（网关里面通过Feign调用微服务的功能，来判断请求是否继续下发或直接拒绝）、接口访问（频率，响应时间，请求元数据日志等）统计、流量控制（重复提交，限流）
 
## 3.ad-common模块
  ### 3.1 通用代码定义、配置定义
    即统一配置
  ### 3.2 统一的响应处理
    advice包用于控制器拦截,Advice在spring中就是对xx功能增强
    @RestControllerAdvice(ControllerAdvice)就是对控制器增强，之所以使用RestControllerAdvice，是因为我们对外提供的都是Rest接口(json)
    我们需要对Controller返回内容做一些额外的工作，即功能增强，就需要利用到这个注解。
    如果对Controller的处理过程比较复杂，根据处理的分类，可以制定多个ResponseBodyAdvice，并使用@Order制定处理顺序
  ### 3.3 统一的异常处理
    1)不直接展示错误，对用户友好
    2)异常分类，便于排查问题进行DEBUG
    3)降低业务代码中对异常处理的耦合
  ### 代码结构
    vo包:存储统一响应对象
    advice包:存储控制器拦截 
    exception包:统一异常处理
    conf包:统一配置
  ### 疑问
    1)CommonResponse实现Serializable序列化接口是高数JVM这个对象是可以序列化的。（需要序列化的情况可能是需要存储到文本文件中、通过Socket传递对象等等）
    实现Serializable是考虑到将来可能的扩展存储。
    2)CommonResponseDataAdvice类中的MethodParameter这个类的作用是来保存方法的参数，包含类型、坐标（第几个参数）、注解发现、参数名等等。
    在代码中，我们需要通过MethodParameter知道定义的类或方法是否加了@IgnoreResponseAdvice注解（可以用在方法和类上）。
    而supports返回true代表需要统一的响应，返回false代表不需要统一的响应。
    3)<T>和<?>有着不同的使用场景：<T>用来"声明"一个泛型类或泛型方法；而<?>则用来"使用"泛型类或泛型方法。
    即<T>是定义类或方法时声明的，<?>是调用时传入的。
    4)响应对象统一格式的好处：
        与前端和客户端保持统一，拥有统一的结构，方便前端和客户端的统一处理，在程序出错时，可以直接显示错误信息
        日志结构的统一，响应是统一的，日志结构也就统一了
        
## 4.Spring boot
  ### 4.1 IOC
    1)读取Bean配置信息： XML(Bean)、Java类@Configuration、注解@Autowire（定义bean之间的依赖关系，同时也是配置信息）
    2)根据bean注册表去实例化bean：bean1 @Component、bean2 @Service、bean3 @Repository ...
    3)将bean实例放到容器中（Spring容器，Bean缓存池，Bean定义注册表）
    4)应用程序使用
  ### 4.2 SpringMVC
    1)client发送http请求给DispatchServlet（servlet.xml）
    2)DispatchServlet寻找处理器，通过HandlerMapping定位到具体的Controller
    3)DispatchServlet调用处理器，提交请求给具体的Controller
    4)Controller调用业务处理服务，调用业务逻辑Service
    5)得到处理结果，从ModelAndView传给DispatchServlet
    6)同时处理视图映射，到了ViewResolver
    7)处理结果从DisoatchServlet传到Model进行渲染
    8)之后Model将模型数据传到View
    9)最后View将Http响应返回给client
  ### 疑问
    @Autowire和@Resource的区别：
    1）@Autowire是Spring开发的，@Resource是jdk开发的
    2）@Autowire是按照type来注入的，而@Resource是按照名称来注入的，如果名称找不到，那么就按照type。对于一个接口，如果有多个实现，那么就必须使用@Resource注入，并指定需要注入的实现类
    @Autowire实现了依赖对象的自动装配，而自动装配的各个Bean都是在Spring容器启动时自动配置好的，并一直保持在Spring容器中。
    
    关于注解一些信息：
    1）@Repository注解用于将数据访问层（DAO层）的类标识为Spring Bean
    2）@Component注解是一个泛化的概念，仅仅表示一个组件（Bean），可以作用在任何层次
    3）@Controller通常作用在控制层，但是目前该功能与@Component相同
    4）@Service通常作用在业务层，但是目前该功能与@Component相同
    通过在类上使用@Repository，@Component，@Controller，@Service注解，Spring会自动创建相应的BeanDefinition对象，并注册到ApplicationContext中
    
## 5.广告投放系统数据表设计
  ### 5.1 四个概念：
    用户账户、推广计划<-- 一对多 -->推广单元<-- 多对多 -->创意
  ### 5.2 推广单元：
    关键词限制、地域限制、兴趣限制
  ### 5.3 表结构：
    用户表ad_user：
    username 账户名称
    token 账户token
    user_status 账户状态
    create_time 创建时间
    update_time 更新时间
    
    推广计划ad_plan:
    user_id 标记当前记录所属用户
    plan_name 推广计划名称
    plan_status 推广计划状态
    start_date 推广计划开始时间
    end_date 推广计划结束时间
    create_time 创建时间
    update_time 更新时间
    
    推广单元ad_unit:
    plan_id 关联的推广计划id
    unit_name 推广单元名称
    unit_status 推广单元状态
    position_type 广告位类型（开屏、贴片等）
    budget 预算
    create_time 创建时间
    update_time 更新时间
    
    限制表：
    {
        关键词限制ad_unit_keyword：
        unit_id 关联的推广单元id
        keyword 关键词
    
        地域限制ad_unit_district:
        unit_id 关联的推广单元id
        province 省
        city 市
    
        兴趣限制ad_unit_it:
        unit_id 关联的推广单元id
        it_tag 兴趣标签
    }
    
    创意表ad_creative：
    name 创意名称
    type 物料类型（图片、视频）
    material_type 物料子类型（bmp、avi）
    height 高度
    width 宽度
    size 物料大小，单位KB
    duration 持续时常，视频不为0
    audit_status 审核状态
    user_id 标记当前所属用户
    url 物料地址
    create_time 创建时间
    update_time 更新时间
    
    创意与推广单元关联表creative_unit：
    creative_id 关联的创意id
    unit_id 关联的推广单元id
  ### 代码结构：
    entity包:存放表结构model
    constant包：存放常量数据
    dao包：数据获取接口
    service包：为用户提供服务
    vo包：存储请求与响应的信息
    utils包：工具类
    
## 6.ad-search模块
  ### 6.1 依赖：
    1)Hystrix监控
    2)Eureka客户端
    3)Feign，以声明的方式调用微服务
    4)服务容错Hystrix，断路器 
    5)服务消费者Ribbon，基于HTTP、TCP的负载均衡器
    6)JPA
    7)数据库连接
    8)apache提供的工具类
    9)集合类操作
    10)binlog监听与解析
    11)kafka
    
  ### 6.2 疑问：
    1)@EnableHystrix和@EnableCircuitBreaker注解的区别：
        由@EnableHystrix的源码可知：
        `
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @Inherited
        @EnableCircuitBreaker
        public @interface EnableHystrix {
        }
        `
      @EnableHystrix是对@EnableCircuitBreaker做了一个包装，功能都为启动熔断功能
  
  ### 6.3 微服务之间的调用：
    1)Ribbon方式调用：
    Ribbon是一个客户端负载均衡器，可以很好的控制HTTP和TCP客户端行为
    · SearchApplication.java 中完成注入，并标记@LoadBalanced开启负载均衡功能
    · SearchController.java 中通过RestTemplate调用服务接口，与常见的RestTemplate不同的是，调用使用的不再是ip + port，而是服务名。通过注册中心（Eureka Server）实现。
    
    2)Feign方式调用：
    Feign可以实现声明式的Web服务客户端
    · 通过@FeignClient指定调用的服务名称
    · 在接口上声明@RequestMapping指明调用服务的地址与请求类型
    · 通过在@FeignClient中配置fallback指定熔断
    · 实现接口：SponsorClient.java ， 熔断：SponsorClientHystrix.java   
 
 
## 7. 索引
   ### 7.1 正向索引：
        通过唯一键/主键生成与对象的映射关系
   ### 7.2 倒排索引：
        它的设计是为了存储全文搜索下某个单词在一个文档或一组文档中存储位置的映射。是在文档检索系统中最常用的数据结构。
        
        倒排索引在广告系统中的应用：
        核心用途是对各个维度限制的"整理"
   ### 7.3 全量索引：
        检索系统在启动时一次性读取当前数据库（注意：不能直接从数据库中直接读取，从系统文件中读）中的所有数据，建立索引
        若是多实例部署的话，每个实例直接从数据库中读取全量数据，会给数据库造成巨大的压力。
        若是单实例部署的话，直接从数据库中读取数据也是可以接受的。
        文件中保存的是预先存储的广告数据，可以以任意序列化的方式存储到文件中，在多实例的情况下，这份文件是放到公共的分布式文件系统上的，如NFS或HDFS
   ### 7.4 增量索引：
        系统运行过程中，监控数据库变化，即增量，实时加载更新，构建索引

## 8. 导出表数据到文件
   ### 8.1 数据字段定义：
        目录结构在ad-common下的dump中
        表字段只写索引需要用到的字段
   ### 8.2 将数据先从数据库预先导出到文件中的好处：
        1)检索系统不需要主动的连接到数据库，即可以不需要知道数据表的定义
        2)多实例检索系统在启动的时候如果直接从数据库中读取数据，会给数据库造成巨大压力，这样可以避免扫描数据库。
   ### 8.3 将数据导出到文件：
        目录结构在ad-sponsor下的test下
   ### 8.4 通过文件构造索引：
        读取文件，通过AdPlanIndex.java的add方法即可构造索引
        目录结构为：ad-search的handler包
        索引之间存在着层级的划分，也就是依赖关系的划分
        
   ### 8.5 疑问：
        1)如果数据库表中的数据特别多时如何处理：
        此时最好不要直接在程序中使用findAll，因为这样会瞬间占据JVM中大量内存，同时也会对数据库造成很大压力。
        解决方法：
           · 使用mysql自己提供的mysqldump工具，将数据表直接导出为文件，再利用程序做批量解析。
           · 使用mysql的limit offset语法，每次只取1000条数据，分批取数据，存数据。
           
# .加载全量索引
## .广告主投放的广告数据，导出放到文件里面
## .最好是实现一个子服务用来导出，现在用sponsor里的test来导出，以一种简单方式导出

## 1.ad-common/src/main/java/com.wlarein.ad/dump DConstant 下放置了那些需要导出数据的表的常量
## 2.ad-common/src/main/java/com.wlarein.ad/dump/table 下放置了那些导出数据的表中的属性
  
  
## 9. MySQL Binlog
   ### 9.1 Binlog：
    Binlog即二进制日志，记录对数据发生或潜在发生更改的SQL语句，并以二进制的形式保存在磁盘中。
    一般mysql默认不开启Binlog，但在增量备份时必须打开。

   ### 9.2 Binlog的作用是：
    有两个主要的作用：
        1）复制: MySQL的Master-Slave协议，让Slave可以通过监听Binlog实现数据复制，达到数据一致的目的
        2）恢复：通过mysqlbinlog工具恢复数据
        3）增量备份

   ### 9.3 Binlog相关的变量：
    log_bin: Binlog开关 / 查看变量：show variables like 'log_bin';
    set global log_bin = ON

    binlog_format: Binlog日志格式 / 查看变量： show variables like 'binlog_format';

   ### 9.4 Binlog日志的三种格式：
    ROW: 仅保存记录被修改的细节，不记录sql语句上下文相关信息
    STATEMENT: 每一条会修改数据的sql都会记录在Binlog中（只需要记录执行语句的细节和上下文环境，避免了记录每一行的变化，
    在一些修改记录比较多的情况下相比ROW类型能大大较少Binlog日志量，节约IO，提升性能；还可用于实时还原；同时主从版本可以不一样，
    从服务器版本可以比主服务器版本高）
    MIXED: 以上两种level的混合使用

   ### 9.5 管理Binlog相关的SQL语句:
    show master logs; 查看所有Binlog的日志列表
    show master status; 查看最后一个Binlog日志的编号名称，及最后一个事件结束的位置(pos)
    flush logs; 刷新Binlog，此刻开始产生一个新编号的Binlog日志文件
    reset master; 清空所有的Binlog日志

   ### 9.6 查看Binlog相关的SQL语句：
    (show binlog events [IN 'log_name'] [FROME pos] [LIMIT [offset,] row_count])

    show binlog events; 查看第一个Binlog日志
    show binlog events in 'binlog.000030'; 查看指定的Binlog日志
    show binlog events in 'binlog.000030' from 931; 从指定的位置开始，查看指定的Binlog日志
    show binlog events in 'binlog.000030' from 931 limit 2; 从指定的位置开始，查看指定的Binlog日志，限制查询的条数
    show binlog events in 'binlog.000030' from 931 limit 1,2; 从指定的位置开始，带有偏移，查看指定的Binlog日志，限制查询的条数

   ### 9.7 Binlog中的Event_type:
    每个Event包含header和data两个部分；header提供了Event的创建时间，哪个服务器等信息，data提供的是针对该Event的具体信息，如具体数据的修改。
    1) QUERY_EVENT: 与数据无关的操作，begin、drop table、truncate table等
    2) TABLE_MAP_EVENT: 记录下一个操作所对应的表信息，存储了数据库名和表名
    3) XID_EVENT: 标记事务提交
    4) WRITE_ROWS_EVENT: 插入数据，即insert操作
    5) UPDATE_ROWS_EVENT: 更新数据，即update操作
    6) DELETE_ROWS_EVENT: 删除数据，即delete操作

## 10. 增量数据的投递
   ### 10.1 kafka通用投递：
    将增量数据投递到kafka，其他子系统对感兴趣的topic进行监听
    
## 11. 广告检索服务
   ### 11.1 