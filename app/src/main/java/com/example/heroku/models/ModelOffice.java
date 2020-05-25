package com.example.heroku.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelOffice implements Parcelable {
    private String name, address, website, phone, openHours, officeId;

    public ModelOffice() {
    }

    protected ModelOffice(Parcel in) {
        name = in.readString();
        address = in.readString();
        website = in.readString();
        phone = in.readString();
        openHours = in.readString();
        officeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(website);
        dest.writeString(phone);
        dest.writeString(openHours);
        dest.writeString(officeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelOffice> CREATOR = new Creator<ModelOffice>() {
        @Override
        public ModelOffice createFromParcel(Parcel in) {
            return new ModelOffice(in);
        }

        @Override
        public ModelOffice[] newArray(int size) {
            return new ModelOffice[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
