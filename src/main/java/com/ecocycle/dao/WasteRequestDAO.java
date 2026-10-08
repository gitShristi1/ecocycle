package com.ecocycle.dao;

import com.ecocycle.model.WasteRequest;
import com.ecocycle.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WasteRequestDAO {

    // shared by every query below; each one adds its own WHERE and ORDER BY
    private static final String SELECT_REQUEST =
            "SELECT r.request_id, r.user_id, r.waste_type_id, t.type_name, "
          + "r.company_id, c.company_name, u.full_name, u.phone, "
          + "r.weight_kg, r.rate_per_kg, r.total_amount, r.pickup_address, r.city, "
          + "r.notes, r.status, r.created_at, r.accepted_at, r.picked_up_at "
          + "FROM waste_requests r "
          + "JOIN waste_types t ON t.waste_type_id = r.waste_type_id "
          + "JOIN users u ON u.user_id = r.user_id "
          + "LEFT JOIN companies c ON c.company_id = r.company_id ";

    /** Saves a new request. Id comes from the trigger, status defaults to SUBMITTED. */
    public void insert(WasteRequest r) throws SQLException {
        String sql = "INSERT INTO waste_requests "
                   + "(user_id, waste_type_id, weight_kg, rate_per_kg, total_amount, "
                   + "pickup_address, city, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getWasteTypeId());
            ps.setBigDecimal(3, r.getWeightKg());
            ps.setBigDecimal(4, r.getRatePerKg());
            ps.setBigDecimal(5, r.getTotalAmount());
            ps.setString(6, r.getPickupAddress());
            ps.setString(7, r.getCity());
            ps.setString(8, r.getNotes());
            ps.executeUpdate();
        }
    }

    /** A user's own requests, newest first. */
    public List<WasteRequest> findByUser(int userId) throws SQLException {
        String sql = SELECT_REQUEST
                   + "WHERE r.user_id = ? "
                   + "ORDER BY r.created_at DESC, r.request_id DESC";
        List<WasteRequest> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Requests no company has accepted yet, oldest first.
     * wasteTypeId <= 0 means "any type"; a blank city means "any city".
     */
    public List<WasteRequest> findOpen(int wasteTypeId, String city) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_REQUEST);
        sql.append("WHERE r.status = 'SUBMITTED' ");
        List<Object> params = new ArrayList<>();

        if (wasteTypeId > 0) {
            sql.append("AND r.waste_type_id = ? ");
            params.add(wasteTypeId);
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append("AND LOWER(r.city) LIKE ? ");
            params.add("%" + city.trim().toLowerCase() + "%");
        }
        sql.append("ORDER BY r.created_at, r.request_id");

        List<WasteRequest> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Cancels the request only if it belongs to this user and is still SUBMITTED.
     * Returns false when nothing was changed.
     */
    public boolean cancelIfSubmitted(int requestId, int userId) throws SQLException {
        String sql = "UPDATE waste_requests SET status = 'CANCELLED' "
                   + "WHERE request_id = ? AND user_id = ? AND status = 'SUBMITTED'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * A company accepts a request. Only one company can ever win: the UPDATE
     * succeeds only while the status is still SUBMITTED. The company must also
     * still be APPROVED (it could have been blocked after it logged in).
     * Returns false when nothing was changed.
     */
    public boolean accept(int requestId, int companyId) throws SQLException {
        String sql = "UPDATE waste_requests "
                   + "SET company_id = ?, status = 'ACCEPTED', accepted_at = SYSDATE "
                   + "WHERE request_id = ? AND status = 'SUBMITTED' "
                   + "AND EXISTS (SELECT 1 FROM companies "
                   + "            WHERE company_id = ? AND status = 'APPROVED')";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setInt(2, requestId);
            ps.setInt(3, companyId);
            return ps.executeUpdate() > 0;
        }
    }

    
    /** Requests this company has accepted (any later status), newest first. */
    public List<WasteRequest> findByCompany(int companyId) throws SQLException {
        String sql = SELECT_REQUEST
                   + "WHERE r.company_id = ? "
                   + "ORDER BY r.accepted_at DESC, r.request_id DESC";
        List<WasteRequest> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Marks the request as picked up, only if it belongs to this company
     * and is still ACCEPTED. Returns false when nothing was changed.
     */
    public boolean markPickedUp(int requestId, int companyId) throws SQLException {
        String sql = "UPDATE waste_requests "
                   + "SET status = 'PICKED_UP', picked_up_at = SYSDATE "
                   + "WHERE request_id = ? AND company_id = ? AND status = 'ACCEPTED'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /** Requests for the admin monitoring screen, newest first (at most 200). A null status means all. */
    public List<WasteRequest> findForAdmin(String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM (").append(SELECT_REQUEST);
        if (status != null) {
            sql.append("WHERE r.status = ? ");
        }
        sql.append("ORDER BY r.created_at DESC, r.request_id DESC) WHERE ROWNUM <= 200");

        List<WasteRequest> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (status != null) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private static WasteRequest mapRow(ResultSet rs) throws SQLException {
        WasteRequest r = new WasteRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setWasteTypeId(rs.getInt("waste_type_id"));
        r.setWasteTypeName(rs.getString("type_name"));
        r.setCompanyId(rs.getInt("company_id"));          // 0 when still null
        r.setCompanyName(rs.getString("company_name"));
        r.setUserName(rs.getString("full_name"));
        r.setUserPhone(rs.getString("phone"));
        r.setWeightKg(rs.getBigDecimal("weight_kg"));
        r.setRatePerKg(rs.getBigDecimal("rate_per_kg"));
        r.setTotalAmount(rs.getBigDecimal("total_amount"));
        r.setPickupAddress(rs.getString("pickup_address"));
        r.setCity(rs.getString("city"));
        r.setNotes(rs.getString("notes"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setAcceptedAt(rs.getTimestamp("accepted_at"));
        r.setPickedUpAt(rs.getTimestamp("picked_up_at"));
        return r;
    }
}