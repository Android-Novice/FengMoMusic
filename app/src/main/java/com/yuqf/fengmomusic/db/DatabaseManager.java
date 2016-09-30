package com.yuqf.fengmomusic.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yuqf.fengmomusic.media.Music;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DatabaseManager {

    private final String logTag = "DatabaseManager";
    private static MyDataBaseHelper helper;
    private static DatabaseManager manager;

    public static DatabaseManager getInstance() {
        if (manager == null) {
            manager = new DatabaseManager();
        }
        return manager;
    }

    private DatabaseManager() {
        helper = MyDataBaseHelper.getInstance();
    }

    /**
     * 【一】*****************************下面是已下载歌曲列表相关操作********************************
     **/

    /**
     * 获取已下载音乐列表
     **/
    public List<DownloadedMusic> getDownloadedList() {
        Log.d(logTag, "get downloadedList....1");
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        if (sqLiteDatabase.isOpen()) {
            Log.d(logTag, "get downloadedList....2");
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DownloadedDao.TABLE_NAME, null);
            /**上面这个查询也可以这么写**/
//            String[] columns = new String[]{
//                    DownloadedDao.MUSIC_ID,
//                    DownloadedDao.MUSIC_NAME,
//                    DownloadedDao.ARTIST_ID,
//                    DownloadedDao.ARTIST,
//                    DownloadedDao.TOTAL_SIZE,
//                    DownloadedDao.DOWNLOAD_URL};
//            cursor = sqLiteDatabase.query(true, DownloadedDao.TABLE_NAME, columns, null, null, null, null, null, null);
            List<DownloadedMusic> musicList = new ArrayList<>();
            Log.d(logTag, "get downloadedList....4");
            while (cursor.moveToNext()) {
                DownloadedMusic music = parseDownloadedMusic(cursor);
                if (music != null) {
                    musicList.add(music);
                }
            }
            Log.d(logTag, "get downloadedList....5");
            cursor.close();
            sqLiteDatabase.close();
            return musicList;
        }
        Log.d(logTag, "get downloadedList....6");
        return null;
    }

    /**
     * 获取已下载音乐个数
     **/
    public int getDownloadedMusicCount() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getDownloadedMusicCount....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) as count FROM" + DownloadedDao.TABLE_NAME, null);
            Log.d(logTag, "getDownloadedMusicCount....2");
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                cursor.close();
                sqLiteDatabase.close();
                Log.d(logTag, "getDownloadedMusicCount....3");
                return count;
            }
        }
        Log.d(logTag, "getDownloadedMusicCount....4");
        return 0;
    }

    /**
     * 根据音乐id和音乐名称查找已下载音乐对象
     **/
    public DownloadedMusic getDownloadedMusicById(int musicId, String music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getDownloadedMusicById....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DownloadedDao.TABLE_NAME +
                    " WHERE " +
                    DownloadedDao.MUSIC_ID + "=" + String.valueOf(musicId) +
                    " and " +
                    DownloadedDao.MUSIC_NAME + "='" + music + "'", null);
            /**上面这个查询也可以这么写**/
//            String[] columns = new String[8];
//            cursor = sqLiteDatabase.query(true, DownloadedDao.TABLE_NAME, columns,
//                    DownloadedDao.MUSIC_ID + "=? and " + DownloadedDao.MUSIC_NAME + "=?",
//                    new String[]{String.valueOf(musicId), music}, null, null, null, null);
            Log.d(logTag, "getDownloadedMusicById....2");
            if (cursor != null && cursor.moveToFirst()) {
                Log.d(logTag, "getDownloadedMusicById....3");
                DownloadedMusic downloadedMusic = parseDownloadedMusic(cursor);
                Log.d(logTag, "getDownloadedMusicById....4");
                cursor.close();
                sqLiteDatabase.close();
                if (downloadedMusic != null) {
                    return downloadedMusic;
                }
            }
        }
        Log.d(logTag, "getDownloadedMusicById....5");
        return null;
    }

    /**
     * 删除所有已下载的音乐
     **/
    public boolean deleteDownloadedMusics() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deleteDownloadMusics....1");
        if (sqLiteDatabase.isOpen()) {
            Log.d(logTag, "deleteDownloadMusics....2");
            int rows = sqLiteDatabase.delete(DownloadedDao.TABLE_NAME, null, null);
            /**也可以这么写**/
//            sqLiteDatabase.execSQL("DELETE FROM "+DownloadedDao.TABLE_NAME);
            sqLiteDatabase.close();
            Log.d(logTag, "deleteDownloadMusics....3");
            return rows > 0;
        }
        return false;
    }

    /**
     * 删除指定音乐
     **/
    public boolean deleteDownloadedMusicById(int musicId, String music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deleteDownloadedMusicById....1");
        if (sqLiteDatabase.isOpen()) {
            int rows = sqLiteDatabase.delete(DownloadedDao.TABLE_NAME,
                    DownloadedDao.MUSIC_ID + "=? and " + DownloadedDao.MUSIC_NAME + "=?",
                    new String[]{String.valueOf(musicId), music});
            /**也可以这么写**/
//            sqLiteDatabase.execSQL("DELETE FROM " + DownloadedDao.TABLE_NAME
//                    + " WHERE "
//                    + DownloadedDao.MUSIC_ID + "=" + String.valueOf(musicId)
//                    + " and "
//                    + DownloadedDao.MUSIC_NAME + "=" + music);
            Log.d(logTag, "deleteDownloadedMusicById....2");
            sqLiteDatabase.close();
            return rows > 0;
        }
        return false;
    }

    /**
     * 记录已下载的音乐
     **/
    public boolean insertDownloadedMusic(int musicId, String music, String artistId, String artist, String downloadUrl, int totalSize) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "insertDownloadedMusic....1");
        if (sqLiteDatabase.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadedDao.MUSIC_ID, musicId);
            contentValues.put(DownloadedDao.MUSIC_NAME, music);
            contentValues.put(DownloadedDao.ARTIST_ID, artistId);
            contentValues.put(DownloadedDao.ARTIST, artist);
            contentValues.put(DownloadedDao.COMPLETE_TIME, new Date().getTime());
            contentValues.put(DownloadedDao.DOWNLOAD_URL, downloadUrl);
            contentValues.put(DownloadedDao.TOTAL_SIZE, totalSize);
            /**注意下这里，用的不是insert而是replace**/
            long rowId = sqLiteDatabase.replace(DownloadedDao.TABLE_NAME, "Hello Kitty", contentValues);
            Log.d(logTag, "insertDownloadedMusic....2");
            sqLiteDatabase.close();
            return rowId != -1;
        }
        return false;
    }

    private DownloadedMusic parseDownloadedMusic(Cursor cursor) {
        try {
            DownloadedMusic music = new DownloadedMusic();
            music.setMusicId(cursor.getInt(cursor.getColumnIndex(DownloadedDao.MUSIC_ID)));
            music.setName(cursor.getString(cursor.getColumnIndex(DownloadedDao.MUSIC_NAME)));
            music.setArtist(cursor.getString(cursor.getColumnIndex(DownloadedDao.ARTIST)));
            music.setArtistId(cursor.getString(cursor.getColumnIndex(DownloadedDao.ARTIST_ID)));
            music.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DownloadedDao.DOWNLOAD_URL)));
            music.setTotalSize(cursor.getInt(cursor.getColumnIndex(DownloadedDao.TOTAL_SIZE)));
            int minSeconds = cursor.getInt(cursor.getColumnIndex(DownloadedDao.COMPLETE_TIME));
            music.setFinishedTime(new Date(minSeconds));
            return music;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 【二】*****************************下面是正在下载歌曲列表相关操作********************************
     **/

    /**
     * 获取正在下载的音乐个数
     **/
    public int getDownloadingMusicCount() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getDownloadingMusicCount....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(DISTINCT " + DownloadingDao.MUSIC_ID + ") as count FROM " + DownloadingDao.TABLE_NAME, null);
            Log.d(logTag, "getDownloadingMusicCount....2");
            if (cursor.moveToFirst()) {
                Log.d(logTag, "getDownloadingMusicCount....3");
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    /**
     * 当前歌曲是否正在下载
     **/
    public boolean isDownloading(int musicId, String music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "isDownloading....1");
        if (sqLiteDatabase.isOpen()) {
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) as count FROM " + DownloadingDao.TABLE_NAME +
                                " WHERE " +
                                DownloadingDao.MUSIC_ID + "=? and " +
                                DownloadingDao.MUSIC_NAME + "=?",
                        new String[]{String.valueOf(musicId), music});
                Log.d(logTag, "isDownloading....2");
                if (cursor.moveToFirst()) {
                    int threadCount = cursor.getInt(0);
                    Log.d(logTag, "isDownloading....3");
                    Cursor cursor1 = sqLiteDatabase.query(DownloadingDao.TABLE_NAME, new String[]{DownloadingDao.THREAD_COUNT},
                            DownloadingDao.MUSIC_ID + "=? and " + DownloadingDao.MUSIC_NAME + "=?",
                            new String[]{String.valueOf(musicId), music}, null, null, null);
                    Log.d(logTag, "isDownloading....4");
                    if (cursor1.moveToFirst()) {
                        int realCount = cursor1.getInt(0);
                        Log.d(logTag, "isDownloading....5");
                        if (realCount == threadCount)
                            return true;
                    }
                }
            } finally {
                Log.d(logTag, "isDownloading....6");
                sqLiteDatabase.close();
            }
        }
        Log.d(logTag, "isDownloading....7");
        return false;
    }

    /**
     * 获取正在下载音乐的分段列表
     **/
    public List<DownloadingPartMusic> getDownloadingMusic(int musicId, String music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getDownloadingMusic....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DownloadingDao.TABLE_NAME +
                            " WHERE " +
                            DownloadingDao.MUSIC_ID + "=? and " +
                            DownloadingDao.MUSIC_NAME + "=?",
                    new String[]{String.valueOf(musicId), music});
            Log.d(logTag, "getDownloadingMusic....2");
            List<DownloadingPartMusic> downloadingPartMusicList = new ArrayList<>();
            while (cursor.moveToNext()) {
                DownloadingPartMusic partMusic = parseDownloadingMusic(cursor);
                downloadingPartMusicList.add(partMusic);
            }
            Log.d(logTag, "getDownloadingMusic....3");
            cursor.close();
            sqLiteDatabase.close();
            if (downloadingPartMusicList.size() == downloadingPartMusicList.get(0).getThreadCount())
                return downloadingPartMusicList;
            Log.d(logTag, "getDownloadingMusic....4");
        }
        return null;
    }

    /**
     * 开始下载时，记录歌曲的断点下载的信息
     **/
    public void insertDownloadingInfo(List<DownloadingPartMusic> downloadingPartMusics) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "insertDownloadingInfo....1");
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.beginTransaction();
            Log.d(logTag, "insertDownloadingInfo....2");
            for (DownloadingPartMusic partMusic : downloadingPartMusics) {
                Log.d(logTag, "insertDownloadingInfo....3");
                ContentValues contentValues = new ContentValues();
                contentValues.put(DownloadingDao.MUSIC_ID, partMusic.getMusicId());
                contentValues.put(DownloadingDao.MUSIC_NAME, partMusic.getName());
                contentValues.put(DownloadingDao.ARTIST_ID, partMusic.getArtistId());
                contentValues.put(DownloadingDao.ARTIST, partMusic.getArtist());
                contentValues.put(DownloadingDao.DOWNLOAD_URL, partMusic.getDownloadUrl());
                contentValues.put(DownloadingDao.TOTAL_SIZE, partMusic.getTotalSize());
                contentValues.put(DownloadingDao.THREAD_COUNT, partMusic.getThreadCount());
                contentValues.put(DownloadingDao.THREAD_INDEX, partMusic.getThreadIndex());
                contentValues.put(DownloadingDao.BLOCK_SIZE, partMusic.getBlockSize());
                contentValues.put(DownloadingDao.COMPLETE_SIZE, partMusic.getCompletedSize());
                contentValues.put(DownloadingDao.COMPLETE_SIZE, partMusic.isCompleted() ? 1 : 0);
                sqLiteDatabase.replace(DownloadingDao.TABLE_NAME, "DOWNLOADING", contentValues);
            }
            Log.d(logTag, "insertDownloadingInfo....4");
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
            Log.d(logTag, "insertDownloadingInfo....5");
        }
    }

    /**
     * 下载过程中，修改下载状态信息
     **/
    public void updateDownloadingInfo(DownloadingPartMusic downloadingPartMusic, boolean completed) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "updateDownloadingInfo....1");
        if (sqLiteDatabase.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadingDao.COMPLETE_SIZE, downloadingPartMusic.getCompletedSize());
            contentValues.put(DownloadingDao.COMPLETED, completed ? 1 : 0);
            Log.d(logTag, "updateDownloadingInfo....2");
            sqLiteDatabase.update(DownloadingDao.TABLE_NAME, contentValues,
                    DownloadingDao.MUSIC_ID + "=? and " + DownloadingDao.THREAD_INDEX + "=?",
                    new String[]{String.valueOf(downloadingPartMusic.getMusicId()), String.valueOf(downloadingPartMusic.getThreadIndex())});
            Log.d(logTag, "updateDownloadingInfo....3");
        }
    }

    /**
     * 下载完成以后，删除正在下载信息
     **/
    public boolean deleteDownloadingInfo(int musicId, String music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deleteDownloadingInfo....1");
        if (sqLiteDatabase.isOpen()) {
            int rows = sqLiteDatabase.delete(DownloadingDao.TABLE_NAME,
                    DownloadingDao.MUSIC_ID + "=? and " + DownloadingDao.MUSIC_NAME + "=?",
                    new String[]{String.valueOf(musicId), music});
            Log.d(logTag, "deleteDownloadingInfo....2");
            sqLiteDatabase.close();
            return rows > 0;
        }
        Log.d(logTag, "deleteDownloadingInfo....3");
        return false;
    }

    private DownloadingPartMusic parseDownloadingMusic(Cursor cursor) {
        DownloadingPartMusic music = new DownloadingPartMusic();
        music.setTotalSize(cursor.getInt(cursor.getColumnIndex(DownloadingDao.TOTAL_SIZE)));
        music.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DownloadingDao.DOWNLOAD_URL)));
        music.setThreadIndex(cursor.getInt(cursor.getColumnIndex(DownloadingDao.THREAD_INDEX)));
        music.setThreadCount(cursor.getInt(cursor.getColumnIndex(DownloadingDao.THREAD_COUNT)));
        music.setName(cursor.getString(cursor.getColumnIndex(DownloadingDao.MUSIC_NAME)));
        music.setMusicId(cursor.getInt(cursor.getColumnIndex(DownloadingDao.MUSIC_ID)));
        music.setCompletedSize(cursor.getInt(cursor.getColumnIndex(DownloadingDao.COMPLETE_SIZE)));
        int flag = cursor.getInt(cursor.getColumnIndex(DownloadingDao.COMPLETED));
        music.setCompleted(flag == 1);
        music.setBlockSize(cursor.getInt(cursor.getColumnIndex(DownloadingDao.BLOCK_SIZE)));
        music.setArtist(cursor.getString(cursor.getColumnIndex(DownloadingDao.ARTIST)));
        music.setArtistId(cursor.getString(cursor.getColumnIndex(DownloadingDao.ARTIST_ID)));
        return music;
    }

    /**
     *【三】*****************************下面是已播放音乐列表相关操作********************************
     **/

    /**
     * 获取播放列表
     **/
    public List<PlayingMusic> getPlayedList() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getPlayedList....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PlayedListDao.TABLE_NAME, null);
            Log.d(logTag, "getPlayedList....2");
            List<PlayingMusic> playingMusicList = new ArrayList<>();
            while (cursor.moveToNext()) {
                PlayingMusic music = parsePlayingMusic(cursor);
                playingMusicList.add(music);
            }
            Log.d(logTag, "getPlayedList....3");
            cursor.close();
            sqLiteDatabase.close();
            return playingMusicList;
        }
        return null;
    }

    /**
     * 根据musicId获取对应的已播放音乐项
     **/
    public PlayingMusic getPlayingMusic(int musicId) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getPlayingMusic....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + PlayedListDao.TABLE_NAME +
                    " WHERE " + PlayedListDao.MUSIC_ID + "=" + String.valueOf(musicId), null);
            Log.d(logTag, "getPlayingMusic....2");
            if (cursor.moveToFirst()) {
                PlayingMusic music = parsePlayingMusic(cursor);
                Log.d(logTag, "getPlayingMusic....3");
                cursor.close();
                sqLiteDatabase.close();
                return music;
            }
        }
        Log.d(logTag, "getPlayingMusic....4");
        return null;
    }

    /**
     * 删除所有音乐播放列表
     **/
    public void deletePlayedList() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deletePlayedList....1");
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.execSQL("DELETE FROM " + PlayedListDao.TABLE_NAME);
            Log.d(logTag, "deletePlayedList....2");
            sqLiteDatabase.close();
        }
    }

    /**
     * 删除指定的音乐播放列表项
     **/
    public void deletePlayingMusic(int musicId) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deletePlayingMusic....1");
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.execSQL("DELETE FROM " + PlayedListDao.TABLE_NAME + " WHERE " + PlayedListDao.MUSIC_ID + "=" + String.valueOf(musicId));
            Log.d(logTag, "deletePlayingMusic....2");
            sqLiteDatabase.close();
        }
    }

    /**
     * 记录正在播放的音乐项
     **/
    public void insertPlayingMusic(Music music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "insertPlayingMusic....1");
        if (sqLiteDatabase.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlayedListDao.MUSIC_ID, music.getId());
            contentValues.put(PlayedListDao.MUSIC_NAME, music.getName());
            contentValues.put(PlayedListDao.ARTIST_ID, music.getArtistId());
            contentValues.put(PlayedListDao.ARTIST, music.getArtist());
            contentValues.put(PlayedListDao.PLAY_URL, music.getPlayUrl());
            contentValues.put(PlayedListDao.DURATION, music.getDuration());
            Log.d(logTag, "insertPlayingMusic....2");
            sqLiteDatabase.replace(PlayedListDao.TABLE_NAME, "PlayingMusic", contentValues);
            Log.d(logTag, "insertPlayingMusic....3");
            sqLiteDatabase.close();
        }
    }

    private PlayingMusic parsePlayingMusic(Cursor cursor) {
        PlayingMusic playingMusic = new PlayingMusic();
        playingMusic.setMusicId(cursor.getInt(cursor.getColumnIndex(PlayedListDao.MUSIC_ID)));
        playingMusic.setPlayUrl(cursor.getString(cursor.getColumnIndex(PlayedListDao.PLAY_URL)));
        playingMusic.setName(cursor.getString(cursor.getColumnIndex(PlayedListDao.MUSIC_NAME)));
        playingMusic.setDuration(cursor.getInt(cursor.getColumnIndex(PlayedListDao.DURATION)));
        playingMusic.setArtist(cursor.getString(cursor.getColumnIndex(PlayedListDao.ARTIST)));
        playingMusic.setArtistId(cursor.getString(cursor.getColumnIndex(PlayedListDao.ARTIST_ID)));
        return playingMusic;
    }

    /**
     *【四】*****************************下面是我的最爱的音乐列表相关操作********************************
     **/

    /**
     * 获取播放列表
     **/
    public List<PlayingMusic> getFavouriteList() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getFavouriteList....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + FavouriteDao.TABLE_NAME, null);
            Log.d(logTag, "getFavouriteList....2");
            List<PlayingMusic> playingMusicList = new ArrayList<>();
            while (cursor.moveToNext()) {
                PlayingMusic music = parseFavouriteMusic(cursor);
                playingMusicList.add(music);
            }
            Log.d(logTag, "getFavouriteList....3");
            cursor.close();
            sqLiteDatabase.close();
            return playingMusicList;
        }
        Log.d(logTag, "getFavouriteList....4");
        return null;
    }

    /**
     * 根据musicId获取对应的已播放音乐项
     **/
    public PlayingMusic getFavouriteMusic(int musicId) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "getFavouriteMusic....1");
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + FavouriteDao.TABLE_NAME +
                    " WHERE " + FavouriteDao.MUSIC_ID + "=" + String.valueOf(musicId), null);
            Log.d(logTag, "getFavouriteMusic....2");
            if (cursor.moveToFirst()) {
                PlayingMusic music = parsePlayingMusic(cursor);
                Log.d(logTag, "getFavouriteMusic....3");
                cursor.close();
                sqLiteDatabase.close();
                return music;
            }
        }
        return null;
    }

    /**
     * 删除所有音乐播放列表
     **/
    public void deleteFavouriteList() {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deleteFavouriteList....1");
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.execSQL("DELETE FROM " + FavouriteDao.TABLE_NAME);
            sqLiteDatabase.close();
            Log.d(logTag, "deleteFavouriteList....2");
        }
    }

    /**
     * 删除指定的音乐播放列表项
     **/
    public void deleteFavouriteMusic(int musicId) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "deleteFavouriteMusic....1");
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.execSQL("DELETE FROM " + FavouriteDao.TABLE_NAME + " WHERE " + FavouriteDao.MUSIC_ID + "=" + String.valueOf(musicId));
            sqLiteDatabase.close();
            Log.d(logTag, "deleteFavouriteMusic....2");
        }
    }

    /**
     * 记录正在播放的音乐项
     **/
    public void insertFavouriteMusic(Music music) {
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Log.d(logTag, "insertFavouriteMusic....1");
        if (sqLiteDatabase.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavouriteDao.MUSIC_ID, music.getId());
            contentValues.put(FavouriteDao.MUSIC_NAME, music.getName());
            contentValues.put(FavouriteDao.ARTIST_ID, music.getArtistId());
            contentValues.put(FavouriteDao.ARTIST, music.getArtist());
            contentValues.put(FavouriteDao.PLAY_URL, music.getPlayUrl());
            contentValues.put(FavouriteDao.DURATION, music.getDuration());
            Log.d(logTag, "insertFavouriteMusic....2");
            sqLiteDatabase.replace(FavouriteDao.TABLE_NAME, "FavouriteMusic", contentValues);
            sqLiteDatabase.close();
            Log.d(logTag, "insertFavouriteMusic....3");
        }
    }

    private PlayingMusic parseFavouriteMusic(Cursor cursor) {
        PlayingMusic playingMusic = new PlayingMusic();
        playingMusic.setMusicId(cursor.getInt(cursor.getColumnIndex(FavouriteDao.MUSIC_ID)));
        playingMusic.setPlayUrl(cursor.getString(cursor.getColumnIndex(FavouriteDao.PLAY_URL)));
        playingMusic.setName(cursor.getString(cursor.getColumnIndex(FavouriteDao.MUSIC_NAME)));
        playingMusic.setDuration(cursor.getInt(cursor.getColumnIndex(FavouriteDao.DURATION)));
        playingMusic.setArtist(cursor.getString(cursor.getColumnIndex(FavouriteDao.ARTIST)));
        playingMusic.setArtistId(cursor.getString(cursor.getColumnIndex(FavouriteDao.ARTIST_ID)));
        return playingMusic;
    }
}
