package com.example.heroku.models;

import com.google.firebase.Timestamp;

public class BookingInformation {
    private String customerName, customerPhone, time, psychologistId, psychologistName, officeId, officeName, officeAddress;
    private Long slot;
    private Timestamp timestamp;
    private  boolean done;

    public BookingInformation() {
    }

    public BookingInformation(String customerName, String customerPhone, String time, String psychologistId, String psychologistName, String officeId, String officeName, String officeAddress, Long slot) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.psychologistId = psychologistId;
        this.psychologistName = psychologistName;
        this.officeId = officeId;
        this.officeName = officeName;
        this.officeAddress = officeAddress;
        this.slot = slot;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPsychologistId() {
        return psychologistId;
    }

    public void setPsychologistId(String psychologistId) {
        this.psychologistId = psychologistId;
    }

    public String getPsychologistName() {
        return psychologistName;
    }

    public void setPsychologistName(String psychologistName) {
        this.psychologistName = psychologistName;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
