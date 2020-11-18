package com.tusi.jdbc.example;

import com.tusi.jdbc.entity.JdbcEntity;

import java.sql.*;

public class JdbcNativeExample {

    public void exec(JdbcEntity jdbcEntity) {
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
            System.out.println("=== jdbc native before ===");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }

            String insertSql = "INSERT INTO student (name) VALUES ('ccc')";
            ps = conn.prepareStatement(insertSql);
            ps.executeUpdate();

            String updateSql = "UPDATE student SET NAME='bbb2' WHERE NAME='bbb'";
            ps = conn.prepareStatement(updateSql);
            ps.executeUpdate();

            String deleteSql = "DELETE FROM student WHERE NAME='aaa'";
            ps = conn.prepareStatement(deleteSql);
            ps.executeUpdate();

            String querySql2 = "SELECT * FROM student";
            ps = conn.prepareStatement(querySql2);
            rs = ps.executeQuery();
            System.out.println("=== jdbc native after ===");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
