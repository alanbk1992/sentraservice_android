package com.gip.fsa.apps.ams.Model;

public class ScanModel {

    int id;
    String movement_id;
    String sn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovement_id() {
        return movement_id;
    }

    public void setMovement_id(String movement_id) {
        this.movement_id = movement_id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public ScanModel(int id, String movement_id, String sn) {
        this.id = id;
        this.movement_id = movement_id;
        this.sn = sn;
    }

}
