package com.tusi.jdbc.example;

import com.tusi.jdbc.entity.JdbcEntity;
import com.tusi.jdbc.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DataSourceExample {
    @Autowired
    private DataSource dataSource;

    public void exec(DataSource dataSource) throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();

            String querySql1 = "SELECT * FROM student";
            ps = conn.prepareStatement(querySql1);
            rs = ps.executeQuery();
            System.out.println("=== dataSource before ===");
            while(rs.next()){
                System.out.println(rs.getString("name"));
            }

            String insertSql = "INSERT INTO student (name) VALUES ('eee')";
            ps = conn.prepareStatement(insertSql);
            ps.executeUpdate();

            String updateSql = "UPDATE student SET NAME='bbb4' WHERE NAME='bbb3'";
            ps = conn.prepareStatement(updateSql);
            ps.executeUpdate();

            String deleteSql = "DELETE FROM student WHERE NAME='ddd'";
            ps = conn.prepareStatement(deleteSql);
            ps.executeUpdate();

            String querySql2 = "SELECT * FROM student";
            ps = conn.prepareStatement(querySql2);
            rs = ps.executeQuery();
            System.out.println("=== dataSource after ===");
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
