package com.gip.fsa.apps.shopee.service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ShopeeInterface {

    @GET("/api/jobs/assign-inprogress")
    Call<ShopeeModel> _goInProgress(
            @Query("accountId") String accountId,
            @Query("skip")      String skip,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/jobs/assign-revisit")
    Call<ShopeeModel> _goRevisit(
            @Query("accountId") String accountId,
            @Query("skip")      String skip,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/jobs/assign-status")
    Call<StatusModel> _goStatus(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/jobs/assign-detail")
    Call<ShopeeModel> _goDetail(
            @Query("accountId") String accountId,
            @Query("jobsId")    String jobsId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/jobs/revisit-detail")
    Call<ShopeeModel> _goRevisitDetail(
            @Query("accountId") String accountId,
            @Query("jobsId")    String jobsId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @Multipart
    @POST("/api/jobs/submit")
    Call<ShopeeModel> _doSubmit(
            @Part("accountId")      RequestBody accountId,
            @Part("jobsId")         RequestBody jobsId,
            @Part("picName")        RequestBody picName,
            @Part("picNumber")      RequestBody picNumber,
            @Part("snEdc")          RequestBody snEdc,
            @Part("snSim")          RequestBody snSim,
            @Part("note")           RequestBody note,
            @Part MultipartBody.Part            photoMerchant,
            @Part MultipartBody.Part            photoEdc,
            @Part MultipartBody.Part            photoFormulir,
            @Part MultipartBody.Part            photoOthers,
            @Part("latitude")       RequestBody latitude,
            @Part("longitude")      RequestBody longitude,
            @Part("status")         RequestBody status,
            @Part("dateTime")       RequestBody datetime,
            @Part("signature")      RequestBody signature
    );

    @Multipart
    @POST("/api/jobs/v2/submit")
    Call<ShopeeModel> submit2(
            @Part("accountId")      RequestBody accountId,
            @Part("jobsId")         RequestBody jobsId,
            @Part("picName")        RequestBody picName,
            @Part("picNumber")      RequestBody picNumber,
            @Part("snEdc")          RequestBody snEdc,
            @Part("snSim")          RequestBody snSim,
            @Part("note")           RequestBody note,
            @Part MultipartBody.Part            photoMerchant,
            @Part MultipartBody.Part            photoEdcBelakang,
            @Part MultipartBody.Part            photoEdcDepan,
            @Part MultipartBody.Part            photoStruk,
            @Part MultipartBody.Part            photoPOSM,
            @Part MultipartBody.Part            photoEdcBankLain,
            @Part MultipartBody.Part            photoFKM,
            @Part MultipartBody.Part            photoBeritaAcara,
            @Part MultipartBody.Part            photoESignature,
            @Part MultipartBody.Part            photoOthers,
            @Part("testTransaksi")              RequestBody testTransaksi,
            @Part("catatanKunjungan")           RequestBody catatanKunjungan,
            @Part("detailRootcause")            RequestBody detailRootcause,
            @Part("faktur_ots")       RequestBody faktur_ots,
            @Part("faktur_tambahan")      RequestBody faktur_tambahan,
            @Part("latitude")       RequestBody latitude,
            @Part("longitude")      RequestBody longitude,
            @Part("status")         RequestBody status,
            @Part("dateTime")       RequestBody datetime,
            @Part("signature")      RequestBody signature
    );

    @Multipart
    @POST("/api/jobs/v3/submit")
    Call<ShopeeModel> submit3(
            @Part("accountId")      RequestBody accountId,
            @Part("jobsId")         RequestBody jobsId,
            @Part("picName")        RequestBody picName,
            @Part("picNumber")      RequestBody picNumber,

            @Part("type_edc")           RequestBody type_edc,
            @Part("snEdc")              RequestBody snEdc,
            @Part("softwareVersion")    RequestBody softwareVersion,
            @Part("kondisi_edc")        RequestBody kondisi_edc,
            @Part("samcard_1")          RequestBody samcard_1,
            @Part("samcard_2")          RequestBody samcard_2,
            @Part("provider_simcard")   RequestBody provider_simcard,
            @Part("snSim")              RequestBody snSim,

            @Part("type_edc_ditarik")           RequestBody type_edc_ditarik,
            @Part("snEdcDitarik")               RequestBody snEdcDitarik,
            @Part("versi_software_ditarik")     RequestBody versi_software_ditarik,
            @Part("kondisi_edc_ditarik")        RequestBody kondisi_edc_ditarik,
            @Part("samcard_1_ditarik")          RequestBody samcard_1_ditarik,
            @Part("samcard_2_ditarik")          RequestBody samcard_2_ditarik,
            @Part("provider_samcard_ditarik")   RequestBody provider_samcard_ditarik,
            @Part("snSimDitarik")               RequestBody snSimDitarik,
            @Part("note")                       RequestBody note,

            @Part MultipartBody.Part            photoMerchant,
            @Part MultipartBody.Part            photoEdcBelakang,
            @Part MultipartBody.Part            photoEdcDepan,
            @Part MultipartBody.Part            photoStruk,
            @Part MultipartBody.Part            photoPOSM,
            @Part MultipartBody.Part            photoEdcBankLain,
            @Part MultipartBody.Part            photoFKM,
            @Part MultipartBody.Part            photoBeritaAcara,
            @Part MultipartBody.Part            bukti_konfirmasi,
            @Part MultipartBody.Part            photoESignature,
            @Part MultipartBody.Part            photoOthers,
            @Part("testTransaksi")              RequestBody testTransaksi,
            @Part("catatanKunjungan")           RequestBody catatanKunjungan,
            @Part("scan_qr")                    RequestBody scan_qr,
            @Part("detailRootcause")            RequestBody detailRootcause,
            @Part("simcard")                    RequestBody simcard,
            @Part("collateral")                 RequestBody collateral,
            @Part("faktur_ots")                 RequestBody faktur_ots,
            @Part("faktur_tambahan")            RequestBody faktur_tambahan,
            @Part("latitude")                   RequestBody latitude,
            @Part("longitude")                  RequestBody longitude,
            @Part("status")                     RequestBody status,
            @Part("reschedule_date")            RequestBody reschedule_date,
            @Part("rest_aging")                 RequestBody rest_aging,
            @Part("dateTime")                   RequestBody datetime,
            @Part("signature")                  RequestBody signature
    );

    @Multipart
    @POST("/api/jobs/v3/submit-msis")
    Call<ShopeeModel> submitCIMB(
            @Part("accountId")      RequestBody accountId,
            @Part("jobsId")         RequestBody jobsId,
            @Part("picName")        RequestBody picName,
            @Part("picNumber")      RequestBody picNumber,

            @Part("type_edc")           RequestBody type_edc,
            @Part("snEdc")              RequestBody snEdc,
            @Part("softwareVersion")    RequestBody softwareVersion,
            @Part("kondisi_edc")        RequestBody kondisi_edc,
            @Part("samcard_1")          RequestBody samcard_1,
            @Part("samcard_2")          RequestBody samcard_2,
            @Part("provider_simcard")   RequestBody provider_simcard,
            @Part("snSim")              RequestBody snSim,

            @Part("type_edc_ditarik")           RequestBody type_edc_ditarik,
            @Part("snEdcDitarik")               RequestBody snEdcDitarik,
            @Part("versi_software_ditarik")     RequestBody versi_software_ditarik,
            @Part("kondisi_edc_ditarik")        RequestBody kondisi_edc_ditarik,
            @Part("samcard_1_ditarik")          RequestBody samcard_1_ditarik,
            @Part("samcard_2_ditarik")          RequestBody samcard_2_ditarik,
            @Part("provider_samcard_ditarik")   RequestBody provider_samcard_ditarik,
            @Part("snSimDitarik")               RequestBody snSimDitarik,
            @Part("note")                       RequestBody note,

            @Part MultipartBody.Part            photoMerchant,
            @Part MultipartBody.Part            photoEdcBelakang,
            @Part MultipartBody.Part            photoEdcDepan,
            @Part MultipartBody.Part            photoStruk,
            @Part MultipartBody.Part            photoPOSM,
            @Part MultipartBody.Part            photoEdcBankLain,
            @Part MultipartBody.Part            photoFKM,
            @Part MultipartBody.Part            photoBeritaAcara,
            @Part MultipartBody.Part            bukti_konfirmasi,
            @Part MultipartBody.Part            photoESignature,
            @Part MultipartBody.Part            photoOthers,
            @Part("testTransaksi")              RequestBody testTransaksi,
            @Part("catatanKunjungan")           RequestBody catatanKunjungan,
            @Part("scan_qr")                    RequestBody scan_qr,
            @Part("detailRootcause")            RequestBody detailRootcause,
            @Part("simcard")                    RequestBody simcard,
            @Part("collateral")                 RequestBody collateral,
            @Part("faktur_ots")                 RequestBody faktur_ots,
            @Part("faktur_tambahan")            RequestBody faktur_tambahan,
            @Part("latitude")                   RequestBody latitude,
            @Part("longitude")                  RequestBody longitude,
            @Part("status")                     RequestBody status,
            @Part("reschedule_date")            RequestBody reschedule_date,
            @Part("rest_aging")                 RequestBody rest_aging,
            @Part("dateTime")                   RequestBody datetime,
            @Part("signature")                  RequestBody signature,

            //CIMB additional data
            @Part("kondisiDetailEdc")           RequestBody kondisiDetailEdc,
            @Part("kelengkapanEdc")             RequestBody kelengkapanEdc,
            @Part("noSimcard")                  RequestBody noSimcard,
            @Part("requestMerchant")            RequestBody requestMerchant,
            @Part("reasonCode")                 RequestBody reasonCode,
            @Part("totalKasir")                 RequestBody totalKasir,
            @Part("namaKasir")                  RequestBody namaKasir,
            @Part("remarkSosialisasiEdc")       RequestBody remarkSosialisasiEdc,
            @Part("materiTraining")             RequestBody materiTraining,
            @Part("remarkEdc")                  RequestBody remarkEdc,
            @Part("subRootCause")               RequestBody subRootCause,
            @Part("hasilCallTms")               RequestBody hasilCallTms,

            @Part("mid3Bulan")               RequestBody mid3Bulan,
            @Part("mid6Bulan")               RequestBody mid6Bulan,
            @Part("mid12Bulan")              RequestBody mid12Bulan,
            @Part("mid18Bulan")              RequestBody mid18Bulan,
            @Part("mid24Bulan")              RequestBody mid24Bulan,
            @Part("mid36Bulan")              RequestBody mid36Bulan,
            @Part("qr")                      RequestBody qr,

            @Part("tid3Bulan")               RequestBody tid3Bulan,
            @Part("tid6Bulan")               RequestBody tid6Bulan,
            @Part("tid9Bulan")               RequestBody tid9Bulan,
            @Part("tid12Bulan")              RequestBody tid12Bulan,
            @Part("tid24Bulan")              RequestBody tid24Bulan,
            @Part("tid36Bulan")              RequestBody tid36Bulan,
            @Part("rekpon")                  RequestBody rekpon
            );
}
