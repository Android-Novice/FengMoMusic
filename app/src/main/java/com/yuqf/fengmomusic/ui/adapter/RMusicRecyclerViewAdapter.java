package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.ui.entity.Music;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class RMusicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<Music> musicList;
    private LayoutInflater inflater;
    private CommonUtils.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private final int Type_Footer = 2;
    private final int Type_Music = 1;

    public RMusicRecyclerViewAdapter() {
        super();
        musicList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == musicList.size())
            return Type_Footer;
        else
            return Type_Music;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Type_Footer:
                View view = inflater.inflate(R.layout.item_music_load_more_layout, parent, false);
                FooterHolder footerHolder = new FooterHolder(view);
                view.setClickable(false);
                return footerHolder;
            case Type_Music:
                View view2 = inflater.inflate(R.layout.item_music_layout, parent, false);
                MusicHolder musicHolder = new MusicHolder(view2);
                view2.setClickable(true);
                musicHolder.downloadButton.setOnClickListener(this);
                view2.setOnClickListener(this);
                return musicHolder;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            if (v.getId() == R.id.music_download_button)
                onRecyclerViewItemClickListener.onItemDownloadClick(v, (int) v.getTag());
            else
                onRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicHolder) {
            Music music = musicList.get(position);
            ((MusicHolder) holder).singerNameTV.setText(music.getName());
            ((MusicHolder) holder).singerNameTV.setText(music.getArtist());
            ((MusicHolder) holder).ratingBar.setRating((float) music.getScore100() / 20);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        if (musicList.size() == 0)
            return 0;
        else
            return musicList.size() + 1;
    }

    public CommonUtils.OnRecyclerViewItemClickListener getOnRecyclerViewItemClickListener() {
        return onRecyclerViewItemClickListener;
    }

    public void setOnRecyclerViewItemClickListener(CommonUtils.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void addMusics(List<Music> musics) {
        musicList.addAll(musics);
        notifyDataSetChanged();
    }

    public void addMusic(Music music) {
        musicList.add(music);
        notifyDataSetChanged();
    }

    public void reloadMusics(List<Music> musics) {
        musicList.clear();
        musicList.addAll(musics);
        notifyDataSetChanged();
    }

    public void removeMusic(Music music) {
        musicList.remove(music);
        notifyDataSetChanged();
    }

    public void removeMusics(List<Music> musics) {
        musicList.removeAll(musics);
        notifyDataSetChanged();
    }

    class FooterHolder extends RecyclerView.ViewHolder {
        private View loadingView;
        private View loadMoreView;

        public FooterHolder(View itemView) {
            super(itemView);
            loadingView = itemView.findViewById(R.id.loading_view);
            loadMoreView = itemView.findViewById(R.id.load_more_view);
        }
    }
}

class MusicHolder extends RecyclerView.ViewHolder {
    public ImageView playingStatusIV;
    public TextView musicNameTV;
    public TextView singerNameTV;
    public RatingBar ratingBar;
    public ImageButton downloadButton;

    public MusicHolder(View itemView) {
        super(itemView);
        playingStatusIV = (ImageView) itemView.findViewById(R.id.small_playing_iv);
        musicNameTV = (TextView) itemView.findViewById(R.id.music_name_tv);
        singerNameTV = (TextView) itemView.findViewById(R.id.singer_name_tv);
        ratingBar = (RatingBar) itemView.findViewById(R.id.music_rating_bar);
        downloadButton = (ImageButton) itemView.findViewById(R.id.music_download_button);
    }
}
