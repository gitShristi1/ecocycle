package com.ecocycle.service;

import com.ecocycle.dao.ProductDAO;
import com.ecocycle.model.Cart;
import com.ecocycle.model.CartLine;
import com.ecocycle.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CartService {

    private static final ProductDAO PRODUCT_DAO = new ProductDAO();

    private CartService() { }

    /**
     * Builds the cart lines with fresh product data from the database.
     * Products that were removed (or whose company was blocked) are dropped from the cart.
     */
    public static List<CartLine> loadLines(Cart cart) throws SQLException {
        Map<Integer, Integer> items = cart.getItems();
        List<CartLine> lines = new ArrayList<>();
        if (items.isEmpty()) {
            return lines;
        }

        Map<Integer, Product> found = new HashMap<>();
        for (Product p : PRODUCT_DAO.findStoreProducts(items.keySet())) {
            found.put(p.getProductId(), p);
        }

        for (Map.Entry<Integer, Integer> e : items.entrySet()) {
            Product p = found.get(e.getKey());
            if (p == null) {
                cart.remove(e.getKey());
            } else {
                lines.add(new CartLine(p, e.getValue()));
            }
        }
        return lines;
    }

    public static BigDecimal total(List<CartLine> lines) {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartLine line : lines) {
            sum = sum.add(line.getLineTotal());
        }
        return sum;
    }

    /** True if any line asks for more than is in stock. */
    public static boolean hasProblem(List<CartLine> lines) {
        for (CartLine line : lines) {
            if (line.isExceedsStock()) {
                return true;
            }
        }
        return false;
    }
}