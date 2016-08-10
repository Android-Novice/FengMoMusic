package com.yuqf.fengmomusic.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yuqf.fengmomusic.MainActivity;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private int timeCount;
    private final int TimerFlag = 11;
    private final int SkipMainFlag = 12;
    private final int DelayTime = 2000;
    private final int StartTimerFlag = 10;
    private TextView textView;
    private final String logTag = "SplashActivity";
    private TimerTask timerTask;
    private Timer timer;

    private final String ImageUrl = "http://e.hiphotos.baidu.com/image/pic/item/21a4462309f79052cc7b95370ef3d7ca7bcbd557.jpg";
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StartTimerFlag:
                    startTimer();
                    break;
                case TimerFlag:
                    timeCount++;
                    textView.setText(String.valueOf(3 - timeCount));
                    if (timeCount >= 3) {
                        cancelTimer();
                        sendDelayMsg();
                        textView.setVisibility(View.INVISIBLE);
                    }
                    break;
                case SkipMainFlag:
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.splash_image_view);
        textView = (TextView) findViewById(R.id.text_view_timer);

        int width = CommonUtils.getScreenWidth(this);
        int height = CommonUtils.getScreenHeight(this);
        View view = findViewById(R.id.splash_bottom);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });

        builder.build().with(this).load(ImageUrl).resize(width * 3 / 4, (height - view.getHeight() - textView.getHeight()) * 3 / 4).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(logTag, "load image success");
                Message message = new Message();
                message.what = StartTimerFlag;
                handler.sendMessage(message);
            }

            @Override
            public void onError() {
                Log.d(logTag, "load image failed");
                sendDelayMsg();
            }
        });
    }

    private void sendDelayMsg() {
        handler.sendEmptyMessageDelayed(SkipMainFlag, DelayTime);
    }

    private void startTimer() {
        textView.setText("");
        textView.setText("3");
        textView.setVisibility(View.VISIBLE);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = TimerFlag;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }

    private void cancelTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
}
