package com.gip.fsa.apps.ams.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit retrofit;
 //public static final String BASE_URL = "https://mocki.io/v1/df2de366-a8a9-4d73-bd1d-ff8699d2dd40/"; //development
   public static final String BASE_URL = "https://sentralogistik.com/api-sentra/v2.asmx/"; //production
    public static final String api_key = "u8AcMIBDMQg5eJwo0xnTdwc4l0dMPMr8";

    public static Retrofit retrofit(){
        if (retrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
