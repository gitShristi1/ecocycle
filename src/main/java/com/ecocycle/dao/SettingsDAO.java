package com.ecocycle.dao;

import com.ecocycle.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO {

    public static final BigDecimal DEFAULT_COMMISSION = new BigDecimal("10");
    private static final String COMMISSION_KEY = "COMMISSION_PERCENT";

    /** The platform commission in percent (10 if the setting is missing or unreadable). */
    public BigDecimal getCommissionPercent() throws SQLException {
        String sql = "SELECT setting_value FROM platform_settings WHERE setting_key = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, COMMISSION_KEY);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new BigDecimal(rs.getString(1).trim());
                    } catch (NumberFormatException e) {
                        // fall through to the default
                    }
                }
            }
        }
        return DEFAULT_COMMISSION;
    }

    public void setCommissionPercent(BigDecimal percent) throws SQLException {
        try (Connection con = DBUtil.getConnection()) {
            String update = "UPDATE platform_settings SET setting_value = ? WHERE setting_key = ?";
            int changed;
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setString(1, percent.toPlainString());
                ps.setString(2, COMMISSION_KEY);
                changed = ps.executeUpdate();
            }
            if (changed == 0) {                      // the row was missing: create it
                String insert = "INSERT INTO platform_settings (setting_key, setting_value) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(insert)) {
                    ps.setString(1, COMMISSION_KEY);
                    ps.setString(2, percent.toPlainString());
                    ps.executeUpdate();
                }
            }
        }
    }
}