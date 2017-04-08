package com.netsun.labuy.utils;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import org.litepal.LitePalApplication;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/2/27.
 */

public class MyApplication extends Application {
    public static final String TAG = "Labuy";
    private static Context context;
    public static String token;
    public static boolean isLogon = false;
    public static String mVersionName = "V1.0";

    @Override
    public void onCreate() {
        context = getApplicationContext();
        LitePalApplication.initialize(context);
        token = PublicFunc.getToken();
        Glide.get(this).register(GlideUrl.class, InputStream.class,new OkHttpUrlLoader.Factory(new OkHttpClient()));
    }

    public static Context getContext() {
        return context;
    }
}
