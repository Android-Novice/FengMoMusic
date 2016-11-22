package com.yuqf.fengmomusic.media;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.MusicPlayerListener;
import com.yuqf.fengmomusic.interfaces.PlayIndexChangedListener;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.FileUtils;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.File;
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
    private List<PlayIndexChangedListener> changedListenerList;

    private MediaPlayer mediaPlayer;
    private List<Music> playingMusics;
    private int oldIndex = -1;
    private int playIndex;
    private final String logTag = "MediaPlayer";
    private Music curMusic;
    private boolean isBuffering;

    private AudioManager audioManager;
    private int audioResult = -1;
    private PlayingStatus playingStatus = PlayingStatus.None;
    private final int MSG_WHAT = 110;
    private final int SMALL_COVER_SIZE = 70;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_WHAT) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    curMusic.setPlayedPosition(mediaPlayer.getCurrentPosition() / 1000);
                    notifyListeners(MusicState.PlayedDuration);
                }
                handler.sendEmptyMessageDelayed(MSG_WHAT, 500);
            }
            return false;
        }
    });

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) {
            musicPlayer = new MusicPlayer();
        }
        return musicPlayer;
    }

    private MusicPlayer() {
        listenerList = new ArrayList<>();
        changedListenerList = new ArrayList<>();
        playingMusics = new ArrayList<>();
        audioManager = MyApplication.getAudioManager();
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

    public void addChangedListener(PlayIndexChangedListener changedListener) {
        if (changedListener != null && !changedListenerList.contains(changedListener))
            changedListenerList.add(changedListener);
    }

    public void setPlayingMusics(List<Music> playingMusics) {
        if (showBufferingToast()) return;
        playIndex = -1;
        this.playingMusics = playingMusics;
    }

    public void setPlayingMusic(Music music) {
        if (showBufferingToast()) return;
        playIndex = -1;
        if (this.playingMusics == null)
            playingMusics = new ArrayList<>();
        playingMusics.clear();
        this.playingMusics.add(music);
    }

    public List<Music> getPlayingMusics() {
        return playingMusics;
    }

    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playingStatus = PlayingStatus.Pause;
            }
            notifyListeners(MusicState.PlayState);
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
                for (PlayIndexChangedListener changedListener : changedListenerList) {
                    if (changedListener != null)
                        changedListener.onPlayingIndexChange(curMusic, playIndex, oldIndex);
                }
                notifyListeners(MusicState.Preparing);
                notifyListeners(MusicState.StartBuffering);

                String musicPath = FileUtils.getMusicPath(curMusic.getName(), curMusic.getArtist());
                File file = new File(musicPath);
                if (!file.exists()) {
                    playWebMusic(curMusic.getId());
                } else {
                    initMediaPlayer(musicPath);
                    loadMusicCover(curMusic.getId());
                }
            } else {

            }
        } else {
            playingStatus = PlayingStatus.Playing;
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
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
            CommonUtils.showToast(R.string.no_switching_when_buffering, true);
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
//                if (mp.isPlaying()) {
//                    curMusic.setPlayedPosition(mp.getCurrentPosition() / 1000);
//                    notifyListeners(MusicState.PlayedDuration);
//                }

//                Log.d(logTag, "onBufferingUpdate: " + String.valueOf(percent) + "\nplayed duration: " + curMusic.getPlayedPosition());
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
                handler.sendEmptyMessageDelayed(MSG_WHAT, 500);
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
                .baseUrl(UrlHelper.Music_File_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService = retrofit.create(RetrofitServices.MusicService.class);
        Call<ResponseBody> call = musicService.getMusicUrl("MUSIC_" + String.valueOf(id), "url", "convert_url", "aac|mp3");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String url = response.body().string();
                        if (!TextUtils.isEmpty(url)) {
                            initMediaPlayer(url);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                notifyListeners(MusicState.Error);
            }
        });

        loadMusicCover(id);
    }

    private void loadMusicCover(int id) {

        Bitmap bitmap = FileUtils.getMusicCover(id, SMALL_COVER_SIZE);
        if (bitmap != null) {
            Log.d(logTag, "=====cover70=====has saved");
            curMusic.setCover(bitmap);
            notifyListeners(MusicState.CoverLoaded);
            return;
        }

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(UrlHelper.Music_Cover_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService1 = retrofit1.create(RetrofitServices.MusicService.class);
        Call<ResponseBody> call1 = musicService1.getCoverUrl("rid_pic", "url", SMALL_COVER_SIZE, id);
        Log.d(logTag, String.format("http://artistpicserver.kuwo.cn/pic.web?type=rid_pic&pictype=url&size=70&rid=%d", id));

        final int musicId = id;
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String coverUrl = response.body().string();
                        Log.d(logTag, "=====cover70=====: " + coverUrl);
                        if (!TextUtils.isEmpty(coverUrl)) {
                            MyApplication.getPicasso().load(coverUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Log.d(logTag, "onBitmapLoaded.... \n");
                                    if (bitmap != null) {
                                        curMusic.setCover(bitmap);
                                        notifyListeners(MusicState.CoverLoaded);
                                        FileUtils.saveMusicCover(bitmap, musicId, SMALL_COVER_SIZE);
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
                        break;
                    case Error:
                        isBuffering = false;
                        listener.onError();
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
