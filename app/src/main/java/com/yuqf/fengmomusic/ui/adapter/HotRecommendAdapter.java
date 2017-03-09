package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.entity.GSonHotRecommend;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HotRecommendAdapter extends RecyclerView.Adapter<HotRecommendAdapter.HotRecommendHolder> {

    private List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItemList;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener itemClickListener;
    private boolean isPopular;

    /*是否为推荐tab页，第三个tab为推荐页*/
    public HotRecommendAdapter(boolean isPopular) {
        super();
        this.isPopular = isPopular;
        hotRecommendItemList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public HotRecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (isPopular)
            itemView = inflater.inflate(R.layout.recommend_recycler_view_item, parent, false);
        else
            itemView = inflater.inflate(R.layout.item_recyclerview_ex_layout, parent, false);
        HotRecommendHolder holder = new HotRecommendHolder(itemView);
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return holder;
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(HotRecommendHolder holder, int position) {
        GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem item = hotRecommendItemList.get(position);
        holder.titleTV.setText(item.getName());
        CommonUtils.setTextMarquee(holder.titleTV);
        holder.countTV.setText(item.getInFo());
        holder.itemView.setTag(position);
        holder.gifImageView.setImageResource(R.drawable.ic_launcher_72);
        CommonUtils.downloadPic(item.getPic(), holder.gifImageView);
    }

    @Override
    public int getItemCount() {
        return hotRecommendItemList.size();
    }

    public void setHotRecommendItemList(List<GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem> hotRecommendItemList) {
        this.hotRecommendItemList = hotRecommendItemList;
        notifyDataSetChanged();
    }

    public GSonHotRecommend.HotRecommendSecond.HotRecommendList.HotRecommendItem getHotRecommendItem(int position) {
        if (position < hotRecommendItemList.size()) {
            return hotRecommendItemList.get(position);
        }
        return null;
    }

    class HotRecommendHolder extends RecyclerView.ViewHolder {
        public GifImageView gifImageView;
        private TextView titleTV;
        private TextView countTV;

        public HotRecommendHolder(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.gif_iv_recommend);
            titleTV = (TextView) itemView.findViewById(R.id.recommend_item_title_tv);
            countTV = (TextView) itemView.findViewById(R.id.recommend_item_info_tv);
        }
    }
}
