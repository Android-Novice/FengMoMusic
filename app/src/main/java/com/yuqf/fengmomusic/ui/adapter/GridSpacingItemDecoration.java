package com.yuqf.fengmomusic.ui.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;
    private int spanCount;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spacing, int spanCount, boolean includeEdge) {
        super();
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int columnIndex = position % spanCount;
        if (includeEdge) {
            outRect.left = spacing - columnIndex * spacing / spanCount;
            outRect.right = (columnIndex + 1) * spacing / spanCount;
        } else {
            outRect.left = columnIndex * spacing / spanCount;
            outRect.right = spacing - (columnIndex + 1) * spacing / spanCount;

        }
        outRect.top = spacing;

        int width = view.getWidth();
        int height = view.getHeight();
        Log.d("SpacingItem", String.format("width:%d\nheight:%d\nTop:%d\nLeft:%d\nRight:%d\nBottom:%d\n",
                width, height, outRect.top, outRect.left, outRect.right, outRect.bottom));
    }


}
