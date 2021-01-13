### 1、搭建 ActiveMQ 服务，基于 JMS 实现对 queue 和 topic 的消息生产和消费。

[ActiveMQ 下载](http://activemq.apache.org/components/classic/download/)
[ActiveMQ 入门文档](http://activemq.apache.org/version-5-getting-started.html)
[Artemis 下载](http://activemq.apache.org/components/artemis/download/)

* 配置文件 conf/activemq.xml，配置可参考 examples\conf 下的示例
* 启动 bin\activemq start。
* 访问地址 http://localhost:8161/admin。
* 默认密码为 admin/admin，在 conf/jetty-real.properties file 中修改。


### 2、搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作

1. [下载](http://kafka.apache.org/downloads) 
2. 解压
`tar -xzf kafka_2.13-2.7.0.tgz`
3. 修改配置文件 config/server.properties，打开 listeners=PLAINTEXT://localhost:9092
4. 启动自带 zookeeper
`.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties`
5. 启动 kafka
`.\bin\windows\kafka-server-start.bat .\config\server.properties`