package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.ui.activity.SearchActivity;
import com.yuqf.fengmomusic.utils.CommonUtils;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private ImageButton btnSearch;
    private View rootView;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        initEditText();

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
