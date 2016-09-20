package com.yuqf.fengmomusic.interfaces;

import android.view.View;

public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, int position);

    //click the downloading on the imagebutton of music list item
    void onItemDownloadClick(View view, int position);
}