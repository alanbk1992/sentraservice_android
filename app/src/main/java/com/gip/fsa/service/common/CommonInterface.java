package com.gip.fsa.service.common;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CommonInterface {

    @FormUrlEncoded
    @POST("/api/common/location")
    Call<CommonModel> _doLocation(
            @Field("accountId")     String accountId,
            @Field("latitude")      String latitude,
            @Field("longitude")     String longitude,
            @Field("dateTime")      String datetime,
            @Field("signature")     String signature
    );

    @FormUrlEncoded
    @POST("/api/common/version")
    Call<CommonModel> _doVersion(
            @Field("accountId")     String accountId,
            @Field("versionCode")   String versionCode,
            @Field("versionName")   String versionName,
            @Field("dateTime")      String datetime,
            @Field("signature")     String signature
    );

    @FormUrlEncoded
    @POST("/api/common/device")
    Call<CommonModel> _doDevice(
            @Field("accountId")     String accountId,
            @Field("serial")        String serial,
            @Field("models")        String models,
            @Field("id")            String id,
            @Field("manufacture")   String manufacture,
            @Field("brand")         String brand,
            @Field("type")          String type,
            @Field("user")          String user,
            @Field("based")         String based,
            @Field("incremental")   String incremental,
            @Field("sdk")           String sdk,
            @Field("board")         String board,
            @Field("host")          String host,
            @Field("fingerprint")   String fingerprint,
            @Field("versionCode")   String versionCode,
            @Field("dateTime")      String datetime,
            @Field("signature")     String signature
    );

    @FormUrlEncoded
    @POST("/api/common/error")
    Call<CommonModel> doError(
            @Field("__id")          String __id,
            @Field("_class")        String _class,
            @Field("_function")     String _function,
            @Field("_description")  String _description,
            @Field("dateTime")      String datetime,
            @Field("signature")     String signature
    );

    @FormUrlEncoded
    @POST("/api/auth/change-password")
    Call<CommonModel> change_password(
            @Field("accountId")     String accountId,
            @Field("oldPassword")   String oldPassword,
            @Field("newPassword")   String newPassword,
            @Field("dateTime")      String datetime,
            @Field("signature")     String signature
    );

}
