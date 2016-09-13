package com.yuqf.customviews.Lyrics;

/**
 * 一句歌词
 **/
public class LyricsItem {
    /**
     * 歌词内容
     **/
    private String lyric;
    /**
     * 以毫秒为单位
     **/
    private int startTime;

    public LyricsItem() {
    }

    public LyricsItem(String lyric, int startTime) {
        this.lyric = lyric;
        this.startTime = startTime;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
