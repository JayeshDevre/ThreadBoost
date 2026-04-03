package com.cod.asyncmicroservice.domain;

public class DashboardResponse {
    private String creditScore;
    private String orderHistory;
    private String recommendation;

    public DashboardResponse() {}

    public DashboardResponse(String creditScore, String orderHistory, String recommendation) {
        this.creditScore = creditScore;
        this.orderHistory = orderHistory;
        this.recommendation = recommendation;
    }

    public String getCreditScore() { return creditScore; }
    public void setCreditScore(String creditScore) { this.creditScore = creditScore; }
    public String getOrderHistory() { return orderHistory; }
    public void setOrderHistory(String orderHistory) { this.orderHistory = orderHistory; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
