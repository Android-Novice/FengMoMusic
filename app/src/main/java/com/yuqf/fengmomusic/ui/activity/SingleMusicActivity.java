package com.yuqf.fengmomusic.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.media.MusicPlayerListener;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.ui.fragment.MusicLyricFragment;
import com.yuqf.fengmomusic.ui.fragment.PlayingMusicFragment;
import com.yuqf.fengmomusic.ui.fragment.PlaylistFragment;
import com.yuqf.fengmomusic.ui.fragment.SingerInfoFragment;
import com.yuqf.fengmomusic.utils.Blur;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingleMusicActivity extends BaseActivity implements MusicPlayerListener, Button.OnClickListener {

    private ImageView[] viewpagerPots;
    private PlayingMusicFragment playingMusicFragment;
    private PlaylistFragment playlistFragment;
    private MusicLyricFragment musicLyricFragment;
    private SingerInfoFragment singerInfoFragment;
    private Fragment[] fragments;
    private ViewPager viewPager;
    private TextView musicTV;
    private TextView singerTV;
    private TextView playedTV;
    private TextView totalTV;
    private SeekBar seekBar;
    private ImageButton btnPlayOrder;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private ImageButton btnFavorite;
    private ImageView blurredIV;
    private boolean isPrepared;
    private final String logTag = "SingleMusicActivity";
    private Music curMusic;
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_music);

        initTopBars();
        hideToolBar();
        toolbarContent.setBackgroundColor(Color.TRANSPARENT);

        initViews();
        initViewPager();
        MusicPlayer.getInstance().addPlayerListener(this);
        curMusic = MusicPlayer.getInstance().getCurMusic();
        musicTV.setText(curMusic.getName());
        CommonUtils.setTextMarquee(musicTV);

        singerTV.setText(curMusic.getArtist());
        CommonUtils.setTextMarquee(singerTV);
        notifyMusicId(curMusic.getId());
    }

    private void initViews() {
        viewpagerPots = new ImageView[4];
        viewpagerPots[0] = (ImageView) findViewById(R.id.play_list_pot);
        viewpagerPots[1] = (ImageView) findViewById(R.id.playing_music_pot);
        viewpagerPots[2] = (ImageView) findViewById(R.id.music_lyric_pot);
        viewpagerPots[3] = (ImageView) findViewById(R.id.singer_info_pot);

        fragments = new Fragment[4];
        playlistFragment = new PlaylistFragment();
        playingMusicFragment = new PlayingMusicFragment();
        musicLyricFragment = new MusicLyricFragment();
        singerInfoFragment = new SingerInfoFragment();
        fragments[0] = playlistFragment;
        fragments[1] = playingMusicFragment;
        fragments[2] = musicLyricFragment;
        fragments[3] = singerInfoFragment;

        musicTV = (TextView) findViewById(R.id.music_name_tv);
        singerTV = (TextView) findViewById(R.id.singer_name_tv);
        playedTV = (TextView) findViewById(R.id.played_time_tv);
        totalTV = (TextView) findViewById(R.id.total_time_tv);
        seekBar = (SeekBar) findViewById(R.id.seek_bar_playing);
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
        btnPlayOrder = (ImageButton) findViewById(R.id.btn_play_order);
        btnPrevious = (ImageButton) findViewById(R.id.btn_previous);
        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnFavorite = (ImageButton) findViewById(R.id.btn_favorite);
        blurredIV = (ImageView) findViewById(R.id.blurred_iv);

        btnPlayPause.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentIndex != position && currentIndex >= 0)
                    viewpagerPots[currentIndex].setSelected(false);
                currentIndex = position;
                viewpagerPots[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(1);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override
    public void onPreparing(Music music) {
        playedTV.setText("00:00");
        totalTV.setText("00:00");
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
        playingMusicFragment.setRoundProgress(0);
        musicLyricFragment.nextMusicLyric();
//        musicCoverIV.setImageResource(R.drawable.music_white);
//        playingMusicFragment.showCover(null);
        if (curMusic != music) {
            curMusic = music;
            notifyMusicId(curMusic.getId());
        }

        musicTV.setText(music.getName());
        CommonUtils.setTextMarquee(musicTV);

        singerTV.setText(music.getArtist());
        CommonUtils.setTextMarquee(singerTV);
        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        if (music.isLocal()) {
            onPrepared(music);
        }
    }

    @Override
    public void onBufferingUpdate(Music music) {
        seekBar.setSecondaryProgress(music.getBufferingProgress());
//        playingMusicFragment.setRoundProgress(music.getBufferingProgress());
    }

    @Override
    public void onPrepared(Music music) {
        int mins = music.getDuration() / 60;
        int seconds = music.getDuration() % 60;
        totalTV.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, seconds));
        musicLyricFragment.showLyric(curMusic);
        isPrepared = true;
    }

    @Override
    public void onMusicCoverLoaded(Music music) {
        Bitmap bitmap = music.getCover();
        if (bitmap != null) {
//            musicCoverIV.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPlayedDurationChanged(Music music) {
        if (!isPrepared) {
            onPreparing(music);
            onPrepared(music);
        }
        int current = music.getPlayedPosition();
        int mins = current / 60;
        int seconds = current % 60;
        playedTV.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, seconds));
        int duration = music.getDuration();
        if (duration > 0) {
            int progress = current * 100 / duration;
            seekBar.setProgress(progress);
            playingMusicFragment.setRoundProgress(progress);
        }
        musicLyricFragment.notifyPlayedDuration();
    }

    @Override
    public void onCompletion(Music music) {
        seekBar.setProgress(100);
        playingMusicFragment.setRoundProgress(100);
//        loadingIV.setVisibility(GONE);
    }

    @Override
    public void onError() {
//        loadingIV.setVisibility(GONE);
        btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
    }

    @Override
    public void onEndBuffering() {
//        loadingIV.setVisibility(GONE);
    }

    @Override
    public void onStartBuffering() {
//        loadingIV.setVisibility(VISIBLE);
    }

    @Override
    public void onPlayStateChanged() {
        if (!MusicPlayer.getInstance().isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.play_arrow_white);
        } else {
            btnPlayPause.setImageResource(R.drawable.pause_white);
        }
    }

    private void notifyMusicId(int musicId) {
        playingMusicFragment.setMusicId(musicId);
        Bitmap bitmap500 = CommonUtils.getMusicCover(musicId, Global.Blurred_Image_Size);
        if (bitmap500 == null) {
            Log.d(logTag, "image 500 is null");
            loadCoverFromWeb(musicId, Global.Blurred_Image_Size);
        } else {
            Log.d(logTag, "image 500 is not null");
            blurredIV.setImageBitmap(bitmap500);
        }
    }

    private void loadCoverFromWeb(int musicId, final int imageSize) {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Music_Cover_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService musicService1 = retrofit1.create(RetrofitServices.MusicService.class);

        Call<ResponseBody> call = musicService1.getCoverUrl("rid_pic", "url", imageSize, musicId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String coverUrl = response.body().string();
                        Log.d(logTag, "300 pic: " + coverUrl);
                        loadCoverByUrl(coverUrl);
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

    final Target target500 = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(logTag, "onBitmapLoaded.... \n");
            showCover(bitmap);
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

    private void loadCoverByUrl(String url) {
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso picasso = builder.build();

        picasso.load(url)
                .placeholder(R.drawable.ic_audiotrack_white_48dp)
                .error(R.drawable.ic_audiotrack_white_48dp)
                .into(target500);
    }

    private void showCover(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            showCover(bitmap);
        }
    }

    private void showCover(Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap blurredBmp = Blur.fastblur(MyApplication.getContext(), bitmap, 25);
            blurredIV.setImageBitmap(blurredBmp);
            CommonUtils.saveMusicCover(blurredBmp, curMusic.getId(), Global.Blurred_Image_Size);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                } else {
                    MusicPlayer.getInstance().replay();
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                }
                break;
            case R.id.btn_previous:
                MusicPlayer.getInstance().previous();
                break;
            case R.id.btn_next:
                MusicPlayer.getInstance().next();
                break;
            case R.id.btn_play_order:
                break;
            case R.id.btn_favorite:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("VolumeTag", String.valueOf(keyCode) + "=======");
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                playingMusicFragment.updateVolume();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
