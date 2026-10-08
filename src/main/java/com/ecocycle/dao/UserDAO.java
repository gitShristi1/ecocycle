package com.ecocycle.dao;

import com.ecocycle.model.User;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    private static final String SELECT_USER =
            "SELECT user_id, full_name, email, password_hash, phone, address, city, "
          + "status, created_at FROM users ";

    /** Saves a new user and returns the generated user_id. */
    public int insert(User u) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {

            // 1) get the next id from the sequence
            int id;
            try (PreparedStatement ps = con.prepareStatement("SELECT seq_users.NEXTVAL FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }

            // 2) insert the row (status and created_at use the table defaults)
            String sql = "INSERT INTO users "
                       + "(user_id, full_name, email, password_hash, phone, address, city) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, u.getFullName());
                ps.setString(3, normalize(u.getEmail()));
                ps.setString(4, u.getPasswordHash());
                ps.setString(5, u.getPhone());
                ps.setString(6, u.getAddress());
                ps.setString(7, u.getCity());
                ps.executeUpdate();
            }
            return id;
        }
    }

    /** Returns the user with this email, or null if there is none. */
    public User findByEmail(String email) throws SQLException {
        String sql = SELECT_USER + "WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Returns the user with this id, or null if there is none. */
    public User findById(int userId) throws SQLException {
        String sql = SELECT_USER + "WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
    
    /** Users for the admin screen, newest first (at most 200). A blank q means everyone. */
    public List<User> search(String q) throws SQLException {
        boolean filter = q != null && !q.trim().isEmpty();
        StringBuilder sql = new StringBuilder("SELECT * FROM (").append(SELECT_USER);
        if (filter) {
            sql.append("WHERE LOWER(full_name) LIKE ? OR LOWER(email) LIKE ? ");
        }
        sql.append("ORDER BY created_at DESC, user_id DESC) WHERE ROWNUM <= 200");

        List<User> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (filter) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /** Changes the status only if it currently has the expected value. */
    public boolean updateStatusIfCurrent(int userId, String expected, String newStatus)
            throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ? AND status = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId);
            ps.setString(3, expected);
            return ps.executeUpdate() > 0;
        }
    }

    /** The account's current status, or null if the user does not exist. */
    public String getStatus(int userId) throws SQLException {
        String sql = "SELECT status FROM users WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    // emails are stored in lower case so "A@x.com" and "a@x.com" are the same account
    private static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setCity(rs.getString("city"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
