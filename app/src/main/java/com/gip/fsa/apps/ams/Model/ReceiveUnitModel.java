package com.gip.fsa.apps.ams.Model;

public class ReceiveUnitModel {

    String unit_id;
    String merchant;
    String type;
    String desc_1;
    String desc_2;
    String status;
    Integer qr_scan;
    String lat;
    String lng;
    boolean update;

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public Integer getQr_scan() {
        return qr_scan;
    }

    public void setQr_scan(Integer qr_scan) {
        this.qr_scan = qr_scan;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public boolean getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
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

    public ReceiveUnitModel(String unit_id, String merchant, String type, String desc_1, String desc_2, String status, Integer qr_scan, String lat, String lng, boolean update) {
        super();
        this.unit_id = unit_id;
        this.merchant = merchant;
        this.type = type;
        this.desc_1 = desc_1;
        this.desc_2 = desc_2;
        this.status = status;
        this.qr_scan = qr_scan;
        this.lat = lat;
        this.lng = lng;
        this.update = update;
    }

}
