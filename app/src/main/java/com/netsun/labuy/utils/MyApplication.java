package com.netsun.labuy.utils;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/2/27.
 */

public class MyApplication extends Application {
    public static final String TAG="Labuy";
    private static Context context;
    public static String token;
    public static boolean isLogon = false;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        LitePalApplication.initialize(context);
//        String[] arr = PublicFunc.getUserAndPassword();
//        if (arr != null) {
//            PublicFunc.login(arr[0], arr[1]);
//            while (PublicFunc.logining) {
//            }
            token = PublicFunc.getToken();
//        }
    }

    public static Context getContext() {
        return context;
    }
}
