package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.activity.CommonMusicListActivity;
import com.yuqf.fengmomusic.ui.adapter.HotRecommendAdapter;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopularFragment extends Fragment {

    private RecyclerView recyclerView;
    private HotRecommendAdapter adapter;
    private View rootView;
    private final static String logTag = "PopularFragment";

    public PopularFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_popular, container, false);
            initRecyclerView();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        return rootView;
    }

    private void initAdapter() {
        adapter = new HotRecommendAdapter();
        adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem hrItem = adapter.getHotRecommendItem(position);
                if (hrItem == null) return;
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put(Global.INTENT_LOAD_MUSIC_KIND, Global.INTENT_LOAD_HOTRECOMMEND);
                hashMap.put(Global.INTENT_HR_ITEM_ID, String.valueOf(hrItem.getSourceId()));
                hashMap.put(Global.INTENT_HR_ITEM_NAME, hrItem.getName());
                CommonUtils.startActivity(MyApplication.getContext(), CommonMusicListActivity.class, hashMap);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        loadHotRecommendList();
    }

    private void loadHotRecommendList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(logTag,"start load hot recommend list...");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(UrlHelper.Hot_Recommend_Base_Url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitServices.SearchService service = retrofit.create(RetrofitServices.SearchService.class);
                Call<GSonHotRecommend> call = service.getHotRecommend();
                call.enqueue(new Callback<GSonHotRecommend>() {
                    @Override
                    public void onResponse(Call<GSonHotRecommend> call, Response<GSonHotRecommend> response) {
                        Log.d(logTag,"start load hot recommend list...SUCCESS");
                        if (response.isSuccessful()) {
                            GSonHotRecommend hotRecommend = response.body();
                            if (hotRecommend != null) {
                                if (hotRecommend.getData() != null) {
                                    GSonHotRecommend.HotRecommendSecond.HotRecommendList hotRecommendList = hotRecommend.getData().getPlayList();
                                    if (hotRecommendList != null) {
                                        if (hotRecommendList.getList() != null)
                                            adapter.setHotRecommendItemList(hotRecommendList.getList());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GSonHotRecommend> call, Throwable t) {
                        Log.d(logTag,"start load hot recommend list...FAILED");
                    }
                });
            }
        }).start();
    }
}
