package com.yuqf.fengmomusic.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

public class SearchActivity extends BaseActivity implements View.OnClickListener, ListView.OnItemClickListener {

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
        historyLeftLV.setOnItemClickListener(this);
        historyRightLV.setOnItemClickListener(this);

        btnClearText = (ImageButton) findViewById(R.id.btn_clear_text);
        btnHome = (ImageButton) findViewById(R.id.btn_home);
        btnClearHistory = (Button) findViewById(R.id.btn_clear_history);
        btnClearHistory.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnClearText.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE);
        loadSearchHistory();
        initEditText();
    }

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
                searchTipsContent.setVisibility(View.INVISIBLE);
                historyContent.setVisibility(View.VISIBLE);
                loadSearchHistory();
                break;
            case R.id.btn_home:
                finish();
                break;
        }
    }

    private void loadSearchHistory() {
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
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView historyTV = (TextView) view.findViewById(R.id.history_list_item_tv);
        String searchText = historyTV.getText().toString();
        startSearch(searchText);
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
            } else {
                btnClearText.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void startSearch(String searchText) {
//        String searchText = editTextSearch.getText().toString();
        editTextSearch.removeTextChangedListener(watcher);
        editTextSearch.setText(searchText);
        editTextSearch.addTextChangedListener(watcher);
        addSearchHistory(searchText);
        try {
            String gbStr = URLEncoder.encode(searchText, "GB2312");
            Log.d("SearchTextDecode", gbStr + "\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String utfStr = URLEncoder.encode(searchText, "utf-8");
            Log.d("SearchTextDecode", utfStr + "\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String asciiStr = URLEncoder.encode(searchText, "ascii");
            Log.d("SearchTextDecode", asciiStr + "\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String unicodeStr = URLEncoder.encode(searchText, "unicode");
            Log.d("SearchTextDecode", unicodeStr + "\n");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        CommonUtils.showToast("开始搜索啦~", true);
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
