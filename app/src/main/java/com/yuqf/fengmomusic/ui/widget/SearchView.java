package com.yuqf.fengmomusic.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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
import com.yuqf.fengmomusic.ui.activity.SearchActivity;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.util.HashMap;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;

/**
 * Created by Yuqf on 2016/12/1.
 */
public class SearchView extends FrameLayout {

    private static final int MessageFlag = 110;
    private int showingIndex = -1;
    private String hotSearchWordPrefix;
    private String[] hotWordArray;
    private ImageButton btnSearch;
    private TextSwitcher textSwitcher;
    private final String logTag = "SearchView";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(logTag, String.format("visible=%s\nactivite=%s\nshown=%s", getVisibility(), isActivated(), isShown()));
            if (msg.what == MessageFlag && isShown()) {
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
            TextView textView = new TextView(MyApplication.getContext());
            textView.setGravity(CENTER_VERTICAL | LEFT);
            textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(MyApplication.getContext(), 32), CENTER_VERTICAL));
            textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorAccent));
            textView.setTextSize(14);
            return textView;
        }
    };

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(context).inflate(R.layout.search_view_layout, this, true);

        hotSearchWordPrefix = context.getResources().getString(R.string.search_hint_prefix);
        hotWordArray = context.getResources().getStringArray(R.array.hot_search_word_list);

        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        textSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startActivity(MyApplication.getContext(), SearchActivity.class, null);
            }
        });

        btnSearch = (ImageButton) findViewById(R.id.go_search_btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingIndex >= 0) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(Global.INTENT_HOT_RECOMMEND_KEY, hotWordArray[showingIndex]);
                    CommonUtils.startActivity(MyApplication.getContext(), SearchActivity.class, hashMap);
                } else
                    CommonUtils.startActivity(MyApplication.getContext(), SearchActivity.class, null);
            }
        });
        if (!isInEditMode()) {

            textSwitcher.setFactory(viewFactory);
            textSwitcher.setInAnimation(context, R.anim.text_switcher_fade_in);
            textSwitcher.setOutAnimation(context, R.anim.text_switcher_fade_out);

            if (hotWordArray.length > 0 && showingIndex < 0) {
                showingIndex = 0;
                String text = hotSearchWordPrefix + hotWordArray[showingIndex];
                textSwitcher.setText(text);
            }
            handler.sendEmptyMessageDelayed(MessageFlag, 20000);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        handler.removeMessages(MessageFlag);
        if (hasWindowFocus && isShown())
            handler.sendEmptyMessageDelayed(MessageFlag, 20000);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (handler != null) {
            handler.removeMessages(MessageFlag);
            handler.sendEmptyMessageDelayed(MessageFlag, 20000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeMessages(MessageFlag);
        }
    }
}
