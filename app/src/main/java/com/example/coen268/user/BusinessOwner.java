package com.example.coen268.user;

public class BusinessOwner extends User {
    protected String businessId;

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
