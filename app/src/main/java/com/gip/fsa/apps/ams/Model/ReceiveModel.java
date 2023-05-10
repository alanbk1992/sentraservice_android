package com.gip.fsa.apps.ams.Model;

public class ReceiveModel {

    String type;
    String total_qty;
    String total_received;
    String order_status;

    public String getType() {
        return type;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public String getTotal_received() {
        return total_received;
    }

    public String getOrder_status() {
        return order_status;
    }

    public ReceiveModel(String type, String total_qty, String total_received, String order_status) {
        super();
        this.type = type;
        this.total_qty = total_qty;
        this.total_received = total_received;
        this.order_status = order_status;
    }
}
