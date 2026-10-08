package com.ecocycle.model;

import java.math.BigDecimal;

/** Headline numbers for the admin dashboard. */
public class PlatformStats {

    private int users;
    private int blockedUsers;
    private int approvedCompanies;
    private int pendingCompanies;
    private int openRequests;
    private int orders;
    private BigDecimal wasteCollectedKg = BigDecimal.ZERO;
    private BigDecimal totalPayments = BigDecimal.ZERO;
    private BigDecimal productSales = BigDecimal.ZERO;
    private BigDecimal commissionEarned = BigDecimal.ZERO;

    public int getUsers() { return users; }
    public void setUsers(int users) { this.users = users; }

    public int getBlockedUsers() { return blockedUsers; }
    public void setBlockedUsers(int blockedUsers) { this.blockedUsers = blockedUsers; }

    public int getApprovedCompanies() { return approvedCompanies; }
    public void setApprovedCompanies(int approvedCompanies) { this.approvedCompanies = approvedCompanies; }

    public int getPendingCompanies() { return pendingCompanies; }
    public void setPendingCompanies(int pendingCompanies) { this.pendingCompanies = pendingCompanies; }

    public int getOpenRequests() { return openRequests; }
    public void setOpenRequests(int openRequests) { this.openRequests = openRequests; }

    public int getOrders() { return orders; }
    public void setOrders(int orders) { this.orders = orders; }

    public BigDecimal getWasteCollectedKg() { return wasteCollectedKg; }
    public void setWasteCollectedKg(BigDecimal wasteCollectedKg) { this.wasteCollectedKg = wasteCollectedKg; }

    public BigDecimal getTotalPayments() { return totalPayments; }
    public void setTotalPayments(BigDecimal totalPayments) { this.totalPayments = totalPayments; }

    public BigDecimal getProductSales() { return productSales; }
    public void setProductSales(BigDecimal productSales) { this.productSales = productSales; }

    public BigDecimal getCommissionEarned() { return commissionEarned; }
    public void setCommissionEarned(BigDecimal commissionEarned) { this.commissionEarned = commissionEarned; }
}