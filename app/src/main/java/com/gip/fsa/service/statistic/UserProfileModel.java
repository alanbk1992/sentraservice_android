package com.gip.fsa.service.statistic;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserProfileModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("data")
    private ArrayList<UserProfileModel.Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<UserProfileModel.Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("fullname")
        private String fullname;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("positionName")
        private String positionName;
        @SerializedName("warehouseName")
        private String warehouseName;
        @SerializedName("isAudit")
        private String isAudit;

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

        public String getWarehouseName() {
            return warehouseName;
        }

        public String getIsAudit() {
            return isAudit;
        }
    }

}
