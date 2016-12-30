package com.yuqf.fengmomusic.ui.activity;

import android.os.Bundle;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.ui.widget.MiniMusicPlayerView;

public class SingerListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_list);

        initTopBars();
        hideToolBar();
/*        showToolBar();
        setToolbarHomeAsUp();
        String singerStr = getResources().getString(R.string.local_singer);
        setToolbarTitle(singerStr);*/

        MiniMusicPlayerView musicPlayerView = (MiniMusicPlayerView) findViewById(R.id.media_player);
        MusicPlayer.getInstance().addPlayerListener(musicPlayerView);
    }
}
