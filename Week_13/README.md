### 1、搭建 ActiveMQ 服务，基于 JMS 实现对 queue 和 topic 的消息生产和消费。

[ActiveMQ 下载](http://activemq.apache.org/components/classic/download/)
[ActiveMQ 入门文档](http://activemq.apache.org/version-5-getting-started.html)
[Artemis 下载](http://activemq.apache.org/components/artemis/download/)

* 配置文件 conf/activemq.xml，配置可参考 examples\conf 下的示例
* 启动 bin\activemq start。
* 访问地址 http://localhost:8161/admin。
* 默认密码为 admin/admin，在 conf/jetty-real.properties file 中修改。


### 2、搭建一个 3 节点 Kafka 集群，测试功能和性能；实现 spring kafka 下对 kafka 集群的操作

1. [kafka下载](http://kafka.apache.org/downloads) 
2. 解压
`tar -xzf kafka_2.13-2.7.0.tgz`
3. 复制 3 个 config/server.properties 配置文件（server-kafka-900x.properties）放在 config 目录，以下配置不同，其他相同。
```
broker.id=1 ## 分别为 1，2，3
listeners=PLAINTEXT://localhost:9001 ## 分别为 9001，9002，9003
log.dirs=/tmp/kafka-logs1 ## 分别为 logs1，logs2，logs3
```
4. 启动自带 zookeeper
`.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties`
5. 清理 zk 上的所有数据，可以删除 zk 的本地文件或者用 [ZooInspector](https://issues.apache.org/jira/secure/attachment/12436620/ZooInspector.zip) 删除。
6. 分别启动 3 个 kafka
`.\bin\windows\kafka-server-start.bat .\config\server-kafka-9001.properties`
`.\bin\windows\kafka-server-start.bat .\config\server-kafka-9002.properties`
`.\bin\windows\kafka-server-start.bat .\config\server-kafka-9003.properties`