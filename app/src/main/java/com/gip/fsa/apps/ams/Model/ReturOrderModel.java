package com.gip.fsa.apps.ams.Model;

public class ReturOrderModel {

    String movement_id;
    String order_no;
    String retur_date;
    int qty;
    String warehouse_id;
    String warehouse_name;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMovement_id() {
        return movement_id;
    }

    public void setMovement_id(String movement_id) {
        this.movement_id = movement_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getRetur_date() {
        return retur_date;
    }

    public void setRetur_date(String retur_date) {
        this.retur_date = retur_date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(String warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getWarehouse_name() {
        return warehouse_name;
    }

    public void setWarehouse_name(String warehouse_name) {
        this.warehouse_name = warehouse_name;
    }

    public ReturOrderModel(String movement_id, String order_no, String retur_date, int qty, String warehouse_id, String warehouse_name, String status) {
        this.movement_id = movement_id;
        this.order_no = order_no;
        this.retur_date = retur_date;
        this.qty = qty;
        this.warehouse_id = warehouse_id;
        this.warehouse_name = warehouse_name;
        this.status = status;
    }
}
