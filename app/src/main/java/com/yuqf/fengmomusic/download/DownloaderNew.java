package com.yuqf.fengmomusic.download;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.db.DownloadedDao;
import com.yuqf.fengmomusic.db.DownloadedMusic;
import com.yuqf.fengmomusic.db.DownloadingDao;
import com.yuqf.fengmomusic.db.DownloadingPartMusic;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.utils.FileUtils;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DownloaderNew {

    private Music music;
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
    private DownloadThreadNew[] threads;
    private int blockSize;
    private String musicPath;
    private String musicUrl;
    private int downloadStatus = DOWNLOADING;
    private long startTime;
    private long endTime;
    private List<DownloadingPartMusic> partMusicList;

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
                        DownloadedDao.getInstance().getDownloaderList().remove(DownloaderNew.this);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public DownloaderNew(Music music) {
        this.music = music;
        musicPath = FileUtils.getMusicPath(music.getName(), music.getArtist());
        Log.d(logTag, "musicPath:\n" + musicPath);
        DownloadedDao.getInstance().getDownloaderList().add(DownloaderNew.this);
    }

    public String getMusicName() {
        return music.getName();
    }

    public String getArtist() {
        return music.getArtist();
    }

    public void setListener(DownloadFileListener listener) {
        this.listener = listener;
    }

    public void download() {
        Log.d(logTag, "============download:\n=========1");
        /**第一步：先判断是否已经下载，如果已经下载，就不下载了，但是如果本地文件不存在，就把已下载表中记录删除，重新下载**/
        DownloadedMusic downloadedMusic = DownloadedDao.getInstance().getDownloadedMusic(music.getId(), music.getName());
        Log.d(logTag, "============download:\n=========2");
        if (downloadedMusic != null) {
            File file = new File(musicPath);
            if (file.exists()) {
                Log.d(logTag, "============download:\n=========3");
                handler.sendEmptyMessage(READY_WHAT);
                totalSize = downloadedMusic.getTotalSize();
                handler.sendEmptyMessage(STARTED_WHAT);
                downloadStatus = SUCCESS;
                handler.sendEmptyMessage(COMPLETED_WHAT);
                return;
            } else {
                DownloadedDao.getInstance().deleteDownloadedMusic(music.getId(), music.getName());
                Log.d(logTag, "============download:\n=========4");
            }
        }

        /**第二步：判断是否已经部分下载，如果是，就把数据信息读出来，如果没有，就创建分段下载的信息**/
        if (DownloadingDao.getInstance().isDownloading(music.getId(), music.getName())) {
            Log.d(logTag, "============download:\n=========5");
            File file = new File(musicPath);
            if (file.exists()) {
                partMusicList = DownloadingDao.getInstance().getDownloadingMusic(music.getId(), music.getName());
                Log.d(logTag, "============download:\n=========6");
                //如果记录的下载线程数和当时下载时的个数不同，认为是下载的线程记录有问题，删除重新下载
                if (partMusicList.size() == partMusicList.get(0).getThreadCount()) {
                    Log.d(logTag, "============download:\n=========7");
                    for (DownloadingPartMusic partMusic : partMusicList) {
                        downloadedSize += partMusic.getCompletedSize();
                    }
                    musicUrl = partMusicList.get(0).getDownloadUrl();
                    totalSize = partMusicList.get(0).getTotalSize();
                    blockSize = partMusicList.get(0).getBlockSize();
                    continueDownloading();
                    return;
                }
            }
            Log.d(logTag, "============download:\n=========8");
            DownloadingDao.getInstance().deleteDownloadingMusic(music.getId(), music.getName());
        }
        Log.d(logTag, "============download:\n=========9");
        startDownload();
    }

    private void continueDownloading() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(logTag, "============download:\n=========10");
                getMusicUrl();
                if (!TextUtils.isEmpty(musicUrl)) {
                    handler.sendEmptyMessage(READY_WHAT);
                    handler.sendEmptyMessage(STARTED_WHAT);
                    handler.sendEmptyMessage(PROGRESS_WHAT);
                    Log.d(logTag, "============download:\n=========11");
                    downloadByBreakPoint(false);
                } else {
                    handleResult(false);
                }
            }
        }).start();
    }

    private void startDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getMusicUrl();
                if (!TextUtils.isEmpty(musicUrl)) {
                    Log.d(logTag, "============download:\n=========13");
                    Log.d(logTag, musicUrl);
                    downloadByBreakPoint(true);
                } else {
                    handleResult(false);
                }
            }
        }).start();
    }

    private void getMusicUrl() {
        try {
            Log.d(logTag, "============download:\n=========12");
            handler.sendEmptyMessage(READY_WHAT);
            Log.d(logTag, "download ready......\n");
            URL url = new URL(String.format(Locale.getDefault(), UrlHelper.Music_Get_Url, music.getId()));
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断点下载
     **/
    private void downloadByBreakPoint(boolean isNewTask) {
        HttpURLConnection connection;
        try {
            Log.d(logTag, "============download:\n=========14");
            connection = getUrlConnection(true);
            if (isNewTask) {
                if (!getNetStatus(connection)) {
                    return;
                }
                Log.d(logTag, "============download:\n=========15");
                blockSize = totalSize / threadCount;
                initPartMusicList();
            } else {
                Log.d(logTag, "============download:\n=========16");
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    handleResult(false);
                    return;
                }
            }
            Log.d(logTag, "============download:\n=========17");
            threads = new DownloadThreadNew[threadCount];
            for (int i = 0; i < threadCount; i++) {
                if (isCancel) return;
                Log.d(logTag, "============download:\n=========18");
                if (!partMusicList.get(i).isCompleted()) {
                    DownloadThreadNew thread = new DownloadThreadNew(partMusicList.get(i), musicPath);
                    threads[i] = thread;
                    thread.start();
                } else {
                    //下载已经完成的就设置线程为null
                    threads[i] = null;
                }
            }
            Log.d(logTag, "============download:\n=========19");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleResult(boolean success) {
        if (!isCancel) {
            Log.d(logTag, "============download:\n=========20");
            this.downloadStatus = success ? SUCCESS : ERROR;
            handler.sendEmptyMessage(COMPLETED_WHAT);
        }
    }

    private HttpURLConnection getUrlConnection(boolean connect) {
        Log.d(logTag, "============download:\n=========21");
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
            Log.d(logTag, "============download:\n=========22");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private boolean getNetStatus(HttpURLConnection connection) {
        try {
            Log.d(logTag, "============download:\n=========23");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                totalSize = connection.getContentLength();
                if (totalSize > 0) {
                    Log.d(logTag, "download started.....\n");
                    handler.sendEmptyMessage(STARTED_WHAT);
                    Log.d(logTag, "============download:\n=========24");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(logTag, "============download:\n=========25");
        handleResult(false);
        return false;
    }

    private void initPartMusicList() {
        Log.d(logTag, "============download:\n=========26");
        partMusicList = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Log.d(logTag, "============download:\n=========27");
            DownloadingPartMusic partMusic = new DownloadingPartMusic();
            partMusic.setMusicId(music.getId());
            partMusic.setName(music.getName());
            partMusic.setArtistId(music.getArtistId());
            partMusic.setArtist(music.getArtist());
            partMusic.setCompletedSize(0);
            partMusic.setCompleted(false);
            partMusic.setTotalSize(totalSize);
            partMusic.setThreadCount(threadCount);
            partMusic.setThreadIndex(i);
            partMusic.setBlockSize(blockSize);
            partMusic.setDownloadUrl(musicUrl);
            partMusicList.add(partMusic);
        }
        Log.d(logTag, "============download:\n=========28");
        DownloadingDao.getInstance().insertDownloadingMusic(partMusicList);
        Log.d(logTag, "============download:\n=========29");
    }

    private synchronized void finishSingleThread() {
        Log.d(logTag, "============download:\n=========30");
        boolean allCompleted = true;
        for (int i = 0; i < threadCount; i++) {
            if (isCancel) return;
            if (threads[i] == null) continue;
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
                    threads[i] = new DownloadThreadNew(partMusicList.get(i), musicPath);
                    threads[i].start();
                    break;
                case DOWNLOADING:
                    allCompleted = false;
                    break;
                case SUCCESS:
                    break;
            }
        }
        Log.d(logTag, "============download:\n=========31");
        if (allCompleted) {
            Log.d(logTag, "==========下载成功===========\n");
            downloadStatus = SUCCESS;
            handler.sendEmptyMessage(COMPLETED_WHAT);
            DownloadingDao.getInstance().deleteDownloadingMusic(music.getId(), music.getName());
            DownloadedDao.getInstance().insertDownloadedMusic(music, totalSize, musicUrl);
        }
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

    private class DownloadThreadNew extends Thread {
        private int startPos;
        private int endPos;
        private String musicPath;
        private int status = DOWNLOADING;
        private DownloadingPartMusic partMusic;

        DownloadThreadNew(DownloadingPartMusic partMusic, String musicPath) {
            this.partMusic = partMusic;
            startPos = partMusic.getThreadIndex() * blockSize + partMusic.getCompletedSize();
            endPos = (partMusic.getThreadIndex() + 1) * blockSize - 1;
            this.musicPath = musicPath;
        }

        public int getThreadIndex() {
            return partMusic.getThreadIndex();
        }

        public int getStatus() {
            return status;
        }

        @Override
        public void run() {
            Log.d(logTag, "============download:\n=========32");
            HttpURLConnection connection = getUrlConnection(false);
            if (partMusic.getThreadIndex() + 1 < partMusic.getThreadCount())
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
                    accessFile.write(data, 0, curSize);
                    int completedSize = partMusic.getCompletedSize();
                    partMusic.setCompletedSize(completedSize + curSize);
                    Log.d(logTag, String.format("download size changed: %d-----%d", partMusic.getThreadIndex(), curSize));
                    downloadedSize += curSize;
                    handler.sendEmptyMessage(PROGRESS_WHAT);
                    DownloadingDao.getInstance().updateDownloadingMusic(partMusic, false);
                    waitForPause();
                }
                Log.d(logTag, "============download:\n=========33");
                DownloadingDao.getInstance().updateDownloadingMusic(partMusic, true);
                Log.d(logTag, "============download:\n=========33");
                if (isCancel) return;
                Log.d(logTag, "====当前线程======下载成功===========：" + String.valueOf(partMusic.getThreadIndex()) + "\n");
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

}
