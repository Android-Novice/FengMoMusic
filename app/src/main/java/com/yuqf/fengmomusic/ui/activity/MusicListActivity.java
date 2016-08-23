package com.yuqf.fengmomusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.ui.fragment.MusicListFragment;
import com.yuqf.fengmomusic.utils.Global;

public class MusicListActivity extends BaseActivity {

    private MusicListFragment musicListFragment;
    private String contentId;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        musicListFragment = (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_music_list);

        initTopBars();
        showToolBar();
        setToolbarHomeAsUp();

        Intent intent = getIntent();
        from = intent.getStringExtra(Global.INTENT_FROM_KEY);
        contentId = intent.getStringExtra(Global.INTENT_CONTENT_KEY);
        try {
            String title = intent.getStringExtra(Global.INTENT_TITLE_KEY);
            if (!TextUtils.isEmpty(title)) {
                setToolbarTitle(title);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicListFragment.loadMusic(from, contentId);
    }
}
