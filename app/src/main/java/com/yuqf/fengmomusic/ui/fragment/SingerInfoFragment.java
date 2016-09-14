package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.ui.entity.GSonSinger;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingerInfoFragment extends Fragment {

    private String artist;
    private int artistId;
    private View rootView;

    public SingerInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_singer_info, container, false);
        return rootView;
    }

    public void showSingerInfo(String artist, int artistId) {
        if (this.artistId == artistId && this.artist == artist) return;
        this.artistId = artistId;
        this.artist = artist;
        if (rootView != null) {
            showInfo();
        }
    }

    private void showInfo() {
        String json = CommonUtils.readSingerInfoJson(artist, artistId);
        if (!TextUtils.isEmpty(json)) {
            parseJson(json);
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Singer_Info_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.SingerService service = retrofit.create(RetrofitServices.SingerService.class);
        Call<ResponseBody> call = service.getSingerInfo(artistId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            parseJson(json);
                            CommonUtils.saveSingerInfoJson(artist, artistId, json);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //异常处理
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //异常处理
            }
        });
    }

    private void parseJson(String json) {
        Gson gson = new Gson();
        GSonSinger singer = gson.fromJson(json, GSonSinger.class);
        updateViews(singer);
    }

    private void updateViews(GSonSinger singer) {

    }
}
