package com.ecocycle.dao;

import com.ecocycle.model.ProductSales;
import com.ecocycle.model.SaleLine;
import com.ecocycle.model.SalesSummary;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    /** Totals for one company. All zeros when it has not sold anything. */
    public SalesSummary getSummary(int companyId) throws SQLException {
        String sql = "SELECT NVL(SUM(i.quantity), 0), "
                   + "NVL(SUM(i.unit_price * i.quantity), 0), "
                   + "NVL(SUM(i.commission_amount), 0) "
                   + "FROM order_items i JOIN orders o ON o.order_id = i.order_id "
                   + "WHERE i.company_id = ? AND o.status <> 'CANCELLED'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new SalesSummary(rs.getInt(1), rs.getBigDecimal(2), rs.getBigDecimal(3));
            }
        }
    }

    /** Sales grouped by product, best seller (by revenue) first. */
    public List<ProductSales> findByProduct(int companyId) throws SQLException {
        String sql = "SELECT p.product_name, SUM(i.quantity) AS units, "
                   + "SUM(i.unit_price * i.quantity) AS revenue, "
                   + "SUM(i.commission_amount) AS commission "
                   + "FROM order_items i "
                   + "JOIN orders o ON o.order_id = i.order_id "
                   + "JOIN products p ON p.product_id = i.product_id "
                   + "WHERE i.company_id = ? AND o.status <> 'CANCELLED' "
                   + "GROUP BY p.product_id, p.product_name "
                   + "ORDER BY revenue DESC";
        List<ProductSales> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductSales(rs.getString("product_name"), rs.getInt("units"),
                            rs.getBigDecimal("revenue"), rs.getBigDecimal("commission")));
                }
            }
        }
        return list;
    }

    /** The most recent sales, newest first. */
    public List<SaleLine> findRecent(int companyId, int limit) throws SQLException {
        // Oracle 10g: sort in the inner query, then cut with ROWNUM in the outer one
        String sql = "SELECT * FROM ("
                   + " SELECT o.order_id, o.created_at, p.product_name, i.quantity, "
                   + "        i.unit_price * i.quantity AS amount, i.commission_amount "
                   + " FROM order_items i "
                   + " JOIN orders o ON o.order_id = i.order_id "
                   + " JOIN products p ON p.product_id = i.product_id "
                   + " WHERE i.company_id = ? AND o.status <> 'CANCELLED' "
                   + " ORDER BY o.created_at DESC, i.order_item_id DESC"
                   + ") WHERE ROWNUM <= ?";
        List<SaleLine> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SaleLine(rs.getInt("order_id"), rs.getTimestamp("created_at"),
                            rs.getString("product_name"), rs.getInt("quantity"),
                            rs.getBigDecimal("amount"), rs.getBigDecimal("commission_amount")));
                }
            }
        }
        return list;
    }
}