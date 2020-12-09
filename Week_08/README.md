### 01 水平分库分表

1. 创建分库 dbtest1、dbtest2，分表 t_order1、t_order2、t_order3，为简单测试，分表为 3 个。
2. 下载 shardingsphere-proxy， https://www.apache.org/dyn/closer.cgi/shardingsphere/5.0.0-alpha/apache-shardingsphere-5.0.0-alpha-shardingsphere-proxy-bin.tar.gz。
3. windows 自带 winZip 软件解压会提示文件名过长，解压失败。使用 7z 软件解压会出现文件名截断情况，启动提示 `找不到或无法加载主类 org.apache.shardingsphere.proxy.Bootstrap` 问题。使用命令 `tar zxvf apache-shardingsphere-5.0.0-alpha-shardingsphere-proxy-bin.tar.gz` 解压。
4. 在 lib 文件夹下添加 mysql-connector-java-5.x.xx.jar，否则启动报错
> Sharding-Proxy ShardingException: Cannot load JDBC driver class `com.mysql.jdbc.Driver`, make sure it in Sharding-Proxy’s classpath
5. 修改配置文件
config-sharding.yaml
```yaml
# 虚拟库名
schemaName: testsharding

dataSourceCommon:
  username: root
  password: 
  connectionTimeoutMilliseconds: 30000
  idleTimeoutMilliseconds: 60000
  maxLifetimeMilliseconds: 1800000
  maxPoolSize: 10
  minPoolSize: 1
  maintenanceIntervalMilliseconds: 30000

dataSources:
  dbtest1:
    url: jdbc:mysql://127.0.0.1:3316/dbtest1?serverTimezone=UTC&useSSL=false
  dbtest2:
    url: jdbc:mysql://127.0.0.1:3316/dbtest2?serverTimezone=UTC&useSSL=false

rules:
- !SHARDING
  tables:
    # 虚拟表名
    t_order:
      actualDataNodes: dbtest${1..2}.t_order${1..3}
      tableStrategy:
        standard:
          shardingColumn: id
          shardingAlgorithmName: t_order_inline
      keyGenerateStrategy:
        column: id
        keyGeneratorName: snowflake
        
  defaultDatabaseStrategy:
    standard:
      shardingColumn: id
      shardingAlgorithmName: database_inline
  defaultTableStrategy:
    none:
  
  shardingAlgorithms:
    database_inline:
      type: INLINE
      props:
        algorithm-expression: dbtest${id % 2 + 1}
    t_order_inline:
      type: INLINE
      props:
        algorithm-expression: t_order${id % 3 + 1}
        # 允许范围查询
        allow-range-query-with-inline-sharding: true
        
  keyGenerators:
    snowflake:
      type: SNOWFLAKE
      props:
        worker-id: 123
```

server.yaml
```yaml
#governance:
#  name: governance_ds
#  registryCenter:
#    type: ZooKeeper
#    serverLists: localhost:2181
#    props:
#      retryIntervalMilliseconds: 500
#      timeToLiveSeconds: 60
#      maxRetries: 3
#      operationTimeoutMilliseconds: 500
#  overwrite: false

authentication:
  users:
    root:
      password: root

props:
  max-connections-size-per-query: 1
  acceptor-size: 16  # The default value is available processors count * 2.
  executor-size: 16  # Infinite by default.
  proxy-frontend-flush-threshold: 128  # The default value is 128.
    # LOCAL: Proxy will run with LOCAL transaction.
    # XA: Proxy will run with XA transaction.
    # BASE: Proxy will run with B.A.S.E transaction.
  proxy-transaction-type: LOCAL
  proxy-opentracing-enabled: false
  proxy-hint-enabled: false
  query-with-cipher-column: true
  sql-show: false
  check-table-metadata-enabled: false
```

6. 测试
```
mysql -h 127.0.0.1 -P3307 -uroot -proot
mysql> show databases;
+--------------+
| Database     |
+--------------+
| testsharding |
+--------------+
mysql> insert `t_order`(`userid`,`total`,`status`) values(1,1111,1),(2,2222,1),(3,3333,1),(4,4444,1),(5,5555,1),(6,6666,1),(7,7777,1),(8,8888,1),(9,9999,1);
mysql> select * from t_order;
mysql> select * from t_order id=543596550203879426;
...
```
### 02 简单的分布式事务应用 demo
