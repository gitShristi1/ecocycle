package com.ecocycle.model;

import java.sql.Timestamp;

public class Feedback {

    private int feedbackId;
    private int productId;
    private String productName;
    private int userId;
    private String userName;
    private int rating;               // 1 to 5
    private String reviewText;
    private Timestamp createdAt;

    public Feedback() { }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Only the first name is shown next to a review, to protect customers' privacy. */
    public String getDisplayName() {
        if (userName == null || userName.trim().isEmpty()) {
            return "Customer";
        }
        return userName.trim().split("\\s+")[0];
    }
}