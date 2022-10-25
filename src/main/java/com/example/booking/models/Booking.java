package com.example.booking.models;

import org.json.JSONObject;

public class Booking {
    
    private String customerName;
    private String email;
    private String phone;
    private String date;
    private int tableSize;
    private String slot;

    public Booking(String customerName, String email, String phone, String date, int tableSize, String slot) {
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.date = date;
        this.tableSize = tableSize;
        this.slot = slot;
    }

    public JSONObject toJSON() {
        return new JSONObject()
            .put("customerName", customerName)
            .put("email", email)
            .put("phone", phone)
            .put("date", date)
            .put("tableSize", tableSize)
            .put("slot", slot);
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public int getTableSize() {
        return tableSize;
    }

    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }
}
