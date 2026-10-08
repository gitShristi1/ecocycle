package com.ecocycle.dao;

import com.ecocycle.model.Feedback;
import com.ecocycle.model.RatingSummary;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    private static final String SELECT_FEEDBACK =
            "SELECT f.feedback_id, f.product_id, p.product_name, f.user_id, u.full_name, "
          + "f.rating, f.review_text, f.created_at "
          + "FROM feedback f "
          + "JOIN products p ON p.product_id = f.product_id "
          + "JOIN users u ON u.user_id = f.user_id ";

    // true when this user has an order item for this product in a non-cancelled order
    private static final String PURCHASED =
            "SELECT 1 FROM order_items i JOIN orders o ON o.order_id = i.order_id "
          + "WHERE i.product_id = ? AND o.user_id = ? AND o.status <> 'CANCELLED'";

    public boolean hasPurchased(int userId, int productId) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM (" + PURCHASED + ")")) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** The review this user wrote for this product, or null. */
    public Feedback findByUserAndProduct(int userId, int productId) throws SQLException {
        String sql = SELECT_FEEDBACK + "WHERE f.user_id = ? AND f.product_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Feedback> findByProduct(int productId) throws SQLException {
        String sql = SELECT_FEEDBACK + "WHERE f.product_id = ? "
                   + "ORDER BY f.created_at DESC, f.feedback_id DESC";
        return query(sql, productId);
    }

    /** Reviews of all of one company's products. */
    public List<Feedback> findByCompany(int companyId) throws SQLException {
        String sql = SELECT_FEEDBACK + "WHERE p.company_id = ? "
                   + "ORDER BY f.created_at DESC, f.feedback_id DESC";
        return query(sql, companyId);
    }

    public RatingSummary getProductRating(int productId) throws SQLException {
        return summary("SELECT COUNT(*), NVL(ROUND(AVG(rating), 1), 0) FROM feedback WHERE product_id = ?",
                productId);
    }

    public RatingSummary getCompanyRating(int companyId) throws SQLException {
        return summary("SELECT COUNT(*), NVL(ROUND(AVG(f.rating), 1), 0) FROM feedback f "
                     + "JOIN products p ON p.product_id = f.product_id WHERE p.company_id = ?",
                companyId);
    }

    /**
     * Saves a review: updates the user's existing one, or creates it. A new review
     * is only created if the user has bought the product (checked inside the same
     * statement). Returns false when the user is not a buyer.
     */
    public boolean save(int userId, int productId, int rating, String text) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {
            if (update(con, userId, productId, rating, text)) {
                return true;
            }
            try {
                return insertIfBuyer(con, userId, productId, rating, text);
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    // ORA-00001: the same user saved at the same moment, so update instead
                    return update(con, userId, productId, rating, text);
                }
                throw e;
            }
        }
    }

    /** Every review, newest first (at most 200). */
    public List<Feedback> findAllForAdmin() throws SQLException {
        String sql = "SELECT * FROM (" + SELECT_FEEDBACK
                   + "ORDER BY f.created_at DESC, f.feedback_id DESC) WHERE ROWNUM <= 200";
        List<Feedback> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Admin moderation: deletes one review. Returns false if it was already gone. */
    public boolean delete(int feedbackId) throws SQLException {
        String sql = "DELETE FROM feedback WHERE feedback_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            return ps.executeUpdate() > 0;
        }
    }
    
    // ---------------------------------------------------------------- helpers

    private boolean update(Connection con, int userId, int productId, int rating, String text)
            throws SQLException {
        String sql = "UPDATE feedback SET rating = ?, review_text = ?, created_at = SYSDATE "
                   + "WHERE product_id = ? AND user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, text);
            ps.setInt(3, productId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean insertIfBuyer(Connection con, int userId, int productId, int rating, String text)
            throws SQLException {
        String sql = "INSERT INTO feedback (product_id, user_id, rating, review_text) "
                   + "SELECT ?, ?, ?, ? FROM dual WHERE EXISTS (" + PURCHASED + ")";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, text);
            ps.setInt(5, productId);
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Feedback> query(String sql, int id) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private RatingSummary summary(String sql, int id) throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new RatingSummary(rs.getInt(1), rs.getBigDecimal(2));
            }
        }
    }

    private static Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackId(rs.getInt("feedback_id"));
        f.setProductId(rs.getInt("product_id"));
        f.setProductName(rs.getString("product_name"));
        f.setUserId(rs.getInt("user_id"));
        f.setUserName(rs.getString("full_name"));
        f.setRating(rs.getInt("rating"));
        f.setReviewText(rs.getString("review_text"));
        f.setCreatedAt(rs.getTimestamp("created_at"));
        return f;
    }
}