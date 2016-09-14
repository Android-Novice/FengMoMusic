package com.yuqf.fengmomusic.ui.adapter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;

public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable drawable;
    private boolean showHeader;

    public LinearLayoutItemDecoration(boolean showHeader) {
        super();
//        TypedArray typedArray = MyApplication.getContext().obtainStyledAttributes(new int[]{R.attr});
//        drawable = typedArray.getDrawable(0);
        drawable = MyApplication.getContext().getResources().getDrawable(R.drawable.music_list_divider_line);
        this.showHeader = showHeader;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        int startIndex = showHeader ? 1 : 0;
        for (int i = startIndex; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            int top = view.getBottom();
            int bottom = top + drawable.getIntrinsicHeight();
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int totalCount = parent.getAdapter().getItemCount();
        int index = parent.getChildAdapterPosition(view);
        if (index == totalCount - 1 || index == 0)
            outRect.set(0, 0, 0, 0);
        else
            outRect.set(0, 0, 0, 1);
    }
}
