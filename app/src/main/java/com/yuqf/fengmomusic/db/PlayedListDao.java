package com.yuqf.fengmomusic.db;

import com.yuqf.fengmomusic.media.Music;
import java.util.List;

public class PlayedListDao {
    public final static String TABLE_NAME = "played_list";
    public final static String MUSIC_ID = "music_id";
    public final static String MUSIC_NAME = "music";
    public final static String ARTIST = "artist";
    public final static String ARTIST_ID = "artist_id";
    public final static String PLAY_URL = "play_url";
    public final static String DURATION = "duration";

    private PlayedListDao playedListDao;

    public PlayedListDao getInstance() {
        if (playedListDao == null) {
            playedListDao = new PlayedListDao();
        }
        return playedListDao;
    }

    private PlayedListDao() {
    }

    public List<PlayingMusic> getPlayList() {
        return DatabaseManager.getInstance().getPlayedList();
    }

    public PlayingMusic getPlayingMusic(int musicId) {
        return DatabaseManager.getInstance().getPlayingMusic(musicId);
    }

    public void insertPlayingMusic(Music music) {
        DatabaseManager.getInstance().insertPlayingMusic(music);
    }

    public void deletePlayingList() {
        DatabaseManager.getInstance().deletePlayedList();
    }

    public void deletePlayingMusic(int musicId){
        DatabaseManager.getInstance().deletePlayingMusic(musicId);
    }
}
