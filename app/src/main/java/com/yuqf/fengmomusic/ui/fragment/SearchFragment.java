package com.yuqf.fengmomusic.ui.fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.ui.activity.SearchActivity;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private Button btnOpen;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        btnOpen = (Button) rootView.findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startActivity(getActivity(), SearchActivity.class, null);
//                showPopupWindow();
            }
        });
        return rootView;
    }

    private PopupWindow popupWindow;
    private List<String> recommendList;
    private ArrayAdapter adapter;

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.recommend_search_tips_layout, null);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(getContext());
            popupWindow.setContentView(contentView);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setTouchable(true);
//            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
//            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        }
        final ListView listView = (ListView) contentView.findViewById(R.id.list_view_tips);
        recommendList = new ArrayList<>();
        recommendList.add("xxxxxxxxxxxxxxxxxxx8");
        recommendList.add("xxxxxxxxxxxxxxxxxxx7");
        recommendList.add("xxxxxxxxxxxxxxxxxxx6");
        recommendList.add("xxxxxxxxxxxxxxxxxxx5");
        recommendList.add("xxxxxxxxxxxxxxxxxxx4");
        recommendList.add("xxxxxxxxxxxxxxxxxxx3");
        recommendList.add("xxxxxxxxxxxxxxxxxxx2");
        recommendList.add("xxxxxxxxxxxxxxxxxxx1");
        adapter = new ArrayAdapter<String>(getContext(), R.layout.history_list_view_left_item, R.id.history_list_item_tv, recommendList);
        listView.setAdapter(adapter);
        popupWindow.showAsDropDown(btnOpen, 0, 0);
        popupWindow.setFocusable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("xxxxxx", "click listview item\n");
                TextView textView = (TextView) view.findViewById(R.id.history_list_item_tv);
                String searchText = textView.getText().toString();
                CommonUtils.showToast(searchText, true);
            }
        });

    }
}
