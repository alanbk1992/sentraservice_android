package com.gip.fsa.apps.shopee.service;

public class CatatanKunjunganModel {

    String name;
    boolean check;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatatanKunjunganModel(String name, boolean check) {
        this.name = name;
        this.check = check;
    }
}
