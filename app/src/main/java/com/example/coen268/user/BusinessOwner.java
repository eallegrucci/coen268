package com.example.coen268.user;

import android.os.Parcel;
import android.os.Parcelable;

public class BusinessOwner extends User implements Parcelable {
    protected String businessId;

    protected BusinessOwner(Parcel in) {
        id = in.readString();
        type = in.readString();
        displayName = in.readString();
        email = in.readString();
        password = in.readString();
        businessId = in.readString();
    }

    public static final Creator<BusinessOwner> CREATOR = new Creator<BusinessOwner>() {
        @Override
        public BusinessOwner createFromParcel(Parcel in) {
            return new BusinessOwner(in);
        }

        @Override
        public BusinessOwner[] newArray(int size) {
            return new BusinessOwner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(type);
        out.writeString(displayName);
        out.writeString(email);
        out.writeString(password);
        out.writeString(businessId);
    }

    public static class BusinessOwnerBuilder {
        private String id;
        private String type;
        private String displayName;
        private String email;
        private String password;
        private String businessId;

        public BusinessOwnerBuilder(String type) {
            this.type = type;
        }

        public BusinessOwnerBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public BusinessOwnerBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public BusinessOwnerBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public BusinessOwnerBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public BusinessOwnerBuilder setBusinessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        public BusinessOwner build() {
            return new BusinessOwner(this);
        }

    }

    private BusinessOwner(BusinessOwnerBuilder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.password = builder.password;
        this.businessId = builder.businessId;
    }

    public String getBusinessId() {
        return businessId;
    }
}
