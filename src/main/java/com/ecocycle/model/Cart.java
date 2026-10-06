package com.ecocycle.model;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** The shopping cart: only product ids and quantities. Prices are always read from the database. */
public class Cart implements Serializable {

    public static final int MAX_QUANTITY = 99;
    public static final int MAX_DISTINCT_ITEMS = 50;

    private final Map<Integer, Integer> items = new LinkedHashMap<>();

    /** The cart stored in this session (created on first use). */
    public static Cart of(HttpSession session) {
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }
            return cart;
        }
    }

    // synchronized: the same user may have two tabs open
    public synchronized boolean canAdd(int productId) {
        return items.containsKey(productId) || items.size() < MAX_DISTINCT_ITEMS;
    }

    /** Sets the quantity (0 or less removes the product). */
    public synchronized void setQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else if (canAdd(productId)) {
            items.put(productId, Math.min(quantity, MAX_QUANTITY));
        }
    }

    public synchronized int getQuantity(int productId) {
        return items.getOrDefault(productId, 0);
    }

    public synchronized void remove(int productId) {
        items.remove(productId);
    }

    public synchronized void clear() {
        items.clear();
    }

    /** Total number of units in the cart (shown in the navigation bar). */
    public synchronized int getItemCount() {
        int n = 0;
        for (int q : items.values()) {
            n += q;
        }
        return n;
    }

    /** A copy of productId -> quantity. */
    public synchronized Map<Integer, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }
}