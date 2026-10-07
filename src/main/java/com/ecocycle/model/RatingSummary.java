package com.ecocycle.model;

import java.math.BigDecimal;

public class RatingSummary {

    private final int count;
    private final BigDecimal average;     // one decimal place, 0 when there are no reviews

    public RatingSummary(int count, BigDecimal average) {
        this.count = count;
        this.average = average;
    }

    public int getCount() { return count; }
    public BigDecimal getAverage() { return average; }
}