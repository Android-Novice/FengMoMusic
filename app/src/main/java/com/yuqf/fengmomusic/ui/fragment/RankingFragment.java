package com.yuqf.fengmomusic.ui.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.ui.adapter.GridSpacingItemDecoration;
import com.yuqf.fengmomusic.ui.adapter.RankingRecyclerViewAdapter;
import com.yuqf.fengmomusic.ui.entity.GsonRankingList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String logTag = "RankingFragment";
    private View parentView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View refreshAreaView;
    private RecyclerView recyclerView;
    private RankingRecyclerViewAdapter adapter;
    private ImageView loadingCoverIV;
    private LinearLayout noAudioDataView;

    private final int PAGE_COUNT = 100;
    private boolean isLoading;
    private int lastVisibleItemIndex;
    private boolean isFirstLoaded;

    public RankingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RankingRecyclerViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_ranking, container, false);

            noAudioDataView = (LinearLayout) parentView.findViewById(R.id.no_data_view);
            refreshAreaView = parentView.findViewById(R.id.refresh_area_view);
            loadingCoverIV = (ImageView) parentView.findViewById(R.id.loading_iv);
            loadingCoverIV.setImageResource(R.drawable.loading_list);
            initSwipeRefreshLayout();
            initRecyclerView();
        } else {
            ViewGroup parent = (ViewGroup) parentView.getParent();
            if (parent != null) {
                parent.removeView(parentView);
            }
        }
        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRanking();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        int screenWidth = CommonUtils.getScreenWidth(getActivity());
        int spanCount = 2;
        if (screenWidth >= 800 && screenWidth <= 860)
            spanCount = 3;
        else if (screenWidth > 960 && screenWidth <= 1200)
            spanCount = 4;
        else if (screenWidth > 1200)
            spanCount = 5;

        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(20, spanCount, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions = new int[gridLayoutManager.getSpanCount() * 4];

                lastVisibleItemIndex = getMMValue(gridLayoutManager.findLastVisibleItemPositions(positions), true);
                Log.d(logTag, "=========onScrolled======== ");
                positions = new int[gridLayoutManager.getSpanCount()];
                int[] firstShowArr = gridLayoutManager.findFirstCompletelyVisibleItemPositions(positions);
                if (firstShowArr.length > 0) {
                    int firstIndex = getMMValue(firstShowArr, false);
                    Log.d(logTag, "first show item index: " + String.valueOf(firstIndex) + "\nLast show item index: " + String.valueOf(lastVisibleItemIndex));
                    swipeRefreshLayout.setEnabled(firstIndex == 0);
                }
            }
        });
    }

    private int getMMValue(int[] arr, boolean max) {
        int value = arr[0];
        if (max) {
            for (int temp : arr) {
                if (value < temp)
                    value = temp;
            }
        } else {
            for (int temp : arr) {
                if (value > temp)
                    value = temp;
            }
        }
        return value;
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.progressbar_default);
        swipeRefreshLayout.setColorSchemeResources(R.color.progressbar_second);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d(logTag, "refreshing....1");
        refreshAreaView.setVisibility(View.VISIBLE);
        isFirstLoaded = false;
        loadRanking();
        Log.d(logTag, "refreshing....3");
    }

    private void loadRanking() {
        if (isLoading || isFirstLoaded) return;
        isFirstLoaded = true;
        isLoading = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Ranking_Get_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitServices.RankingService rankingService = retrofit.create(RetrofitServices.RankingService.class);

        Call<GsonRankingList> call = rankingService.getGsonRankingList("query", "tree", "2", "0", String.valueOf(PAGE_COUNT), "json", "mbox", "3");

        call.enqueue(new Callback<GsonRankingList>() {
            @Override
            public void onResponse(Call<GsonRankingList> call, Response<GsonRankingList> response) {
                hideLoadingIV();
                int code = response.code();
                String message = response.message();
                String msg = response.raw().message();
                String error = "";
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null)
                    error = errorBody.toString();
                Log.d(logTag, "=============" + String.valueOf(code) + "\n" + message + "\n" + msg + "\n" + error);
                if (response.isSuccessful()) {
                    try {
                        GsonRankingList rankingList = response.body();
                        adapter.reloadItems(rankingList.getChild());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                isLoading = false;
                updateShowingState(true);
                finishRefreshing();
            }

            @Override
            public void onFailure(Call<GsonRankingList> call, Throwable t) {
                Log.e(logTag, "=============Error:" + t.getMessage() + "\n" + t.getStackTrace().toString() + "\n");
                t.printStackTrace();
                finishRefreshing();
                isLoading = false;
                hideLoadingIV();
                updateShowingState(false);
            }
        });
    }

    private void updateShowingState(boolean success) {
        if (adapter.getItemCount() > 0) {
            noAudioDataView.setVisibility(View.GONE);
        } else {
            noAudioDataView.setVisibility(View.VISIBLE);
            TextView noDataTV = (TextView) parentView.findViewById(R.id.no_data_tv);
            if (success)
                noDataTV.setText(getResources().getString(R.string.no_audio_data));
            else
                noDataTV.setText(getResources().getString(R.string.net_error));
        }
    }

    private void hideLoadingIV() {
        ((AnimationDrawable) loadingCoverIV.getDrawable()).stop();
        loadingCoverIV.setVisibility(View.GONE);
    }

    private void finishRefreshing() {
        refreshAreaView.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
