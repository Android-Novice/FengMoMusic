package com.yuqf.fengmomusic.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMgr.getActivityMgr().removeActivity(this);
    }

}
