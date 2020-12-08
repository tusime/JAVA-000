package com.tusi.demo.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class OrderDao {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> list() {
        String sql = "SELECT * FROM t_order;";
        List<Map<String, Object>> lm = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            log.info("dataSource={}", conn.getMetaData().getURL());
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("userId", rs.getLong("userId"));
                m.put("total", rs.getLong("total"));
                m.put("status", rs.getInt("status"));
                m.put("createTime", rs.getTimestamp("create_time"));
                m.put("updateTime", rs.getTimestamp("update_time"));
                lm.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lm;
    }

    public int insertOrder() {
        String sql = "insert `t_order`(`userid`,`total`,`status`) values(?,?,?)";
        SecureRandom sr = new SecureRandom();
        long userid = sr.nextInt(1000);
        long total = sr.nextInt(100000)+10;
        try (Connection conn = dataSource.getConnection()) {
            log.info("dataSource={}", conn.getMetaData().getURL());
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userid);
            pstmt.setLong(2, total);
            pstmt.setLong(3, 1);
            log.info("userid={} , total={}", userid, total);
            int res = pstmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
