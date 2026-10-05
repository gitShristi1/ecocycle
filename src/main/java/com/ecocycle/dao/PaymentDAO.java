package com.ecocycle.dao;

import com.ecocycle.model.Payment;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /**
     * Records the payment for a picked-up request: marks it PAID and inserts a
     * payments row, as ONE transaction. Both happen or neither does.
     *
     * Returns false when the request is not this company's or is not PICKED_UP
     * (so nothing was changed). Any database error is rolled back and rethrown.
     */
    public boolean recordPayment(int requestId, int companyId) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);          // start the transaction
            try {
                // step 1: only a PICKED_UP request that belongs to this company can be paid
                String update = "UPDATE waste_requests SET status = 'PAID', paid_at = SYSDATE "
                              + "WHERE request_id = ? AND company_id = ? AND status = 'PICKED_UP'";
                int changed;
                try (PreparedStatement ps = con.prepareStatement(update)) {
                    ps.setInt(1, requestId);
                    ps.setInt(2, companyId);
                    changed = ps.executeUpdate();
                }
                if (changed == 0) {
                    con.rollback();
                    return false;
                }

                // step 2: the amount is copied from the request itself, not from the browser
                String insert = "INSERT INTO payments (request_id, user_id, company_id, amount) "
                              + "SELECT request_id, user_id, company_id, total_amount "
                              + "FROM waste_requests WHERE request_id = ?";
                try (PreparedStatement ps = con.prepareStatement(insert)) {
                    ps.setInt(1, requestId);
                    ps.executeUpdate();
                }

                con.commit();                  // both steps worked: make them permanent
                return true;
            } catch (SQLException e) {
                con.rollback();                // anything failed: undo both steps
                throw e;
            }
        }
    }

    /** A user's payments received, newest first. */
    public List<Payment> findByUser(int userId) throws SQLException {
        String sql = "SELECT p.payment_id, p.request_id, t.type_name, c.company_name, "
                   + "p.amount, p.paid_at "
                   + "FROM payments p "
                   + "JOIN waste_requests r ON r.request_id = p.request_id "
                   + "JOIN waste_types t ON t.waste_type_id = r.waste_type_id "
                   + "JOIN companies c ON c.company_id = p.company_id "
                   + "WHERE p.user_id = ? "
                   + "ORDER BY p.paid_at DESC, p.payment_id DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setRequestId(rs.getInt("request_id"));
                    p.setWasteTypeName(rs.getString("type_name"));
                    p.setCompanyName(rs.getString("company_name"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setPaidAt(rs.getTimestamp("paid_at"));
                    list.add(p);
                }
            }
        }
        return list;
    }
}