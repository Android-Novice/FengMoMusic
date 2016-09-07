package com.yuqf.fengmomusic.base;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;

import com.squareup.picasso.Picasso;

public class MyApplication extends Application {
    private static Context sContext;
    private static Picasso picasso;
    private static NotificationManager notificationManager;
    private static AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Picasso.Builder builder = new Picasso.Builder(sContext);
        picasso = builder.build();
        notificationManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager = (AudioManager) sContext.getSystemService(AUDIO_SERVICE);
    }

    public static Context getContext() {
        return sContext;
    }

    public static Picasso getPicasso() {
        return picasso;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static AudioManager getAudioManager() {
        return audioManager;
    }
}
