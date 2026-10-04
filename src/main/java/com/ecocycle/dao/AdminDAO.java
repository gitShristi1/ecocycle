package com.ecocycle.dao;

import com.ecocycle.model.Admin;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    private static final String SELECT_ADMIN =
            "SELECT admin_id, full_name, email, password_hash, created_at FROM admins ";

    /** Saves a new admin and returns its id. */
    public int insert(Admin a) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {

            int id;
            try (PreparedStatement ps = con.prepareStatement("SELECT seq_admins.NEXTVAL FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }

            String sql = "INSERT INTO admins (admin_id, full_name, email, password_hash) "
                       + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, a.getFullName());
                ps.setString(3, normalize(a.getEmail()));
                ps.setString(4, a.getPasswordHash());
                ps.executeUpdate();
            }
            return id;
        }
    }

    public Admin findByEmail(String email) throws SQLException {
        String sql = SELECT_ADMIN + "WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    private static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminId(rs.getInt("admin_id"));
        a.setFullName(rs.getString("full_name"));
        a.setEmail(rs.getString("email"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
