package com.yuqf.fengmomusic.media;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;

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

    public static MusicPlayer getInstance() {
            if (musicPlayer == null) {
                musicPlayer = new MusicPlayer();
            }
        return musicPlayer;
    }

    private MusicPlayer() {
        listenerList = new ArrayList<>();
        playingMusics = new ArrayList<>();
    }

    public Music getCurMusic() {
        return curMusic;
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
        playIndex = -1;
        this.playingMusics = playingMusics;
    }

    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
    }

    public void replay() {
        play(playIndex);
    }

    public void next() {
        play(playIndex + 1);
    }

    public void previous() {
        int index = playIndex - 1;
        if (playIndex == 0)
            index = playingMusics.size() - 1;
        play(index);
    }

    public void play(int position) {
        initMediaPlayer();
        if (playIndex != position) {
            if (position < 0 || (position > playingMusics.size() - 1))
                position = 0;
            oldIndex = playIndex;
            playIndex = position;
            mediaPlayer.reset();
//            playedDuration = 0;
//            duration = 0;
//            musicCover = null;
//            bufferPercent = 0;
            curMusic = playingMusics.get(playIndex);
            if (!curMusic.isLocal()) {
                if (changedListener != null)
                    changedListener.onPlayingIndexChange(curMusic, playIndex, oldIndex);
                notifyListeners(PlayState.Preparing);
                notifyListeners(PlayState.StartBuffering);
                playWebMusic(curMusic.getId());
            }
        } else {
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) return;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                curMusic.setBufferingProgress(percent);
                notifyListeners(PlayState.Buffering);
                if (mp.isPlaying()) {
                    curMusic.setPlayedPosition(mp.getCurrentPosition() / 1000);
                    notifyListeners(PlayState.PlayedDuration);
                }

                Log.d(logTag, "onBufferingUpdate: " + String.valueOf(percent) + "\nplayed duration: " + curMusic.getPlayedPosition());
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(logTag, "onCompletion.... \n");
                notifyListeners(PlayState.Completion);
                next();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(logTag, "onError: what:" + String.valueOf(what) + "\nExtra:" + String.valueOf(extra));
                notifyListeners(PlayState.Error);
                return false;
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d(logTag, "onInfo: what:" + String.valueOf(what) + "\nExtra:" + String.valueOf(extra));
                switch (what) {
                    case 703:
                        notifyListeners(PlayState.StartBuffering);
                        break;
                    case 702:
                        notifyListeners(PlayState.EndBuffering);
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
                notifyListeners(PlayState.EndBuffering);
                notifyListeners(PlayState.Prepared);
                mp.start();
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d(logTag, "onSeekComplete.... \n");
            }
        });
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
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepareAsync();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                notifyListeners(PlayState.Error);
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
                                        notifyListeners(PlayState.CoverLoaded);
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

    private void notifyListeners(PlayState playState) {
        for (MusicPlayerListener listener : listenerList) {
            if (listener != null) {
                switch (playState) {
                    case Buffering:
                        listener.onBufferingUpdate(curMusic);
                        break;
                    case Completion:
                        listener.onCompletion(curMusic);
                        break;
                    case EndBuffering:
                        listener.onEndBuffering();
                        break;
                    case StartBuffering:
                        listener.onStartBuffering();
                        break;
                    case CoverLoaded:
                        listener.onMusicCoverLoaded(curMusic);
                        break;
                    case Error:
                        listener.onError();
                        break;
                    case PlayedDuration:
                        listener.onPlayedDurationChanged(curMusic);
                        break;
                    case Prepared:
                        listener.onPrepared(curMusic);
                        break;
                    case Preparing:
                        listener.onPreparing(curMusic);
                        break;
                }
            }
        }
    }

    enum PlayState {
        Preparing,
        StartBuffering,
        Buffering,
        EndBuffering,
        Prepared,
        CoverLoaded,
        PlayedDuration,
        Completion,
        Error,
    }

}
