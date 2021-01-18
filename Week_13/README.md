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

7. 创建名为 test32 的 topic，有 3 个 partitions，每个 partitions 有 2 个副本
`.\bin\windows\kafka-topics.bat --zookeeper localhost:2181 --create --topic test32 --partitions 3 --replication-factor 2`

8. 查看 topic 描述
`.\bin\windows\kafka-topics.bat --zookeeper localhost:2181 --describe --topic test32`  
表示 Partition 0 的 Leader 的 broker.id 为 1，副本在 broker.id 1 和 3 上，并且都处于同步状态，ISR：In-Sync Replica 同步的副本。
```
Topic: test32   PartitionCount: 3       ReplicationFactor: 2    Configs:
        Topic: test32   Partition: 0    Leader: 1       Replicas: 1,3   Isr: 1,3
        Topic: test32   Partition: 1    Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: test32   Partition: 2    Leader: 3       Replicas: 3,2   Isr: 3,2
```

9. 创建 test32 的消费者 localhost:9001，开始监听
`.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9001 --topic test32 --from-beginning`

10. 创建 test32 的生产者 localhost:9003，输入生产，有中文乱码问题
`.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9003 --topic test32`

11. 测试
生产者发送 100000 个 1000 字节的消息，流控每秒 2000 个消息，调高发送数和流控测试机器极限  
`.\bin\windows\kafka-producer-perf-test.bat --topic test32 --num-records 100000 --record-size 1000 --throughput 2000 --producer-props bootstrap.servers=localhost:9002`  
消费者单线程消费 100000 个 1048576 byte（1000字节）消息，调高消费数和线程测试极限  
`.\bin\windows\kafka-consumer-perf-test.bat --bootstrap-server localhost:9002 --topic test32 --fetch-size 1048576 --messages 100000 --threads 1`  