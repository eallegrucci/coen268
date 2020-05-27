package com.example.coen268.user;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    protected String type;
    protected String displayName;
    protected String email;
    protected String password;
    protected String streetAddress;
    protected String city;
    protected String zip;

    public User() {
        type = "";
        displayName = "";
        email = "";
        password = "";
        streetAddress = "";
        city = "";
        zip = "";
    }

    protected User(Parcel in) {
        type = in.readString();
        displayName = in.readString();
        email = in.readString();
        password = in.readString();
        streetAddress = in.readString();
        city = in.readString();
        zip = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(type);
        out.writeString(displayName);
        out.writeString(email);
        out.writeString(password);
        out.writeString(password);
        out.writeString(streetAddress);
        out.writeString(city);
        out.writeString(zip);
    }

    public static class UserBuilder {
        private String type;
        private String displayName;
        private String email;
        private String password;
        private String streetAddress;
        private String city;
        private String zip;

        public UserBuilder(String type) {
            this.type = type;
        }

        public UserBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public UserBuilder setCity(String city) {
            this.city = city;
            return this;
        }

        public UserBuilder setZip(String zip) {
            this.zip = zip;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

    private User(UserBuilder builder) {
        this.type = builder.type;
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.password = builder.password;
        this.streetAddress = builder.streetAddress;
        this.city = builder.city;
        this.zip = builder.zip;
    }

    public String getAccountType() { return type; }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStreetAddress() { return streetAddress; }

    public String getCity() { return city; }

    public String getZip() { return zip; }

    public String toString() {
        return "Account type: " + type +
                " Display name: " + displayName +
                " Email: " + email +
                " Password: " + password +
                " Street address: " + streetAddress +
                " City: " + city +
                " ZIP: " + zip;
    }

    public interface OnCreateAccountListener {
        void createAccount(User user);

        void onCredentialsCompleted(User user);
    }
}
