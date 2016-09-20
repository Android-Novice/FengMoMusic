package com.yuqf.fengmomusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqf.fengmomusic.base.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CommonUtils {

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if (rect != null && rect.top != 0)
            return rect.top;
        else {
            int result = 0;
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = activity.getResources().getDimensionPixelSize(resourceId);
            }
            if (result != 0)
                return result;
            else
                return 40;
        }
    }

    public static void startActivity(Context context, Class<?> tClass, List<Pair<String, String>> pairList) {
        Intent intent = new Intent(context, tClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (pairList != null)
            for (Pair<String, String> pair : pairList) {
                intent.putExtra(pair.first, pair.second);
            }
        context.startActivity(intent);
    }

    private static Toast toast;

    public static void showToast(int resId, boolean isLongTime) {
        String text = MyApplication.getContext().getResources().getString(resId);
        showToast(text, isLongTime);
    }

    public static void showToast(String text, boolean isLongTime) {
        int time = isLongTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), text, time);
        } else {
//            toast.cancel();
            toast.setDuration(time);
            toast.setText(text);
        }
        toast.show();
    }

    //the item of RecyclerView click listener
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        //click the downloading on the imagebutton of music list item
        void onItemDownloadClick(View view, int position);
    }

    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

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

    private static Bitmap decodeBitmap(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return BitmapFactory.decodeFile(filePath);
        }
        return null;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int screenWidth, int screenHeight) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        float widthRate = (float) screenWidth / bmpWidth;
        float heightRate = (float) screenHeight / bmpHeight;

        if (widthRate < heightRate)
            widthRate = heightRate;
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bmpWidth * widthRate), (int) (bmpHeight * widthRate), false);
        return bitmap;
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

    public class UrlHelper {
        public final static String Singer_Get_Base_Url = "http://artistlistinfo.kuwo.cn/";
        public final static String Singer_Head_Get_Base_Url = "http://img1.sycdn.kuwo.cn/star/starheads/";

        public final static String Ranking_Get_Base_Url = "http://qukudata.kuwo.cn/";
        public final static String Music_From_Ranking_Base_Url = "http://kbangserver.kuwo.cn/";
        public final static String Music_From_Singer_Base_Url = "http://search.kuwo.cn/";

        public final static String Music_Cover_Base_Url = "http://artistpicserver.kuwo.cn/";
        public final static String Music_File_Base_Url = "http://antiserver.kuwo.cn/";

        //        public final static String Lyric_Base_Url="http://ttlyrics.duapp.com/api/lrc/";
        public final static String Lyric_Base_Url = "http://lyrics.kugou.com/";

        public final static String Singer_Info_Base_Url = "http://search.kuwo.cn/";

    }
}
