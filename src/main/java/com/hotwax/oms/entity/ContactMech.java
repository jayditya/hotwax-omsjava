package com.hotwax.oms.entity;

public class ContactMech {
    private int contactMechId;
    private int customerId;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String phoneNumber;
    private String email;

    // Getters and Setters
    public int getContactMechId() { return contactMechId; }
    public void setContactMechId(int contactMechId) { this.contactMechId = contactMechId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}