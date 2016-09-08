package com.yuqf.fengmomusic.ui.fragment;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yuqf.customviews.ReflectionImageView;
import com.yuqf.customviews.RoundImageView;
import com.yuqf.customviews.RoundProgressBar;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayingMusicFragment extends Fragment implements Button.OnClickListener {
    private RoundImageView roundImageView;
    private RoundProgressBar roundProgressBar;
    private ReflectionImageView reflectionImageView;
    private View rootView;
    private final String logTag = "PlayingMusicFragment";
    private Bitmap roundImg;
    private int maxVolume;
    private ImageButton btnVolume;
    private SeekBar volumeSeekBar;
    private int curVolume;

    private int musicId;

    public PlayingMusicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(parent);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_playing_music, container, false);

            roundProgressBar = (RoundProgressBar) rootView.findViewById(R.id.round_progress_bar);
            roundProgressBar.setProgress(0);

            roundImageView = (RoundImageView) rootView.findViewById(R.id.round_image_view);
            reflectionImageView = (ReflectionImageView) rootView.findViewById(R.id.reflection_image_view);
            btnVolume = (ImageButton) rootView.findViewById(R.id.volume_btn);
            btnVolume.setOnClickListener(this);
            volumeSeekBar = (SeekBar) rootView.findViewById(R.id.volume_seek_bar);
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                boolean isTouching = false;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d("VolumeTag", "Progress: " + String.valueOf(progress) + "\nfromUser:" + String.valueOf(fromUser));
                    if (fromUser) {
                        curVolume = progress;
                        MyApplication.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        changeVolumeBtnBitmap(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("VolumeTag", "onStartTrackingTouch");
                    isTouching = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isTouching = false;
                    Log.d("VolumeTag", "onStopTrackingTouch");
                }
            });

            maxVolume = MyApplication.getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volumeSeekBar.setMax(maxVolume);
            updateVolume();

            setMusicId(musicId);
        }
        return rootView;
    }

    public void setRoundProgress(int progress) {
        if (roundProgressBar != null) {
            roundProgressBar.setProgress(progress);
        }
    }

    public void updateVolume() {
        curVolume = MyApplication.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("VolumeTag", "maxVolume: " + String.valueOf(maxVolume) + "\ncurVolume: " + String.valueOf(curVolume));
        volumeSeekBar.setProgress(curVolume);
        changeVolumeBtnBitmap(curVolume);
    }

    private void showCover(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d(logTag, "showCover: cover is not null....\n");
            roundImg = null;
            roundImageView.setImageBitmap(bitmap);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1080);
            valueAnimator.setTarget(roundImageView);
            valueAnimator.setDuration(50 * 1000 * 3);
            valueAnimator.setRepeatMode(ValueAnimator.INFINITE);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rotateVale = (float) animation.getAnimatedValue();
                    roundImageView.setRotation(rotateVale);
                    Bitmap roundBmp = roundImageView.getRoundImage();
                    if (roundImg == null || roundBmp != roundImg) {
                        roundImg = roundBmp;
                        reflectionImageView.setImageBitmap(roundImg);
                    }
                    reflectionImageView.setRotation(-rotateVale);
                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.volume_btn) {
            int volume = MyApplication.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
            int nextVolume = 0;
            if (volume == 0) {
                nextVolume = curVolume;
            }
            MyApplication.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, nextVolume, 0);
            volumeSeekBar.setProgress(nextVolume);
            changeVolumeBtnBitmap(nextVolume);
        }
    }

    private void changeVolumeBtnBitmap(int volume) {
        int resId;
        if (volume == 0) {
            resId = R.drawable.ic_volume_off_white_24dp;
        } else if (volume >= maxVolume * 3 / 4) {
            resId = R.drawable.ic_volume_up_white_24dp;
        } else if (volume > maxVolume / 4 && volume < maxVolume * 3 / 4) {
            resId = R.drawable.ic_volume_down_white_24dp;
        } else {
            resId = R.drawable.ic_volume_mute_white_24dp;
        }
        btnVolume.setImageResource(resId);
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
        setRoundProgress(0);
        if (rootView != null) {
            Bitmap bitmap300 = CommonUtils.getMusicCover(musicId, Global.Round_Image_Size);
            if (bitmap300 == null) {
                loadCoverFromWeb(musicId);
            } else {
                showCover(bitmap300);
            }
        }
    }

    private void loadCoverFromWeb(int musicId) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Music_Cover_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService1 = retrofit1.create(RetrofitServices.MusicService.class);

        Call<ResponseBody> call = musicService1.getCoverUrl("rid_pic", "url", Global.Round_Image_Size, musicId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String coverUrl = response.body().string();
                        Log.d(logTag, "300 pic: " + coverUrl);
                        MyApplication.getPicasso().load(coverUrl)
                                .placeholder(R.drawable.ic_audiotrack_white_48dp)
                                .error(R.drawable.ic_audiotrack_white_48dp)
                                .into(target300);
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

    final Target target300 = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(logTag, "onBitmapLoaded.... \n");

            showCover(bitmap);
            CommonUtils.saveMusicCover(bitmap, musicId, Global.Round_Image_Size);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d(logTag, "onBitmapFailed.... \n");
            showCover(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d(logTag, "onPrepareLoad.... \n");
            showCover(placeHolderDrawable);
        }
    };

    private void showCover(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            showCover(bitmap);
        }
    }

}
