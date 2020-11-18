package com.tusi.jdbc.example;

import com.tusi.jdbc.entity.JdbcEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;

@Component
public class TransactionExample {
    @Transactional(rollbackFor = Exception.class)
    public void exec(JdbcEntity jdbcEntity) throws Exception {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(jdbcEntity.getDriverclass());
            conn = DriverManager.getConnection(jdbcEntity.getUrl(),
                    jdbcEntity.getUsername(), jdbcEntity.getPassword());

            String querySql1 = "SELECT * FROM student";
            ps = conn.prepareStatement(querySql1);
            rs = ps.executeQuery();
            System.out.println("=== jdbc transactional before ===");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }

            String insertSql = "INSERT INTO student (name) VALUES ('ddd')";
            ps = conn.prepareStatement(insertSql);
            ps.executeUpdate();

            String updateSql = "UPDATE student SET NAME='bbb3' WHERE NAME='bbb2'";
            ps = conn.prepareStatement(updateSql);
            ps.executeUpdate();

            String deleteSql = "DELETE FROM student WHERE NAME='ccc'";
            ps = conn.prepareStatement(deleteSql);
            ps.executeUpdate();

            String querySql2 = "SELECT * FROM student";
            ps = conn.prepareStatement(querySql2);
            rs = ps.executeQuery();
            System.out.println("=== jdbc transactional after ===");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }
        } finally {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
