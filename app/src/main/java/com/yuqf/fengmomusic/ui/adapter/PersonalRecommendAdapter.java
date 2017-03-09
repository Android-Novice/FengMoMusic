package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.ui.entity.GsonPersonalRecommendationItem;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Yuqf on 2017/3/1.
 */

public class PersonalRecommendAdapter extends RecyclerView.Adapter<PersonalRecommendAdapter.PersonalViewHolder> {

    private List<GsonPersonalRecommendationItem> personalList;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener itemClickListener;

    public PersonalRecommendAdapter() {
        personalList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public PersonalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_recyclerview_ex_layout, parent, false);
        PersonalViewHolder viewHolder = new PersonalViewHolder(itemView);
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonalViewHolder holder, int position) {
        GsonPersonalRecommendationItem personalItem = personalList.get(position);
        holder.itemView.setTag(position);
        holder.countTV.setText(personalItem.getPlaycnt() + "次播放");
        holder.titleTV.setText(personalItem.getName());
        CommonUtils.setTextMarquee(holder.titleTV);
        holder.gifImageView.setImageResource(R.drawable.ic_launcher_72);
        CommonUtils.downloadPic(personalItem.getPic(), holder.gifImageView);
    }

    @Override
    public int getItemCount() {
        return personalList.size();
    }

    public void setPersonalList(List<GsonPersonalRecommendationItem> personalList) {
        this.personalList = personalList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public GsonPersonalRecommendationItem getPersonalItem(int position) {
        if (personalList.size() > position) {
            return personalList.get(position);
        }
        return null;
    }

    class PersonalViewHolder extends RecyclerView.ViewHolder {
        public GifImageView gifImageView;
        private TextView titleTV;
        private TextView countTV;

        public PersonalViewHolder(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.gif_iv_recommend);
            titleTV = (TextView) itemView.findViewById(R.id.recommend_item_title_tv);
            countTV = (TextView) itemView.findViewById(R.id.recommend_item_info_tv);
        }
    }
}
