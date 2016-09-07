package com.yuqf.fengmomusic.base;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.utils.CommonUtils;

public class BaseActivity extends AppCompatActivity {

    private View statusBarReplaceView;
    private Toolbar toolbar;
    public View toolbarContent;
    private final String logTag = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMgr.getActivityMgr().addActivity(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        StatusBarCompat.compat(this, R.color.colorPrimary);

//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(getResources().getColor(R.color.my_statusbar_color));

    }

    //Init ActionBar and status bar
    public void initTopBars() {
        try {
            toolbarContent = findViewById(R.id.toolbar);
            statusBarReplaceView = findViewById(R.id.tb_status_bkg);
            toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
            if (statusBarReplaceView != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    statusBarReplaceView.setVisibility(View.GONE);
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    statusBarReplaceView.setVisibility(View.VISIBLE);
                    int statusBarHeight = CommonUtils.getStatusBarHeight(this);
                    statusBarReplaceView.setLayoutParams(new LinearLayout.LayoutParams(statusBarReplaceView.getWidth(), statusBarHeight));
                }
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                Drawable drawable = getResources().getDrawable(R.drawable.arrow_back_white_36dp);
                drawable.setBounds(0, 0, drawable.getMinimumWidth() * 3 / 4, drawable.getMinimumHeight() * 3 / 4);
                toolbar.setNavigationIcon(drawable);
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            }
        } catch (Exception ex) {
            Log.e("logTag", ex.getMessage());
            Log.e("logTag", ex.getStackTrace().toString());
        }
    }

    public void hideToolBar() {
        if (toolbar != null)
            getSupportActionBar().hide();
    }

    public void showToolBar() {
        if (toolbar != null) {
            getSupportActionBar().show();
        }
    }

    public void setToolbarHomeAsUp() {
        if (toolbar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_white_36dp);
        }
    }

    public void setToolbarTitle(String title) {
        if (toolbar != null) {
//            toolbar.setTitle(title);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMgr.getActivityMgr().removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:

                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
