package com.ecocycle.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {

    private int paymentId;
    private int requestId;
    private String wasteTypeName;     // filled in by queries that join other tables
    private String companyName;
    private BigDecimal amount;
    private Timestamp paidAt;
    private String userName;

    public Payment() { }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getWasteTypeName() { return wasteTypeName; }
    public void setWasteTypeName(String wasteTypeName) { this.wasteTypeName = wasteTypeName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}