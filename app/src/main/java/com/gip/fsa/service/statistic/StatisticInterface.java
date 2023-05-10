package com.gip.fsa.service.statistic;

import com.gip.fsa.apps.shopee.service.ShopeeModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StatisticInterface {

    @GET("/api/jobs/assign-status")
    Call<StatusModel> _goStatus(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/jobs/dashboard")
    Call<StatusModel> total_status(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/auth/profile")
    Call<UserProfileModel> user_profile(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/common/account-logo")
    Call<LogoModel> _goGetLogo(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/common/account-photo")
    Call<ProfilePictureModel> _goGetProfilePicture(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );
}
