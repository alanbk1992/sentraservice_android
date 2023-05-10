package com.gip.fsa.apps.ams.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubmitReceiveModel {

    @SerializedName("api_key")
    private String api_key;
    @SerializedName("movement_id")
    private String movement_id;
    @SerializedName("receive_by")
    private String receive_by;
    @SerializedName("data")
    @Expose
    private ArrayList<ReceiveUnitModel> data = null;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getMovement_id() {
        return movement_id;
    }

    public void setMovement_id(String movement_id) {
        this.movement_id = movement_id;
    }

    public String getReceive_by() {
        return receive_by;
    }

    public void setReceive_by(String receive_by) {
        this.receive_by = receive_by;
    }

    public ArrayList<ReceiveUnitModel> getData() {
        return data;
    }

    public void setData(ArrayList<ReceiveUnitModel> data) {
        this.data = data;
    }

    public SubmitReceiveModel(String api_key, String movement_id, String receive_by, ArrayList<ReceiveUnitModel> data) {
        super();
        this.api_key = api_key;
        this.movement_id = movement_id;
        this.receive_by = receive_by;
        this.data = data;
    }

}
