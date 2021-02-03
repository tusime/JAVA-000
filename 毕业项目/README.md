[TOPIC]

### JVM

#### Java 字节码
字节码文件（.class）可以通过 javap 命令反编译成操作码，再借助操作码助记符阅读。JVM 是基于栈的计算器，操作码表示栈操作。通过字节码可以理解 Java 原理。

### 类加载
类加载器从文件系统中加载字节码文件（.class），变成 JVM 中的类。  
类加载器特点
* 双亲委托
* 负责依赖
* 缓存加载
  
自定义类加载器使用场景  
* 加载加密的 jar 包。  
* 从非标准的来源加载代码，例如字节码在数据库、网络上。
  
自定义类加载器步骤
* 继承 ClassLoader 类。
* 重写 findClass 方法。
* 读取 class 文件获得 byte 数组。
* 使用 ClassLoader 的 defineClass 方法将 byte 数组转化为 Class 实例。

### Java 内存模型

### JVM 启动参数
设置合适的程序运行环境和分析问题，最常用列举：
`-server`：JVM 使用 server 模式
`-Xmx`：最大堆内存
`-Xms`：堆内存空间的初始大小
`-XX:+UseG1GC`：使用 G1 垃圾回收器
`-XX:+PrintGCDetails`：打印 GC 日志

### JDK 内置命令行工具
jps/jinfo 查看 java 进程
jstat 查看 gc 信息
jmap 查看 heap、类统计信息
jstack 查看线程信息
jcmd 综合命令
图形化工具：jconsole，jvisualvm，jmc
IDE 插件：VisualGC

### 垃圾回收与 GC 
垃圾是内存中不再使用的对象，回收垃圾是为了更高效的使用内存。回收垃圾策略：串行 GC（Serial GC/ParNewGC），并行 GC（Parallel GC），CMS GC，G1 GC，ZGC，Shenandoah GC。
  
  
## NIO

### Socket 编程与 IO 模型
TCP/IP 的编程抽象是套接字 Socket。Socket 编程优化：单线程-》多线程-》多线程-线程池，属于 BIO，继续优化需改为 NIO。
不同的 IO 模型：Blocking IO，Non-blocking IO，IO Multiplexing，Event-driven IO，Asynchronous IO。

### Java NIO 与 Netty
Java NIO 的 Reactor 模型，处理了 NIO 半包问题。Netty 对 Java NIO 进行了扩展，是对 Reactor 反应器模式的一个实现。

### API 网关
流量网关，性能好，Nginx。业务网关，扩展性好，Zuul 2.x，Spring Cloud Gateway，Soul，都基于 Netty。
  
  
## Concurrent

### 多线程，线程池，并发包
概念：可见性，原子性，有序性问题。happens-before 原则。
线程接口：Runnable 没有返回值，Callable 有返回值，返回值接口 Future。
线程方法，等待，唤醒，睡眠，中断等操作。
锁相关：synchronized，Lock。
线程池：Executor，ExecutorService，ThreadPoolExecutor。线程工具类 Excutors。
并发集合类：CopyOnWriteArrayList，ConcurrentHashMap...
并发工具类：CountDownLatch，CyclicBarrier，Semaphore...
原子操作类：基于 Compare and swap。
其他：volatile，final。
  
  
## Spring

### Spring
IoC 与 AOP 原理，Bean 生命周期，Spring 配置原理。

### SpringBoot
更大程度上的约定大于配置。
自动化 Spring 配置：基于 Configuration，EnableXX，Condition。配置文件 application.yaml 文件被 Configuration 转化为 bean。
整合各种第三方类库，spring-boot-starter 原理。

* * *

## 数据库

### MySQL 原理
数据库设计范式。MySQL 执行过程与原理，配置优化，数据库设计优化，SQL 优化，数据库事务，隔离性，锁。

### 主从复制，读写分离
读写压力，可以利用主从复制将读写分离。读写分离实现方式 Spring 配置多数据源，利用 ShardingSphere-jdbc 分离，使用中间件 ShardingSphere-Proxy 自动分离。主从结构解决读扩展，高可用。

### 数据库拆分
单机容量不变，单机性能问题。
数据库垂直拆分。拆库：将一个库根据业务拆分，拆表：单表数据量过大，对单表进行拆分。
数据库水平拆分。按主键分库分表，按时间分库分表。

### 分布式事务
XA 分布式事务协议，BASE 柔性事务，分布式事务框架 Seata，Hmily。

* * *

## 分布式

### RPC
RPC 和 MQ 是分布式技术的基础。RPC 远程过程调用，即像调用本地方法一样调用远程方法。RPC 原理。RPC 技术框架。
1. 本地代理存根：Stub，存根类似于注册
2. 本地序列化反序列化
3. 网络通信
4. 远程序列化反序列化
5. 远程服务存根: Skeleton
6. 调用实际业务服务
7. 原路返回服务结果
8. 返回给本地调用方

### Dubbo

### Spring Cloud 

### 微服务

* * *

## 缓存

### 缓存原理
缓存的本质是空间换时间。
本地缓存
远程缓存
缓存策略

### Redis
Redis 数据结构

* * *

## MQ

### 消息队列基础

### ActiveMQ

### Kafka 

### RabbitMQ

### RocketMQ

### Pulsar

### EIP

### 自定义MQ

* * *



