package com.example.coen268.user;

public class Customer extends User {

    public Customer(String displayName, String email, String password) {
        super(displayName, email, password);
    }

    public static class CustomerBuilder {
        private String displayName;
        private String email;
        private String password;

        public CustomerBuilder() {
            displayName = "";
            email = "";
            password = "";
        }

        public CustomerBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public CustomerBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }

    }

    private Customer(CustomerBuilder builder) {
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.password = builder.password;
    }
}
