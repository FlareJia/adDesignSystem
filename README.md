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
