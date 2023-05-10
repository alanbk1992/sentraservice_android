package com.gip.fsa.apps.ams.Model;

public class ReturReasonModel {

    String type;
    String dependence;
    String reason;

    public String getType() {
        return type;
    }

    public String getDependence() {
        return dependence;
    }

    public String getReason() {
        return reason;
    }

    public ReturReasonModel(String type, String dependence, String reason) {
        this.type = type;
        this.dependence = dependence;
        this.reason = reason;
    }

}
