package com.hotwax.oms.entity;

import java.util.List;

public class OrderHeader {
    private int orderId;
    private String orderDate; // Changed to String to fix JSON error
    private int customerId;
    private int shippingContactMechId;
    private int billingContactMechId;
    private List<OrderItem> orderItems;

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; } // Now accepts String

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getShippingContactMechId() { return shippingContactMechId; }
    public void setShippingContactMechId(int shippingContactMechId) { this.shippingContactMechId = shippingContactMechId; }

    public int getBillingContactMechId() { return billingContactMechId; }
    public void setBillingContactMechId(int billingContactMechId) { this.billingContactMechId = billingContactMechId; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}