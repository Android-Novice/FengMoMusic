package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.media.MusicPlayer;
import com.yuqf.fengmomusic.ui.activity.SearchActivity;
import com.yuqf.fengmomusic.ui.adapter.HotRecommendAdapter;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.ui.widget.MiniMusicPlayerView;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private ImageButton btnSearch;
    private RecyclerView recyclerView;
    private MiniMusicPlayerView mediaPlayer;
    private HotRecommendAdapter adapter;

    private View rootView;

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new HotRecommendAdapter();
        adapter.setItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
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
        return rootView;
    }

    private void initEditText() {
        searchEditText = (EditText) rootView.findViewById(R.id.search_edit_text);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startActivity(getContext(), SearchActivity.class, null);
            }
        });
    }

    private void init() {
        initEditText();
        initRecyclerView();
        btnSearch = (ImageButton) rootView.findViewById(R.id.go_search_btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getHint().toString();
                Pair<String, String> pair = new Pair<String, String>(Global.INTENT_HOT_RECOMMEND_KEY, searchText);
                List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>();
                CommonUtils.startActivity(getContext(), SearchActivity.class, pairList);
            }
        });
        mediaPlayer = (MiniMusicPlayerView) rootView.findViewById(R.id.media_player);
        MusicPlayer.getInstance().addPlayerListener(mediaPlayer);
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

//    private PopupWindow popupWindow;
//    private List<String> recommendList;
//    private ArrayAdapter adapter;
//
//    private void showPopupWindow() {
//        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.recommend_search_tips_layout, null);
//        if (popupWindow == null) {
//            popupWindow = new PopupWindow(getContext());
//            popupWindow.setContentView(contentView);
//            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//            popupWindow.setFocusable(true);
//            popupWindow.setTouchable(true);
////            popupWindow.setOutsideTouchable(true);
//            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
////            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
//        }
//        final ListView listView = (ListView) contentView.findViewById(R.id.list_view_tips);
//        recommendList = new ArrayList<>();
//        recommendList.add("xxxxxxxxxxxxxxxxxxx8");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx7");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx6");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx5");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx4");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx3");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx2");
//        recommendList.add("xxxxxxxxxxxxxxxxxxx1");
//        adapter = new ArrayAdapter<String>(getContext(), R.layout.history_list_view_left_item, R.id.history_list_item_tv, recommendList);
//        listView.setAdapter(adapter);
//        popupWindow.showAsDropDown(btnOpen, 0, 0);
//        popupWindow.setFocusable(true);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("xxxxxx", "click listview item\n");
//                TextView textView = (TextView) view.findViewById(R.id.history_list_item_tv);
//                String searchText = textView.getText().toString();
//                CommonUtils.showToast(searchText, true);
//            }
//        });
//
//    }
}
