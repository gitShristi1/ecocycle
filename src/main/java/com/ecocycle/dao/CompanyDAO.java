package com.ecocycle.dao;

import com.ecocycle.model.Company;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    private static final String SELECT_COMPANY =
            "SELECT company_id, company_name, email, password_hash, phone, address, city, "
          + "status, created_at FROM companies ";

    /** Saves a new company (status starts as PENDING) and returns its id. */
    public int insert(Company c) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {

            int id;
            try (PreparedStatement ps = con.prepareStatement("SELECT seq_companies.NEXTVAL FROM dual");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }

            String sql = "INSERT INTO companies "
                       + "(company_id, company_name, email, password_hash, phone, address, city) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, c.getCompanyName());
                ps.setString(3, normalize(c.getEmail()));
                ps.setString(4, c.getPasswordHash());
                ps.setString(5, c.getPhone());
                ps.setString(6, c.getAddress());
                ps.setString(7, c.getCity());
                ps.executeUpdate();
            }
            return id;
        }
    }

    public Company findByEmail(String email) throws SQLException {
        String sql = SELECT_COMPANY + "WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Company findById(int companyId) throws SQLException {
        String sql = SELECT_COMPANY + "WHERE company_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM companies WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalize(email));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** All companies with the given status, oldest first (used by the admin approval screen). */
    public List<Company> findByStatus(String status) throws SQLException {
        String sql = SELECT_COMPANY + "WHERE status = ? ORDER BY created_at";
        List<Company> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /** Changes a company's status. Returns true if a row was updated. */
    public boolean updateStatus(int companyId, String status) throws SQLException {
        String sql = "UPDATE companies SET status = ? WHERE company_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        }
    }
    
     /**
     * Changes the status only if the company currently has the expected status.
     * Returns false when nothing was changed (for example, someone else already handled it).
     */
    public boolean updateStatusIfCurrent(int companyId, String expectedStatus, String newStatus)
            throws SQLException {
        String sql = "UPDATE companies SET status = ? WHERE company_id = ? AND status = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, companyId);
            ps.setString(3, expectedStatus);
            return ps.executeUpdate() > 0;
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM companies WHERE status = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<Company> findAll() throws SQLException {
        String sql = SELECT_COMPANY + "ORDER BY created_at DESC, company_id DESC";
        List<Company> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** The account's current status, or null if the company does not exist. */
    public String getStatus(int companyId) throws SQLException {
        String sql = "SELECT status FROM companies WHERE company_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static Company mapRow(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setCompanyId(rs.getInt("company_id"));
        c.setCompanyName(rs.getString("company_name"));
        c.setEmail(rs.getString("email"));
        c.setPasswordHash(rs.getString("password_hash"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
