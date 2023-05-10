package com.gip.fsa.service.statistic;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LogoModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("total")
    private String  total;
    @SerializedName("data")
    private ArrayList<Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public String getTotal() {
        return total;
    }
    public ArrayList<Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("id")
        private String id;
        @SerializedName("_logo")
        private String _logo;
        @SerializedName("name")
        private String name;
        @SerializedName("logo")
        private String url;

        public String getId() {
            return id;
        }

        public String get_logo() {
            return _logo;
        }

        public String getName() {
            return name.trim().toLowerCase();
        }

        public String getUrl() {
            return url;
        }
    }
}