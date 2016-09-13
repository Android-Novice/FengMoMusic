package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuqf.customviews.LyricsView;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.ui.entity.GSonLyric;
import com.yuqf.fengmomusic.ui.entity.GsonLyricList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicLyricFragment extends Fragment {

    private View rootView;
    private Music music;
    private View loadingContent;
    private TextView lyricStatusTV;
    private LyricsView lyricTV;
    private final String logTag = "MusicLyricFragment";
    private String artist;
    private String name;


    public MusicLyricFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_music_lyric, container, false);
            loadingContent = rootView.findViewById(R.id.no_lyric_content);
            lyricStatusTV = (TextView) rootView.findViewById(R.id.lyric_status_tv);
            lyricTV = (LyricsView) rootView.findViewById(R.id.lyric_tv);
        } else {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
        return rootView;
    }

    public void notifyPlayedDuration() {
        lyricTV.setPlayingIndex(music.getPlayedPosition());
    }

    public void nextMusicLyric() {
        showLoadState(0);
    }

    public void showLyric(Music music) {
        this.music = music;
        if (rootView == null) return;
        showLoadState(0);

        artist = music.getArtist();
        if (artist.contains("&")) {
            String[] array = artist.split("&");
            if (array.length > 2)
                artist = array[0];
        }
        name = music.getName();
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf("("));
            if (name.endsWith("-"))
                name = name.substring(0, name.length() - 1);
        }
        String lyric = CommonUtils.readLyric(artist, name, music.getId());
        if (!TextUtils.isEmpty(lyric)) {
            showLoadState(2);
            lyricTV.setText(lyric);
            return;
        }
        String key = name + " " + artist;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Lyric_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final RetrofitServices.LyricService lyricService = retrofit.create(RetrofitServices.LyricService.class);
        Call<GsonLyricList> call = lyricService.getLyricList(key, music.getDuration() * 1000);
        call.enqueue(new Callback<GsonLyricList>() {
            @Override
            public void onResponse(Call<GsonLyricList> call, Response<GsonLyricList> response) {
                if (response.isSuccessful()) {
                    GsonLyricList lyricList = response.body();
                    if (lyricList != null && lyricList.getCandidates().size() > 0) {
                        getLyrics(lyricList.getCandidates(), 0, lyricService, true);
                        return;
                    }
                }
                showLoadState(1);
            }

            @Override
            public void onFailure(Call<GsonLyricList> call, Throwable t) {
                showLoadState(1);
            }
        });
    }

    /**
     * 0:正在加载，1：加载失败；2：加载完成
     **/
    private void showLoadState(int status) {
        switch (status) {
            case 0:
                loadingContent.setVisibility(View.VISIBLE);
                lyricStatusTV.setText(R.string.finding_lyric);
                lyricTV.setVisibility(View.INVISIBLE);
                break;
            case 1:
                loadingContent.setVisibility(View.VISIBLE);
                lyricStatusTV.setText(R.string.no_found_lyric);
                lyricTV.setVisibility(View.INVISIBLE);
                break;
            case 2:
                loadingContent.setVisibility(View.INVISIBLE);
                lyricTV.setText(null);
                lyricTV.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getLyrics(List<GsonLyricList.LyricInfo> lyricInfos, int position, RetrofitServices.LyricService lyricService, boolean sameDuration) {
        if (position >= lyricInfos.size()) {
            getLyrics(lyricInfos, 0, lyricService, false);
            return;
        }
        GsonLyricList.LyricInfo lyricInfo = lyricInfos.get(position);
        String song = lyricInfo.getSong();
        String name = music.getName();
        String singer = lyricInfo.getSinger();
        final String artist = music.getArtist();
        int songDuration = lyricInfo.getDuration();
        int musicDuration = music.getDuration() * 1000;
        String printStr = "Position:%d\nSong:%s\nname:%s\nsinger:%s\nartist:%s\nDuration1:%d\nDuration2:%d\n";
        Log.d(logTag, String.format(printStr, position, song, name, singer, artist, songDuration, musicDuration));
        boolean compareDuration = (Math.abs(musicDuration - songDuration) <= 1000);
        if (!sameDuration)//不需要相同时长
            compareDuration = true;
        if ((song == name || name.contains(song) || song.contains(name)) && compareDuration) {
            final List<GsonLyricList.LyricInfo> lyricInfos1 = lyricInfos;
            final int position1 = position + 1;
            final RetrofitServices.LyricService lyricService1 = lyricService;
            final boolean sameDuration1 = sameDuration;
            Call<GSonLyric> call = lyricService.getLyric(lyricInfo.getId(), lyricInfo.getAccesskey());
            call.enqueue(new Callback<GSonLyric>() {
                @Override
                public void onResponse(Call<GSonLyric> call, Response<GSonLyric> response) {
                    if (response.isSuccessful()) {
                        GSonLyric lyric = response.body();
                        if (lyric.getStatus() == 200 && lyric.getContent().length() > 0) {
                            String content = decryptBASE64(lyric.getContent());
                            if (!TextUtils.isEmpty(content)) {
                                showLoadState(2);
                                lyricTV.setText(content);
                                CommonUtils.saveLyric(artist, MusicLyricFragment.this.name, music.getId(), content);
                                return;
                            }
                        }
                    }
                    if (!sameDuration1)
                        showLoadState(1);
                    else
                        getLyrics(lyricInfos1, position1, lyricService1, true);
                }

                @Override
                public void onFailure(Call<GSonLyric> call, Throwable t) {
                    if (!sameDuration1)
                        showLoadState(1);
                    else
                        getLyrics(lyricInfos1, position1, lyricService1, true);
                }
            });
        } else {
            if (!sameDuration)
                showLoadState(1);
            else
                getLyrics(lyricInfos, position + 1, lyricService, true);
        }
    }

    /**
     * BASE64 解密
     *
     * @param str
     * @return
     */
    public static String decryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return new String(Base64.decode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
