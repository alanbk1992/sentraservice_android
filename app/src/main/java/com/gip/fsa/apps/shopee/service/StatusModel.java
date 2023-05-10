package com.gip.fsa.apps.shopee.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StatusModel {

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
        @SerializedName("sum_Month")
        private String sum_Month;
        @SerializedName("sum_Week")
        private String sum_Week;
        @SerializedName("sum_Today")
        private String sum_Today;

        public String getSum_Month() {
            return sum_Month;
        }
        public String getSum_Week() {
            return sum_Week;
        }
        public String getSum_Today() {
            return sum_Today;
        }
    }
}