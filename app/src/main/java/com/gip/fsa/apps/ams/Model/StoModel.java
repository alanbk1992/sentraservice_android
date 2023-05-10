package com.gip.fsa.apps.ams.Model;

public class StoModel {

    String sto_id;
    String sto_name;
    String status;
    String sto_date;
    Integer qty;

    public String getSto_id() {
        return sto_id;
    }

    public String getSto_name() {
        return sto_name;
    }

    public String getStatus() {
        return status;
    }

    public String getSto_date() {
        return sto_date;
    }

    public Integer getQty() {
        return qty;
    }

    public StoModel(String sto_id, String sto_name, String status, String sto_date, Integer qty) {
        super();
        this.sto_id = sto_id;
        this.sto_name = sto_name;
        this.status = status;
        this.sto_date = sto_date;
        this.qty = qty;
    }
}
