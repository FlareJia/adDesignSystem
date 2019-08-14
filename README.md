# advertisingDesignAndImplementation
spring boot and spring cloud implement advertising design
# adDesignSystem

条件数据--(请求)-->广告系统--(匹配)-->响应

广告投放系统 广告检索系统 广告计费系统 报表系统 曝光检测系统...

java 1.8 
mysql 8.0
spring cloud Finchley(Eureka Zuul Feign ...)
Kafka 2.1.0

------------------------------------------------------------------------------------------
广告系统实现的功能：
1.广告主的广告投放
  1.1 推广计划
  1.2 推广单元

2.媒体方的曝光
  计费方式：
  2.1 CPM cost per million
  2.2 CPT cost per time
  2.3 CPC cost per click
  
3.广告系统的扩展
  3.1 采用更多维度
  3.2 用户画像
  3.3 AI
  

-------------------------------------------------------------------------------------------
1.eureka
  1.1 服务注册和服务发现
  1.2 CS架构 一个服务器 多个客户端
  1.3 eureka server是一个服务注册中心 其他微服务来注册 且维持一个心跳连接
  1.4 多实例的eureka启动：
    启动命令：
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server1
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server2
    java -jar ad-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=server3
    实现Eureka Server的高可用
  1.5 Eureka的角色组成：
    Eureka Server 提供服务注册与发现
    Service Provider 服务提供方，将自身服务注册到Eureka Server上，从而让Eureka Server持有服务的元信息，让其他的服务消费方能够找到当前服务
    Service Consumer 服务消费方，从Eureka Server上获取注册服务列表，从而能够消费服务
    Service Provider/Consumer 相对于Server，都叫Eureka client

2.微服务
  2.1 微服务的架构及其应用场景
    点对点的方式：服务直接直接调用，每个微服务都开放REST API，并调用其他微服务的接口（简单系统的时候可以用）
    API-Gateway方式：业务接口通过API网关暴露，是所有客户端接口的唯一入口。微服务之间的通信也通过API网关（Zuul）
  2.2 Zuul作为网关
    Zuul的生命周期：filters{Pre Routing Post Error Costom}
    网关可以用来做权限校验
 
3.ad-common模块
  3.1 通用代码定义、配置定义
  3.2 统一的响应处理
    advice包用于控制器拦截,Advice在spring中就是对xx功能增强
    @RestControllerAdvice(ControllerAdvice)就是对控制器增强，之所以使用RestControllerAdvice，是因为我们对外提供的都是Rest接口(json)
    我们需要对Controller返回内容做一些额外的工作，即功能增强，就需要利用到这个注解。
    如果对Controller的处理过程比较复杂，根据处理的分类，可以制定多个ResponseBodyAdvice，并使用@Order制定处理顺序
  3.3 统一的异常处理
  
  
  