package com.gip.fsa.service.statistic;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProfilePictureModel {
    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("total")
    private String  total;
    @SerializedName("data")
    private ArrayList<ProfilePictureModel.Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public String getTotal() {
        return total;
    }
    public ArrayList<ProfilePictureModel.Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("id")
        private String id;
        @SerializedName("fullname")
        private String fullname;
        @SerializedName("ingenicoPhoto")
        private String ingenicoPhotoUrl;
        @SerializedName("mmsPhoto")
        private String mmsPhotoUrl;
        @SerializedName("gssPhoto")
        private String gssPhotoUrl;

        public String getId() {
            return id;
        }

        public String getFullname() {
            return fullname;
        }

        public String getIngenicoPhoto() {
            return ingenicoPhotoUrl;
        }

        public String getMmsPhoto() {
            return mmsPhotoUrl;
        }

        public String getGssPhoto() {
            return gssPhotoUrl;
        }
    }
}
