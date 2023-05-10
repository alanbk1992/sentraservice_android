package com.gip.fsa.apps.auth;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AuthModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("data")
    private ArrayList<Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("id")
        private String id;
        @SerializedName("guid")
        private String guid;
        @SerializedName("username")
        private String username;
        @SerializedName("password")
        private String password;
        @SerializedName("fullname")
        private String fullname;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("positionName")
        private String positionName;
        @SerializedName("warehouseCode")
        private String warehouseCode;
        @SerializedName("warehouseName")
        private String warehouseName;

        public String getId() {
            return id;
        }
        public String getGuid() { return guid; }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getFullname() {
            return fullname;
        }
        public String getEmail() {
            return email;
        }
        public String getPhone() {
            return phone;
        }
        public String getPositionName() {
            return positionName;
        }
        public String getWarehouseCode() {
            return warehouseCode;
        }
        public String getWarehouseName() {
            return warehouseName;
        }
    }
}
