package com.gip.fsa.service.common;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommonModel {

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
        @SerializedName("param_1")
        private String param_1;

        public String getParam_1() {
            return param_1;
        }
    }
}
