package com.ecocycle.dao;

import com.ecocycle.model.PlatformStats;
import com.ecocycle.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDAO {

    public PlatformStats load() throws SQLException {
        PlatformStats s = new PlatformStats();
        try (Connection con = DBUtil.getConnection()) {
            s.setUsers(count(con, "SELECT COUNT(*) FROM users"));
            s.setBlockedUsers(count(con, "SELECT COUNT(*) FROM users WHERE status = 'BLOCKED'"));
            s.setApprovedCompanies(count(con, "SELECT COUNT(*) FROM companies WHERE status = 'APPROVED'"));
            s.setPendingCompanies(count(con, "SELECT COUNT(*) FROM companies WHERE status = 'PENDING'"));
            s.setOpenRequests(count(con, "SELECT COUNT(*) FROM waste_requests WHERE status = 'SUBMITTED'"));
            s.setOrders(count(con, "SELECT COUNT(*) FROM orders WHERE status <> 'CANCELLED'"));

            s.setWasteCollectedKg(amount(con,
                    "SELECT NVL(SUM(weight_kg), 0) FROM waste_requests "
                  + "WHERE status IN ('PICKED_UP', 'PAID')"));
            s.setTotalPayments(amount(con, "SELECT NVL(SUM(amount), 0) FROM payments"));
            s.setProductSales(amount(con,
                    "SELECT NVL(SUM(i.unit_price * i.quantity), 0) FROM order_items i "
                  + "JOIN orders o ON o.order_id = i.order_id WHERE o.status <> 'CANCELLED'"));
            s.setCommissionEarned(amount(con,
                    "SELECT NVL(SUM(i.commission_amount), 0) FROM order_items i "
                  + "JOIN orders o ON o.order_id = i.order_id WHERE o.status <> 'CANCELLED'"));
        }
        return s;
    }

    private static int count(Connection con, String sql) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static BigDecimal amount(Connection con, String sql) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }
}