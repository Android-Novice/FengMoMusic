package com.yuqf.fengmomusic.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.activity.SingerListActivity;
import com.yuqf.fengmomusic.utils.CommonUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends Fragment {


    public RecommendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        Button button = (Button) rootView.findViewById(R.id.btn_singer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startActivity(MyApplication.getContext(), SingerListActivity.class, null);
            }
        });
        return rootView;
    }

}
