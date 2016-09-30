package com.yuqf.fengmomusic.db;

import java.util.List;

public class DownloadingDao {
    public final static String TABLE_NAME = "downloading_info";
    public final static String MUSIC_ID = "music_id";
    public final static String MUSIC_NAME = "music";
    public final static String ARTIST = "artist";
    public final static String ARTIST_ID = "artist_id";
    public final static String DOWNLOAD_URL = "download_url";
    public final static String TOTAL_SIZE = "total_size";
    public final static String THREAD_INDEX = "thread_index";
    public final static String THREAD_COUNT = "thread_count";
    public final static String BLOCK_SIZE = "block_size";
    public final static String COMPLETE_SIZE = "complete_size";
    public final static String COMPLETED = "completed";

    private static DownloadingDao downloadingDao;

    public static DownloadingDao getInstance() {
        if (downloadingDao == null)
            downloadingDao = new DownloadingDao();
        return downloadingDao;
    }

    private DownloadingDao() {
    }

    public boolean isDownloading(int musicId, String music) {
        return DatabaseManager.getInstance().isDownloading(musicId, music);
    }

    public List<DownloadingPartMusic> getDownloadingMusic(int musicId, String music) {
        return DatabaseManager.getInstance().getDownloadingMusic(musicId, music);
    }

    public int getDownloadingTotalCount() {
        return DatabaseManager.getInstance().getDownloadingMusicCount();
    }

    public void insertDownloadingMusic(List<DownloadingPartMusic> list) {
        DatabaseManager.getInstance().insertDownloadingInfo(list);
    }

    public void updateDownloadingMusic(DownloadingPartMusic partMusic, boolean completed) {
        DatabaseManager.getInstance().updateDownloadingInfo(partMusic, completed);
    }

    public void deleteDownloadingMusic(int musicId, String music) {
        DatabaseManager.getInstance().deleteDownloadingInfo(musicId, music);
    }
}
