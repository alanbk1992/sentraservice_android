package com.gip.fsa.apps.ams.Api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info {

    //Apps Version
    @SerializedName("version_code")
    @Expose
    private int version_code;

    @SerializedName("version_name")
    @Expose
    private String version_name;

    public int getVersion_code() {
        return version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    //Login
    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("position_id")
    @Expose
    private String position_id;

    public String getUser_id() {
        return user_id;
    }

    public String getPosition_id() {
        return position_id;
    }

    //User Info
    @SerializedName("fullname")
    @Expose
    private String fullname;

    @SerializedName("position_name")
    @Expose
    private String position_name;

    @SerializedName("warehouse_name")
    @Expose
    private String warehouse_name;

    @SerializedName("warehouse_address")
    @Expose
    private String warehouse_address;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("base64_photo")
    @Expose
    private String base64_photo;

    @SerializedName("intransit_edc")
    @Expose
    private String intransit_edc;

    @SerializedName("intransit_simcard")
    @Expose
    private String intransit_simcard;

    @SerializedName("intransit_peripheral")
    @Expose
    private String intransit_peripheral;

    @SerializedName("intransit_thermal")
    @Expose
    private String intransit_thermal;

    @SerializedName("installation_edc")
    @Expose
    private String installation_edc;

    @SerializedName("installation_simcard")
    @Expose
    private String installation_simcard;

    @SerializedName("installation_peripheral")
    @Expose
    private String installation_peripheral;

    @SerializedName("installation_thermal")
    @Expose
    private String installation_thermal;

    @SerializedName("retur_edc")
    @Expose
    private String retur_edc;

    @SerializedName("retur_simcard")
    @Expose
    private String retur_simcard;

    @SerializedName("retur_peripheral")
    @Expose
    private String retur_peripheral;

    @SerializedName("retur_thermal")
    @Expose
    private String retur_thermal;


    public String getFullname() {
        return fullname;
    }

    public String getPosition_name() {
        return position_name;
    }

    public String getWarehouse_name() {
        return warehouse_name;
    }

    public String getWarehouse_address() {
        return warehouse_address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBase64_photo() {
        return base64_photo;
    }

    public String getIntransit_edc() {
        return intransit_edc;
    }

    public String getIntransit_simcard() {
        return intransit_simcard;
    }

    public String getIntransit_peripheral() {
        return intransit_peripheral;
    }

    public String getIntransit_thermal() {
        return intransit_thermal;
    }

    public String getInstallation_edc() {
        return installation_edc;
    }

    public String getInstallation_simcard() {
        return installation_simcard;
    }

    public String getInstallation_peripheral() {
        return installation_peripheral;
    }

    public String getInstallation_thermal() {
        return installation_thermal;
    }

    public String getRetur_edc() {
        return retur_edc;
    }

    public String getRetur_simcard() {
        return retur_simcard;
    }

    public String getRetur_peripheral() {
        return retur_peripheral;
    }

    public String getRetur_thermal() {
        return retur_thermal;
    }

    //Receive Order
    @SerializedName("movement_id")
    @Expose
    private String movement_id;

    @SerializedName("order_no")
    @Expose
    private String order_no;

    @SerializedName("sender_name")
    @Expose
    private String sender_name;

    public String getMovement_id() {
        return movement_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public String getSender_name() {
        return sender_name;
    }
}
