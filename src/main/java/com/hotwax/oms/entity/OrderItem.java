package com.hotwax.oms.entity;

public class OrderItem {
    private int orderItemSeqId;
    private int productId;
    private int quantity;
    private String status;

    // Getters and Setters
    public int getOrderItemSeqId() { return orderItemSeqId; }
    public void setOrderItemSeqId(int orderItemSeqId) { this.orderItemSeqId = orderItemSeqId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}