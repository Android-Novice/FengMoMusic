package com.yuqf.fengmomusic.db;

import com.yuqf.fengmomusic.download.DownloaderNew;
import com.yuqf.fengmomusic.media.Music;

import java.util.ArrayList;
import java.util.List;

public class DownloadedDao {
    public final static String TABLE_NAME = "downloaded_info";
    public final static String MUSIC_ID = "music_id";
    public final static String MUSIC_NAME = "music";
    public final static String ARTIST = "artist";
    public final static String ARTIST_ID = "artist_id";
    public final static String DOWNLOAD_URL = "download_url";
    public final static String TOTAL_SIZE = "total_size";
    public final static String COMPLETE_TIME = "finished_time";
    private List<DownloaderNew> downloaderList;

    private static DownloadedDao downloadedDao;

    public static DownloadedDao getInstance() {
        if (downloadedDao == null)
            downloadedDao = new DownloadedDao();
        return downloadedDao;
    }

    private DownloadedDao() {
        if (downloaderList == null) {
            downloaderList = new ArrayList<>();
        }
    }

    public List<DownloaderNew> getDownloaderList() {
        return downloaderList;
    }

    public List<DownloadedMusic> getDownloadedList() {
        return DatabaseManager.getInstance().getDownloadedList();
    }

    public DownloadedMusic getDownloadedMusic(int musicId, String music) {
        return DatabaseManager.getInstance().getDownloadedMusicById(musicId, music);
    }

    public int getDownloadedMusicCount() {
        return DatabaseManager.getInstance().getDownloadedMusicCount();
    }

    public boolean deleteDownloadedList() {
        return DatabaseManager.getInstance().deleteDownloadedMusics();
    }

    public boolean deleteDownloadedMusic(int musicId, String music) {
        return DatabaseManager.getInstance().deleteDownloadedMusicById(musicId, music);
    }

    public void insertDownloadedMusic(Music music, int totalSize, String downloadUrl) {
        DatabaseManager.getInstance().insertDownloadedMusic(music.getId(), music.getName(), music.getArtistId(), music.getArtist(), downloadUrl, totalSize);
    }
}
