package com.ecocycle.model;

import java.math.BigDecimal;

public class WasteType {

    private int wasteTypeId;
    private String typeName;
    private BigDecimal ratePerKg;
    private boolean active;

    public WasteType() { }

    public int getWasteTypeId() { return wasteTypeId; }
    public void setWasteTypeId(int wasteTypeId) { this.wasteTypeId = wasteTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public BigDecimal getRatePerKg() { return ratePerKg; }
    public void setRatePerKg(BigDecimal ratePerKg) { this.ratePerKg = ratePerKg; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}