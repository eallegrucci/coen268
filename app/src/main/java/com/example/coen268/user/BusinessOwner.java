package com.example.coen268.user;

public class BusinessOwner extends User {

    public BusinessOwner(String displayName, String email, String password) {
        super(displayName, email, password);
    }

    public static class BusinessOwnerBuilder {
        private String displayName;
        private String email;
        private String password;

        public BusinessOwnerBuilder() {
            displayName = "";
            email = "";
            password = "";
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

        public BusinessOwner build() {
            return new BusinessOwner(this);
        }

    }

    private BusinessOwner(BusinessOwnerBuilder builder) {
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.password = builder.password;
    }
}
