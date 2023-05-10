package com.gip.fsa.service.order;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OrderInterface {

    @GET("/api/jobs")
    Call<OrderModel> _goJobs(
            @Query("accountId") String accountId,
            @Query("type")      String type,
            @Query("filter")    String filter,
            @Query("jo")        String jo,
            @Query("keywords")  String keywords,
            @Query("skip")      String skip,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/auth/filter")
    Call<FilterModel> filter_jobs(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

    @GET("/api/auth/job-order")
    Call<FilterJobModel> job_order(
            @Query("accountId") String accountId,
            @Query("dateTime")  String dateTime,
            @Query("signature") String signature
    );

}
