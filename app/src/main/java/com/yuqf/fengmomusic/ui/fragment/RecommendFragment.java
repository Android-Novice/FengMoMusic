package com.yuqf.fengmomusic.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yuqf.customviews.Banner.OnBannerClickListener;
import com.yuqf.customviews.BannerView;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.activity.SingerListActivity;
import com.yuqf.fengmomusic.ui.entity.GSonFocusPictureList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.ui.fragment.RecommendUtils.ParseRecommendHelper;
import com.yuqf.fengmomusic.ui.fragment.RecommendUtils.RecommendImageLoader;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends Fragment {

    private View rootView;
    private BannerView bannerView;
    private final String logTag = "RecommendFragment";

    public RecommendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
            bannerView = (BannerView) rootView.findViewById(R.id.bannerview);
            LinearLayout button = (LinearLayout) rootView.findViewById(R.id.btn_all_singer);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtils.startActivity(MyApplication.getContext(), SingerListActivity.class, null);
                }
            });
        } else {
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (parentView != null) {
                parentView.removeView(parentView);
            }
        }
        startLoad();
        return rootView;
    }

    private void startLoad() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Recommend_Page_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.RecommendService service = retrofit.create(RetrofitServices.RecommendService.class);
        Call<ResponseBody> call = service.getAllRecommendInfo();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String gsonStr = response.body().string();
                        ParseRecommendHelper.getInstance().parseRecommendInfo(gsonStr);
                        ParseRecommendHelper.getInstance().parsePopularInfo(gsonStr);
                        ParseRecommendHelper.getInstance().parseStarActivityList(gsonStr);
                        initBannerView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void initBannerView() {
        final GSonFocusPictureList pictureList = ParseRecommendHelper.getInstance().getFocusPictureList();

        bannerView.setBannerClickListener(new OnBannerClickListener() {
            @Override
            public void BannerClick(int position) {
                Log.d(logTag, "Banner Click, Position: " + position);
                GSonFocusPictureList.FocusPictureItem focusPictureItem = pictureList.getList().get(position);
                CommonUtils.showToast("点击的是第" + String.valueOf(position + 1) + "个, 内容：" + focusPictureItem.getName(), true);
                ParseRecommendHelper.getInstance().skipPageWhenBannerClick(focusPictureItem);
            }
        });
        List<String> urls = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (GSonFocusPictureList.FocusPictureItem item :
                pictureList.getList()) {
            urls.add(item.getPic());
            nameList.add(item.getName());
        }

//        bannerView.setPageTransformer(TransferAnimation.FlipHorizontal);
        bannerView.setPageRandomTransformer();
        bannerView.setBannerList(RecommendImageLoader.getInstance(), urls, nameList);
    }

}

