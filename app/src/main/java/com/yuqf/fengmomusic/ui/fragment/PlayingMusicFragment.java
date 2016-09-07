package com.yuqf.fengmomusic.ui.fragment;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
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

import com.yuqf.customviews.ReflectionImageView;
import com.yuqf.customviews.RoundImageView;
import com.yuqf.customviews.RoundProgressBar;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;

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

            roundImageView = (RoundImageView) rootView.findViewById(R.id.round_image_view);
            roundProgressBar = (RoundProgressBar) rootView.findViewById(R.id.round_progress_bar);
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
                        changeVoumeBtnBitmap(progress);
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
            showCover(null);

            maxVolume = MyApplication.getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volumeSeekBar.setMax(maxVolume);
            updateVolume();
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
        changeVoumeBtnBitmap(curVolume);
    }

    public void showCover(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d(logTag, "showCover: cover is not null....\n");
            roundImg = null;
            roundImageView.setImageBitmap(bitmap);
//            reflectionImageView.setImageBitmap(bitmap);
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
//                    reflectionImageView.setRotation(rotateVale);
                }
            });
            valueAnimator.start();
        } else {
            Log.d(logTag, "====== showCover: cover is null....\n");
            roundImageView.setImageResource(R.drawable.ic_audiotrack_white_48dp);
            final Bitmap roundImg = roundImageView.getRoundImage();
            if (roundImg != null) {
                reflectionImageView.setImageBitmap(roundImg);
            }
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
            changeVoumeBtnBitmap(nextVolume);
        }
    }

    private void changeVoumeBtnBitmap(int volume) {
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

}
