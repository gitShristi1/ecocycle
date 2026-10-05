package com.ecocycle.dao;

import com.ecocycle.model.WasteType;
import com.ecocycle.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WasteTypeDAO {

    private static final String SELECT_TYPE =
            "SELECT waste_type_id, type_name, rate_per_kg, active FROM waste_types ";

    public List<WasteType> findAll() throws SQLException {
        return query(SELECT_TYPE + "ORDER BY type_name");
    }

    /** Only the types users may choose from. */
    public List<WasteType> findActive() throws SQLException {
        return query(SELECT_TYPE + "WHERE active = 'Y' ORDER BY type_name");
    }

    public WasteType findById(int id) throws SQLException {
        String sql = SELECT_TYPE + "WHERE waste_type_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** The id is filled in by the database trigger. A duplicate name throws ORA-00001. */
    public void insert(String typeName, BigDecimal ratePerKg) throws SQLException {
        String sql = "INSERT INTO waste_types (type_name, rate_per_kg) VALUES (?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, typeName);
            ps.setBigDecimal(2, ratePerKg);
            ps.executeUpdate();
        }
    }

    public boolean updateRate(int id, BigDecimal ratePerKg) throws SQLException {
        String sql = "UPDATE waste_types SET rate_per_kg = ? WHERE waste_type_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, ratePerKg);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setActive(int id, boolean active) throws SQLException {
        String sql = "UPDATE waste_types SET active = ? WHERE waste_type_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, active ? "Y" : "N");
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<WasteType> query(String sql) throws SQLException {
        List<WasteType> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private static WasteType mapRow(ResultSet rs) throws SQLException {
        WasteType t = new WasteType();
        t.setWasteTypeId(rs.getInt("waste_type_id"));
        t.setTypeName(rs.getString("type_name"));
        t.setRatePerKg(rs.getBigDecimal("rate_per_kg"));
        t.setActive("Y".equals(rs.getString("active")));
        return t;
    }
}