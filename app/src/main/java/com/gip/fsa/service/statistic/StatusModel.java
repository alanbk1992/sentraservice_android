package com.gip.fsa.service.statistic;

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
        @SerializedName("total_before")
        private String total_before;
        @SerializedName("progress_before")
        private String progress_before;
        @SerializedName("revisit_before")
        private String revisit_before;

        @SerializedName("total")
        private String total;
        @SerializedName("progress")
        private String progress;
        @SerializedName("revisit")
        private String revisit;

        @SerializedName("sum")
        private String sum;
        @SerializedName("sum_progress")
        private String sum_progress;
        @SerializedName("sum_revisit")
        private String sum_revisit;
        @SerializedName("done")
        private String done;
        @SerializedName("close")
        private String close;

        @SerializedName("incoming")
        private String incoming;


        public String getTotal_before() {
            return total_before;
        }

        public String getProgress_before() {
            return progress_before;
        }

        public String getRevisit_before() {
            return revisit_before;
        }

        public String getTotal() {
            return total;
        }

        public String getProgress() {
            return progress;
        }

        public String getRevisit() {
            return revisit;
        }

        public String getSum() {
            return sum;
        }

        public String getSum_progress() {
            return sum_progress;
        }

        public String getSum_revisit() {
            return sum_revisit;
        }

        public String getDone() {
            return done;
        }

        public String getClose() {
            return close;
        }

        public String getIncoming() {
            return incoming;
        }
    }
}