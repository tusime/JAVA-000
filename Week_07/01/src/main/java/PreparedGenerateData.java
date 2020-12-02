import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedGenerateData {
    //"com.mysql.jdbc.Driver";
    static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    static final String URL = "jdbc:mysql://localhost:3306/testdb?useSSL=false";
    static final String USERNAME = "root";
    static final String PASSWORD = "root";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        exec();
        long end = System.currentTimeMillis();
        // 76580 ms，多值 Multiple Values 用时 8647ms
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
