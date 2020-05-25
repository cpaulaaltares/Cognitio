package com.example.heroku.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PsychologyCategory implements Parcelable {
    private String name, username, password , categoryID;
    private Long rating;

    public PsychologyCategory() {
    }

    protected PsychologyCategory(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        categoryID = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
        }
    }

    public static final Creator<PsychologyCategory> CREATOR = new Creator<PsychologyCategory>() {
        @Override
        public PsychologyCategory createFromParcel(Parcel in) {
            return new PsychologyCategory(in);
        }

        @Override
        public PsychologyCategory[] newArray(int size) {
            return new PsychologyCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(categoryID);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(rating);
        }
    }
}
