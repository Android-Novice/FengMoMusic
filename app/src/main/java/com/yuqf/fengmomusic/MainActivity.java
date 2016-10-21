package com.yuqf.fengmomusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.media.MusicService;
import com.yuqf.fengmomusic.ui.fragment.MineFragment;
import com.yuqf.fengmomusic.ui.fragment.RankingFragment;
import com.yuqf.fengmomusic.ui.fragment.SearchFragment;
import com.yuqf.fengmomusic.ui.fragment.SingerFragment;
import com.yuqf.fengmomusic.utils.CommonUtils;

public class MainActivity extends BaseActivity {

    private TextView[] topTextViews;
    private Fragment[] fragments;
    private int selectedIndex = -1;
    private int screenWidth;
    private ImageView circlePointIV;
    private ViewPager viewPager;
    private ViewPagerPageChangeListener pageChangeListener;
    private final String logTag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTopBars();
        hideToolBar();

        initViewPager();
        initTopViews();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService musicService = ((MusicService.MusicPlayerBinder) service).getService();
            MyApplication.setMusicService(musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyApplication.setMusicService(null);
        }
    };

    private void initViewPager() {
        circlePointIV = (ImageView) findViewById(R.id.selected_category_iv);

        fragments = new Fragment[4];
        fragments[0] = new SingerFragment();
        fragments[1] = new RankingFragment();
        fragments[2] = new SearchFragment();
        fragments[3] = new MineFragment();

        viewPager = (ViewPager) findViewById(R.id.viewpager_main);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        pageChangeListener = new ViewPagerPageChangeListener();
        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    private void initTopViews() {
        Log.d(logTag, "initTopViews");
        screenWidth = CommonUtils.getScreenWidth(this);
        Log.d(logTag, "screenWidth: " + String.valueOf(screenWidth));
        topTextViews = new TextView[4];
        topTextViews[0] = (TextView) findViewById(R.id.text_view_singer);
        topTextViews[1] = (TextView) findViewById(R.id.text_view_ranking);
        topTextViews[2] = (TextView) findViewById(R.id.text_view_search);
        topTextViews[3] = (TextView) findViewById(R.id.text_view_mine);
        TopTextViewClickListener listener = new TopTextViewClickListener();
        for (TextView tv : topTextViews) {
            tv.setOnClickListener(listener);
        }
        topTextViews[0].performClick();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    private class ViewPagerPageChangeListener implements android.support.v4.view.ViewPager.OnPageChangeListener {
        final ViewGroup.LayoutParams params = circlePointIV.getLayoutParams();

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (selectedIndex < 0) {
                moveSelectedView(0, false);
                return;
            }
            Log.d("ViewPagerListener", String.valueOf(position) + "_" + String.valueOf(selectedIndex) + "_" + String.valueOf(positionOffset));
            int movingLeft = 0;
            int perCategoryWidth = screenWidth / 4;
            if (selectedIndex <= position)//move to right pager
            {
                movingLeft = (int) (perCategoryWidth * (position + .5f) + perCategoryWidth * positionOffset);
            } else if (selectedIndex > position) {
                movingLeft = (int) (perCategoryWidth * (position + 1.5f) - perCategoryWidth * (1 - positionOffset));
            }
            ((ViewGroup.MarginLayoutParams) params).setMargins(movingLeft - circlePointIV.getWidth() / 2, 0, 0, 0);
            circlePointIV.requestLayout();
        }

        @Override
        public void onPageSelected(int position) {
            Log.d("ViewPagerListener", String.valueOf(position));
//            selectedIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                int position = viewPager.getCurrentItem();
                Log.d("ViewPagerListener", "viewPager currentItem: " + String.valueOf(position));
                moveSelectedView(position, false);
            }
        }
    }

    private class TopTextViewClickListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(logTag, "TopTextViewClickListener");
            int curIndex = -1;
            switch (v.getId()) {
                case R.id.text_view_singer:
                    curIndex = 0;
                    break;
                case R.id.text_view_ranking:
                    curIndex = 1;
                    break;
                case R.id.text_view_search:
                    curIndex = 2;
                    break;
                case R.id.text_view_mine:
                    curIndex = 3;
                    break;
            }
            if (selectedIndex == curIndex) {
                return;
            }
            moveSelectedView(curIndex, true);
        }
    }

    private void moveSelectedView(int curIndex, boolean useAnimation) {
        if (useAnimation) {
            viewPager.setCurrentItem(curIndex);
        } else {
            int perCategoryWidth = screenWidth / 4;
            int endLeft = curIndex * perCategoryWidth + perCategoryWidth / 2;
            final ViewGroup.LayoutParams params = circlePointIV.getLayoutParams();
            ((ViewGroup.MarginLayoutParams) params).setMargins(endLeft - circlePointIV.getWidth() / 2, 0, 0, 0);
            circlePointIV.requestLayout();
            selectedIndex = curIndex;
        }
    }
}
