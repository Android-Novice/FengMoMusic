package com.yuqf.fengmomusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuqf.fengmomusic.base.MyApplication;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "fengmo.db";
    private static MyDataBaseHelper helper;

    private final String Downloaded_Table_Create_V1 =
            "CREATE TABLE if NOT EXISTS " + DownloadedDao.TABLE_NAME + " (" +
                    DownloadedDao.MUSIC_ID + " INTEGER PRIMARY KEY, " +
                    DownloadedDao.ARTIST_ID + " TEXT, " +
                    DownloadedDao.MUSIC_NAME + " TEXT, " +
                    DownloadedDao.ARTIST + " TEXT, " +
                    DownloadedDao.COMPLETE_TIME + " INTEGER, " +
                    DownloadedDao.DOWNLOAD_URL + " TEXT, " +
                    DownloadedDao.TOTAL_SIZE + " INTEGER);";

    private final String Downloading_Table_Create_V1 =
            "CREATE TABLE if NOT EXISTS " + DownloadingDao.TABLE_NAME + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DownloadingDao.MUSIC_ID + " INTEGER, "
                    + DownloadingDao.MUSIC_NAME + " TEXT, "
                    + DownloadingDao.ARTIST_ID + " TEXT, "
                    + DownloadingDao.ARTIST + " TEXT, "
                    + DownloadingDao.DOWNLOAD_URL + " TEXT, "
                    + DownloadingDao.TOTAL_SIZE + " INTEGER, "
                    + DownloadingDao.THREAD_COUNT + " INTEGER, "
                    + DownloadingDao.THREAD_INDEX + " INTEGER, "
                    + DownloadingDao.BLOCK_SIZE + " INTEGER, "
                    + DownloadingDao.COMPLETE_SIZE + " INTEGER, "
                    + DownloadingDao.COMPLETED + " INTEGER);";

    private final String PlayedList_Table_Create_V1 =
            "CREATE TABLE if NOT EXISTS " + PlayedListDao.TABLE_NAME + " ("
                    + PlayedListDao.MUSIC_ID + " INTEGER PRIMARY KEY, "
                    + PlayedListDao.MUSIC_NAME + " TEXT, "
                    + PlayedListDao.ARTIST_ID + " TEXT, "
                    + PlayedListDao.ARTIST + " TEXT, "
                    + PlayedListDao.PLAY_URL + " TEXT, "
                    + PlayedListDao.DURATION + " INTEGER);";

    private final String Favourite_Table_Create_V1 =
            "CREATE TABLE if NOT EXISTS " + FavouriteDao.TABLE_NAME + " ("
                    + FavouriteDao.MUSIC_ID + " INTEGER PRIMARY KEY, "
                    + FavouriteDao.MUSIC_NAME + " TEXT, "
                    + FavouriteDao.ARTIST_ID + " TEXT, "
                    + FavouriteDao.ARTIST + " TEXT, "
                    + FavouriteDao.PLAY_URL + " TEXT, "
                    + FavouriteDao.DURATION + " INTEGER);";

    public static MyDataBaseHelper getInstance() {
        if (helper == null) {
            helper = new MyDataBaseHelper(MyApplication.getContext(), DB_NAME, null, DB_VERSION);
        }
        return helper;
    }

    private MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Downloaded_Table_Create_V1);
        db.execSQL(Downloading_Table_Create_V1);
        db.execSQL(PlayedList_Table_Create_V1);
        db.execSQL(Favourite_Table_Create_V1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {

        }
        if (oldVersion < 3) {

        }
        //....
    }
}
