package com.ecocycle.dao;

import com.ecocycle.model.Product;
import com.ecocycle.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String SELECT_PRODUCT =
            "SELECT p.product_id, p.company_id, c.company_name, p.product_name, p.description, "
          + "p.price, p.stock, p.image_path, p.status, p.created_at "
          + "FROM products p JOIN companies c ON c.company_id = p.company_id ";

    /** Saves a new product. The id comes from the trigger, status defaults to ACTIVE. */
    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO products (company_id, product_name, description, price, stock) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getCompanyId());
            ps.setString(2, p.getProductName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.executeUpdate();
        }
    }

    /** A company's own products that have not been removed, newest first. */
    public List<Product> findByCompany(int companyId) throws SQLException {
        String sql = SELECT_PRODUCT
                   + "WHERE p.company_id = ? AND p.status = 'ACTIVE' "
                   + "ORDER BY p.created_at DESC, p.product_id DESC";
        List<Product> list = new ArrayList<>();
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

    /** One product, but only if it belongs to this company and is not removed. */
    public Product findOwnedActive(int productId, int companyId) throws SQLException {
        String sql = SELECT_PRODUCT
                   + "WHERE p.product_id = ? AND p.company_id = ? AND p.status = 'ACTIVE'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Updates name, description, price and stock. Returns false if nothing matched. */
    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE products SET product_name = ?, description = ?, price = ?, stock = ? "
                   + "WHERE product_id = ? AND company_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getProductId());
            ps.setInt(6, p.getCompanyId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Soft delete: the row stays (old orders may point to it) but is hidden. */
    public boolean remove(int productId, int companyId) throws SQLException {
        String sql = "UPDATE products SET status = 'REMOVED' "
                   + "WHERE product_id = ? AND company_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Products shown in the store: active products of approved companies.
     * q: text to look for in the name or description (blank = anything)
     * maxPrice: upper price limit (null = no limit)
     * sort: "price_asc", "price_desc", anything else = newest first
     */
    public List<Product> search(String q, BigDecimal maxPrice, boolean inStockOnly, String sort)
            throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_PRODUCT);
        sql.append("WHERE p.status = 'ACTIVE' AND c.status = 'APPROVED' ");
        List<Object> params = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            String like = "%" + q.trim().toLowerCase() + "%";
            sql.append("AND (LOWER(p.product_name) LIKE ? OR LOWER(p.description) LIKE ?) ");
            params.add(like);
            params.add(like);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        if (inStockOnly) {
            sql.append("AND p.stock > 0 ");
        }

        // the ORDER BY text comes from this fixed list, never from the browser
        if ("price_asc".equals(sort)) {
            sql.append("ORDER BY p.price, p.product_id");
        } else if ("price_desc".equals(sort)) {
            sql.append("ORDER BY p.price DESC, p.product_id");
        } else {
            sql.append("ORDER BY p.created_at DESC, p.product_id DESC");
        }

        List<Product> list = new ArrayList<>();
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

    private static Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setCompanyId(rs.getInt("company_id"));
        p.setCompanyName(rs.getString("company_name"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setImagePath(rs.getString("image_path"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}