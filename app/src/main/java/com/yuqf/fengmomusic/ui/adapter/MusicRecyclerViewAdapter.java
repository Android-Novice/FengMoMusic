package com.yuqf.fengmomusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.db.DownloadedDao;
import com.yuqf.fengmomusic.db.DownloadedMusic;
import com.yuqf.fengmomusic.interfaces.OnRecyclerViewItemClickListener;
import com.yuqf.fengmomusic.media.Music;
import com.yuqf.fengmomusic.ui.entity.GsonRMusicList;
import com.yuqf.fengmomusic.ui.entity.GsonSMusicList;
import com.yuqf.fengmomusic.utils.CommonUtils;
import com.yuqf.fengmomusic.utils.FileUtils;
import com.yuqf.fengmomusic.utils.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Music> musicList;
    private List<GsonRMusicList.RMusic> rMusicList;
    private List<GsonSMusicList.SMusic> sMusicList;

    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private final int Type_Footer = 2;
    private final int Type_Music = 1;
    private final int HeaderType = 3;
    private boolean isLoading;

    private Music playingMusic;
    private boolean showHeader;
    private boolean showFooter;

    public MusicRecyclerViewAdapter(boolean showHeader, boolean showFooter) {
        super();
        musicList = new ArrayList<>();
        inflater = LayoutInflater.from(MyApplication.getContext());
        rMusicList = new ArrayList<>();
        sMusicList = new ArrayList<>();
        this.showHeader = showHeader;
        this.showFooter = showFooter;
    }

    @Override
    public int getItemViewType(int position) {
        int totalCount = getItemCount();
        if (position == 0) {
            if (showHeader)
                return HeaderType;
        } else if (position == totalCount - 1) {
            if (showFooter)
                return Type_Footer;
        }
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
                final View view2 = inflater.inflate(R.layout.item_music_layout, parent, false);
                MusicHolder musicHolder = new MusicHolder(view2);
                view2.setClickable(true);
                musicHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRecyclerViewItemClickListener != null) {
                            onRecyclerViewItemClickListener.onItemDownloadClick(view2, (int) view2.getTag());
                        }
                    }
                });
                view2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onRecyclerViewItemClickListener != null) {
                            onRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
                        }
                    }
                });
                return musicHolder;
            case HeaderType:
                View header = inflater.inflate(R.layout.item_header_layout, parent, false);
                HeaderHolder headerHolder = new HeaderHolder(header);
                header.setClickable(false);
                return headerHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicHolder) {
            int newPosition = position;
            if (showHeader)
                newPosition = position - 1;
            Music music = musicList.get(newPosition);
            TextView musicNameTV = ((MusicHolder) holder).musicNameTV;
            musicNameTV.setText(music.getName());
            ((MusicHolder) holder).singerNameTV.setText(music.getArtist());
            float rating = ((float) music.getRating()) / 20;
            Log.d("MusicRecyclerAdapter", String.valueOf(music.getRating()) + "\n rating: " + String.valueOf(rating));
            ((MusicHolder) holder).ratingBar.setRating(rating);
            holder.itemView.setTag(newPosition);
            ImageView imageView = ((MusicHolder) holder).playingStatusIV;
            if (playingMusic == music) {
                CommonUtils.setTextMarquee(musicNameTV);
                imageView.setVisibility(View.VISIBLE);
            } else
                imageView.setVisibility(View.INVISIBLE);
            ((MusicHolder) holder).downloadButton.setVisibility(isDownloaded(music) ? View.INVISIBLE : View.VISIBLE);
        } else if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).loadMoreView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            ((FooterHolder) holder).loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private boolean isDownloaded(Music curMusic) {
        String path = FileUtils.getMusicPath(curMusic.getName(), curMusic.getArtist());
        File file = new File(path);
        if (file.exists()) {
            DownloadedMusic music = DownloadedDao.getInstance().getDownloadedMusic(curMusic.getId(), curMusic.getName());
            if (music != null) {
                return true;
            }
        }
        return false;
    }

//    public Music getMusicByPosition(int position) {
//        if (position > musicList.size() || position == 0)
//            return null;
//        else
//            return musicList.get(position - 1);
//    }

    public List<Music> getMusicList() {
        return musicList;
    }

    @Override
    public int getItemCount() {
        if (musicList.size() == 0)
            return 0;
        else {
            int totalCount = musicList.size();
            if (showHeader)
                totalCount++;
            if (showFooter)
                totalCount++;
            return totalCount;
        }
    }

    public void notifyLoadStatus(boolean isLoading) {
        this.isLoading = isLoading;
        if (isLoading)
            notifyDataSetChanged();
    }

    public void updateItemState(Music curMusic) {
        this.playingMusic = curMusic;
        notifyDataSetChanged();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
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

    public void addMusicList(List<Music> musics) {
        musicList.addAll(musics);
        notifyDataSetChanged();
    }

    public void removeAll() {
        musicList.clear();
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

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private View headerView;

        public HeaderHolder(View itemView) {
            super(itemView);
            headerView = itemView.findViewById(R.id.header_view);
            headerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Global.HEADER_HEIGHT));
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
