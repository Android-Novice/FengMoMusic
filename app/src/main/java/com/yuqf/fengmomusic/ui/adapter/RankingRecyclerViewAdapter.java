package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.GsonRankingList;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class RankingRecyclerViewAdapter extends RecyclerView.Adapter<RankingRecyclerViewAdapter.RankingHolder> {

    private final String logTag = "RecyclerViewAdapter";
    private List<GsonRankingList.ChildRanking> childRankingList;
    private LayoutInflater layoutInflater;
    private List<String> sourceIdList;
    private CommonUtils.OnRecyclerViewItemClickListener viewItemClickListener;
    private Picasso picasso;

    public RankingRecyclerViewAdapter() {
        super();
        layoutInflater = LayoutInflater.from(MyApplication.getContext());
        picasso = MyApplication.getPicasso();
        childRankingList = new ArrayList<>();
        sourceIdList = new ArrayList<>();
    }

    @Override
    public RankingRecyclerViewAdapter.RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_ranking_layout, parent, false);
        RankingHolder holder = new RankingHolder(view);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewItemClickListener != null) {
                    viewItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RankingRecyclerViewAdapter.RankingHolder holder, int position) {
        GsonRankingList.ChildRanking childRanking = childRankingList.get(position);
        holder.rankingNameTV.setText(childRanking.getName());
        holder.rankingDateTV.setText(childRanking.getInfo());
        holder.rankingInfoTV.setText(childRanking.getIntro());
        String picPath = childRanking.getPic2();
        if (TextUtils.isEmpty(picPath))
            picPath = childRanking.getPic5();
        if (TextUtils.isEmpty(picPath))
            picPath = childRanking.getPic();
        if (!TextUtils.isEmpty(picPath)) {
            picasso.load(picPath).placeholder(R.drawable.ranking_default).error(R.drawable.ranking_default).into(holder.rankingIV);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return childRankingList.size();
    }

    public GsonRankingList.ChildRanking GetChildByPosition(int position) {
        if (position >= childRankingList.size())
            return null;
        else
            return childRankingList.get(position);
    }

    public void addItem(GsonRankingList.ChildRanking ranking) {
        addHavePicItem(ranking);
        notifyDataSetChanged();
    }

    public void addItems(List<GsonRankingList.ChildRanking> rankings) {
        addHavePicItems(rankings);
        notifyDataSetChanged();
    }

    public void reloadItems(List<GsonRankingList.ChildRanking> rankings) {
        childRankingList.clear();
        sourceIdList.clear();
        addHavePicItems(rankings);
        notifyDataSetChanged();
    }

    private void addHavePicItems(List<GsonRankingList.ChildRanking> rankings) {
        for (GsonRankingList.ChildRanking childRanking : rankings) {
            addHavePicItem(childRanking);
        }
    }

    private void addHavePicItem(GsonRankingList.ChildRanking childRanking) {
        String oldDate = childRanking.getInfo();
        oldDate = oldDate.replace("更新于", "");
        childRanking.setInfo(oldDate);
        if (isHavePic(childRanking)) {
            childRankingList.add(childRanking);
            sourceIdList.add(childRanking.getSourceid());
        }
        List<GsonRankingList.ChildRanking> grandsonList = childRanking.getChild();
        if (grandsonList != null && grandsonList.size() >= 0) {
            for (GsonRankingList.ChildRanking grandson : grandsonList) {
                if (isHavePic(grandson)) {
                    if (!sourceIdList.contains(grandson.getSourceid())) {
                        grandson.setInfo(oldDate);
                        childRankingList.add(grandson);
                        sourceIdList.add(grandson.getSourceid());
                    }
                }
            }
        }
    }

    private boolean isHavePic(GsonRankingList.ChildRanking ranking) {
        if (TextUtils.isEmpty(ranking.getPic()) && TextUtils.isEmpty(ranking.getPic2()) && TextUtils.isEmpty(ranking.getPic5()))
            return false;
        return true;
    }

    public void removeItem(GsonRankingList.ChildRanking ranking) {
        removeRanking(ranking);
        notifyDataSetChanged();
    }

    public void removeItems(List<GsonRankingList.ChildRanking> rankings) {
        for (GsonRankingList.ChildRanking ranking : rankings) {
            removeRanking(ranking);
        }
        notifyDataSetChanged();
    }

    private void removeRanking(GsonRankingList.ChildRanking ranking) {
        if (isHavePic(ranking)) {
            childRankingList.remove(ranking);
            sourceIdList.remove(ranking.getSourceid());
        }
        List<GsonRankingList.ChildRanking> grandsonList = ranking.getChild();
        if (grandsonList != null && grandsonList.size() >= 0) {
            for (GsonRankingList.ChildRanking grandson : grandsonList) {
                if (isHavePic(grandson)) {
                    childRankingList.remove(grandson);
                    sourceIdList.remove(grandson.getSourceid());
                }
            }
        }
    }

    public CommonUtils.OnRecyclerViewItemClickListener getViewItemClickListener() {
        return viewItemClickListener;
    }

    public void setViewItemClickListener(CommonUtils.OnRecyclerViewItemClickListener viewItemClickListener) {
        this.viewItemClickListener = viewItemClickListener;
    }

    class RankingHolder extends RecyclerView.ViewHolder {
        private ImageView rankingIV;
        private TextView rankingNameTV;
        private TextView rankingDateTV;
        private TextView rankingInfoTV;

        public RankingHolder(View itemView) {
            super(itemView);
            rankingIV = (ImageView) itemView.findViewById(R.id.ranking_iv);
            rankingNameTV = (TextView) itemView.findViewById(R.id.ranking_name_tv);
            rankingDateTV = (TextView) itemView.findViewById(R.id.ranking_date_tv);
            rankingInfoTV = (TextView) itemView.findViewById(R.id.ranking_info_tv);
        }
    }
}
