package com.yuqf.fengmomusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.GsonSingerList;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class SingerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String logTag = "RecyclerViewAdapter";
    private List<GsonSingerList.Singer> singerList;
    private LayoutInflater layoutInflater;
    private final int SingerType = 1;
    private final int FooterType = 2;
    private CommonUtils.OnRecyclerViewItemClickListener viewItemClickListener;
    private boolean loading;
    private Picasso picasso;

    public SingerRecyclerViewAdapter(Context context) {
        super();
        singerList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
        this.picasso = MyApplication.getPicasso();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SingerType:
                View singerView = layoutInflater.inflate(R.layout.item_singer_layout, parent, false);
                SingerHolder singerHolder = new SingerHolder(singerView);
                singerView.setClickable(true);

                return singerHolder;
            case FooterType:
                View footerView = layoutInflater.inflate(R.layout.item_load_more_layout, parent, false);
                FooterHolder footerHolder = new FooterHolder(footerView);
                return footerHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingerHolder) {
            GsonSingerList.Singer singer = singerList.get(position);
            String name = singer.getName();
            String url = CommonUtils.UrlHelper.Singer_Head_Get_Base_Url + singer.getPic();
            String playCount = String.valueOf(singer.getListen()) + MyApplication.getContext().getResources().getString(R.string.play_count_suffix);
            String musicCount = String.valueOf(singer.getMusic_num()) + MyApplication.getContext().getResources().getString(R.string.music_count_suffix);
            ((SingerHolder) holder).singerNameTV.setText(name);
            ((SingerHolder) holder).playCountTV.setText(playCount);
            ((SingerHolder) holder).musicCountTV.setText(musicCount);
            final ImageView headIV = ((SingerHolder) holder).singerHeadIV;
            picasso.load(url).placeholder(R.drawable.head_default).error(R.drawable.head_default).into(headIV);
        } else if (holder instanceof FooterHolder) {
            if (loading) {
                ((FooterHolder) holder).loadingView.setVisibility(View.VISIBLE);
                ((FooterHolder) holder).loadMoreView.setVisibility(View.GONE);
            } else {
                ((FooterHolder) holder).loadingView.setVisibility(View.GONE);
                ((FooterHolder) holder).loadMoreView.setVisibility(View.VISIBLE);
            }
        }
    }

    public CommonUtils.OnRecyclerViewItemClickListener getViewItemClickListener() {
        return viewItemClickListener;
    }

    public void setViewItemClickListener(CommonUtils.OnRecyclerViewItemClickListener viewItemClickListener) {
        this.viewItemClickListener = viewItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (singerList.size() > 0)
            return singerList.size() + 1;
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount())
            return FooterType;
        else
            return SingerType;
    }

    public void notifyLoadStatus(boolean loading) {
        this.loading = loading;
        if (loading)
            notifyDataSetChanged();
    }

    public void addItem(GsonSingerList.Singer singer) {
        singerList.add(singer);
        notifyDataSetChanged();
    }

    public void addItems(List<GsonSingerList.Singer> singers) {
        singerList.addAll(singers);
        notifyDataSetChanged();
    }

    public void reloadItems(List<GsonSingerList.Singer> singers) {
        singerList.clear();
        singerList.addAll(singers);
        notifyDataSetChanged();
    }

    public void removeItem(GsonSingerList.Singer singer) {
        singerList.remove(singer);
        notifyDataSetChanged();
    }

    public void removeItems(List<GsonSingerList.Singer> singers) {
        singerList.removeAll(singers);
        notifyDataSetChanged();
    }

    class SingerHolder extends RecyclerView.ViewHolder {
        private ImageView singerHeadIV;
        private TextView singerNameTV;
        private TextView musicCountTV;
        private TextView playCountTV;

        public SingerHolder(View itemView) {
            super(itemView);
            singerHeadIV = (ImageView) itemView.findViewById(R.id.singer_iv);
            singerNameTV = (TextView) itemView.findViewById(R.id.singer_name_tv);
            musicCountTV = (TextView) itemView.findViewById(R.id.music_count_tv);
            playCountTV = (TextView) itemView.findViewById(R.id.play_count_tv);
        }
    }
}

class FooterHolder extends RecyclerView.ViewHolder {
    public View loadingView;
    public View loadMoreView;

    public FooterHolder(View itemView) {
        super(itemView);
        loadingView = itemView.findViewById(R.id.loading_view);
        loadMoreView = itemView.findViewById(R.id.load_more_view);
    }
}
