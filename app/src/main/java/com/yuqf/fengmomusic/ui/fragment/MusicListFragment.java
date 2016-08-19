package com.yuqf.fengmomusic.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.ui.adapter.RMusicRecyclerViewAdapter;
import com.yuqf.fengmomusic.utils.CommonUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicListFragment extends Fragment {

    private final String logTag = "MusicListFragment";
    private View parentView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View refreshAreaView;
    private RecyclerView recyclerView;
    private RMusicRecyclerViewAdapter adapter;
    private ImageView loadingCoverIV;
    private LinearLayout noAudioDataView;

    private int lastVisibleItemIndex;
    private boolean allLoaded;
    private boolean isLoading;
    private int pIndex = -1;
    private int contentId;

    public MusicListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RMusicRecyclerViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_music_list, container, false);
        noAudioDataView = (LinearLayout) parentView.findViewById(R.id.no_data_view);
        refreshAreaView = parentView.findViewById(R.id.refresh_area_view);
        loadingCoverIV = (ImageView) parentView.findViewById(R.id.loading_iv);
        loadingCoverIV.setImageResource(R.drawable.loading_list);

        return parentView;
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemIndex == adapter.getItemCount() - 1) {

                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemIndex = layoutManager.findFirstCompletelyVisibleItemPosition();
                swipeRefreshLayout.setEnabled(firstVisibleItemIndex == 0);

                lastVisibleItemIndex = layoutManager.findLastCompletelyVisibleItemPosition();
            }
        });
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.progressbar_default);
        swipeRefreshLayout.setColorSchemeResources(R.color.progressbar_second);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    public void loadMusic(int contentId) {
        if (this.contentId < 0) {
            this.contentId = contentId;
            loadMusic();
        }
    }

    private void loadMusic() {
        if (isLoading || allLoaded) return;
        this.pIndex++;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtils.UrlHelper.Music_From_Ranking_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}
