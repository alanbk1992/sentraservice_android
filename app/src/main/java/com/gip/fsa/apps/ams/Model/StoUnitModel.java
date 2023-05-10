package com.gip.fsa.apps.ams.Model;

public class StoUnitModel {

    String sn;
    String pn;
    String lat;
    String lng;
    String sto_status;
    String notes;
    boolean update;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSto_status() {
        return sto_status;
    }

    public void setSto_status(String sto_status) {
        this.sto_status = sto_status;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public StoUnitModel(String sn, String pn, String lat, String lng, String sto_status, String notes, boolean update) {
        super();
        this.sn = sn;
        this.pn = pn;
        this.lat = lat;
        this.lng = lng;
        this.sto_status = sto_status;
        this.notes = notes;
        this.update = update;
    }

}
