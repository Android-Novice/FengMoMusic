package com.yuqf.fengmomusic.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.ActivityMgr;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicPlayer {

    private static MusicPlayer musicPlayer;
    private List<MusicPlayerListener> listenerList;
    private PlayIndexChangedListener changedListener;

    private MediaPlayer mediaPlayer;
    private List<Music> playingMusics;
    private int oldIndex = -1;
    private int playIndex;
    private final String logTag = "MediaPlayer";
    private Music curMusic;
    private boolean isBuffering;

    private AudioManager audioManager;
    private NotificationManager manager;
    private int audioResult = -1;
    private PlayingStatus playingStatus = PlayingStatus.None;

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }
        return musicPlayer;
    }

    private MusicPlayer() {
        listenerList = new ArrayList<>();
        playingMusics = new ArrayList<>();
        audioManager = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        manager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void releasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        MyApplication.getNotificationManager().cancel(Global.NOTIFICATION_ID);
    }

    public Music getCurMusic() {
        return curMusic;
    }

    public boolean isBuffering() {
        return isBuffering;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void addPlayerListener(MusicPlayerListener playerListener) {
        if (!listenerList.contains(playerListener) && playerListener != null)
            listenerList.add(playerListener);
    }

    public void setChangedListener(PlayIndexChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public void setPlayingMusics(List<Music> playingMusics) {
        if (showBufferingToast()) return;
        playIndex = -1;
        this.playingMusics = playingMusics;
    }

    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playingStatus = PlayingStatus.Pause;
            }
            showNotification(curMusic);
        }
    }

    public void replay() {
        if (playingStatus == PlayingStatus.Pause || playingStatus == PlayingStatus.Error) {
            play(playIndex);
        }
    }

    public void next() {
        play(playIndex + 1);
    }

    public void setMusicPlayerState() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            showNotification(curMusic);
            notifyListeners(MusicState.PlayState);
        }
    }

    public void previous() {
        int index = playIndex - 1;
        if (playIndex == 0)
            index = playingMusics.size() - 1;
        play(index);
    }

    public void play(int position) {
        if (audioResult == -1) {
            audioResult = audioManager.requestAudioFocus(new AudioFocusChangedListener(), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (audioResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return;
        if (showBufferingToast()) return;

        if (playIndex != position || playingStatus == PlayingStatus.Error) {
            playingStatus = PlayingStatus.Playing;
            if (position < 0 || (position > playingMusics.size() - 1))
                position = 0;
            oldIndex = playIndex;
            playIndex = position;

            curMusic = playingMusics.get(playIndex);
            if (!curMusic.isLocal()) {
                isBuffering = true;
                if (changedListener != null)
                    changedListener.onPlayingIndexChange(curMusic, playIndex, oldIndex);
                notifyListeners(MusicState.Preparing);
                notifyListeners(MusicState.StartBuffering);
                playWebMusic(curMusic.getId());
            }
        } else {
            playingStatus = PlayingStatus.Playing;
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
        showNotification(curMusic);
    }

    class AudioFocusChangedListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.d(logTag, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK\n");
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(0.1f, .1f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.d(logTag, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT\n");
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(logTag, "onAudioFocusChange: AUDIOFOCUS_GAIN\n");
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(1f, 1f);
                    }
                    if (playingStatus != PlayingStatus.Pause)
                        replay();
                    break;
                default:
                    Log.d(logTag, "onAudioFocusChange: \n" + String.valueOf(focusChange));
                    break;
            }
        }
    }

    private boolean showBufferingToast() {
        if (isBuffering) {
            Toast.makeText(MyApplication.getContext(), R.string.no_switching_when_buffering, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void initMediaPlayer(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                curMusic.setBufferingProgress(percent);
                notifyListeners(MusicState.Buffering);
                if (mp.isPlaying()) {
                    curMusic.setPlayedPosition(mp.getCurrentPosition() / 1000);
                    notifyListeners(MusicState.PlayedDuration);
                }

                Log.d(logTag, "onBufferingUpdate: " + String.valueOf(percent) + "\nplayed duration: " + curMusic.getPlayedPosition());
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(logTag, "onCompletion.... \n");
                if (playingStatus != PlayingStatus.Error) {
                    notifyListeners(MusicState.Completion);
                    next();
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(logTag, "onError: what:" + String.valueOf(what) + "\nExtra:" + String.valueOf(extra));
                notifyListeners(MusicState.Error);
                playingStatus = PlayingStatus.Error;
                return false;
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d(logTag, "onInfo: what:" + String.valueOf(what) + "\nExtra:" + String.valueOf(extra));
                switch (what) {
                    case 703:
                        notifyListeners(MusicState.StartBuffering);
                        break;
                    case 702:
                        notifyListeners(MusicState.EndBuffering);
                        break;
                }
                return false;
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                curMusic.setDuration(mp.getDuration() / 1000);
                Log.d(logTag, "onPrepared: Duration:" + String.valueOf(curMusic.getDuration()) + " \n");
                notifyListeners(MusicState.EndBuffering);
                notifyListeners(MusicState.Prepared);
                mp.start();
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d(logTag, "onSeekComplete.... \n");
            }
        });
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playWebMusic(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Music_File_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService = retrofit.create(RetrofitServices.MusicService.class);
        Call<ResponseBody> call = musicService.getMusicUrl("MUSIC_" + String.valueOf(id), "url", "convert_url", "mp3|aac");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String url = response.body().string();
                        if (!TextUtils.isEmpty(url)) {
                            initMediaPlayer(url);
//                            mediaPlayer.setDataSource(url);
//                            mediaPlayer.prepareAsync();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                notifyListeners(MusicState.Error);
            }
        });

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Music_Cover_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService1 = retrofit1.create(RetrofitServices.MusicService.class);
        Call<ResponseBody> call1 = musicService1.getCoverUrl("rid_pic", "url", 70, id);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String coverUrl = response.body().string();
                        if (!TextUtils.isEmpty(coverUrl)) {
                            MyApplication.getPicasso().load(coverUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Log.d(logTag, "onBitmapLoaded.... \n");
                                    if (bitmap != null) {
                                        curMusic.setCover(bitmap);
                                        notifyListeners(MusicState.CoverLoaded);
                                    }
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Log.d(logTag, "onBitmapFailed.... \n");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    Log.d(logTag, "onPrepareLoad.... \n");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(logTag, "onFailure.... \n");
            }
        });
    }

    private void notifyListeners(MusicState musicState) {
        for (MusicPlayerListener listener : listenerList) {
            if (listener != null) {
                switch (musicState) {
                    case Buffering:
                        listener.onBufferingUpdate(curMusic);
                        break;
                    case Completion:
                        isBuffering = false;
                        listener.onCompletion(curMusic);
                        break;
                    case EndBuffering:
                        isBuffering = false;
                        listener.onEndBuffering();
                        break;
                    case StartBuffering:
                        isBuffering = true;
                        listener.onStartBuffering();
                        break;
                    case CoverLoaded:
                        listener.onMusicCoverLoaded(curMusic);
                        showNotification(curMusic);
                        break;
                    case Error:
                        isBuffering = false;
                        listener.onError();
                        showNotification(curMusic);
                        break;
                    case PlayedDuration:
                        listener.onPlayedDurationChanged(curMusic);
                        break;
                    case Prepared:
                        isBuffering = false;
                        listener.onPrepared(curMusic);
                        break;
                    case Preparing:
                        listener.onPreparing(curMusic);
                        break;
                    case PlayState:
                        listener.onPlayStateChanged();
                        break;
                }
            }
        }
    }

    private void showNotification(Music music) {
        Log.d(logTag, "showNotification\n");
        String packageName = MyApplication.getContext().getPackageName();
        RemoteViews remoteViews = new RemoteViews(packageName, R.layout.custom_notification_layout);
        remoteViews.setTextViewText(R.id.music_name_tv, music.getName());
        remoteViews.setTextViewText(R.id.singer_name_tv, music.getArtist());

        if (music.getCover() != null) {
            Log.d(logTag, "showNotification\n have cover");
            remoteViews.setImageViewBitmap(R.id.music_cover_iv, music.getCover());
        } else {
            remoteViews.setImageViewResource(R.id.music_cover_iv, R.drawable.music_white);
        }

        if (mediaPlayer == null || mediaPlayer.isPlaying() || isBuffering)
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
                .setTicker(music.getName())
                .setSmallIcon(R.drawable.ic_launcher_36)
                .setOngoing(true)
                .build();
        notification.priority = Notification.PRIORITY_HIGH;
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        manager.notify(Global.NOTIFICATION_ID, notification);
    }

    enum MusicState {
        Preparing,
        StartBuffering,
        Buffering,
        PlayState,
        EndBuffering,
        Prepared,
        CoverLoaded,
        PlayedDuration,
        Completion,
        Error,
    }

    enum PlayingStatus {
        None,
        Playing,
        Pause,
        Error,
    }
}
