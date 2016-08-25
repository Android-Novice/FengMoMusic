package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.ui.entity.GsonRMusicList;
import com.yuqf.fengmomusic.ui.entity.GsonSMusicList;
import com.yuqf.fengmomusic.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<Music> musicList;
    private List<GsonRMusicList.RMusic> rMusicList;
    private List<GsonSMusicList.SMusic> sMusicList;

    private LayoutInflater inflater;
    private CommonUtils.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private final int Type_Footer = 2;
    private final int Type_Music = 1;
    private boolean isLoading;

    private int playingIndex = -1;
    private Music playingMusic;

    public MusicRecyclerViewAdapter() {
        super();
        musicList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
        rMusicList = new ArrayList<>();
        sMusicList = new ArrayList<>();
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
            TextView musicNameTV = ((MusicHolder) holder).musicNameTV;
            musicNameTV.setText(music.getName());
            ((MusicHolder) holder).singerNameTV.setText(music.getArtist());
            float rating = ((float) music.getRating()) / 20;
            Log.d("MusicRecyclerAdapter", String.valueOf(music.getRating()) + "\n rating: " + String.valueOf(rating));
            ((MusicHolder) holder).ratingBar.setRating(rating);
            holder.itemView.setTag(position);
            ImageView imageView = ((MusicHolder) holder).playingStatusIV;
            if (playingMusic == music && playingIndex == position) {
                CommonUtils.setTextMarquee(musicNameTV);
                imageView.setVisibility(View.VISIBLE);
            } else
                imageView.setVisibility(View.INVISIBLE);
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).loadMoreView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            ((FooterHolder) holder).loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    public Music getMusicByPosition(int position) {
        if (position > musicList.size() - 1)
            return null;
        else
            return musicList.get(position);
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    @Override
    public int getItemCount() {
        if (musicList.size() == 0)
            return 0;
        else
            return musicList.size() + 1;
    }

    public void notifyLoadStatus(boolean isLoading) {
        this.isLoading = isLoading;
        if (isLoading)
            notifyDataSetChanged();
    }

    public void updateItemState(Music curMusic, int newIndex, int oldIndex) {
        this.playingIndex = newIndex;
        this.playingMusic = curMusic;
        notifyDataSetChanged();
    }

    public void setOnRecyclerViewItemClickListener(CommonUtils.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void addRMusics(List<GsonRMusicList.RMusic> musics) {
        rMusicList.addAll(musics);
        for (GsonRMusicList.RMusic music : musics) {
            {
                Music music1 = parseRMusic(music);
                musicList.add(music1);
            }
        }
        notifyDataSetChanged();
    }

    public void addRMusic(GsonRMusicList.RMusic music) {
        rMusicList.add(music);
        musicList.add(parseRMusic(music));
        notifyDataSetChanged();
    }

    public void reloadRMusics(List<GsonRMusicList.RMusic> musics) {
        musicList.clear();
        addRMusics(musics);
    }

    public void removeRMusic(GsonRMusicList.RMusic music) {
        int index = rMusicList.indexOf(music);
        if (index >= 0) {
            rMusicList.remove(music);
            musicList.remove(index);
            notifyDataSetChanged();
        }
    }

    public void removeRMusics(List<GsonRMusicList.RMusic> musics) {
        for (GsonRMusicList.RMusic rMusic : musics) {
            int index = rMusicList.indexOf(rMusic);
            if (index >= 0) {
                rMusicList.remove(rMusic);
                musicList.remove(index);
            }
        }
        notifyDataSetChanged();
    }

    public void addSMusics(List<GsonSMusicList.SMusic> musics) {
        sMusicList.addAll(musics);
        for (GsonSMusicList.SMusic sMusic : musics) {
            Music music = parseSMusic(sMusic);
            musicList.add(music);
        }
        notifyDataSetChanged();
    }

    public void addSMusic(GsonSMusicList.SMusic music) {
        sMusicList.add(music);
        musicList.add(parseSMusic(music));
        notifyDataSetChanged();
    }

    public void reloadSMusics(List<GsonSMusicList.SMusic> musics) {
        musicList.clear();
        addSMusics(musics);
    }

    public void removeSMusic(GsonSMusicList.SMusic music) {
        int index = sMusicList.indexOf(music);
        if (index >= 0) {
            sMusicList.remove(music);
            musicList.remove(index);
            notifyDataSetChanged();
        }
    }

    public void removeSMusics(List<GsonSMusicList.SMusic> musics) {
        for (GsonSMusicList.SMusic sMusic : musics) {
            int index = sMusicList.indexOf(sMusic);
            if (index >= 0) {
                sMusicList.remove(sMusic);
                musicList.remove(index);
            }
        }
        notifyDataSetChanged();
    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        private View loadingView;
        public View loadMoreView;

        public FooterHolder(View itemView) {
            super(itemView);
            loadingView = itemView.findViewById(R.id.loading_view);
            loadMoreView = itemView.findViewById(R.id.load_more_view);
        }
    }

    public Music parseSMusic(GsonSMusicList.SMusic music) {
        Music newMusic = new Music();
        newMusic.setId(music.getMusicrid());
        newMusic.setArtist(music.getArtist());
        newMusic.setArtistId(music.getArtistid());
        newMusic.setAlbum(music.getAlbum());
        newMusic.setAlbumId(music.getAlbumid());
        newMusic.setRating(music.getScore100());
        newMusic.setName(music.getName());
        newMusic.setLocal(false);
        return newMusic;
    }

    public Music parseRMusic(GsonRMusicList.RMusic music) {
        Music newMusic = new Music();
        newMusic.setId(music.getId());
        newMusic.setArtist(music.getArtist());
        newMusic.setArtistId(music.getArtistid());
        newMusic.setAlbum(music.getAlbum());
        newMusic.setAlbumId(music.getAlbumid());
        newMusic.setRating(music.getScore100());
        newMusic.setName(music.getName());
        newMusic.setLocal(false);
        return newMusic;
    }

//    class Music {
//        private int id;
//        private String name;
//        private String artist;
//        private String artistid;
//        private String album;
//        private int albumid;
//        private int score100;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getArtist() {
//            return artist;
//        }
//
//        public void setArtist(String artist) {
//            this.artist = artist;
//        }
//
//        public String getArtistid() {
//            return artistid;
//        }
//
//        public void setArtistid(String artistid) {
//            this.artistid = artistid;
//        }
//
//        public String getAlbum() {
//            return album;
//        }
//
//        public void setAlbum(String album) {
//            this.album = album;
//        }
//
//        public int getAlbumid() {
//            return albumid;
//        }
//
//        public void setAlbumid(int albumid) {
//            this.albumid = albumid;
//        }
//
//        public int getScore100() {
//            return score100;
//        }
//
//        public void setScore100(int score100) {
//            this.score100 = score100;
//        }
//    }

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
}
