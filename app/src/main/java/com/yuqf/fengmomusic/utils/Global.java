package com.yuqf.fengmomusic.utils;

public class Global {
    public static final String INTENT_TITLE_KEY = "ATitle";
    public static final String INTENT_CONTENT_KEY = "ContentId";
    public static final String INTENT_FROM_KEY = "From";
    public static final String INTENT_COVER_KEY = "Cover";

    public static final String INTENT_FROM_RANKING = "Ranking";
    public static final String INTENT_FROM_SINGER = "Singer";

    public static final String RECEIVER_ACTION = "com.yuqf.fengmo.musicplayer.receiver";
    public static final String ACTION_KEY = "BUTTON_CLICK";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_CLOSE = "CLOSE";
    public static final int NOTIFICATION_ID = 1001;

    public static final String RANKING_DEFAULT_NAME = "default_ranking";
    public static final String RANKING_DEFAULT_URL = "default/ranking.jpg";

    public static final String SINGER_DEFAULT_URL = "default/singer.jpg";

    public static final int HEADER_HEIGHT = 200;

    public static final int Round_Image_Size = 300;
    public static final int Blurred_Image_Size = 500;

    public static final String INTENT_HOT_RECOMMEND_KEY = "HotRecommend";

    public static final String INTENT_HR_ITEM_ID = "HRItem_Id";
    public static final String INTENT_HR_ITEM_NAME = "HRItem_Name";

    /*加载的音乐类型：1代表Hot Recommend；2代表本地音乐*/
    public static final String INTENT_LOAD_MUSIC_KIND = "Load_Music_Kind";
    public static final String INTENT_LOAD_HOTRECOMMEND = "1";
    public static final String INTENT_LOAD_LOCAL = "2";
}
