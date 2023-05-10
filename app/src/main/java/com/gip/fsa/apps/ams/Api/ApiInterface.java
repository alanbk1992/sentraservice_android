package com.gip.fsa.apps.ams.Api;

import com.gip.fsa.service.order.FilterModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("apps_version")
    Call<Result> postVersion(
            @Field("api_key") String api_key,
            @Field("application_id") String application_id
    );

    @FormUrlEncoded
    @POST("login")
    Call<Result> postLogin(
            @Field("api_key") String api_key,
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("user_info")
    Call<Result> postUserInfo(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("change_password")
    Call<Result> postChangePassword(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password
    );

    //INTRANSIT
    //==================================================
    @FormUrlEncoded
    @POST("receive_order")
    Call<ResponseBody> postReceiveOrder(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("receive_unit")
    Call<ResponseBody> postReceiveUnit(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("submit_receive_unit")
    Call<ResponseBody> postSubmitReceiveUnit(
            @Field("api_key") String api_key,
            @Field("receive_by") String receive_by,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("submit_receive_unit_v22")
    Call<ResponseBody> postSubmitReceiveUnit_v22(
            @Field("api_key") String api_key,
            @Field("receive_by") String receive_by,
            @Field("account_id") String account_id,
            @Field("datetime") String datetime,
            @Field("signature") String signature,
            @Field("data") String data
    );

    //INSTALLATION
    //==================================================
    @FormUrlEncoded
    @POST("installation_order")
    Call<ResponseBody> postInstallationOrder(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("installation_unit")
    Call<ResponseBody> postInstallationUnit(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("check_tid")
    Call<ResponseBody> postCheckTID(
            @Field("api_key") String api_key,
            @Field("tid") String tid
    );

    @FormUrlEncoded
    @POST("submit_installation_unit")
    Call<ResponseBody> postSubmitInstallationUnit(
            @Field("api_key") String api_key,
            @Field("receive_by") String receive_by,
            @Field("data") String data
    );

    //RETUR
    //==================================================
    @FormUrlEncoded
    @POST("retur_order")
    Call<ResponseBody> postReturOrder(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("list_simcard")
    Call<ResponseBody> postListSimCard(
            @Field("api_key") String api_key,
            @Field("warehouse_id") String user_id
    );

    @FormUrlEncoded
    @POST("list_customer")
    Call<ResponseBody> postListCustomer(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("list_sn")
    Call<ResponseBody> postListSN(
            @Field("api_key") String api_key,
            @Field("warehouse_id") String warehouse_id,
            @Field("customer") String customer
    );

    @FormUrlEncoded
    @POST("retur_unit")
    Call<ResponseBody> postReturUnit(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("movement_id") String movement_id
    );

    @FormUrlEncoded
    @POST("retur_check_unit")
    Call<ResponseBody> postReturCheckUnit(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("type") String type,
            @Field("sn") String sn
    );

    @FormUrlEncoded
    @POST("list_retur_reason")
    Call<ResponseBody> postReturReason(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("name") String name,
            @Field("type") String type,
            @Field("dependence") String dependence
    );

    @FormUrlEncoded
    @POST("submit_retur_unit")
    Call<ResponseBody> postSubmitReturUnit(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("movement_id") String movement_id,
            @Field("order_no") String order_no,
            @Field("qty") Integer qty,
            @Field("receiver_id") String receiver_id,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("list_item_type")
    Call<ResponseBody> postListItemType(
            @Field("api_key") String api_key,
            @Field("user_id") String user_id,
            @Field("item") String item
    );

    //FMS
    //=================
    @GET("/api/auth/filter")
    Call<ResponseBody> filter_jobs(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

}
