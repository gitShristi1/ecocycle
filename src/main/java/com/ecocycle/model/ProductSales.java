package com.ecocycle.model;

import java.math.BigDecimal;

/** Sales of one product. */
public class ProductSales {

    private final String productName;
    private final int units;
    private final BigDecimal revenue;
    private final BigDecimal commission;

    public ProductSales(String productName, int units, BigDecimal revenue, BigDecimal commission) {
        this.productName = productName;
        this.units = units;
        this.revenue = revenue;
        this.commission = commission;
    }

    public String getProductName() { return productName; }
    public int getUnits() { return units; }
    public BigDecimal getRevenue() { return revenue; }
    public BigDecimal getCommission() { return commission; }
    public BigDecimal getNet() { return revenue.subtract(commission); }
}