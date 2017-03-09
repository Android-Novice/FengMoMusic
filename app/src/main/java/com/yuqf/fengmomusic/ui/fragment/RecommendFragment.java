package com.yuqf.fengmomusic.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yuqf.customviews.Banner.OnBannerClickListener;
import com.yuqf.customviews.BannerView;
import com.yuqf.fengmomusic.MainActivity;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.activity.CommonMusicListActivity;
import com.yuqf.fengmomusic.ui.activity.SingerListActivity;
import com.yuqf.fengmomusic.ui.adapter.HotRecommendAdapter;
import com.yuqf.fengmomusic.ui.adapter.PersonalRecommendAdapter;
import com.yuqf.fengmomusic.ui.entity.GSonFocusPictureList;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.ui.entity.GsonPersonalRecommendationItem;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.ui.fragment.RecommendUtils.ParseRecommendHelper;
import com.yuqf.fengmomusic.ui.fragment.RecommendUtils.RecommendImageLoader;
import com.yuqf.fengmomusic.ui.widget.RecyclerViewEx;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
    private LinearLayout recommendContent;
    /*
    * 保存个性化推荐的Json字符串，用于加载展示全部个性化推荐列表
    * */
    private String personalJsonStr;

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
            recommendContent = (LinearLayout) rootView.findViewById(R.id.recommend_content);
            startLoad();
        } else {
            ViewGroup parentView = (ViewGroup) rootView.getParent();
            if (parentView != null) {
                parentView.removeView(parentView);
            }
        }
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
                        initRecommendView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        Retrofit prRetrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Personal_Recommendation_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.RecommendService prService = prRetrofit.create(RetrofitServices.RecommendService.class);
        Call<ResponseBody> prCall = prService.getPersonalRecommendation();
        prCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String gsonStr = response.body().string();
                        int subIndex = gsonStr.indexOf("playlist\":");
                        if (subIndex > 0) {
                            personalJsonStr = gsonStr.substring(subIndex + 10, gsonStr.length() - 1);
                            Log.d(logTag, personalJsonStr.substring(personalJsonStr.length() - 100));
                            Log.d(logTag, personalJsonStr);
                            GsonBuilder builder = new GsonBuilder();
                            Type type = new TypeToken<ArrayList<GsonPersonalRecommendationItem>>() {
                            }.getType();
                            List<GsonPersonalRecommendationItem> personalList = builder.create().fromJson(personalJsonStr, type);
                            initPersonalRecommendation(personalList);
                        }
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

    private void initRecommendView() {
        final List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItems = ParseRecommendHelper.getInstance().getHotRecommendItemList();
        if (hotRecommendItems.size() > 0) {
            RecyclerViewEx recyclerViewEx = new RecyclerViewEx(MyApplication.getContext());
            recyclerViewEx.setIntroduceAreaVisibility(View.GONE);
            recyclerViewEx.setViewTitle(getString(R.string.hot_recommend_title));
            recyclerViewEx.setMoreBtnVisibility(View.VISIBLE);
            int spanCount = hotRecommendItems.size() > 3 ? 3 : hotRecommendItems.size();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            HotRecommendAdapter adapter = new HotRecommendAdapter(false);
            /*这里有个坑儿，
            subList的第一个参数是开始索引，
            第二个参数是指结束索引，
            开始索引包含在sublist中，
            但是结束索引不包含在subList中，上
            了这个地方的当了*/
            adapter.setHotRecommendItemList(hotRecommendItems.subList(0, spanCount));
            recommendContent.addView(recyclerViewEx);
            recyclerViewEx.setAdapter(adapter, linearLayoutManager);

            recyclerViewEx.setMoreBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) RecommendFragment.this.getActivity()).moveToPopularFragment();
                }
            });

            adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < 0) return;
                    GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem item = hotRecommendItems.get(position);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(Global.INTENT_LOAD_MUSIC_KIND, Global.INTENT_LOAD_HOTRECOMMEND);
                    hashMap.put(Global.INTENT_HR_ITEM_ID, String.valueOf(item.getSourceId()));
                    hashMap.put(Global.INTENT_HR_ITEM_NAME, item.getName());
                    CommonUtils.startActivity(MyApplication.getContext(), CommonMusicListActivity.class, hashMap);
                }

                @Override
                public void onItemDownloadClick(View view, int position) {

                }
            });
        }
    }

    private void initPersonalRecommendation(final List<GsonPersonalRecommendationItem> list) {
        if (list.size() > 0) {
            RecyclerViewEx recyclerViewEx = new RecyclerViewEx(MyApplication.getContext());
            recyclerViewEx.setMoreBtnVisibility(View.VISIBLE);
            recyclerViewEx.setIntroduceAreaVisibility(View.GONE);
            recyclerViewEx.setViewTitle(getString(R.string.personal_recommendation));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            int spanCount = list.size() > 3 ? 3 : list.size();
            PersonalRecommendAdapter adapter = new PersonalRecommendAdapter();
            adapter.setPersonalList(list.subList(0, spanCount));
            recyclerViewEx.setAdapter(adapter, linearLayoutManager);

            recommendContent.addView(recyclerViewEx);

            adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < 0) return;
                    GsonPersonalRecommendationItem item = list.get(position);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(Global.INTENT_LOAD_MUSIC_KIND, Global.INTENT_LOAD_HOTRECOMMEND);
                    hashMap.put(Global.INTENT_HR_ITEM_ID, String.valueOf(item.getSourceid()));
                    hashMap.put(Global.INTENT_HR_ITEM_NAME, item.getName());
                    CommonUtils.startActivity(MyApplication.getContext(), CommonMusicListActivity.class, hashMap);
                }

                @Override
                public void onItemDownloadClick(View view, int position) {

                }
            });
            recyclerViewEx.setMoreBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}

