package com.ecocycle.model;

import java.math.BigDecimal;

/** A company's sales totals. */
public class SalesSummary {

    private final int units;
    private final BigDecimal revenue;      // what customers paid for this company's products
    private final BigDecimal commission;   // the platform's share, saved at the time of each sale

    public SalesSummary(int units, BigDecimal revenue, BigDecimal commission) {
        this.units = units;
        this.revenue = revenue;
        this.commission = commission;
    }

    public int getUnits() { return units; }
    public BigDecimal getRevenue() { return revenue; }
    public BigDecimal getCommission() { return commission; }

    /** Revenue minus the platform commission. */
    public BigDecimal getNet() { return revenue.subtract(commission); }
}