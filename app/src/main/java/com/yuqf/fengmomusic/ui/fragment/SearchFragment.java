package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.activity.HRItemMusicListActivity;
import com.yuqf.fengmomusic.ui.activity.SearchActivity;
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

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;

public class SearchFragment extends Fragment {

    private ImageButton btnSearch;
    private RecyclerView recyclerView;
    private HotRecommendAdapter adapter;
    private String hotSearchWordPrefix;
    private String[] hotWordArray;
    private View rootView;
    private static final int MessageFlag = 110;
    private int showingIndex = -1;
    private final static String logTag = "SearchFragment";
    private TextSwitcher textSwitcher;

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hotSearchWordPrefix = getResources().getString(R.string.search_hint_prefix);
        hotWordArray = getResources().getStringArray(R.array.hot_search_word_list);
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search, container, false);
            init();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        if (hotWordArray.length > 0 && showingIndex < 0) {
            showingIndex = 0;
            String text = hotSearchWordPrefix + hotWordArray[showingIndex];
            textSwitcher.setText(text);
        }
        handler.sendEmptyMessageDelayed(MessageFlag, 20000);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeMessages(MessageFlag);
        }
    }

    private void initAdapter() {
        adapter = new HotRecommendAdapter();
        adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem hrItem = adapter.getHotRecommendItem(position);
                if (hrItem == null) return;
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put(Global.INTENT_HR_ITEM_ID, String.valueOf(hrItem.getSourceId()));
                hashMap.put(Global.INTENT_HR_ITEM_NAME, hrItem.getName());
                CommonUtils.startActivity(MyApplication.getContext(), HRItemMusicListActivity.class, hashMap);
            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
    }

    private void init() {
        initTextSwitcher();
        initRecyclerView();
        btnSearch = (ImageButton) rootView.findViewById(R.id.go_search_btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingIndex >= 0) {
                    Log.d(logTag, "SearchTextHint: " + hotWordArray[showingIndex]);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(Global.INTENT_HOT_RECOMMEND_KEY, hotWordArray[showingIndex]);
                    CommonUtils.startActivity(getContext(), SearchActivity.class, hashMap);
                } else
                    CommonUtils.startActivity(getContext(), SearchActivity.class, null);
            }
        });
    }

    private void initTextSwitcher() {
        textSwitcher = (TextSwitcher) rootView.findViewById(R.id.text_switcher);
        textSwitcher.setFactory(viewFactory);
        textSwitcher.setInAnimation(MyApplication.getContext(), R.anim.text_switcher_fade_in);
        textSwitcher.setOutAnimation(MyApplication.getContext(), R.anim.text_switcher_fade_out);
        textSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startActivity(getContext(), SearchActivity.class, null);
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
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(UrlHelper.Hot_Recommend_Base_Url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitServices.SearchService service = retrofit.create(RetrofitServices.SearchService.class);
                Call<GSonHotRecommend> call = service.getHotRecommend();
                call.enqueue(new Callback<GSonHotRecommend>() {
                    @Override
                    public void onResponse(Call<GSonHotRecommend> call, Response<GSonHotRecommend> response) {
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

                    }
                });
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MessageFlag && isVisible() && isAdded()) {
                if (hotWordArray.length <= 0) return;
                showingIndex++;
                if (hotWordArray.length <= showingIndex) {
                    showingIndex = 0;
                }
                String text = hotSearchWordPrefix + hotWordArray[showingIndex];
                textSwitcher.setText(text);
                handler.sendEmptyMessageDelayed(MessageFlag, 20000);
            }
            super.handleMessage(msg);
        }
    };

    ViewSwitcher.ViewFactory viewFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            TextView textView = new TextView(SearchFragment.this.getContext());
            textView.setGravity(CENTER_VERTICAL | LEFT);
            textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(SearchFragment.this.getContext(), 32), CENTER_VERTICAL));
            textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorAccent));
            textView.setTextSize(14);
            return textView;
        }
    };
}
