package com.yuqf.fengmomusic.utils;

import android.app.Activity;
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

import com.yuqf.fengmomusic.base.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public static void startActivity(Activity activity, Class<?> tClass, List<Pair<String, String>> pairList) {
        Intent intent = new Intent(activity, tClass);
        for (Pair<String, String> pair : pairList) {
            intent.putExtra(pair.first, pair.second);
        }
        activity.startActivity(intent);
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

    private static Bitmap decodeBitmap(String filePath){
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

    public class UrlHelper {
        public final static String Singer_Get_Base_Url = "http://artistlistinfo.kuwo.cn/";
        public final static String Singer_Head_Get_Base_Url = "http://img1.sycdn.kuwo.cn/star/starheads/";

        public final static String Ranking_Get_Base_Url = "http://qukudata.kuwo.cn/";
        public final static String Music_From_Ranking_Base_Url = "http://kbangserver.kuwo.cn/";
        public final static String Music_From_Singer_Base_Url = "http://search.kuwo.cn/";

        public final static String Music_Cover_Base_Url = "http://artistpicserver.kuwo.cn/";
        public final static String Music_File_Base_Url = "http://antiserver.kuwo.cn/";
    }
}
