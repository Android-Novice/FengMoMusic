package com.yuqf.fengmomusic.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
//        if (action.equals(Global.RECEIVER_ACTION)) {
//            String value = intent.getStringExtra(Global.ACTION_KEY);
//            switch (value) {
//                case Global.ACTION_CLOSE:
//                    musicPlayer.pause();
////                    MyApplication.getNotificationManager().cancel(Global.NOTIFICATION_ID);
//                    break;
//                case Global.ACTION_NEXT:
//                    musicPlayer.next();
//                    break;
//                case Global.ACTION_PLAY:
//                    musicPlayer.setMusicPlayerState();
//                    break;
//            }
//        }
    }
}
