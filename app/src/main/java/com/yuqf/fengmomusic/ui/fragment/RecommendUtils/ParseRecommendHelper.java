package com.yuqf.fengmomusic.ui.fragment.RecommendUtils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.ui.activity.MusicListActivity;
import com.yuqf.fengmomusic.ui.activity.WebBrowserActivity;
import com.yuqf.fengmomusic.ui.entity.GSonFocusPictureList;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.ui.entity.GSonNewDiscItem;
import com.yuqf.fengmomusic.ui.entity.GsonPersonalRecommendationItem;
import com.yuqf.fengmomusic.ui.entity.GsonStarActivityList;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuqf on 2017/1/4.
 */
public class ParseRecommendHelper {
    /*Json通用结束标志*/
    private final String END_FLAG = "}]}";
    /*活动Json开始标志*/
    private final String HUODONG_FLAG = "huoDong";
    /*原创精选Json开始标志*/
    private final String ORIGINAL_Str = "original";
    /*新碟上架的Json开始标志*/
    private final String NEWDISC_FLAG = "newDiscShelves";
    /*图片轮播的Json开始标志*/
    private final String FOCUSPICTURE_FLAG = "focusPicture";
    /*推荐播放列表Json开始标志*/
    private final String PLAYLIST_FLAG = "playList";
    /*魔性列表JSon开始标志*/
    private final String SPECIAL_FLAG = "specialColumn";
    /*小美或者老王推歌的URL开头*/
    private final String XIAOMEI_LAOWANG_FLAG = "http://album.kuwo.cn/album/h/mbox?id=";
    /*这里是因为为了和主活动中第三个tab页的json解析通用，所以把Json补充一下，保持json格式一致*/
    private final String POPULAR_JSON_START_STR = "{\"data\":{\"total\":16,\"rn\":100,\"pn\":1,\"playList\":";
    private final String POPULAR_JSON_END_STR = "},\"msg\":\"成功\",\"msgs\":null,\"status\":200}";

    private GSonFocusPictureList focusPictureList;
    private List<Music> musicList;
    private String newCoverUrl;
    private List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItemList;
    /*明星活动*/
    private List<GsonStarActivityList.StartActivity> starActivities;
    /*个性化推荐列表*/
    private String personalRecommendJsonStr;
    private List<GsonPersonalRecommendationItem> personalRecommendationItems;
    private List<GSonNewDiscItem> newDiscItemList;
    private static ParseRecommendHelper instance;

    public static ParseRecommendHelper getInstance() {
        if (instance == null) {
            instance = new ParseRecommendHelper();
        }
        return instance;
    }

    public void parseRecommendInfo(String jsonStr) {
        int focusPicStartIndex = jsonStr.indexOf(FOCUSPICTURE_FLAG);
        if (focusPicStartIndex > 0) {
            int focusPicEndIndex = jsonStr.indexOf(END_FLAG, focusPicStartIndex);
            String focusPicJson = jsonStr.substring(focusPicStartIndex + FOCUSPICTURE_FLAG.length() + 2, focusPicEndIndex + END_FLAG.length());
            Log.d("parseRecommendInfo", "====FocusPic: \n" + focusPicJson);
            GsonBuilder builder = new GsonBuilder();
            focusPictureList = builder.create().fromJson(focusPicJson, GSonFocusPictureList.class);
        }
    }

    public void parseNewDiscInfo(String jsonStr) {
        int newDiscStartIndex = jsonStr.indexOf(NEWDISC_FLAG);
        if (newDiscStartIndex > 0) {
            int newDiscEndIndex = jsonStr.indexOf(END_FLAG, newDiscStartIndex);
            String newDiscFullJson = jsonStr.substring(newDiscStartIndex + NEWDISC_FLAG.length() + 2, newDiscEndIndex + END_FLAG.length());
            Log.d("parseNewDiscInfo", "=====NewDisc:\n" + newDiscFullJson);
            String newDiscListJson = newDiscFullJson.substring(newDiscFullJson.indexOf("list") + 4 + 2, newDiscFullJson.length() - 1);
            GsonBuilder builder = new GsonBuilder();
            Type type = new TypeToken<ArrayList<GSonNewDiscItem>>() {
            }.getType();
            newDiscItemList = builder.create().fromJson(newDiscListJson, type);
        }
    }

    public void parsePopularInfo(String jsonStr) {
        int popularStartIndex = jsonStr.indexOf(PLAYLIST_FLAG);
        if (popularStartIndex > 0) {
            int popularEndIndex = jsonStr.indexOf(END_FLAG, popularStartIndex);
            String popularJson = jsonStr.substring(popularStartIndex + PLAYLIST_FLAG.length() + 2, popularEndIndex + END_FLAG.length());
            Log.d("parseRecommendInfo", "====popularJson: \n" + popularJson);

            GsonBuilder builder = new GsonBuilder();
            GSonHotRecommend popularItem = builder.create().fromJson(POPULAR_JSON_START_STR + popularJson + POPULAR_JSON_END_STR, GSonHotRecommend.class);
            hotRecommendItemList = popularItem.getData().getPlayList().getList();
        }
    }

    public void parsePersonalRecommendList(String jsonStr) {
        Log.d("PersonalRecommend", jsonStr.substring(jsonStr.length() - 100));
        Log.d("PersonalRecommend", jsonStr);
        this.personalRecommendJsonStr = jsonStr;
        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<ArrayList<GsonPersonalRecommendationItem>>() {
        }.getType();
        personalRecommendationItems = builder.create().fromJson(personalRecommendJsonStr, type);
    }

    public void parseStarActivityList(String jsonStr) {
        int activityStartIndex = jsonStr.indexOf(HUODONG_FLAG);
        if (activityStartIndex > 0) {
            int activityEndIndex = jsonStr.indexOf(END_FLAG, activityStartIndex);
            String activityJson = jsonStr.substring(activityStartIndex + HUODONG_FLAG.length() + 2, activityEndIndex + END_FLAG.length());
            GsonBuilder builder = new GsonBuilder();
            GsonStarActivityList activityList = builder.create().fromJson(activityJson, GsonStarActivityList.class);
            starActivities = activityList.getList();
        }
    }

    public List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> getHotRecommendItemList() {
        return hotRecommendItemList;
    }

    public List<GsonStarActivityList.StartActivity> getStarActivities() {
        return starActivities;
    }

    public List<GsonPersonalRecommendationItem> getPersonalRecommendationItems() {
        return personalRecommendationItems;
    }

    public GSonFocusPictureList getFocusPictureList() {
        return focusPictureList;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public String getNewCoverUrl() {
        return newCoverUrl;
    }

    public void skipPageWhenBannerClick(GSonFocusPictureList.FocusPictureItem curClickedItem) {
//        if (curClickedItem.getNodeId() == 13) {
//
//        } else {
//            Log.d("FocusPictureItem", curClickedItem.getSourceId());
//            if (curClickedItem.getSourceId().contains(XIAOMEI_LAOWANG_FLAG)) {
//                showXiaoMeiOrLaoWangTuiGe(curClickedItem);
//            } else {
//
//            }
//        }
        int source = curClickedItem.getSource();
        switch (source) {
            case 11:
            case 17:
                showRecommendWebViewActivity(curClickedItem);
                break;
            case 13:
                showAlbumMusic(curClickedItem);
                break;
            case 21:
                if (curClickedItem.getSourceId().contains(XIAOMEI_LAOWANG_FLAG)) {
                    showXiaoMeiOrLaoWangTuiGe(curClickedItem);
                } else {
                    Log.e("FocusPictureItem", "parse FocusPictureItem Error, Source: " + curClickedItem.getSource() + "\nsourceId: " + curClickedItem.getSourceId());
                }
                break;
            default:
                Log.e("FocusPictureItem", "unknown FocusPictureItem, Source: " + curClickedItem.getSource() + "\nsourceId: " + curClickedItem.getSourceId());
                break;
        }
    }

    private void showAlbumMusic(GSonFocusPictureList.FocusPictureItem curItem) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Global.INTENT_FROM_KEY, Global.INTENT_FROM_ALBUM);
        map.put(Global.INTENT_TITLE_KEY, curItem.getName());
        map.put(Global.INTENT_CONTENT_KEY, curItem.getSourceId());
        map.put(Global.INTENT_COVER_KEY, curItem.getPic());
        CommonUtils.startActivity(MyApplication.getContext(), MusicListActivity.class, map);
    }

    /*轮播项跳转（格式是http://album.kuwo.cn/album/h/mbox?id=这种），小美推歌或者老王推歌*/
    private void showXiaoMeiOrLaoWangTuiGe(GSonFocusPictureList.FocusPictureItem curItem) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Global.INTENT_FROM_KEY, Global.INTENT_FROM_RECOMMEND);
        map.put(Global.INTENT_TITLE_KEY, curItem.getName());
        map.put(Global.INTENT_CONTENT_KEY, curItem.getSourceId());
        map.put(Global.INTENT_COVER_KEY, curItem.getPic());
        CommonUtils.startActivity(MyApplication.getContext(), MusicListActivity.class, map);
    }

    private void showRecommendWebViewActivity(GSonFocusPictureList.FocusPictureItem curItem) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Global.INTENT_TITLE_KEY, curItem.getName());
        map.put(Global.INTENT_CONTENT_KEY, curItem.getSourceId());
        CommonUtils.startActivity(MyApplication.getContext(), WebBrowserActivity.class, map);
    }

    public void downloadHtmlContent(final String contentUrl, final RecommendInfoLoadedListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean success = false;
                try {
                    URL url = new URL(contentUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Accept-Encoding", "identity");
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream stream = connection.getInputStream();
                        OutputStream outputStream = new ByteArrayOutputStream();
                        byte[] tempBytes = new byte[2048];
                        int length;
                        while ((length = stream.read(tempBytes)) > 0) {
                            outputStream.write(tempBytes, 0, length);
                        }

                        String html = outputStream.toString();
                        if (musicList == null)
                            musicList = new ArrayList<>();
                        musicList.clear();
                        newCoverUrl = findBitmapUrl(html);
                        musicList = findMusicList(html);
                        outputStream.close();
                        stream.close();
                        connection.disconnect();
                        success = musicList.size() > 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onLoadCompleted(success);
                }
            }
        }).start();
    }

    private String findBitmapUrl(String html) {
        String url;
        while (true) {
            int index = html.indexOf("<div class=\"jdxPs\"");
            html = html.substring(index);
            if (index > 0) {
                String startStr = "<img src=\"";
                int startIndex = html.indexOf(startStr);
                if (startIndex > 0) {
                    startIndex = startIndex + startStr.length();
                    int endIndex = html.indexOf("\"", startIndex);
                    if (endIndex > 0) {
                        url = html.substring(startIndex, endIndex);
                        Log.d("FocusPictureItem", "Image Url: " + url);
                        if (!TextUtils.isEmpty(url)) {
                            return url;
                        }
                    }
                }
            } else
                break;
        }
        return "";
    }

    private List<Music> findMusicList(String html) {
        List<Music> musicList = new ArrayList<>();
        String startFlag = "<ul c-id=\"data-list\" class";
        int startIndex = html.indexOf(startFlag);
        html = html.substring(startIndex);
        String musicIntroduceFlag = "<div class=\"music_list_tip\"";
        int curStartIndex = 0;
        while (true) {
            startIndex = html.indexOf(startFlag, curStartIndex);
            int nextIndex = html.indexOf(startFlag, startIndex + startFlag.length());
            if (startIndex >= 0) {
                Music music = parseMusic(html, startIndex);
                int introduceStartIndex = html.indexOf(musicIntroduceFlag, startIndex);
                if (introduceStartIndex > 0 && ((nextIndex > 0 && introduceStartIndex < nextIndex) || (nextIndex < 0))) {
                    int pStartIndex = html.indexOf("<p>", introduceStartIndex);
                    int pEndIndex = html.indexOf("</p>", pStartIndex);
                    if (pStartIndex > 0 && pStartIndex < pEndIndex) {
                        String introduce = html.substring(pStartIndex + 3, pEndIndex);
                        Log.d("FocusPictureItem", "====================\nintroduce: " + introduce);
                        music.setMusicDescribe(introduce);
                    }
                }
                musicList.add(music);
                if (nextIndex >= 0)
                    curStartIndex = nextIndex;
                else
                    break;
            } else
                break;
        }
        return musicList;
    }

    private Music parseMusic(String html, int startIndex) {
        int idStartIndex = html.indexOf("data-rid=\"", startIndex);
        int idEndIndex = html.indexOf("\"", idStartIndex + 10);

        int infoStartIndex = html.indexOf("title=\"", idEndIndex);
        int infoEndIndex = html.indexOf("\"", infoStartIndex + 7);

        Music music = new Music();
        String idStr = html.substring(idStartIndex + 10, idEndIndex);
        String info = html.substring(infoStartIndex + 7, infoEndIndex).replace("&#13;", "");
        int nameStartIndex = info.indexOf("歌名：");
        int nameEndIndex = info.indexOf("歌手：", nameStartIndex);
        int singerEndIndex = info.indexOf("专辑：", nameEndIndex);
        int albumEndIndex = info.indexOf("来源：", singerEndIndex);
        String name = info.substring(nameStartIndex + 3, nameEndIndex).trim();
        String singer = info.substring(nameEndIndex + 3, singerEndIndex).trim();
        String album = info.substring(singerEndIndex + 3, albumEndIndex).trim();
        Log.d("FocusPictureItem", "====================\nid: " + idStr + "\nname: " + name + "\nsinger: " + singer + "\nalbum: " + album);
        music.setId(Integer.parseInt(idStr));
        music.setName(name);
        music.setArtist(singer);
        music.setAlbum(album);
        return music;
    }
}
