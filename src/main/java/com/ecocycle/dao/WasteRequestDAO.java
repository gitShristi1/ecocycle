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
        String sql = "SELECT r.request_id, r.user_id, r.waste_type_id, t.type_name, "
                   + "r.weight_kg, r.rate_per_kg, r.total_amount, r.pickup_address, "
                   + "r.city, r.notes, r.status, r.created_at "
                   + "FROM waste_requests r "
                   + "JOIN waste_types t ON t.waste_type_id = r.waste_type_id "
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

    private static WasteRequest mapRow(ResultSet rs) throws SQLException {
        WasteRequest r = new WasteRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setWasteTypeId(rs.getInt("waste_type_id"));
        r.setWasteTypeName(rs.getString("type_name"));
        r.setWeightKg(rs.getBigDecimal("weight_kg"));
        r.setRatePerKg(rs.getBigDecimal("rate_per_kg"));
        r.setTotalAmount(rs.getBigDecimal("total_amount"));
        r.setPickupAddress(rs.getString("pickup_address"));
        r.setCity(rs.getString("city"));
        r.setNotes(rs.getString("notes"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}