package com.example.coen268.user;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    protected String id;
    protected String type;
    protected String displayName;
    protected String email;
    protected String password;

    public User() {
        id = "";
        type = "";
        displayName = "";
        email = "";
        password = "";
    }

    protected User(Parcel in) {
        id = in.readString();
        type = in.readString();
        displayName = in.readString();
        email = in.readString();
        password = in.readString();
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
        out.writeString(id);
        out.writeString(type);
        out.writeString(displayName);
        out.writeString(email);
        out.writeString(password);
    }

    public static class UserBuilder {
        private String id;
        private String type;
        private String displayName;
        private String email;
        private String password;

        public UserBuilder(String type) {
            this.type = type;
        }

        public UserBuilder setId(String id) {
            this.id = id;
            return this;
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

        public User build() {
            return new User(this);
        }

    }

    private User(UserBuilder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.password = builder.password;
    }

    public String getId() { return id; }

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

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return "Account type: " + type +
                " Display name: " + displayName +
                " Email: " + email +
                " Password: " + password;
    }

    public interface OnCreateAccountListener {
        void createAccount(User user);

        void onCredentialsCompleted(User user);
    }
}
