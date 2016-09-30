package com.yuqf.fengmomusic.download;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.utils.FileUtils;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class Downloader {

    private int musicId;
    private String music;
    private String artist;

    private int totalSize;
    private int downloadedSize;
    private DownloadFileListener listener;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int DOWNLOADING = 3;
    public static final int CANCEL = 4;
    public static final int PAUSE = 5;

    private final int READY_WHAT = 109;
    private final int STARTED_WHAT = 110;
    private final int PROGRESS_WHAT = 111;
    private final int COMPLETED_WHAT = 112;

    private final String logTag = "Downloader";

    /**
     * 这个值决定断点下载的分段数量
     **/
    private int threadCount = 3;
    private int retryTimes = threadCount;
    private DownloadThread[] threads;
    private int blockSize;
    private String musicPath;
    private String musicUrl;
    private int downloadStatus = DOWNLOADING;
    private long startTime;
    private long endTime;

    final Handler handler = new Handler(MyApplication.getContext().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (listener != null) {
                switch (msg.what) {
                    case READY_WHAT:
                        listener.onReady();
                        break;
                    case STARTED_WHAT:
                        startTime = System.currentTimeMillis();
                        listener.onStarted(totalSize);
                        break;
                    case PROGRESS_WHAT:
                        listener.onProgressChanged((int) ((float) 100 * downloadedSize / totalSize));
                        break;
                    case COMPLETED_WHAT:
                        endTime = System.currentTimeMillis();
                        int spendTime = (int) ((endTime - startTime) / 1000);
                        Log.d(logTag, "Download complete, Spend Time:" + String.valueOf(spendTime));
                        listener.onProgressChanged(100);
                        listener.onCompleted(downloadStatus);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public Downloader(int musicId, String music, String artist) {
        this.musicId = musicId;
        this.music = music;
        this.artist = artist;
        Log.d(logTag, "musicPath:\n" + musicPath);
        musicPath = FileUtils.getMusicPath(music, artist);
    }

    public void download(DownloadFileListener listener) {
        this.listener = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(READY_WHAT);
                    Log.d(logTag, "download ready......\n");
                    URL url = new URL(String.format(Locale.getDefault(), UrlHelper.Music_Get_Url, musicId));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    if (isCancel) return;
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        StringBuilder builder = new StringBuilder();
                        InputStream stream = connection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(stream);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String curStr;
                        while (!TextUtils.isEmpty(curStr = reader.readLine())) {
                            builder.append(curStr);
                        }
                        stream.close();
                        connection.disconnect();
                        musicUrl = builder.toString().trim();
                        if (!TextUtils.isEmpty(musicUrl)) {
//                            downloadByBreakPoint();
                            downloadMusic();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handleResult(false);
            }
        }
        ).start();
    }

    private boolean paused;
    private boolean isCancel;

    public void pause() {
        if (paused) return;
        Log.d(logTag, "============pause=========");
        downloadStatus = PAUSE;
        paused = true;
    }

    public void continueTask() {
        if (!paused) return;
        Log.d(logTag, "============continue=========");
        paused = false;
        downloadStatus = DOWNLOADING;
    }

    private void waitForPause() {
        while (paused && !isCancel) {
            try {
                Log.d(logTag, "=========wait===pause=========");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        if (isCancel) return;
        Log.d(logTag, "============cancel=========");
        downloadStatus = CANCEL;
        isCancel = true;
        handler.sendEmptyMessage(COMPLETED_WHAT);
    }

    private HttpURLConnection getUrlConnection(boolean connect) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(musicUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Encoding", "identity");
            if (connect)
                connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private boolean startDownloadMusic(HttpURLConnection connection) {
        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                totalSize = connection.getContentLength();
                if (totalSize > 0) {
                    Log.d(logTag, "download started.....\n");
                    handler.sendEmptyMessage(STARTED_WHAT);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleResult(false);
        return false;
    }

    /**
     * 断点下载
     **/
    private void downloadByBreakPoint() {
        HttpURLConnection connection;
        try {
            connection = getUrlConnection(true);
            if (!startDownloadMusic(connection)) {
                return;
            }
            if (isCancel) return;

            blockSize = totalSize / threadCount;
            threads = new DownloadThread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                if (isCancel) return;
                DownloadThread thread = new DownloadThread(i, musicPath);
                threads[i] = thread;
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleResult(boolean success) {
        if (!isCancel) {
            this.downloadStatus = success ? SUCCESS : ERROR;
            handler.sendEmptyMessage(COMPLETED_WHAT);
        }
    }

    private synchronized void finishSingleThread() {
        boolean allCompleted = true;
        for (int i = 0; i < threadCount; i++) {
            if (isCancel) return;
            int index = threads[i].getThreadIndex();
            int status = threads[i].getStatus();
            Log.d(logTag, String.format(Locale.getDefault(), "ThreadIndex: %d,\n Status:%d", index, status));
            switch (status) {
                case ERROR:
                    retryTimes--;
                    if (retryTimes < 0) {
                        downloadStatus = ERROR;
                        handler.sendEmptyMessage(COMPLETED_WHAT);
                        Log.d(logTag, "==========下载失败===========\n");
                        return;
                    }
                    threads[i] = null;
                    threads[i] = new DownloadThread(i, musicPath);
                    threads[i].start();
                    break;
                case DOWNLOADING:
                    allCompleted = false;
                    break;
                case SUCCESS:
                    break;
            }
        }
        if (allCompleted) {
            Log.d(logTag, "==========下载成功===========\n");
            downloadStatus = SUCCESS;
            handler.sendEmptyMessage(COMPLETED_WHAT);
        }
    }

    private class DownloadThread extends Thread {
        private int startPos;
        private int endPos;
        private String musicPath;
        private int status = DOWNLOADING;
        private int threadIndex;

        DownloadThread(int threadIndex, String musicPath) {
            this.threadIndex = threadIndex;
            startPos = threadIndex * blockSize;
            endPos = (threadIndex + 1) * blockSize - 1;
            this.musicPath = musicPath;
        }

        public int getThreadIndex() {
            return threadIndex;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public void run() {
            HttpURLConnection connection = getUrlConnection(false);
            if (threadIndex + 1 < threadCount)
                connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            else
                connection.setRequestProperty("Range", "bytes=" + startPos + "-");
            try {
                Log.d(logTag, String.valueOf(connection.getResponseCode() + "============ResponseCode======\n"));
                RandomAccessFile accessFile = new RandomAccessFile(musicPath, "rwd");
                accessFile.seek(startPos);

                InputStream inputStream = connection.getInputStream();
                byte[] data = new byte[1024];
                int curSize;
                while ((curSize = inputStream.read(data)) > 0) {
                    if (isCancel) return;
//                    synchronized (this) {
                    downloadedSize += curSize;
                    Log.d(logTag, String.format("download size changed: %d-----%d", threadIndex, curSize));
                    handler.sendEmptyMessage(PROGRESS_WHAT);
//                    }
                    accessFile.write(data, 0, curSize);
                    waitForPause();
                }
                if (isCancel) return;
                Log.d(logTag, "====当前线程======下载成功===========：" + String.valueOf(threadIndex) + "\n");
                accessFile.close();
                inputStream.close();
                status = SUCCESS;
            } catch (IOException e) {
                status = ERROR;
                e.printStackTrace();
            }
            if (!isCancel)
                finishSingleThread();
        }
    }

    /**
     * 直接下载
     **/
    private void downloadMusic() {
        HttpURLConnection connection = null;
        InputStream input = null;
        OutputStream stream = null;
        try {
            waitForPause();
            if (isCancel) return;
            connection = getUrlConnection(true);
            if (!startDownloadMusic(connection)) {
                return;
            }
            if (isCancel) return;
            input = connection.getInputStream();
            stream = new FileOutputStream(new File(musicPath));
            byte[] bytes = new byte[1024];
            int curSize;
            while ((curSize = input.read(bytes)) != -1) {
                if (isCancel) return;
                downloadedSize += curSize;
                stream.write(bytes, 0, curSize);
                Log.d(logTag, String.format("download size changed: -----%d", curSize));
                handler.sendEmptyMessage(PROGRESS_WHAT);
                waitForPause();
            }
            if (isCancel) return;
            downloadStatus = SUCCESS;
        } catch (IOException e) {
            downloadStatus = ERROR;
            e.printStackTrace();
        } finally {
            Log.d(logTag, "download completed.....\n");
            try {
                if (stream != null)
                    stream.close();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();

            handler.sendEmptyMessage(COMPLETED_WHAT);
        }
    }

}
