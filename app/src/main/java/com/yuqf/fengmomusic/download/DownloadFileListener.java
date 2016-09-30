package com.yuqf.fengmomusic.download;

/**
 * Created by admin on 2016/9/20.
 */
public interface DownloadFileListener {
    void onReady();

    void onStarted(int totalSize);

    void onProgressChanged(int progress);

    void onCompleted(int state);
}
