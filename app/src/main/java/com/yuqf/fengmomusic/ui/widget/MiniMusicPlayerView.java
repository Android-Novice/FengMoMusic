package com.yuqf.fengmomusic.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.media.MusicPlayerListener;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.Locale;

public class MiniMusicPlayerView extends FrameLayout implements ImageButton.OnClickListener, MusicPlayerListener {

    private ImageView musicCoverIV;
    private TextView musicNameTV;
    private TextView singerTV;
    private ImageButton prevBtn;
    private ImageButton nextBtn;
    //    private ImageButton pauseBtn; @drawable/pause_white
    private ImageButton playBtn;
    private ProgressBar progressBar;
    private TextView playedTimeTV;
    private TextView totalTimeTV;
    private ImageView loadingIV;

    private boolean isPrepared;

    public MiniMusicPlayerView(Context context) {
        super(context);
        initViews(context);
    }

    public MiniMusicPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public MiniMusicPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater.from(context).inflate(R.layout.bottom_media_player, this, true);

        musicCoverIV = (ImageView) findViewById(R.id.music_cover_iv);
        musicCoverIV.setOnClickListener(this);

        musicNameTV = (TextView) findViewById(R.id.music_name_tv);
        singerTV = (TextView) findViewById(R.id.singer_name_tv);
        musicNameTV.setOnClickListener(this);

        singerTV.setOnClickListener(this);

        loadingIV = (ImageView) findViewById(R.id.loading_iv);

        prevBtn = (ImageButton) findViewById(R.id.btn_previous);
        nextBtn = (ImageButton) findViewById(R.id.btn_next);
        playBtn = (ImageButton) findViewById(R.id.btn_play_pause);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);

        playedTimeTV = (TextView) findViewById(R.id.played_time_tv);
        totalTimeTV = (TextView) findViewById(R.id.total_time_tv);

        progressBar = (ProgressBar) findViewById(R.id.bottom_player_progressbar);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        MusicPlayer.getInstance().addPlayerListener(this);
        Music music = MusicPlayer.getInstance().getCurMusic();
        if (music != null) {
            onPlayedDurationChanged(music);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                MusicPlayer.getInstance().previous();
                break;
            case R.id.btn_next:
                MusicPlayer.getInstance().next();
                break;
            case R.id.btn_play_pause:
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().pause();
                    playBtn.setImageResource(R.drawable.play_arrow_white);
                } else {
                    MusicPlayer.getInstance().replay();
                    playBtn.setImageResource(R.drawable.pause_white);
                }
                break;
            case R.id.music_cover_iv:
            case R.id.music_name_tv:
            case R.id.singer_name_tv:
                break;
        }
    }

    @Override
    public void onPreparing(Music music) {
        if (getVisibility() == GONE)
            setVisibility(VISIBLE);
        playedTimeTV.setVisibility(VISIBLE);
        totalTimeTV.setVisibility(VISIBLE);
        playedTimeTV.setText("00:00");
        totalTimeTV.setText("00:00");
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        musicCoverIV.setImageResource(R.drawable.music_white);

        musicNameTV.setText(music.getName());
        CommonUtils.setTextMarquee(musicNameTV);
//        musicNameTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        musicNameTV.setSingleLine(true);
//        musicNameTV.setSelected(true);
//        musicNameTV.setFocusable(true);
//        musicNameTV.setFocusableInTouchMode(true);

        singerTV.setText(music.getArtist());
        CommonUtils.setTextMarquee(singerTV);
        playBtn.setImageResource(R.drawable.pause_white);
        if (music.isLocal()) {
            onPrepared(music);
        }
    }

    @Override
    public void onBufferingUpdate(Music music) {
        progressBar.setSecondaryProgress(music.getBufferingProgress());
    }

    @Override
    public void onPrepared(Music music) {
        int mins = music.getDuration() / 60;
        int seconds = music.getDuration() % 60;
        totalTimeTV.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, seconds));
        isPrepared = true;
    }

    @Override
    public void onMusicCoverLoaded(Music music) {
        Bitmap bitmap = music.getCover();
        if (bitmap != null) {
            musicCoverIV.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPlayedDurationChanged(Music music) {
        if (!isPrepared) {
            onPreparing(music);
            onPrepared(music);
            onMusicCoverLoaded(music);
        }
        int current = music.getPlayedPosition();
        int mins = current / 60;
        int seconds = current % 60;
        playedTimeTV.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, seconds));
        int duration = music.getDuration();
        if (duration > 0)
            progressBar.setProgress(current * 100 / duration);
    }

    @Override
    public void onCompletion(Music music) {
        progressBar.setProgress(100);
        loadingIV.setVisibility(GONE);
    }

    @Override
    public void onError() {
        loadingIV.setVisibility(GONE);
    }

    @Override
    public void onEndBuffering() {
        loadingIV.setVisibility(GONE);
    }

    @Override
    public void onStartBuffering() {
        loadingIV.setVisibility(VISIBLE);
    }

    @Override
    public void onPlayStateChanged() {
        if (!MusicPlayer.getInstance().isPlaying()) {
            playBtn.setImageResource(R.drawable.play_arrow_white);
        } else {
            playBtn.setImageResource(R.drawable.pause_white);
        }
    }
}
