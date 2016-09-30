package com.yuqf.fengmomusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqf.fengmomusic.base.MyApplication;

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
}
