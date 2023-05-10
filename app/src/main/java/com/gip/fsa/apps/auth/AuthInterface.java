package com.gip.fsa.apps.auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthInterface {

    @FormUrlEncoded
    @POST("/api/auth/login")
    Call<AuthModel> doLogin(
            @Field("username")  String username,
            @Field("password")  String password,
            @Field("dateTime")  String datetime,
            @Field("signature") String signature
    );
}
