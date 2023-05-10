package com.gip.fsa.service.order;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FilterModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("data")
    private ArrayList<FilterModel.Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<FilterModel.Datas> getDatas() {
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
