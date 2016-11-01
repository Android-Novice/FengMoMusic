package com.yuqf.fengmomusic.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.ui.adapter.LinearLayoutItemDecoration;
import com.yuqf.fengmomusic.ui.adapter.MusicRecyclerViewAdapter;
import com.yuqf.fengmomusic.ui.entity.RetrofitServices;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.PinYinUtils;
import com.yuqf.fengmomusic.utils.UrlHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private final String logTag = "SearchActivity";
    private final String SP_FILE_NAME = "Search_History";
    private final String SEARCH_COUNT_KEY = "SearchCount";
    private final String SEARCH_KEY_PREFIX = "Search_Index_";
    private SharedPreferences sharedPreferences;
    private List<String> searchedStrList;
    private TextView noHistoryTV;
    /**
     * 历史记录的容器
     **/
    private View historyListContent;
    /**
     * 历史记录的顶级容器
     **/
    private View historyContent;
    /**
     * 搜索推荐内容的容器
     **/
    private View searchTipsContent;
    private ListView tipsLV;
    private ListView historyLeftLV;
    private ListView historyRightLV;
    private EditText editTextSearch;
    private ImageButton btnClearText;
    private ImageButton btnHome;
    private Button btnClearHistory;
    private List<HashMap<String, String>> searchTipsList;
    private SimpleAdapter simpleAdapter;
    private int searchPageIndex = -1;
    private final int PerPageCount = 300;
    private String curGBEncodeSearchStr;
    private RecyclerView recyclerView;
    private MusicRecyclerViewAdapter musicAdapter;
    private int lastVisibleItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initTopBars();
        hideToolBar();

        editTextSearch = (EditText) findViewById(R.id.search_edit_text);
        noHistoryTV = (TextView) findViewById(R.id.text_view_no_history);

        searchTipsContent = findViewById(R.id.search_tips_content);
        searchTipsContent.setVisibility(View.GONE);
        tipsLV = (ListView) findViewById(R.id.list_view_tips);
        tipsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text_view_tip_name);
                String searchText = textView.getText().toString();
                startSearch(searchText);
            }
        });
        historyContent = findViewById(R.id.history_related_content);
        historyListContent = findViewById(R.id.search_history_content);
        historyLeftLV = (ListView) findViewById(R.id.list_view_history1);
        historyRightLV = (ListView) findViewById(R.id.list_view_history2);

        btnClearText = (ImageButton) findViewById(R.id.btn_clear_text);
        btnHome = (ImageButton) findViewById(R.id.btn_home);
        btnClearHistory = (Button) findViewById(R.id.btn_clear_history);
        btnClearHistory.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnClearText.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE);
        initEditText();
        initRecycler();

        loadSearchHistory();
    }

    ListView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView historyTV = (TextView) view.findViewById(R.id.history_list_item_tv);
            String searchText = historyTV.getText().toString();
            startSearch(searchText);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_history:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                searchedStrList.clear();
                loadSearchHistory();
                break;
            case R.id.btn_clear_text:
                editTextSearch.setText("");
                setViewsVisibility(1, 3, 3);
                break;
            case R.id.btn_home:
                finish();
                break;
        }
    }

    @SuppressWarnings("ResourceType")
    private void setViewsVisibility(int historyStatus, int recommendStatus, int resultStatus) {
        int historyVisible = getVisibility(historyStatus);
        int tipsVisible = getVisibility(recommendStatus);
        int resultVisible = getVisibility(resultStatus);
        historyContent.setVisibility(historyVisible);
        searchTipsContent.setVisibility(tipsVisible);
        recyclerView.setVisibility(resultVisible);
    }

    private Integer getVisibility(int status) {
        switch (status) {
            case 1:
                return View.VISIBLE;
            case 2:
                return View.INVISIBLE;
            case 3:
                return View.GONE;
            default:
                return View.VISIBLE;
        }
    }

    private void loadSearchHistory() {
        setViewsVisibility(1, 3, 3);
        searchedStrList = getSearchHistory();
        if (searchedStrList.size() == 0) {
            noHistoryTV.setVisibility(View.VISIBLE);
            historyListContent.setVisibility(View.INVISIBLE);
        } else {
            noHistoryTV.setVisibility(View.GONE);
            historyListContent.setVisibility(View.VISIBLE);
            List<String> leftList = new ArrayList<>();
            List<String> rightList = new ArrayList<>();
            for (int i = 0; i < searchedStrList.size(); i++) {
                if (i % 2 != 0) {
                    rightList.add(searchedStrList.get(i));
                } else {
                    leftList.add(searchedStrList.get(i));
                }
            }
            historyLeftLV.setAdapter(new ArrayAdapter<>(this, R.layout.history_list_view_left_item, R.id.history_list_item_tv, leftList));
            historyRightLV.setAdapter(new ArrayAdapter<>(this, R.layout.history_list_view_right_item, R.id.history_list_item_tv, rightList));

            historyLeftLV.setOnItemClickListener(listener);
            historyRightLV.setOnItemClickListener(listener);
        }
    }

    private void initRecycler() {
        musicAdapter = new MusicRecyclerViewAdapter(false, true);
        musicAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemDownloadClick(View view, int position) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(false));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemIndex == musicAdapter.getItemCount() - 1) {
                    musicAdapter.notifyLoadStatus(true);
                    doSearch();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItemIndex = layoutManager.findLastCompletelyVisibleItemPosition();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initEditText() {
        editTextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    startSearch(editTextSearch.getText().toString());
                }
                return false;
            }
        });
        editTextSearch.addTextChangedListener(watcher);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String inputStr = s.toString();
            if (TextUtils.isEmpty(inputStr)) return;
            String encodeStr;
            try {
                encodeStr = URLEncoder.encode(inputStr, "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                encodeStr = PinYinUtils.convertToPinYin(inputStr);
            }
            if (!TextUtils.isEmpty(encodeStr)) {
                Log.d("SearchTextDecode", encodeStr + "\n");
                getSearchRecommendList(encodeStr);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())) {
                btnClearText.setVisibility(View.VISIBLE);
                setViewsVisibility(3, 3, 3);
            } else {
                loadSearchHistory();
                btnClearText.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void startSearch(String searchText) {
        if (TextUtils.isEmpty(searchText)) return;
        setViewsVisibility(3, 3, 1);
//        String searchText = editTextSearch.getText().toString();
        editTextSearch.removeTextChangedListener(watcher);
        editTextSearch.setText(searchText);
        editTextSearch.addTextChangedListener(watcher);
        addSearchHistory(searchText);
        try {
            musicAdapter.removeAll();
            curGBEncodeSearchStr = URLEncoder.encode(searchText, "GB2312");
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
            doSearch();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CommonUtils.showToast("开始搜索啦~", true);
    }

    private void doSearch() {
        searchPageIndex++;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.Search_Songs_Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.SearchService searchService = retrofit.create(RetrofitServices.SearchService.class);
        Call<ResponseBody> call = searchService.getSearchString(curGBEncodeSearchStr, searchPageIndex, PerPageCount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String content = response.body().string();
                        if (!TextUtils.isEmpty(content)) {
                            parseMusicList(content);
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

    private void parseMusicList(String content) {
        //SONGNAME=追梦人 ARTIST=白若溪 ALBUM=中国新歌声 第六期
        // SCORE=3 NSIG1=1412109533 NSIG2=2635023571
        // MUSICRID=MUSIC_8722832 MKVNSIG1=0 MKVNSIG2=0 HASECHO=0
        // WMACODE=WMA128 MKVCODE= FORMATS=WMA96|WMA128|MP3128|MP3192|MP3H|AAC48|AL
        // ALBUMID=1044144 ARTISTID=11056 APPROVESN= HASKDAT=0 PLAYCNT=11434
        // SCORE100=67 RELEASEDATE=2016-08-19
        // IS_POINT=0 MUTI_VER=0 ONLINE=1 NEW=0 PAY=0 COPYRIGHT=0
        content = content.replace("\r\n", "");
        String[] splitArr = content.split("SONGNAME=");
        List<Music> musics = new ArrayList<>();
        for (String split : splitArr) {
            if (split.contains("ARTIST=") && split.contains("MUSICRID=MUSIC_")) {
                try {
                    int artistIndex = split.indexOf("ARTIST=");
                    int albumIndex = split.indexOf("ALBUM=");
                    int scoreIndex = split.indexOf("SCORE=");
                    int musicIdIndex = split.indexOf("MUSICRID=MUSIC_");
                    int mkvnsigIndex = split.indexOf("MKVNSIG1");
                    int albumIdIndex = split.indexOf("ALBUMID=");
                    int artistIdIndex = split.indexOf("ARTISTID=");
                    int approvesnIndex = split.indexOf("APPROVESN=");
                    int score100Index = split.indexOf("SCORE100=");
                    int releaseIndex = split.indexOf("RELEASEDATE=");

                    int id = Integer.parseInt(split.substring(musicIdIndex + 15, mkvnsigIndex));
                    String name = split.substring(0, artistIndex);

                    String artist = split.substring(artistIndex + 7, albumIndex);
                    String artistId = split.substring(artistIdIndex + 9, approvesnIndex);

                    String album = split.substring(albumIndex + 6, scoreIndex);
                    int albumId = Integer.parseInt(split.substring(albumIdIndex + 8, artistIdIndex));

                    int score = Integer.parseInt(split.substring(score100Index + 9, releaseIndex));

                    Music music = new Music();
                    music.setAlbum(album);
                    music.setAlbumId(albumId);
                    music.setArtist(artist);
                    music.setArtistId(artistId);
                    music.setId(id);
                    music.setLocal(false);
                    music.setName(name);
                    music.setRating(score);
                    musics.add(music);
                } catch (Exception ex) {
                }
            }
        }
        musicAdapter.addMusicList(musics);
    }

    private List<String> getSearchHistory() {
        List<String> historyList = new ArrayList<>();
        int count = sharedPreferences.getInt(SEARCH_COUNT_KEY, 0);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String key = SEARCH_KEY_PREFIX + String.valueOf(i);
                if (sharedPreferences.contains(key)) {
                    historyList.add(sharedPreferences.getString(key, ""));
                }
            }
        }
        return historyList;
    }

    private void addSearchHistory(String searchText) {
        if (!searchedStrList.contains(searchText.trim())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String key = SEARCH_KEY_PREFIX + searchedStrList.size();
            editor.putString(key, searchText.trim());
            searchedStrList.add(searchText.trim());
            editor.putInt(SEARCH_COUNT_KEY, searchedStrList.size());
            editor.apply();
        }
    }

    private void getSearchRecommendList(String inputPinYin) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlHelper.SEARCH_RECOMMEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices.SearchService service = retrofit.create(RetrofitServices.SearchService.class);
        Call<ResponseBody> call = service.getRecommendString(inputPinYin);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String content = "";
                    try {
                        InputStream stream = response.body().byteStream();
                        InputStreamReader reader = new InputStreamReader(stream, "GBK");
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String inputLine = null;
                        while (((inputLine = bufferedReader.readLine()) != null)) {
                            content += inputLine;
                        }
                        Log.d(logTag, "get recommend tips: \n" + content);
                        if (!TextUtils.isEmpty(content)) {
                            getRecommendList(content);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(logTag, "get recommend tips failed...\n");
            }
        });
    }

    private void getRecommendList(String content) {
        content = content.replace("\r\n", "");
        String[] splitArr = content.toUpperCase().split("RELWORD=");
        if (searchTipsList == null)
            searchTipsList = new ArrayList<>();
        else
            searchTipsList.clear();
        for (String split : splitArr) {
            if (split.contains("RNUM=") && split.contains("SNUM=")) {
                int index = split.indexOf("SNUM=");
                int rIndex = split.indexOf("RNUM");
                String recommendStr = split.substring(0, index);
                String searchCountStr = split.substring(index + 5, rIndex);
                HashMap<String, String> map = new HashMap<>();
                map.put("info", recommendStr);
                map.put("count", searchCountStr);
                searchTipsList.add(map);
            }
        }
        historyContent.setVisibility(View.INVISIBLE);
        searchTipsContent.setVisibility(View.VISIBLE);
        if (simpleAdapter == null) {
            simpleAdapter = new SimpleAdapter(this, searchTipsList, R.layout.search_tips_list_view_item,
                    new String[]{"info", "count"}, new int[]{R.id.text_view_tip_name, R.id.text_view_search_times});
            tipsLV.setAdapter(simpleAdapter);
        } else {
            simpleAdapter.notifyDataSetChanged();
        }
    }

//    private PopupMenu popupMenu;
//    private void showPopupMenu(List<String> recommendList) {
//        closePopupMenu();
//        if (recommendList.size() == 0) return;
//        popupMenu = new PopupMenu(this, editTextSearch);
//        Menu menu = popupMenu.getMenu();
//        for (String item : recommendList) {
//            menu.add(item);
//        }
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                String searchText = item.getTitle().toString();
//                editTextSearch.setText(searchText);
////                startSearch();
//                return true;
//            }
//        });
//        popupMenu.show();
//    }
//
//    private void closePopupMenu() {
//        if (popupMenu != null) {
//            popupMenu.dismiss();
//            popupMenu = null;
//        }
//    }
//
//    private PopupWindow popupWindow;
//    private List<String> recommendList;
//    private ArrayAdapter adapter;
//
//    private void showPopupWindow() {
//        View contentView = LayoutInflater.from(this).inflate(R.layout.recommend_search_tips_layout, null);
//        if (popupWindow == null) {
//            popupWindow = new PopupWindow(this);
//            popupWindow.setContentView(contentView);
//            popupWindow.setWidth(editTextSearch.getWidth());
//            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//            popupWindow.getContentView().setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Log.d(logTag, "click popupwindow\n");
//                    popupWindow.setFocusable(false);
//                    popupWindow.dismiss();
//                    return true;
//                }
//            });
//            popupWindow.setFocusable(true);
//            popupWindow.setTouchable(true);
////            popupWindow.setOutsideTouchable(true);
//            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
////            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
//        }
//        final ListView listView = (ListView) contentView.findViewById(R.id.list_view_tips);
//        adapter = new ArrayAdapter<String>(this, R.layout.history_list_view_left_item, R.id.history_list_item_tv, recommendList);
//        listView.setAdapter(adapter);
//
//        popupWindow.showAsDropDown(editTextSearch, 0, 0);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(logTag, "click listview item\n");
//                TextView textView = (TextView) view.findViewById(R.id.history_list_item_tv);
//                String searchText = textView.getText().toString();
//                editTextSearch.setFocusable(true);
//                editTextSearch.setText(searchText);
//            }
//        });
//    }
//
//    private void closePopupWindow() {
//        if (popupWindow != null) {
//            popupWindow.dismiss();
//            popupWindow = null;
//        }
//    }

}
