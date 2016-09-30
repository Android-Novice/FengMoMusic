package com.yuqf.fengmomusic.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.ActivityMgr;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.MusicPlayerListener;
import com.yuqf.fengmomusic.utils.Global;

public class MusicService extends Service implements MusicPlayerListener {

    private Music playingMusic;
    private MusicPlayerBinder binder = new MusicPlayerBinder();

    public MusicService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicPlayer.getInstance().addPlayerListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopForeground(true);
        return super.onUnbind(intent);
    }

    public class MusicPlayerBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onPreparing(Music music) {
        this.playingMusic = music;
        showNotification();
    }

    @Override
    public void onPlayStateChanged() {
        showNotification();
    }

    @Override
    public void onMusicCoverLoaded(Music music) {
        showNotification();
    }

    @Override
    public void onStartBuffering() {

    }

    @Override
    public void onEndBuffering() {

    }

    @Override
    public void onBufferingUpdate(Music music) {

    }

    @Override
    public void onPrepared(Music music) {

    }

    @Override
    public void onPlayedDurationChanged(Music music) {

    }

    @Override
    public void onCompletion(Music music) {

    }

    @Override
    public void onError() {

    }

    private void showNotification() {
        String packageName = MyApplication.getContext().getPackageName();
        RemoteViews remoteViews = new RemoteViews(packageName, R.layout.custom_notification_layout);
        remoteViews.setTextViewText(R.id.music_name_tv, playingMusic.getName());
        remoteViews.setTextViewText(R.id.singer_name_tv, playingMusic.getArtist());

        if (playingMusic.getCover() != null) {
            remoteViews.setImageViewBitmap(R.id.music_cover_iv, playingMusic.getCover());
        } else {
            remoteViews.setImageViewResource(R.id.music_cover_iv, R.drawable.music_white);
        }

        if (MusicPlayer.getInstance().isPlaying())
            remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.pause_white);
        else
            remoteViews.setImageViewResource(R.id.btn_play_pause, R.drawable.play_arrow_white);

        Intent buttonIntent = new Intent(Global.RECEIVER_ACTION);

        buttonIntent.putExtra(Global.ACTION_KEY, Global.ACTION_PLAY);
        PendingIntent ppPendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_play_pause, ppPendingIntent);

        buttonIntent.putExtra(Global.ACTION_KEY, Global.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent);

        buttonIntent.putExtra(Global.ACTION_KEY, Global.ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_play_close, closePendingIntent);

        Intent viewIntent = new Intent(MyApplication.getContext(), ActivityMgr.getActivityMgr().getLastActivity().getClass());
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent startPendingIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.music_cover_iv, startPendingIntent);

        Notification notification = new NotificationCompat.Builder(MyApplication.getContext())
                .setContent(remoteViews)
                .setAutoCancel(false)
                .setTicker(playingMusic.getName())
                .setSmallIcon(R.drawable.ic_launcher_36)
                .setOngoing(true)
                .build();
        notification.priority = Notification.PRIORITY_HIGH;
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(Global.NOTIFICATION_ID, notification);
    }

    class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MusicPlayer musicPlayer = MusicPlayer.getInstance();
            if (action.equals(Global.RECEIVER_ACTION)) {
                String value = intent.getStringExtra(Global.ACTION_KEY);
                switch (value) {
                    case Global.ACTION_CLOSE:
                        musicPlayer.pause();
//                    MyApplication.getNotificationManager().cancel(Global.NOTIFICATION_ID);
                        MusicService.this.stopForeground(true);
                        break;
                    case Global.ACTION_NEXT:
                        musicPlayer.next();
                        break;
                    case Global.ACTION_PLAY:
                        musicPlayer.setMusicPlayerState();
                        break;
                }
            }
        }
    }

}
