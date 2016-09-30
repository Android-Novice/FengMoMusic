package com.yuqf.fengmomusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.db.DownloadedDao;
import com.yuqf.fengmomusic.download.DownloadFileListener;
import com.yuqf.fengmomusic.download.Downloader;
import com.yuqf.fengmomusic.download.DownloaderNew;
import com.yuqf.fengmomusic.utils.CommonUtils;

public class DownloadActivity extends BaseActivity implements Button.OnClickListener {

    private TextView musicTV;
    private TextView progressTV;
    private ProgressBar progressBar;

    private Button btnPause;
    private Button btnContinue;
    private Button btnCancel;
    private Downloader downloader;
    private DownloaderNew downloaderNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        initTopBars();
        showToolBar();
        setToolbarHomeAsUp();

        musicTV = (TextView) findViewById(R.id.downloading_music_name_tv);
        progressTV = (TextView) findViewById(R.id.downloading_progress_tv);
        progressBar = (ProgressBar) findViewById(R.id.downloading_progress_bar);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnPause.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            final int id = intent.getIntExtra("musicId", -1);
            if (id != -1) {
                final String artist = intent.getStringExtra("artist");
                final String music = intent.getStringExtra("music");
                musicTV.setText(music);

                downloader = new Downloader(id, music, artist);
                downloader.download(downloadFileListener);
                return;
            }
        }
        downloaderNew = DownloadedDao.getInstance().getDownloaderList().get(0);
        downloaderNew.setListener(downloadFileListener);
        musicTV.setText(downloaderNew.getMusicName() + "\n" + downloaderNew.getArtist());
        downloaderNew.download();

    }

    DownloadFileListener downloadFileListener = new DownloadFileListener() {
        @Override
        public void onReady() {
            progressTV.setText("0%");
            progressBar.setProgress(0);
        }

        @Override
        public void onStarted(int totalSize) {
            CommonUtils.showToast("当前音乐总大小：" + String.valueOf(totalSize), true);
        }

        @Override
        public void onProgressChanged(int progress) {
            progressBar.setProgress(progress);
            progressTV.setText(String.valueOf(progress) + "%");
        }

        @Override
        public void onCompleted(int state) {
            downloader = null;
            CommonUtils.showToast(state == Downloader.SUCCESS ? "当前音乐下载完成！！！" :
                    (state == Downloader.CANCEL ? "当前音乐下载取消" : "当前音乐下载失败"), true);
        }
    };

    @Override
    public void onClick(View v) {
        if (downloader != null || downloaderNew != null) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    if (downloaderNew != null)
                        downloaderNew.cancel();
                    else
                        downloader.cancel();
                    break;
                case R.id.btn_pause:
                    if (downloaderNew != null)
                        downloaderNew.pause();
                    else
                        downloader.pause();
                    break;
                case R.id.btn_continue:
                    if (downloaderNew != null)
                        downloaderNew.continueTask();
                    else
                        downloader.continueTask();
                    break;
            }
        }
    }
}
