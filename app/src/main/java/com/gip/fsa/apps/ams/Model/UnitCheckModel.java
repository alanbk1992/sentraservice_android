package com.gip.fsa.apps.ams.Model;

public class UnitCheckModel {

    String sn;
    String lat;
    String lng;
    String check_status;

    public UnitCheckModel(String sn, String lat, String lng, String check_status) {
        super();
        this.sn = sn;
        this.lat = lat;
        this.lng = lng;
        this.check_status = check_status;
    }
}
