package com.gip.fsa.apps.ams.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private Info info;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Info getInfo() {
        return info;
    }

}
