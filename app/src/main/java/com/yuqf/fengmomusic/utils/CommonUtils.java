package com.yuqf.fengmomusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqf.fengmomusic.base.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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

    public static void startActivity(Context context, Class<?> tClass, HashMap<String, String> hashMap) {
        Intent intent = new Intent(context, tClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (hashMap != null)
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        context.startActivity(intent);
    }

    private static Toast toast = null;

    public static void showToast(int resId, boolean isLongTime) {
        String text = MyApplication.getContext().getResources().getString(resId);
        showToast(text, isLongTime);
    }

    public static void showToast(String text, boolean isLongTime) {
        Log.d("Show_Toast", text + "\n=======================");
        int time = isLongTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), text, time);
        } else {
            toast.setDuration(time);
            toast.setText(text);
        }
        toast.show();
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

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     *  根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *  根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /*
    * 根据图片地址下载图片，并显示到ImageView
    * */
    public static void downloadPic(String picUrl, final ImageView imageView) {
        new AsyncTask<String, Void, File>() {
            @Override
            protected File doInBackground(String... params) {
                String filePath = FileUtils.getHotRecommendCoverPath(params[0]);
                File file = new File(filePath);
                if (!file.exists()) {
                    HttpURLConnection connection = null;
                    try {
                        FileOutputStream fileStream = new FileOutputStream(file);

                        URL url = new URL(params[0]);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(10000);
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream stream = connection.getInputStream();

                            byte[] bytes = new byte[1024];
                            int len;
                            while ((len = stream.read(bytes)) != -1) {
                                fileStream.write(bytes, 0, len);
                            }
                            stream.close();
                            fileStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                    }
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
                imageView.setImageURI(Uri.fromFile(file));
            }
        }.execute(picUrl);
    }
}
