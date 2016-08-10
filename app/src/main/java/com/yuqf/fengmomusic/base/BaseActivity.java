package com.yuqf.fengmomusic.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.utils.CommonUtils;

public class BaseActivity extends AppCompatActivity {

    private View statusBarReplaceView;
    private Toolbar toolbar;
    private final String logTag = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMgr.getActivityMgr().addActivity(this);
//        StatusBarCompat.compat(this, R.color.colorPrimary);

//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(getResources().getColor(R.color.my_statusbar_color));

    }

    //Init ActionBar and status bar
    public void initTopBars() {
        try {
            statusBarReplaceView = findViewById(R.id.tb_status_bkg);
            toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
            if (statusBarReplaceView != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    statusBarReplaceView.setVisibility(View.GONE);
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    statusBarReplaceView.setVisibility(View.VISIBLE);
                    int statusBarHeight = CommonUtils.getStatusBarHeight(this);
                    statusBarReplaceView.setLayoutParams(new RelativeLayout.LayoutParams(statusBarReplaceView.getWidth(), statusBarHeight));
                }
            if (toolbar != null)
                setSupportActionBar(toolbar);
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
        if (toolbar != null)
            getSupportActionBar().show();
    }

    public void setToolbarHomeAsUp() {
        if (toolbar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMgr.getActivityMgr().removeActivity(this);
    }

}
