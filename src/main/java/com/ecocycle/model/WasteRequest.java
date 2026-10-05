package com.ecocycle.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WasteRequest {

    private int requestId;
    private int userId;
    private int wasteTypeId;
    private String wasteTypeName;     // filled in by queries that join waste_types
    private int companyId;            // 0 until a company accepts
    private String companyName;       // filled in by queries that join companies
    private String userName;          // filled in by queries that join users
    private String userPhone;
    private BigDecimal weightKg;
    private BigDecimal ratePerKg;     // the rate at the time of submission
    private BigDecimal totalAmount;
    private String pickupAddress;
    private String city;
    private String notes;
    private String status;
    private Timestamp createdAt;
    private Timestamp acceptedAt;
    private Timestamp pickedUpAt;

    public WasteRequest() { }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getWasteTypeId() { return wasteTypeId; }
    public void setWasteTypeId(int wasteTypeId) { this.wasteTypeId = wasteTypeId; }

    public String getWasteTypeName() { return wasteTypeName; }
    public void setWasteTypeName(String wasteTypeName) { this.wasteTypeName = wasteTypeName; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public BigDecimal getRatePerKg() { return ratePerKg; }
    public void setRatePerKg(BigDecimal ratePerKg) { this.ratePerKg = ratePerKg; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Timestamp acceptedAt) { this.acceptedAt = acceptedAt; }

    public Timestamp getPickedUpAt() { return pickedUpAt; }
    public void setPickedUpAt(Timestamp pickedUpAt) { this.pickedUpAt = pickedUpAt; }
}