package com.yuqf.fengmomusic.base;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;

import com.squareup.picasso.Picasso;
import com.yuqf.fengmomusic.media.MusicService;

public class MyApplication extends Application {
    private static Context sContext;
    private static Picasso picasso;
    private static NotificationManager notificationManager;
    private static AudioManager audioManager;
    private static MusicService musicService;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Picasso.Builder builder = new Picasso.Builder(sContext);
        picasso = builder.build();
        notificationManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager = (AudioManager) sContext.getSystemService(AUDIO_SERVICE);

        ///**这个放在这里，在程序被强制干掉以后，通知还是能够不消失**/
//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent, connection, BIND_AUTO_CREATE);
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

    public static MusicService getMusicService() {
        return musicService;
    }

    public static void setMusicService(MusicService service) {
        musicService = service;
    }

//    ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            musicService = ((MusicService.MusicPlayerBinder) service).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicService = null;
//        }
//    };
}
