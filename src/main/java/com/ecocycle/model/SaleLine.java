package com.ecocycle.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** One recent sale (one product in one order). */
public class SaleLine {

    private final int orderId;
    private final Timestamp createdAt;
    private final String productName;
    private final int quantity;
    private final BigDecimal amount;
    private final BigDecimal commission;

    public SaleLine(int orderId, Timestamp createdAt, String productName,
                    int quantity, BigDecimal amount, BigDecimal commission) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;
        this.commission = commission;
    }

    public int getOrderId() { return orderId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getCommission() { return commission; }
    public BigDecimal getNet() { return amount.subtract(commission); }
}