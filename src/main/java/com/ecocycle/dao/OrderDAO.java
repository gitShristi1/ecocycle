package com.ecocycle.dao;

import com.ecocycle.model.Order;
import com.ecocycle.util.DBUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderDAO {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal DEFAULT_COMMISSION = new BigDecimal("10");

    /** One item exactly as it was sold. */
    private static class SoldLine {
        int productId;
        int companyId;
        int quantity;
        BigDecimal unitPrice;
        BigDecimal commissionPercent;
        BigDecimal commissionAmount;
    }

    /**
     * Places an order as ONE transaction: reduce the stock of every product, then
     * save the order and its items (with the commission). If anything fails,
     * everything is undone, so stock is never lost without an order behind it.
     *
     * cartItems is productId -> quantity. Prices are read from the database here;
     * nothing about money comes from the browser.
     *
     * Returns the new order id. Throws OrderException when an item is sold out or gone.
     */
    public int placeOrder(int userId, String shippingAddress, Map<Integer, Integer> cartItems)
            throws SQLException, OrderException {
        if (cartItems.isEmpty()) {
            throw new OrderException("Your cart is empty.");
        }

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);                   // start the transaction
            try {
                BigDecimal commissionPercent = readCommissionPercent(con);
                List<SoldLine> sold = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;

                // Lowest product id first. Two buyers with overlapping carts then lock
                // rows in the same order, so they wait for each other instead of deadlocking.
                for (Map.Entry<Integer, Integer> e : new TreeMap<>(cartItems).entrySet()) {
                    SoldLine line = reserve(con, e.getKey(), e.getValue(), commissionPercent);
                    sold.add(line);
                    total = total.add(line.unitPrice.multiply(BigDecimal.valueOf(line.quantity)));
                }

                int orderId = nextOrderId(con);
                insertOrder(con, orderId, userId, total, shippingAddress);
                for (SoldLine line : sold) {
                    insertItem(con, orderId, line);
                }

                con.commit();                           // everything worked: make it permanent
                return orderId;
            } catch (SQLException | OrderException ex) {
                con.rollback();                         // anything failed: undo all of it
                throw ex;
            }
        }
    }

    /** A user's own orders, newest first. */
    public List<Order> findByUser(int userId) throws SQLException {
        String sql = "SELECT o.order_id, o.user_id, o.total_amount, o.shipping_address, "
                   + "o.status, o.created_at, "
                   + "(SELECT NVL(SUM(i.quantity), 0) FROM order_items i "
                   + " WHERE i.order_id = o.order_id) AS item_count "
                   + "FROM orders o WHERE o.user_id = ? "
                   + "ORDER BY o.created_at DESC, o.order_id DESC";
        List<Order> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setUserId(rs.getInt("user_id"));
                    o.setTotalAmount(rs.getBigDecimal("total_amount"));
                    o.setShippingAddress(rs.getString("shipping_address"));
                    o.setStatus(rs.getString("status"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                    o.setItemCount(rs.getInt("item_count"));
                    list.add(o);
                }
            }
        }
        return list;
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Takes quantity units out of stock, but only while the product is active, its
     * company is approved and enough units are left. The row stays locked until
     * the transaction ends.
     */
    private SoldLine reserve(Connection con, int productId, int qty, BigDecimal percent)
            throws SQLException, OrderException {
        String update = "UPDATE products SET stock = stock - ? "
                      + "WHERE product_id = ? AND status = 'ACTIVE' AND stock >= ? "
                      + "AND EXISTS (SELECT 1 FROM companies c "
                      + "            WHERE c.company_id = products.company_id AND c.status = 'APPROVED')";
        int changed;
        try (PreparedStatement ps = con.prepareStatement(update)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            changed = ps.executeUpdate();
        }
        if (changed == 0) {
            throw new OrderException(describeProblem(con, productId, qty));
        }

        // our UPDATE locked the row, so this price cannot change before we commit
        BigDecimal price;
        int companyId;
        String select = "SELECT price, company_id FROM products WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(select)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                price = rs.getBigDecimal(1);
                companyId = rs.getInt(2);
            }
        }

        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));
        SoldLine line = new SoldLine();
        line.productId = productId;
        line.companyId = companyId;
        line.quantity = qty;
        line.unitPrice = price;
        line.commissionPercent = percent;
        line.commissionAmount = lineTotal.multiply(percent).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        return line;
    }

    /** Works out a customer-friendly reason why a product could not be reserved. */
    private String describeProblem(Connection con, int productId, int qty) throws SQLException {
        String sql = "SELECT product_name, stock, status FROM products WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !"ACTIVE".equals(rs.getString("status"))) {
                    return "A product in your cart is no longer available.";
                }
                String name = rs.getString("product_name");
                int stock = rs.getInt("stock");
                if (stock < qty) {
                    return "Not enough stock for \"" + name + "\" (only " + stock + " left).";
                }
                return "\"" + name + "\" is no longer available.";
            }
        }
    }

    /** The commission percentage set by the admin (10 if the setting is missing or invalid). */
    private BigDecimal readCommissionPercent(Connection con) throws SQLException {
        String sql = "SELECT setting_value FROM platform_settings WHERE setting_key = 'COMMISSION_PERCENT'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                try {
                    BigDecimal v = new BigDecimal(rs.getString(1).trim());
                    if (v.signum() >= 0 && v.compareTo(HUNDRED) <= 0) {
                        return v;
                    }
                } catch (NumberFormatException e) {
                    // fall through to the default
                }
            }
        }
        return DEFAULT_COMMISSION;
    }

    private int nextOrderId(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT seq_orders.NEXTVAL FROM dual");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private void insertOrder(Connection con, int orderId, int userId, BigDecimal total, String address)
            throws SQLException {
        String sql = "INSERT INTO orders (order_id, user_id, total_amount, shipping_address) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ps.setBigDecimal(3, total);
            ps.setString(4, address);
            ps.executeUpdate();
        }
    }

    private void insertItem(Connection con, int orderId, SoldLine line) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, company_id, quantity, "
                   + "unit_price, commission_percent, commission_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, line.productId);
            ps.setInt(3, line.companyId);
            ps.setInt(4, line.quantity);
            ps.setBigDecimal(5, line.unitPrice);
            ps.setBigDecimal(6, line.commissionPercent);
            ps.setBigDecimal(7, line.commissionAmount);
            ps.executeUpdate();
        }
    }
}