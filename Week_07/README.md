#### 01 插入100万订单模拟数据，测试不同方式的插入效率

1. PreparedStatement addBatch 76580 ms 
``` java
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedGenerateData {
    static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/testdb?useSSL=false";
    static final String USERNAME = "root";
    static final String PASSWORD = "root";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        exec();
        long end = System.currentTimeMillis();
        // 76580 ms
        System.out.println("耗时：" + (end - start) + " ms");
    }

    public static void exec() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 关闭自动提交
            conn.setAutoCommit(false);
            String insertOrderSql = "insert `t_order`(`userid`,`total`,`status`) values(?,?,?)";
            // 随机数
            SecureRandom sr = new SecureRandom();
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql)) {
                for (int i=1;i<=1000000;i++) {
                    long userid = sr.nextInt(1000);
                    long total = sr.nextInt(100000)+10;
                    pstmt.setLong(1, userid);
                    pstmt.setLong(2, total);
                    pstmt.setLong(3, 1);
                    pstmt.addBatch();
                    if(i % 500 == 0) {
                        pstmt.executeBatch();
                        // 清空batch
                        pstmt.clearBatch();
                    }
                }
                conn.commit();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
```

2. 多值 Multiple Values，用时 8647 ms

3. load data 暂未测

#### 02 配置异步复制，半同步复制、组复制


#### 03 读写分离-动态切换数据源版本1.0

1. 配置 3 个 MySQL 数据库实例，为方便区分，3 个数据库结构一致，数据内容不同，sql 文件为 init.sql
	* jdbc:mysql://localhost:3316/testdb
	* jdbc:mysql://localhost:3326/testdb
	* jdbc:mysql://localhost:3336/testdb

2. 测试
	* 主库读取：http://127.0.0.1:8080/list1
	* 从库读取：http://127.0.0.1:8080/list2

#### 04 读写分离-数据库框架版本2.0

1. 将 03 中 MySQL 数据库的数据库表清空，并配置主从数据库，3316 为主数据库。
2. 测试
	* 主库写：http://127.0.0.1:8080/list
	* 从库读：http://127.0.0.1:8080/insert

