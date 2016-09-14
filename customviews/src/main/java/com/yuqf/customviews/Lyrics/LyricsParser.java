package com.yuqf.customviews.Lyrics;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class LyricsParser {
    private static final String LEFT_TAG = "\\[";
    private static final String RIGHT_TAG = "]";

    public static List<LyricsItem> getLyrics(String content) {
        List<LyricsItem> lyricsItems = new ArrayList<>();
        content = content.replace("\r\n", "");
        if (!TextUtils.isEmpty(content)) {
            String[] lyricArr = content.split(LEFT_TAG);
            for (String curItem : lyricArr) {
                if (TextUtils.isEmpty(curItem)) continue;
                int timeEndIndex = curItem.indexOf(RIGHT_TAG);
                if (timeEndIndex <= 0) {
                    LyricsItem item = new LyricsItem(curItem, -1);
                    lyricsItems.add(item);
                } else {
                    String curTimeStr = curItem.substring(0, timeEndIndex);
                    String curContent = curItem.substring(timeEndIndex + 1);
                    LyricsItem item = getLyricsItem(curTimeStr, curContent);
//                    item.setLyric(String.valueOf(lyricsItems.size()) + "------" + item.getLyric());
                    lyricsItems.add(item);
                }
            }
        }
        return lyricsItems;
    }

    private static LyricsItem getLyricsItem(String curTime, String curContent) {
        LyricsItem item = new LyricsItem();
        item.setLyric(curContent);
        item.setStartTime(getTime(curTime));
//        Log.d("LyricsParser", "curTime:" + String.valueOf(item.getStartTime()) + "\ncontent: " + item.getLyric());
        return item;
    }

    private static int getTime(String curTime) {
        String[] timeArr = curTime.split(":");
        String hourStr = "0";
        String minStr;
        String secondStr;
        if (timeArr.length == 3) {
            hourStr = timeArr[0];
            minStr = timeArr[1];
            secondStr = timeArr[2];
        } else {
            minStr = timeArr[0];
            secondStr = timeArr[1];
        }

        int hour = Integer.parseInt(hourStr) * 60 * 60 * 1000;
        int min = Integer.parseInt(minStr) * 60 * 1000;
        int second = (int) (Float.parseFloat(secondStr) * 1000);
        return hour + min + second;
    }
}
