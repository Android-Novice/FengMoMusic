package com.yuqf.fengmomusic.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;

/**
 * Created by Yuqf on 2017/1/17.
 */
public class RecyclerViewEx extends FrameLayout {

    private Context context;
    private TextView titleTV;
    private TextView moreTV;
    private TextView introduceTV;
    private ImageView introduceIV;
    private LinearLayout introduceArea;
    private RecyclerView recyclerView;
    private OnClickListener clickListener;

    public RecyclerViewEx(Context context) {
        this(context, null);
    }

    public RecyclerViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs, defStyleAttr);
    }

    private void initViews(Context context, AttributeSet attrs, int defStyleAttr) {
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(context).inflate(R.layout.custom_recyclerview, this, true);
        this.context = context;
        titleTV = (TextView) findViewById(R.id.recycler_title);
        moreTV = (TextView) findViewById(R.id.btn_more);
        introduceTV = (TextView) findViewById(R.id.text_view_introduce);
        introduceArea = (LinearLayout) findViewById(R.id.introduce_area);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        introduceIV = (ImageView) findViewById(R.id.image_view_title);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewEx, defStyleAttr, 0);
        String introduce = typedArray.getString(R.styleable.RecyclerViewEx_view_introduce);
        String title = typedArray.getString(R.styleable.RecyclerViewEx_view_title);
        String moreBtnText = typedArray.getString(R.styleable.RecyclerViewEx_more_btn_text);
        int introduceAreaVisible = typedArray.getInteger(R.styleable.RecyclerViewEx_introduce_area_visibility, 1);
        int moreBtnVisible = typedArray.getInteger(R.styleable.RecyclerViewEx_more_btn_visibility, 1);
        int srcResId = typedArray.getResourceId(R.styleable.RecyclerViewEx_introduce_src, 0);
        typedArray.recycle();

        introduceIV.setImageResource(srcResId);
        introduceTV.setText(introduce);
        titleTV.setText(title);
        if (!TextUtils.isEmpty(moreBtnText)) {
            moreTV.setText(moreBtnText);
        }

        setMoreBtnVisibility(moreBtnVisible);
        setIntroduceAreaVisibility(introduceAreaVisible);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        moreTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });
    }

    public void setIntroduceText(String introduceText) {
        introduceTV.setText(introduceText);
    }

    public void setIntroduceBitmap(Bitmap bitmap) {
        introduceIV.setImageBitmap(bitmap);
    }

    public void setViewTitle(String title) {
        titleTV.setText(title);
    }

    public void setMoreBtnVisibility(int visibility) {
        moreTV.setVisibility(visibility);
    }

    public void setIntroduceAreaVisibility(int visibility) {
        introduceArea.setVisibility(visibility);
    }

    public void setAdapter(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    public void setMoreBtnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
