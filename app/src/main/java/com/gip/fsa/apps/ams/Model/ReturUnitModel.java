package com.gip.fsa.apps.ams.Model;

public class ReturUnitModel {

    String merchant;
    String type;
    String desc_1;
    String desc_2;
    String status;
    String job_type;
    String reason;
    String lat;
    String lng;
    boolean update;

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc_1() {
        return desc_1;
    }

    public void setDesc_1(String desc_1) {
        this.desc_1 = desc_1;
    }

    public String getDesc_2() {
        return desc_2;
    }

    public void setDesc_2(String desc_2) {
        this.desc_2 = desc_2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public ReturUnitModel(String merchant,String type,String desc_1, String desc_2, String status, String job_type, String reason, String lat, String lng, boolean update) {
        this.merchant = merchant;
        this.type = type;
        this.desc_1 = desc_1;
        this.desc_2 = desc_2;
        this.status = status;
        this.job_type = job_type;
        this.reason = reason;
        this.lat = lat;
        this.lng = lng;
        this.update = update;
    }
}
