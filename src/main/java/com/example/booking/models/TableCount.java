package com.example.booking.models;

public class TableCount {
    
    private String date;
    private String slot;
    private int tableSize;
    private int count;

    public TableCount(String date, String slot, int tableSize, int count) {
        this.date = date;
        this.slot = slot;
        this.tableSize = tableSize;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
