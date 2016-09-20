package com.yuqf.fengmomusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class SingerFragment extends Fragment {

    private final String logTag = "SingerFragment";
    private View parentView;
    private LinearLayout singerKindContainer;
    private LinearLayout singerOrderContainer;
    private List<SingerListFragment> fragmentList;
    private List<String> singerKindOrderList;

    private int category = 0;//0-10
    private int prefixIndex = 0;
    private SingerListFragment curFragment;

    public SingerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentList = new ArrayList<>();
        singerKindOrderList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_singer, container, false);

            initChoiceLayout();
        } else {
            ViewGroup viewGroup = (ViewGroup) parentView.getParent();
            if (viewGroup != null)
                viewGroup.removeView(parentView);
        }
        return parentView;
    }

    private void initChoiceLayout() {
        singerKindContainer = (LinearLayout) parentView.findViewById(R.id.singer_kinds_view);
        singerOrderContainer = (LinearLayout) parentView.findViewById(R.id.singer_order_view);

        String[] singerKindsArr = getResources().getStringArray(R.array.singer_kind_list);
        String[] singerOrderArr = getResources().getStringArray(R.array.singer_order_list);
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContext());

        for (int i = 0; i < singerKindsArr.length; i++) {
            String kind = singerKindsArr[i];
            View view = inflater.inflate(R.layout.singer_order_choice_layout, singerKindContainer, false);
            final TextView kindTV = (TextView) view.findViewById(R.id.singer_choice_tv);
            kindTV.setText(kind);
            initTopChoiceItem(i, category, singerKindsArr.length, view, kindTV);
            final int curIndex = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category == curIndex) return;
                    Log.d(logTag, "click singer kind index: " + String.valueOf(category));
                    View oldView = singerKindContainer.getChildAt(category);
                    TextView oldTV = (TextView) oldView.findViewById(R.id.singer_choice_tv);
                    oldTV.setSelected(false);
                    category = curIndex;
                    kindTV.setSelected(true);
                    showFragmentByKind();
                }
            });
            singerKindContainer.addView(view);
        }
        for (int i = 0; i < singerOrderArr.length; i++) {
            String order = singerOrderArr[i];
            View view = inflater.inflate(R.layout.singer_order_choice_layout, singerOrderContainer, false);
            final TextView orderTV = (TextView) view.findViewById(R.id.singer_choice_tv);
            orderTV.setText(order);
            initTopChoiceItem(i, prefixIndex, singerOrderArr.length, view, orderTV);
            final int curIndex = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prefixIndex == curIndex) return;
                    View oldView = singerOrderContainer.getChildAt(prefixIndex);
                    TextView oldTV = (TextView) oldView.findViewById(R.id.singer_choice_tv);
                    oldTV.setSelected(false);
                    prefixIndex = curIndex;
                    orderTV.setSelected(true);
                    showFragmentByKind();
                }
            });
            singerOrderContainer.addView(view);
        }
        showFragmentByKind();
    }

    private void initTopChoiceItem(int curIndex, int actualIndex, int length, View view, TextView textView) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (curIndex == 0) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, 3, 0);
        } else if (curIndex == length - 1) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(3, 0, 0, 0);
        } else {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(3, 0, 3, 0);
        }
        if (actualIndex == curIndex) {
            textView.setSelected(true);
        } else {
            textView.setSelected(false);
        }
    }

    private void showFragmentByKind() {

        if (curFragment != null && curFragment.getPrefix() == prefixIndex && curFragment.getCategory() == category) {

        } else {
            String curKOStr = String.valueOf(category) + "_" + String.valueOf(prefixIndex);
            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            SingerListFragment oldShowingFragment = curFragment;
            if (!singerKindOrderList.contains(curKOStr)) {
                curFragment = new SingerListFragment();
                curFragment.setCategory(category);
                curFragment.setPrefix(prefixIndex);

                fragmentList.add(curFragment);
                singerKindOrderList.add(curKOStr);

                transaction.add(R.id.fragment_container, curFragment);
            } else {
                for (SingerListFragment fragment : fragmentList) {
                    if (fragment.getPrefix() == prefixIndex && fragment.getCategory() == category) {
                        curFragment = fragment;
                        break;
                    }
                }
            }
            if (oldShowingFragment != null)
                transaction.hide(oldShowingFragment);
            transaction.show(curFragment);
            transaction.commit();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (fragmentList != null)
            fragmentList.clear();
    }
}
