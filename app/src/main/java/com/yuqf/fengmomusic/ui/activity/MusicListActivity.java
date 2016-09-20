package com.yuqf.fengmomusic.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.ui.fragment.MusicListFragment;
import com.yuqf.fengmomusic.utils.Blur;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.FileUtils;
import com.yuqf.fengmomusic.utils.Global;

public class MusicListActivity extends BaseActivity {

    private final String logTag = "MusicListActivity";
    private MusicListFragment musicListFragment;
    private String contentId;
    private String from;
    private int screenWidth;
    private ImageView normalIV;
    private ImageView blurredIV;
    private FrameLayout photoContentView;
    private ImageView backgroundBlurredIV;
    private ImageView backgroundNormalIV;
    private Bitmap blurredBmp;
    private float scrolledTopRate = .5f;
    private float scrolledAlpha = 0.55f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        initTopBars();
        showToolBar();
        setToolbarHomeAsUp();

        screenWidth = CommonUtils.getScreenWidth(this);

        normalIV = (ImageView) findViewById(R.id.normal_iv);
        blurredIV = (ImageView) findViewById(R.id.blurred_iv);
        backgroundBlurredIV = (ImageView) findViewById(R.id.music_list_background_blurred);
        backgroundNormalIV = (ImageView) findViewById(R.id.music_list_background_normal);
        photoContentView = (FrameLayout) findViewById(R.id.photo_content);

        musicListFragment = (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_music_list);
        musicListFragment.setScrolledListener(new MusicListFragment.OnFragmentScrolledListener() {
            @Override
            public void onNotifyScrolled(float alpha, int scrollY) {
                if (alpha == 0)
                    toolbarContent.setVisibility(View.INVISIBLE);
                else
                    toolbarContent.setVisibility(View.VISIBLE);
                toolbarContent.setAlpha(alpha);
                backgroundNormalIV.setAlpha((1 - alpha) * scrolledAlpha);
                normalIV.setTop((int) (-scrollY * scrolledTopRate));
                normalIV.setAlpha((1 - alpha) * scrolledAlpha);
                blurredIV.setTop((int) (-scrollY * scrolledTopRate));
            }
        });

        toolbarContent.setAlpha(0);
        toolbarContent.setBackgroundColor(Color.TRANSPARENT);
        int height = (int) (Global.HEADER_HEIGHT * scrolledTopRate);
        photoContentView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenWidth - height));
        normalIV.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenWidth));
        blurredIV.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenWidth));

        Intent intent = getIntent();
        from = intent.getStringExtra(Global.INTENT_FROM_KEY);
        contentId = intent.getStringExtra(Global.INTENT_CONTENT_KEY);
        String coverUrl = intent.getStringExtra(Global.INTENT_COVER_KEY);
        try {
            String title = intent.getStringExtra(Global.INTENT_TITLE_KEY);
            setBitmap(title, coverUrl);

            if (!TextUtils.isEmpty(title)) {
                setToolbarTitle(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicListFragment.loadMusic(from, contentId);
    }

    private void setBitmap(String title, String coverUrl) {
        Log.d(logTag, "entering....\ntitle:" + title + "\nurl:" + coverUrl);
        Bitmap normalBmp = null;
        boolean hasWebCover = true;
        if (from.equals(Global.INTENT_FROM_RANKING)) {
            Log.d(logTag, "1....\n");
            normalBmp = FileUtils.getRankingCover(title, coverUrl);
            if (normalBmp == null) {
                Log.d(logTag, "2....\n");
                hasWebCover = false;
                normalBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ranking_default);
            }
        } else if (from.equals(Global.INTENT_FROM_SINGER)) {
            Log.d(logTag, "3....\n");
            normalBmp = FileUtils.getSingerHead(coverUrl);
            if (normalBmp == null) {
                Log.d(logTag, "4....\n");
                hasWebCover = false;
                normalBmp = BitmapFactory.decodeResource(getResources(), R.drawable.singer_default);
            }
        }
        Log.d(logTag, "5....\n");
        Bitmap blurringBmp = Bitmap.createBitmap(normalBmp);
//        normalBmp = CommonUtils.scaleBitmap(normalBmp, screenWidth, Global.HEADER_HEIGHT);
        normalIV.setImageBitmap(normalBmp);

        int height = (int) (normalBmp.getHeight() * Global.HEADER_HEIGHT * scrolledTopRate / screenWidth);

        Bitmap bottomNormalBkg = Bitmap.createBitmap(normalBmp, 0, normalBmp.getHeight() - height, normalBmp.getWidth(), height);
        backgroundNormalIV.setImageBitmap(bottomNormalBkg);

        Log.d(logTag, "6....\n");
        blurredBmp = null;
        if (from.equals(Global.INTENT_FROM_RANKING)) {
            if (hasWebCover) {
                Log.d(logTag, "7....\n");
                blurredBmp = FileUtils.getBlurredRankingBitmap(title, coverUrl);
                if (blurredBmp == null) {
                    Log.d(logTag, "8....\n");
                    blurredBmp = Blur.fastblur(this, blurringBmp, 13);
                    FileUtils.saveBlurredRankingBitmap(blurredBmp, title, coverUrl);
                }
            } else {
                Log.d(logTag, "9....\n");
                blurredBmp = FileUtils.getBlurredRankingBitmap(Global.RANKING_DEFAULT_NAME, Global.RANKING_DEFAULT_URL);
                if (blurredBmp == null) {
                    Log.d(logTag, "10....\n");
                    blurredBmp = Blur.fastblur(this, blurringBmp, 13);
                    FileUtils.saveBlurredRankingBitmap(blurredBmp, Global.RANKING_DEFAULT_NAME, Global.RANKING_DEFAULT_URL);
                }
            }
        } else if (from.equals(Global.INTENT_FROM_SINGER)) {
            if (hasWebCover) {
                Log.d(logTag, "11....\n");
                blurredBmp = FileUtils.getBlurredSingerBitmap(coverUrl);
                if (blurredBmp == null) {
                    Log.d(logTag, "12....\n");
                    blurredBmp = Blur.fastblur(this, blurringBmp, 13);
                    FileUtils.saveBlurredSingerBitmap(blurredBmp, coverUrl);
                }
            } else {
                Log.d(logTag, "13....\n");
                blurredBmp = FileUtils.getBlurredSingerBitmap(Global.SINGER_DEFAULT_URL);
                if (blurredBmp == null) {
                    Log.d(logTag, "14....\n");
                    blurredBmp = Blur.fastblur(this, blurringBmp, 13);
                    FileUtils.saveBlurredSingerBitmap(blurredBmp, Global.SINGER_DEFAULT_URL);
                }
            }
        }
        Log.d(logTag, "15....\n");
        blurredIV.setImageBitmap(blurredBmp);

        Bitmap bottomBkg = Bitmap.createBitmap(blurredBmp, 0, blurredBmp.getHeight() - 5, blurredBmp.getWidth(), 5);
        backgroundBlurredIV.setImageBitmap(bottomBkg);
    }
}
