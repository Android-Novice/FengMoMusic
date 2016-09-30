package com.yuqf.fengmomusic.db;

import com.yuqf.fengmomusic.media.Music;

import java.util.List;

public class FavouriteDao {
    public final static String TABLE_NAME = "favorite_list";
    public final static String MUSIC_ID = "music_id";
    public final static String MUSIC_NAME = "music";
    public final static String ARTIST = "artist";
    public final static String ARTIST_ID = "artist_id";
    public final static String PLAY_URL = "play_url";
    public final static String DURATION = "duration";
    private FavouriteDao favouriteDao;

    public FavouriteDao getInstance() {
        if (favouriteDao == null) {
            favouriteDao = new FavouriteDao();
        }
        return favouriteDao;
    }

    private FavouriteDao() {
    }

    public List<PlayingMusic> getFavouriteList() {
        return DatabaseManager.getInstance().getFavouriteList();
    }

    public PlayingMusic getFavoriteMusic(int musicId) {
        return DatabaseManager.getInstance().getFavouriteMusic(musicId);
    }

    public void insertFavoriteMusic(Music music) {
        DatabaseManager.getInstance().insertFavouriteMusic(music);
    }

    public void deleteFavoriteList() {
        DatabaseManager.getInstance().deleteFavouriteList();
    }

    public void deleteFavoriteMusic(int musicId) {
        DatabaseManager.getInstance().deleteFavouriteMusic(musicId);
    }
}
