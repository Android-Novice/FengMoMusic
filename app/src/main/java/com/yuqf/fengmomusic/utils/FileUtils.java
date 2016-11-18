package com.yuqf.fengmomusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yuqf.fengmomusic.base.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class FileUtils {

    public static void saveMusicCover(Bitmap bitmap, int musicId, int size) {
        if (bitmap != null) {
            String filePath = getMusicCoverPath(musicId, size);
            saveBitmap(bitmap, filePath);
        }
    }

    public static Bitmap getMusicCover(int musicId, int size) {
        String filePath = getMusicCoverPath(musicId, size);
        return decodeBitmap(filePath);
    }

    private static String getMusicCoverPath(int musicId, int size) {
        String dirPath = getSRDirPath();
        String name = String.valueOf(size) + ".jpgm";
        String filePath = dirPath + "/Music_Cover/" + String.valueOf(musicId) + "/" + name;
        return filePath;
    }

    public static void saveSingerHead(Bitmap bitmap, String url) {
        if (bitmap != null) {
            String filePath = getSingerPath(url, false);
            saveBitmap(bitmap, filePath);
        }
    }

    public static Bitmap getSingerHead(String url) {
        String filePath = getSingerPath(url, false);
        return decodeBitmap(filePath);
    }

    public static void saveRankingCover(Bitmap bitmap, String rankingName, String url) {
        if (bitmap != null) {
            String filePath = getRankingPath(rankingName, url, false);
            saveBitmap(bitmap, filePath);
        }
    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        if (bitmap != null) {
            final File file = new File(filePath);
            String parentPath = file.getParent();
            File parentDir = new File(parentPath);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            final Bitmap savingBmp = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final OutputStream stream = new FileOutputStream(file);
                        savingBmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static String getRankingPath(String rankingName, String url, boolean isBlurred) {
        String webJpgName = url.substring(url.lastIndexOf("/") + 1);
        String dirPath = getSRDirPath();
        String suffix = isBlurred ? "blur" : "r";
        String filePath = dirPath + "/" + rankingName + "/" + webJpgName + suffix;
        return filePath;
    }

    private static String getSingerPath(String url, boolean isBlurred) {
        String dirPath = getSRDirPath();
        String suffix = isBlurred ? "blur" : "s";
        String filePath = dirPath + "/" + url + suffix;
        return filePath;
    }

    public static Bitmap getRankingCover(String rankingName, String url) {
        String filePath = getRankingPath(rankingName, url, false);
        return decodeBitmap(filePath);
    }

    /**
     * get singer head Or Ranking cover saved path
     **/
    public static String getSRDirPath() {
        String filePath = "";
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()) {
            filePath = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        }
        if (TextUtils.isEmpty(filePath))
            filePath = MyApplication.getContext().getFilesDir().getPath();
        return filePath;
    }

    public static void saveBlurredSingerBitmap(Bitmap bitmap, String url) {
        String filePath = getSingerPath(url, true);
        saveBitmap(bitmap, filePath);
    }

    public static void saveBlurredRankingBitmap(Bitmap bitmap, String rankingName, String url) {
        String filePath = getRankingPath(rankingName, url, true);
        saveBitmap(bitmap, filePath);
    }

    public static Bitmap getBlurredSingerBitmap(String url) {
        String filePath = getSingerPath(url, true);
        return decodeBitmap(filePath);
    }

    public static Bitmap getBlurredRankingBitmap(String rankingName, String url) {
        String filePath = getRankingPath(rankingName, url, true);
        return decodeBitmap(filePath);
    }

    @Nullable
    private static Bitmap decodeBitmap(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return BitmapFactory.decodeFile(filePath);
        }
        return null;
    }

    public static void saveLyric(String artist, String name, int musicId, String content) {
        String lrcFile = getLyricPath(artist, name, musicId);
        saveTextToDevice(content, lrcFile);
//        try {
//            FileOutputStream stream = new FileOutputStream(lrcFile);
//            OutputStreamWriter writer = new OutputStreamWriter(stream);
//            writer.write(content, 0, content.length());
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static String readLyric(String artist, String name, int musicId) {
        String lrcFile = getLyricPath(artist, name, musicId);
        return readTextFromDevice(lrcFile);
    }

    private static String getLyricPath(String artist, String name, int musicId) {
        String rootPath = getDocumentRootPath();
        String lyricDir = rootPath + "/" + artist + "/" + name;
        File parentDir = new File(lyricDir);
        if (!parentDir.exists())
            parentDir.mkdirs();
        return parentDir + "/" + String.valueOf(musicId) + ".lrc";
    }

    public static void saveSingerInfoJson(String artist, String artistId, String json) {
        String jsonPath = getSingerJsonPath(artist, artistId);
        saveTextToDevice(json, jsonPath);
    }

    public static String readSingerInfoJson(String artist, String artistId) {
        String jsonPath = getSingerJsonPath(artist, artistId);
        return readTextFromDevice(jsonPath);
    }

    private static String getSingerJsonPath(String artist, String artistId) {
        String rootPath = getDocumentRootPath();
        String jsonDir = rootPath + "/" + artist;
        File parentDir = new File(jsonDir);
        if (!parentDir.exists())
            parentDir.mkdirs();
        return jsonDir + "/" + String.valueOf(artistId) + ".json";
    }

    @Nullable
    private static String readTextFromDevice(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        try {
            FileInputStream stream = new FileInputStream(filePath);
            if (stream != null) {
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveTextToDevice(String content, String filePath) {
        try {
            FileOutputStream stream = new FileOutputStream(filePath);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(content, 0, content.length());
            writer.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDocumentRootPath() {
        String filePath = "";
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()) {
            filePath = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();
        }
        if (TextUtils.isEmpty(filePath))
            filePath = MyApplication.getContext().getFilesDir().getPath();
        return filePath;
    }

    public static String getMusicPath(String music, String artist) {
        String filePath = getMusicRootPath();
        artist = artist.replace("&", "+");
        File fileDir = new File(filePath);
        music = FileUtils.removeFileNameInvalidChars(music);
        if (!fileDir.exists())
            fileDir.mkdirs();
        return String.format(Locale.getDefault(), "%s/%s-%s.mp3", filePath, artist, music);
    }

    private static String getMusicRootPath() {
        String filePath = "";
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()) {
            filePath = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath();
        }
        if (TextUtils.isEmpty(filePath))
            filePath = MyApplication.getContext().getFilesDir().getPath();
        return filePath;
    }

    public static String removeFileNameInvalidChars(String fileName) {
        fileName = removeFileNameInvalidChar(fileName, "(", ")");
        fileName = removeFileNameInvalidChar(fileName, "[", "]");
        return fileName;
    }

    private static String removeFileNameInvalidChar(String fileName, String leftChar, String rightChar) {
        if (fileName.contains(leftChar) && fileName.contains(rightChar)) {
            int leftIndex = fileName.indexOf(leftChar);
            int rightIndex = fileName.indexOf(rightChar);
            if (leftIndex < rightIndex) {
                String redundantStr = fileName.substring(leftIndex, rightIndex + 1);
                fileName = fileName.replace(redundantStr, "");
            }
            if (fileName.endsWith("-"))
                fileName = fileName.substring(0, fileName.length() - 1);
        }
        return fileName;
    }

    public static String getHotRecommendCoverPath(String picUrl) {
//        String extension = picUrl.substring(picUrl.lastIndexOf("."));
        String md5Name = CommonUtils.md5(picUrl);
        String rootPath = getSRDirPath() + "/" + "HotRecommend";
        File file = new File(rootPath);
        if (!file.exists())
            file.mkdirs();

        String fileName = rootPath + "/" + md5Name;
        return fileName;
    }
}
