package com.gip.fsa.apps.shopee.service;

import com.orm.SugarRecord;

public class OfflineOrder extends SugarRecord<OfflineOrder> {
    public String accountId, jobsId, picName, picNumber, snEdc, snEdcDitarik, softwareVersion, snSim, snSimDitarik, note;
    public String pathMerchantPhoto, pathEdcBackPhoto, pathEdcFrontPhoto, pathReceiptPhoto, pathPosmPhoto, pathOtherEdcPhoto, pathFkmPhoto, pathNotePhoto, pathConfirmPhoto, pathESignature, pathOthers;
    public String transactionTest, visitNote, scanQR, detailRootCause, simCard, collateral, fakturOts, fakturTambahan, latitude, longitude, status, rescheduleDate, restAging, dateTime, signature;
    public String orderCategory, orderCustomer, orderDate, orderTicket, orderMerchantName, orderMerchantAddress, orderMerchantAddress2, orderZip, orderCity, orderContactPerson, orderSpk, orderTid, orderTidCimb, orderMid, orderNote, orderSla;

    public OfflineOrder(){
    }

    public OfflineOrder(String accountId, String jobsId, String picName, String picNumber, String snEdc, String snEdcDitarik, String softwareVersion, String snSim, String snSimDitarik, String note,
                        String pathMerchantPhoto, String pathEdcBackPhoto, String pathEdcFrontPhoto, String pathReceiptPhoto, String pathPosmPhoto, String pathOtherEdcPhoto, String pathFkmPhoto, String pathNotePhoto, String pathConfirmPhoto, String pathESignature, String pathOthers,
                        String transactionTest, String visitNote, String scanQR, String detailRootCause, String simCard, String collateral, String fakturOts, String fakturTambahan, String latitude, String longitude, String status, String rescheduleDate, String restAging, String dateTime, String signature,
                        String orderCategory, String orderCustomer, String orderDate, String orderTicket, String orderMerchantName, String orderMerchantAddress, String orderMerchantAddress2, String orderZip, String orderCity, String orderContactPerson, String orderSpk, String orderTid, String orderTidCimb, String orderMid, String orderNote, String orderSla){
        this.accountId = accountId;
        this.jobsId = jobsId;
        this.picName = picName;
        this.picNumber = picNumber;
        this.snEdc = snEdc;
        this.snEdcDitarik = snEdcDitarik;
        this.softwareVersion = softwareVersion;
        this.snSim = snSim;
        this.snSimDitarik = snSimDitarik;
        this.note = note;

        this.pathMerchantPhoto = pathMerchantPhoto;
        this.pathEdcBackPhoto = pathEdcBackPhoto;
        this.pathEdcFrontPhoto = pathEdcFrontPhoto;
        this.pathReceiptPhoto = pathReceiptPhoto;
        this.pathPosmPhoto = pathPosmPhoto;
        this.pathOtherEdcPhoto = pathOtherEdcPhoto;
        this.pathFkmPhoto = pathFkmPhoto;
        this.pathNotePhoto = pathNotePhoto;
        this.pathConfirmPhoto = pathConfirmPhoto;
        this.pathESignature = pathESignature;
        this.pathOthers = pathOthers;

        this.transactionTest = transactionTest;
        this.visitNote = visitNote;
        this.scanQR = scanQR;
        this.detailRootCause = detailRootCause;
        this.simCard = simCard;
        this.collateral = collateral;
        this.fakturOts = fakturOts;
        this.fakturTambahan = fakturTambahan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.rescheduleDate = rescheduleDate;
        this.restAging = restAging;
        this.dateTime = dateTime;
        this.signature = signature;

        this.orderCategory = orderCategory;
        this.orderCustomer = orderCustomer;
        this.orderDate = orderDate;
        this.orderTicket = orderTicket;
        this.orderMerchantName = orderMerchantName;
        this.orderMerchantAddress = orderMerchantAddress;
        this.orderMerchantAddress2 = orderMerchantAddress2;
        this.orderZip = orderZip;
        this.orderCity = orderCity;
        this.orderContactPerson = orderContactPerson;
        this.orderSpk = orderSpk;
        this.orderTid = orderTid;
        this.orderTidCimb = orderTidCimb;
        this.orderMid = orderMid;
        this.orderNote = orderNote;
        this.orderSla = orderSla;
    }
}
