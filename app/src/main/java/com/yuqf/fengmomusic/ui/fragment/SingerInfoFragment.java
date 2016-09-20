package com.yuqf.fengmomusic.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuqf.customviews.RoundImageView;
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

    private final String logTag = "SingerInfoFragment";
    private String artist;
    private String artistId;
    private View rootView;
    private TextView nameTV;
    private TextView genderTV;
    private TextView otherNameTV;
    private TextView countryTV;
    private TextView weightTV;
    private TextView tallTV;
    private TextView birthPlaceTV;
    private TextView birthdayTV;
    private TextView languageTV;
    private TextView constellationTV;
    private TextView singerDetailTV;
    private GSonSinger gSonSinger;
    private View detailContent;
    private View noDetailContent;
    private RoundImageView headerIV;

    public SingerInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(logTag, "1===============");
        if (rootView == null) {
            Log.d(logTag, "1===============2");
            rootView = inflater.inflate(R.layout.fragment_singer_info, container, false);
            nameTV = (TextView) rootView.findViewById(R.id.name_tv);
            genderTV = (TextView) rootView.findViewById(R.id.gender_tv);
            otherNameTV = (TextView) rootView.findViewById(R.id.other_name_tv);
            countryTV = (TextView) rootView.findViewById(R.id.country_tv);
            weightTV = (TextView) rootView.findViewById(R.id.weight_tv);
            tallTV = (TextView) rootView.findViewById(R.id.tall_tv);
            birthdayTV = (TextView) rootView.findViewById(R.id.birthday_tv);
            birthPlaceTV = (TextView) rootView.findViewById(R.id.birth_place_tv);
            languageTV = (TextView) rootView.findViewById(R.id.language_tv);
            constellationTV = (TextView) rootView.findViewById(R.id.constellation_tv);
            singerDetailTV = (TextView) rootView.findViewById(R.id.singer_detail_tv);
            detailContent = rootView.findViewById(R.id.detail_content);
            noDetailContent = rootView.findViewById(R.id.no_detail_content);
            headerIV = (RoundImageView) rootView.findViewById(R.id.round_image_view);
            showInfo();
        } else {
            Log.d(logTag, "1===============3");
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (parentView != null) {
                parentView.removeView(rootView);
            }
        }
        return rootView;
    }

    public void showSingerInfo(String artist, String artistId) {
        Log.d(logTag, "1===============4");
        if (this.artistId == artistId && this.artist == artist) return;
        Log.d(logTag, "1===============5");
        gSonSinger = null;
        this.artistId = artistId;
        this.artist = artist;
        if (rootView != null)
            showInfo();
    }

    private void showInfo() {
        Log.d(logTag, "1===============6");
        String json = CommonUtils.readSingerInfoJson(artist, artistId);
        if (!TextUtils.isEmpty(json)) {
            Log.d(logTag, "1===============7");
            showContent(true);
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
                        Log.d(logTag, "1===============8");
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            showContent(true);
                            parseJson(json);
                            CommonUtils.saveSingerInfoJson(artist, artistId, json);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //异常处理
                showContent(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //异常处理
                showContent(false);
            }
        });
    }

    private void showContent(boolean hasDetail) {
        Log.d(logTag, "1===============9");
        if (rootView != null) {
            Log.d(logTag, "1===============10");
            detailContent.setVisibility(hasDetail ? View.VISIBLE : View.INVISIBLE);
            noDetailContent.setVisibility(!hasDetail ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void parseJson(String json) {
        Log.d(logTag, "1===============11");
        Gson gson = new Gson();
        GSonSinger singer = gson.fromJson(json, GSonSinger.class);
        updateViews(singer);
    }

    private void updateViews(GSonSinger singer) {
        Log.d(logTag, "1===============12");
        this.gSonSinger = singer;
        updateViews();
    }

    private void updateViews() {
        Log.d(logTag, "1===============13");
        if (rootView == null) return;
        Log.d(logTag, "1===============14");
        if (gSonSinger == null) {
            Log.d(logTag, "1===============15");
            showContent(false);
            return;
        }
        nameTV.setText(getString(gSonSinger.getName()));
        otherNameTV.setText(getString(gSonSinger.getOnname()));
        countryTV.setText(getString(gSonSinger.getCountry()));
        birthdayTV.setText(getString(gSonSinger.getBirthday()));
        birthPlaceTV.setText(getString(gSonSinger.getBirthplace()));
        tallTV.setText(getString(gSonSinger.getTall()));
        weightTV.setText(getString(gSonSinger.getWeight()));
        languageTV.setText(getString(gSonSinger.getLanguage()));
        genderTV.setText(getString(gSonSinger.getGender()));
        constellationTV.setText(getString(gSonSinger.getConstellation()));
        singerDetailTV.setText(getString(gSonSinger.getInfo()));

        Bitmap bitmap = CommonUtils.getSingerHead(gSonSinger.getPic());
        if (bitmap != null)
            headerIV.setImageBitmap(bitmap);
        else
            headerIV.setImageResource(R.drawable.singer_default);
    }

    private String getString(String srcStr) {
        if (TextUtils.isEmpty(srcStr))
            return "--";
        else {
            srcStr = srcStr.replace("&lt;br&gt;", "\n");
            srcStr = srcStr.replace("&nbsp;", " ");
            srcStr = srcStr.replace("&quot;", "\"");
            return srcStr;
        }
    }
}
