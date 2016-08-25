package com.yuqf.fengmomusic.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

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
