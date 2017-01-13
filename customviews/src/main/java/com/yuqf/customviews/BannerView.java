package com.yuqf.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqf.customviews.Banner.IImageLoader;
import com.yuqf.customviews.Banner.OnBannerClickListener;
import com.yuqf.customviews.Banner.TransferAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Yuqf on 2016/12/7.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    final String logTag = "BannerView";
    final int INDICATOR_DEFAULT_RADIUS = 8;
    final int INDICATOR_NORMAL_RES_ID = R.drawable.indicator_spot_background;
    final int INDICATOR_SELECTED_RES_ID = R.drawable.indicator_spot_selected_background;
    final int INDICATOR_MARIGIN = 3;
    final int MESSAGE_FLAG = 110;

    private Context context;
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
    /*显示圆点的区域高度*/
    private int indicatorContainerPaddingLeft;
    private int indicatorContainerPaddingRight;
    private int indicatorContainerPaddingTop;
    private int indicatorContainerPaddingBottom;
    /*图片对应的指示点的宽高或者是直径，对应的TextView就是宽高*/
    private int indicatorRadius;
    /*自动轮播延时*/
    private int playDelaySeconds = 2;
    private int indicatorTextSize;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_FLAG && isShown()) {
                Log.d(logTag, "=========handleMessage=======");
                showIndex++;
                int currentIndex = viewPager.getCurrentItem();
                viewPager.setCurrentItem(currentIndex + 1, true);
                start(false);
            }
        }
    };

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
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View rootView = LayoutInflater.from(context).inflate(R.layout.banner_view_layout, this, true);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        currentTV = (TextView) rootView.findViewById(R.id.introduce_tv);
        indicatorContainer = (LinearLayout) rootView.findViewById(R.id.indicator_container);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView, defStyleAttr, 0);
        indicatorRadius = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicator_radius, INDICATOR_DEFAULT_RADIUS);
        indicatorContainerPaddingBottom = typedArray.getLayoutDimension(R.styleable.BannerView_indicator_container_padding_bottom, 0);
        indicatorContainerPaddingLeft = typedArray.getLayoutDimension(R.styleable.BannerView_indicator_container_padding_left, 0);
        indicatorContainerPaddingRight = typedArray.getLayoutDimension(R.styleable.BannerView_indicator_container_padding_right, 0);
        indicatorContainerPaddingTop = typedArray.getLayoutDimension(R.styleable.BannerView_indicator_container_padding_top, 0);
        playDelaySeconds = typedArray.getInt(R.styleable.BannerView_auto_play_delay_seconds, 2);
        indicatorTextSize = typedArray.getDimensionPixelSize(R.styleable.BannerView_indicator_item_text_size, 16);
        typedArray.recycle();
        Log.d(logTag, "======TextSize: " + indicatorTextSize);
        Log.d(logTag, "======playDelaySeconds: " + playDelaySeconds);
        currentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, indicatorTextSize);
        indicatorContainer.setPadding(indicatorContainerPaddingLeft, indicatorContainerPaddingTop, indicatorContainerPaddingRight, indicatorContainerPaddingBottom);
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
        Log.d(logTag, "======image count: " + String.valueOf(imageUrls.size()));
        stop();
        this.imageLoader = imageLoader;
        imageCount = imageUrls.size();
        this.imageUrls = imageUrls;
        this.imageTitles = imageTitles;

        createImageViews();
        createIndicators();

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(1, false);
        start(true);
    }

    public void setPageRandomTransformer() {
        int value = new Random().nextInt(17) + 1;
        switch (value) {
            case 1:
                setPageTransformer(TransferAnimation.FlipHorizontal);
                break;
            case 2:
                setPageTransformer(TransferAnimation.Accordion);
                break;
            case 3:
                setPageTransformer(TransferAnimation.BackgroundToForeground);
                break;
            case 4:
                setPageTransformer(TransferAnimation.CubeIn);
                break;
            case 5:
                setPageTransformer(TransferAnimation.CubeOut);
                break;
            case 6:
                setPageTransformer(TransferAnimation.Default);
                break;
            case 7:
                setPageTransformer(TransferAnimation.DepthPage);
                break;
            case 8:
                setPageTransformer(TransferAnimation.FlipVertical);
                break;
            case 9:
                setPageTransformer(TransferAnimation.ForegroundToBackground);
                break;
            case 10:
                setPageTransformer(TransferAnimation.RotateDown);
                break;
            case 11:
                setPageTransformer(TransferAnimation.ZoomOutSlide);
                break;
            case 12:
                setPageTransformer(TransferAnimation.ZoomOut);
                break;
            case 13:
                setPageTransformer(TransferAnimation.Tablet);
                break;
            case 14:
                setPageTransformer(TransferAnimation.Stack);
                break;
            case 15:
                setPageTransformer(TransferAnimation.RotateUp);
                break;
            case 16:
                setPageTransformer(TransferAnimation.ScaleInOut);
                break;
            case 17:
                setPageTransformer(TransferAnimation.ZoomIn);
                break;
            default:
                setPageTransformer(TransferAnimation.Default);
                break;
        }
    }

    public void setPageTransformer(Class<? extends ViewPager.PageTransformer> tClass) {
        try {
            viewPager.setPageTransformer(true, tClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void start(boolean removeSrc) {
        if (handler != null) {
            Log.d(logTag, "sendEmptyMessageDelayed======1");
            if (removeSrc && handler.hasMessages(MESSAGE_FLAG)) {
                Log.d(logTag, "sendEmptyMessageDelayed======2");
                handler.removeMessages(MESSAGE_FLAG);
            }
            if (!handler.hasMessages(MESSAGE_FLAG)) {
                Log.d(logTag, "sendEmptyMessageDelayed======3");
                handler.sendEmptyMessageDelayed(MESSAGE_FLAG, playDelaySeconds * 1000);
            }
        }
    }

    private void stop() {
        if (handler != null) {
            handler.removeMessages(MESSAGE_FLAG);
        }
    }

    private void createIndicators() {
        indicators.clear();
        indicatorContainer.removeAllViews();
        for (int i = 0; i < imageCount; i++) {
            ImageView indicator = new ImageView(this.context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indicatorRadius, indicatorRadius);
            layoutParams.setMargins(INDICATOR_MARIGIN, 0, INDICATOR_MARIGIN, 0);
            indicator.setLayoutParams(layoutParams);
            indicator.setBackgroundResource(INDICATOR_NORMAL_RES_ID);
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

    public void setBannerClickListener(OnBannerClickListener bannerClickListener) {
        this.bannerClickListener = bannerClickListener;
    }

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(logTag, "onTouchEvent========Touch Down========");
                stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                Log.d(logTag, "onTouchEvent========Touch Up========");
                start(true);
                break;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(logTag, "dispatchTouchEvent========Touch Down========");
                stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                Log.d(logTag, "dispatchTouchEvent========Touch Up========");
                start(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int currentIndex = viewPager.getCurrentItem();
        Log.d(logTag, "========onPageScrollStateChanged: " + String.valueOf(currentIndex));
        if (state == 0 || state == 1) {
            if (currentIndex == 0) {
                viewPager.setCurrentItem(imageCount, false);
            } else if (currentIndex == imageCount + 1) {
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(logTag, "========onPageSelected: " + String.valueOf(position));
        if (position == 0)
            position = 1;
        if (position == imageCount + 1)
            position = imageCount;
        showIndex = position - 1;
        for (int i = 0; i < indicators.size(); i++) {
            ImageView indicator = indicators.get(i);
            if (i == showIndex)
                indicator.setBackgroundResource(INDICATOR_SELECTED_RES_ID);
            else
                indicator.setBackgroundResource(INDICATOR_NORMAL_RES_ID);
        }
        String text = imageTitles.get(showIndex);
        currentTV.setText(text);
        currentTV.setSingleLine(true);
        currentTV.setSelected(true);
        currentTV.setFocusable(true);
        currentTV.setFocusableInTouchMode(true);
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
                        Log.d(logTag, "========setOnClickListener: " + String.valueOf(position));
                        int curPosition = position - 1;
                        if (position == 1)
                            curPosition = 0;
                        if (position == imageCount + 1)
                            curPosition = imageCount - 1;
                        bannerClickListener.BannerClick(curPosition);
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(logTag, "=========onAttachedToWindow=======");
        start(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(logTag, "=========onDetachedFromWindow=======");
        super.onDetachedFromWindow();
        stop();
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d(logTag, "=========onWindowFocusChanged=======");
        super.onWindowFocusChanged(hasWindowFocus);
        stop();
        if (hasWindowFocus && isShown())
            start(true);
    }
}
