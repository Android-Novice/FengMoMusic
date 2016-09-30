package com.yuqf.fengmomusic.ui.fragment;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.download.DownloaderNew;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.interfaces.PlayIndexChangedListener;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.ui.activity.DownloadActivity;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.adapter.MusicRecyclerViewAdapter;
import com.yuqf.fengmomusic.ui.entity.GsonRMusicList;
import com.yuqf.fengmomusic.ui.entity.GsonSMusicList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicListFragment extends Fragment implements PlayIndexChangedListener {

    private final String logTag = "MusicListFragment";
    private View parentView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View refreshAreaView;
    private RecyclerView recyclerView;
    private MusicRecyclerViewAdapter adapter;
    private ImageView loadingCoverIV;
    private LinearLayout noAudioDataView;

    private int lastVisibleItemIndex;
    private boolean allLoaded;
    private boolean isLoading;
    private int pIndex = -1;
    private String contentId;
    private String from;

    public MusicListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicPlayer.getInstance().addChangedListener(this);
        adapter = new MusicRecyclerViewAdapter(true, true);
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MusicPlayer.getInstance().setPlayingMusics(adapter.getMusicList());
                MusicPlayer.getInstance().play(position);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {
                Music music = adapter.getMusicList().get(position);
                DownloaderNew downloader = new DownloaderNew(music);
                Intent intent = new Intent(getContext(), DownloadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("artist", music.getArtist());
//                intent.putExtra("musicId", music.getId());
//                intent.putExtra("music", music.getName());
                MyApplication.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onPlayingIndexChange(Music music, int curPosition, int oldPosition) {
        adapter.updateItemState(music);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_music_list, container, false);
        noAudioDataView = (LinearLayout) parentView.findViewById(R.id.no_data_view);
        refreshAreaView = parentView.findViewById(R.id.refresh_area_view);
        loadingCoverIV = (ImageView) parentView.findViewById(R.id.loading_iv);
        loadingCoverIV.setImageResource(R.drawable.loading_list);

        initSwipeRefreshLayout();
        initRecyclerView();
        return parentView;
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(true));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemIndex == adapter.getItemCount() - 1) {
                    adapter.notifyLoadStatus(true);
                    loadMusic();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemIndex = layoutManager.findFirstCompletelyVisibleItemPosition();
                swipeRefreshLayout.setEnabled(firstVisibleItemIndex == 0);

                lastVisibleItemIndex = layoutManager.findLastCompletelyVisibleItemPosition();

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(0);
                View view = recyclerView.getChildAt(0);
                int top = view.getTop();
                if (viewHolder != null) {
                    float alpha = -top / (float) Global.HEADER_HEIGHT;
                    if (alpha > 1)
                        alpha = 1;
                    if (scrolledListener != null)
                        scrolledListener.onNotifyScrolled(alpha, -top);
                } else {
                    if (scrolledListener != null)
                        scrolledListener.onNotifyScrolled(1, Global.HEADER_HEIGHT);
                }
                Log.d(logTag, String.format("dx: %d\n dy: %dy \n top: %d\n", dx, dy, top));
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
                swipeRefreshLayout.setRefreshing(true);
                Log.d(logTag, "refreshing....1");
                refreshAreaView.setVisibility(View.VISIBLE);
                pIndex = -1;
                loadMusic();
                Log.d(logTag, "refreshing....3");
            }
        });
    }

    public void loadMusic(String from, String contentId) {
        if (TextUtils.isEmpty(contentId)) return;
        if (TextUtils.isEmpty(this.contentId)) {
            this.contentId = contentId;
            this.from = from;
        }
        loadMusic();
    }

    private void loadMusic() {
        switch (from) {
            case Global.INTENT_FROM_RANKING:
                loadRMusic();
                break;
            case Global.INTENT_FROM_SINGER:
                loadSMusic();
                break;
        }
    }

    private void loadRMusic() {
        if (isLoading || allLoaded) {
            finishRefreshing();
            return;
        }
        this.pIndex++;
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Music_From_Ranking_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService service = retrofit.create(RetrofitServices.MusicService.class);
        Call<GsonRMusicList> call = service.getGsonRMusicList("pc", "json", "bang", "content", contentId, pIndex, 100);
        call.enqueue(new Callback<GsonRMusicList>() {
            @Override
            public void onResponse(Call<GsonRMusicList> call, Response<GsonRMusicList> response) {

                hideLoadingIV();
                if (response.isSuccessful()) {
                    GsonRMusicList gsonMusics = response.body();
                    List<GsonRMusicList.RMusic> musicList = gsonMusics.getMusiclist();
                    if (musicList == null || musicList.size() == 0)
                        allLoaded = true;
                    else {
                        if (!swipeRefreshLayout.isRefreshing()) {
                            adapter.addRMusics(musicList);
                        } else {
                            adapter.reloadRMusics(musicList);
                        }
                    }
                }
                isLoading = false;
                updateShowingState(true);
                finishRefreshing();
            }

            @Override
            public void onFailure(Call<GsonRMusicList> call, Throwable t) {
                t.printStackTrace();
                isLoading = false;
                finishRefreshing();
                hideLoadingIV();
                updateShowingState(false);
            }
        });
    }

    private void loadSMusic() {
        if (isLoading || allLoaded) {
            finishRefreshing();
            return;
        }
        this.pIndex++;
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Music_From_Singer_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.MusicService service = retrofit.create(RetrofitServices.MusicService.class);
        Call<ResponseBody> call = service.getGsonSMusicList("artist2music", contentId, pIndex, 100);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                hideLoadingIV();
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        json = json.replace("'new'", "'_new'");
                        json = json.replace("'", "\"");
                        json = json.replace("&nbsp;", " ");

                        Gson gson = new Gson();
                        GsonSMusicList gsonSMusicList = gson.fromJson(json, GsonSMusicList.class);
                        List<GsonSMusicList.SMusic> musicList = gsonSMusicList.getMusiclist();
                        if (musicList == null || musicList.size() == 0)
                            allLoaded = true;
                        else {
                            if (!swipeRefreshLayout.isRefreshing()) {
                                adapter.addSMusics(musicList);
                            } else {
                                adapter.reloadSMusics(musicList);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                isLoading = false;
                updateShowingState(true);
                finishRefreshing();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                isLoading = false;
                finishRefreshing();
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
        adapter.notifyLoadStatus(false);
    }

    private void hideLoadingIV() {
        Log.d(logTag, "=============First load singer infomation success, set loadingCoverIV's Visibility as Gone");
        ((AnimationDrawable) loadingCoverIV.getDrawable()).stop();
        loadingCoverIV.setVisibility(View.GONE);
    }

    private void finishRefreshing() {
        refreshAreaView.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private OnFragmentScrolledListener scrolledListener;

    public void setScrolledListener(OnFragmentScrolledListener scrolledListener) {
        this.scrolledListener = scrolledListener;
    }

    public interface OnFragmentScrolledListener {
        void onNotifyScrolled(float alpha, int scrollY);
    }
}
