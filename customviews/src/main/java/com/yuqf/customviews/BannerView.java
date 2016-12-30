package com.yuqf.customviews;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqf.customviews.Banner.IImageLoader;
import com.yuqf.customviews.Banner.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuqf on 2016/12/7.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private final String logTag = "BannerView";
    private final int FLAG = 110;
    private final int INDICATOR_WIDTH = 8;
    private final int INDICATOR_HEIGHT = 8;
    private final int INDICATOR_RES_ID = R.drawable.indicator_spot_drawable;
    private final int INDICATOR_MARIGIN = 3;

    private Context context;
    /*显示圆点的区域高度*/
    private int textAreaHeight;
    /*图片对应的指示点的宽高或者是直径，对应的TextView就是宽高*/
    private int spotRadius;
    private List<String> imageUrls;
    private List<ImageView> imageViews;
    private List<ImageView> indicators;
    private List<String> imageTitles;
    private ViewPager viewPager;
    private TextView currentTV;
    private LinearLayout indicatorContainer;
    private OnBannerClickListener bannerClickListener;
    private int showIndex = -1;
    private int imageCount;
    private IImageLoader imageLoader;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == FLAG) {

                return true;
            }
            return false;
        }
    });

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageViews = new ArrayList<>();
        indicators = new ArrayList<>();
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.banner_view_layout, this, true);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        currentTV = (TextView) rootView.findViewById(R.id.introduce_tv);
        indicatorContainer = (LinearLayout) rootView.findViewById(R.id.indicator_container);

    }

    /*设置图片的Url，目前暂时只支持网络图片*/
    public void setBannerList(IImageLoader imageLoader, List<String> imageUrls, List<String> imageTitles) {
        if (imageUrls == null || imageUrls.size() <= 0) {
            Log.e(logTag, "imageUrls is null or imageUrls.size() is 0");
            return;
        }
        if (imageLoader == null) {
            Log.e(logTag, "imageloader is nll");
            return;
        }
        this.imageLoader = imageLoader;
        imageCount = imageUrls.size();
        this.imageUrls = imageUrls;
        this.imageTitles = imageTitles;
    }

    private void createIndicators() {
        indicators.clear();
        indicatorContainer.removeAllViews();
        for (int i = 0; i < imageCount; i++) {
            ImageView indicator = new ImageView(this.context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(INDICATOR_WIDTH, INDICATOR_HEIGHT);
            layoutParams.setMargins(INDICATOR_MARIGIN, 0, INDICATOR_MARIGIN, 0);
            indicator.setLayoutParams(layoutParams);
            indicator.setBackgroundResource(INDICATOR_RES_ID);
            indicators.add(indicator);
            indicatorContainer.addView(indicator);
        }
    }

    private void createImageViews() {
        for (int i = 0; i <= imageCount + 1; i++) {
            ImageView imageView = new ImageView(this.context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imageUrl;
            if (i == 0)
                imageUrl = imageUrls.get(imageCount - 1);
            else if (i == imageCount + 1)
                imageUrl = imageUrls.get(0);
            else
                imageUrl = imageUrls.get(i - 1);
            imageLoader.loadImage(imageUrl, imageView);
            imageViews.add(imageView);
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View childView = imageViews.get(position);
            container.addView(childView);
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bannerClickListener != null) {
                        bannerClickListener.BannerClick(position);
                    }
                }
            });
            return childView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position < imageViews.size()) {
                container.removeView(imageViews.get(position));
            }
        }

        @Override
        public int getCount() {
            return imageViews.size();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int currentIndex = viewPager.getCurrentItem();
        if (state == 0 || state == 1) {
            if (currentIndex == 0) {
                viewPager.setCurrentItem(imageCount, false);
            } else {
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }
}
