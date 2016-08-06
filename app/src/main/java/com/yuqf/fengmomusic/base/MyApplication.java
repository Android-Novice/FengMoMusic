package com.yuqf.fengmomusic.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 2016/8/5.
 */
public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
