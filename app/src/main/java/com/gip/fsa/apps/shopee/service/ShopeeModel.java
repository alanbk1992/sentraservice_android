package com.gip.fsa.apps.shopee.service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ShopeeModel {

    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String  message;
    @SerializedName("data")
    private ArrayList<Datas> data;

    public String getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public ArrayList<Datas> getDatas() {
        return data;
    }

    public class Datas {
        @SerializedName("id")
        private String id;
        @SerializedName("category")
        private String category;
        @SerializedName("customer")
        private String customer;
        @SerializedName("import_Date")
        private String import_Date;
        @SerializedName("import_Ticket_Receive")
        private String import_Ticket_Receive;
        @SerializedName("import_Bank")
        private String import_Bank;
        @SerializedName("bank")
        private String bank;
        @SerializedName("case")
        private String cases;
        @SerializedName("contract_Number")
        private String contract_Number;
        @SerializedName("ticket_Number")
        private String ticket_Number;
        @SerializedName("spk_Number")
        private String spk_Number;
        @SerializedName("work_Type")
        private String work_Type;
        @SerializedName("tid")
        private String tid;
        @SerializedName("tid_Cimb")
        private String tid_Cimb;
        @SerializedName("mid")
        private String mid;
        @SerializedName("merchant_Name")
        private String merchant_Name;
        @SerializedName("merchant_Address")
        private String merchant_Address;
        @SerializedName("merchant_Address_2")
        private String merchant_Address_2;
        @SerializedName("postal_Code")
        private String postal_Code;
        @SerializedName("city")
        private String city;
        @SerializedName("contact_Person")
        private String contact_Person;
        @SerializedName("pic_Name")
        private String pic_Name;
        @SerializedName("pic_Number")
        private String pic_Number;
        @SerializedName("note")
        private String note;
        @SerializedName("noteBefore")
        private String noteBefore;
        @SerializedName("damage_Type")
        private String damage_Type;
        @SerializedName("init_Code")
        private String init_Code;
        @SerializedName("sla")
        private String sla;
        @SerializedName("sn_Edc")
        private String sn_Edc;
        @SerializedName("sn_Sim")
        private String sn_Sim;

        public String getId() {
            return id;
        }
        public String getCategory() {
            return category;
        }
        public String getCustomer() {
            return customer;
        }
        public String getImport_Date() {
            return import_Date;
        }
        public String getImport_Ticket_Receive() {
            return import_Ticket_Receive;
        }
        public String getImport_Bank() {
            return import_Bank;
        }
        public String getBank() {
            return bank;
        }
        public String getCases() {
            return cases;
        }
        public String getContract_Number() {
            return contract_Number;
        }
        public String getTicket_Number() {
            return ticket_Number;
        }
        public String getSpk_Number() {
            return spk_Number;
        }
        public String getWork_Type() {
            return work_Type;
        }
        public String getTid() {
            return tid;
        }
        public String getTid_Cimb() {
            return tid_Cimb;
        }
        public String getMid() {
            return mid;
        }
        public String getMerchant_Name() {
            return merchant_Name;
        }
        public String getMerchant_Address() {
            return merchant_Address;
        }
        public String getMerchant_Address_2() {
            return merchant_Address_2;
        }
        public String getPostal_Code() {
            return postal_Code;
        }
        public String getCity() {
            return city;
        }
        public String getPic_Name() {
            return pic_Name;
        }
        public String getPic_Number() {
            return pic_Number;
        }
        public String getNote() {
            return note;
        }
        public String getNoteBefore() {
            return noteBefore;
        }
        public String getDamage_Type() {
            return damage_Type;
        }
        public String getInit_Code() {
            return init_Code;
        }
        public String getSla() {
            return sla;
        }
        public String getSn_Edc() {
            return sn_Edc;
        }
        public String getSn_Sim() {
            return sn_Sim;
        }
        public String getContact_Person() {
            return contact_Person;
        }
    }
}