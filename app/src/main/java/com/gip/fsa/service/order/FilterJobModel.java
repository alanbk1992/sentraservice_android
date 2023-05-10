package com.gip.fsa.service.order;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FilterJobModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("data")
    private ArrayList<FilterJobModel.Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<FilterJobModel.Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}
