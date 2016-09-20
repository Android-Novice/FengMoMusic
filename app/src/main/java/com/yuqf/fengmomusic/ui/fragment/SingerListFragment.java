package com.yuqf.fengmomusic.ui.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.activity.MusicListActivity;
import com.yuqf.fengmomusic.ui.adapter.SingerRecyclerViewAdapter;
import com.yuqf.fengmomusic.ui.entity.GsonSingerList;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;
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

public class SingerListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String logTag = "SingerListFragment";
    private View parentView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View refreshAreaView;
    private RecyclerView recyclerView;
    private SingerRecyclerViewAdapter adapter;
    private ImageView loadingCoverIV;
    private LinearLayout noAudioDataView;

    private int category = -1;//0-10,singer kind
    private int pageIndex = -1; //current kind showing page index
    private final int PAGE_COUNT = 100;
    private final String URL_TYPE = "artistlist";
    private final String URL_ORDER = "hot";
    private int prefix = -1;
    private String[] orderArr = null;
    private boolean isLoading;
    private int lastVisibleItemIndex;
    //value is false until all pages of current kind are loaded;
    private boolean loadAllFinish;

    public SingerListFragment() {
    }

    public void setPrefix(int prefix) {
        if (this.prefix < 0) {
            this.prefix = prefix;
        }
    }

    public void setCategory(int category) {
        if (this.category < 0)
            this.category = category;
    }

    public int getPrefix() {
        return prefix;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SingerRecyclerViewAdapter(getContext());
        adapter.setViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GsonSingerList.Singer singer = adapter.getSingerByPosition(position);
                String titleV = singer.getName();

                String contentV = String.valueOf(singer.getId());
                List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>();
                pairList.add(new Pair<>(Global.INTENT_TITLE_KEY, titleV));
                pairList.add(new Pair<>(Global.INTENT_FROM_KEY, Global.INTENT_FROM_SINGER));
                pairList.add(new Pair<>(Global.INTENT_CONTENT_KEY, contentV));
                pairList.add(new Pair<>(Global.INTENT_COVER_KEY, singer.getPic()));
                CommonUtils.startActivity(getActivity(), MusicListActivity.class, pairList);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_singer_list, container, false);

        noAudioDataView = (LinearLayout) parentView.findViewById(R.id.no_data_view);
        refreshAreaView = parentView.findViewById(R.id.refresh_area_view);
        loadingCoverIV = (ImageView) parentView.findViewById(R.id.loading_iv);
        loadingCoverIV.setImageResource(R.drawable.loading_list);
        initSwipeRefreshLayout();
        initRecyclerView();
        loadSinger();
        return parentView;
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        int screenWidth = CommonUtils.getScreenWidth(getActivity());
        int spanCount = 2;
        if (screenWidth >= 720 && screenWidth <= 900)
            spanCount = 3;
        else if (screenWidth > 900 && screenWidth <= 1200)
            spanCount = 4;
        else if (screenWidth > 1200)
            spanCount = 5;
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(20, spanCount, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemIndex == adapter.getItemCount() - 1) {
                    adapter.notifyLoadStatus(true);
                    loadSinger();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstShowIndex = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                lastVisibleItemIndex = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                swipeRefreshLayout.setEnabled(firstShowIndex <= 0);
            }
        });
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
        pageIndex = -1;
        loadSinger();
        Log.d(logTag, "refreshing....3");
    }

    public void loadSinger() {
        if (category < 0 || category > 10 || prefix < 0) return;
        if (isLoading || loadAllFinish) return;
        isLoading = true;
        this.pageIndex++;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Singer_Get_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(RetrofitServices.SingerConverterFactory.create())
                .build();

        RetrofitServices.SingerService singerService = retrofit.create(RetrofitServices.SingerService.class);

        Call<ResponseBody> call = null;
        if (prefix == 0)
            call = singerService.getGsonSingerList(URL_TYPE, String.valueOf(category), URL_ORDER, String.valueOf(pageIndex), String.valueOf(PAGE_COUNT));
        else {
            if (orderArr == null)
                orderArr = getContext().getResources().getStringArray(R.array.singer_order_list);
            String prefixStr = orderArr[prefix];
            if (prefixStr == "#")
                prefixStr = "%23";
            call = singerService.getGsonSingerList(URL_TYPE, String.valueOf(category), URL_ORDER, String.valueOf(pageIndex), String.valueOf(PAGE_COUNT), prefixStr);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                        String body = response.body().string();
                        Gson gson = new GsonBuilder().create();

                        body = body.replace("\"", "");
                        body = body.replace("'", "\"");
                        GsonSingerList gsonSingerList = gson.fromJson(body, GsonSingerList.class);
                        if (gsonSingerList.getTotal() == 0)
                            loadAllFinish = true;
                        else {
                            List<GsonSingerList.Singer> singers = gsonSingerList.getArtistlist();
                            if (singers == null || singers.size() == 0)
                                loadAllFinish = true;
                            else {
                                if (swipeRefreshLayout.isRefreshing()) {
                                    finishRefreshing();
                                    adapter.reloadItems(singers);
                                } else {
                                    adapter.addItems(singers);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//不再下载保存到手机sdcard
//                    try {
//                        String path = Environment.getExternalStorageDirectory().toString() + "/mydownload.json";
//                        File file = new File(path);
//                        if (!file.exists())
//                            file.createNewFile();
//                        FileOutputStream outputStream = new FileOutputStream(file);
//                        body = response.body().string();
//                        outputStream.write(body.getBytes());
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }
                isLoading = false;
                updateShowingState(true);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
        adapter.notifyLoadStatus(false);
    }

    private void hideLoadingIV() {
        if (pageIndex == 0) {
            Log.d(logTag, "=============First load singer infomation success, set loadingCoverIV's Visibility as Gone");
            ((AnimationDrawable) loadingCoverIV.getDrawable()).stop();
            loadingCoverIV.setVisibility(View.GONE);
        }
    }

    private void finishRefreshing() {
        refreshAreaView.setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
