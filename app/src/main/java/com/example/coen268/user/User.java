package com.example.coen268.user;

public class User {
    protected String displayName;
    protected String email;
    protected String password;

    public User() {
        displayName = "";
        email = "";
        password = "";
    }

    public User(String displayName, String email, String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public interface OnCreateAccountListener {
        void createAccount(User user);
    }
}
