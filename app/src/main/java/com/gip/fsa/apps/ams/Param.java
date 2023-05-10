package com.gip.fsa.apps.ams;

import android.os.Build;

import com.gip.fsa.BuildConfig;

public class Param {

    public static Integer loading_interval = 500;
    public static Integer loading_time = 10000;
    public static Integer popup_time = 2000;

    public static String applicationId = BuildConfig.APPLICATION_ID;
    public static int versionCode = BuildConfig.VERSION_CODE;
    public static String versionName = BuildConfig.VERSION_NAME;
    public static String versionSDK = Build.VERSION.RELEASE;
    public static String deviceInfo = String.valueOf(versionCode)+"."+versionName+"-"+versionSDK;

}
