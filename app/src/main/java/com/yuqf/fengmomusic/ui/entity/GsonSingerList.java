package com.yuqf.fengmomusic.ui.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

//'total':'13704','pn':'0','rn':'100','category':'1','new_album':'1','new_album_cnt':'44','
//artistlist':[{'id':'947','name':'薛之谦','music_num':'268','listen':'49205187','like':'0','new_album':'0','new_album_cnt':'0','pic':'120/70/62/2990715719.jpg'}
public class GsonSingerList {
    private int total;
    private int pn;
    private int rn;
    private int category;
    private int new_album;
    private int new_album_cnt;
    private List<Singer> artistlist;

    public List<Singer> getArtistlist() {
        return artistlist;
    }

    public void setArtistlist(List<Singer> artistlist) {
        this.artistlist = artistlist;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getRn() {
        return rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getNew_album() {
        return new_album;
    }

    public void setNew_album(int new_album) {
        this.new_album = new_album;
    }

    public int getNew_album_cnt() {
        return new_album_cnt;
    }

    public void setNew_album_cnt(int new_album_cnt) {
        this.new_album_cnt = new_album_cnt;
    }

    public class Singer implements Parcelable {
        private int id;
        private String name;
        private int music_num;
        private long listen;
        private String like;
        private int new_album;
        private int new_album_cnt;
        private String pic;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMusic_num() {
            return music_num;
        }

        public void setMusic_num(int music_num) {
            this.music_num = music_num;
        }

        public long getListen() {
            return listen;
        }

        public void setListen(long listen) {
            this.listen = listen;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public int getNew_album() {
            return new_album;
        }

        public void setNew_album(int new_album) {
            this.new_album = new_album;
        }

        public int getNew_album_cnt() {
            return new_album_cnt;
        }

        public void setNew_album_cnt(int new_album_cnt) {
            this.new_album_cnt = new_album_cnt;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public final Parcelable.Creator<Singer> CREATOR = new Creator<Singer>() {
            @Override
            public Singer createFromParcel(Parcel source) {
                Singer singer = new Singer();
                singer.setId(source.readInt());
                singer.setName(source.readString());
                singer.setMusic_num(source.readInt());
                singer.setListen(source.readLong());
                singer.setLike(source.readString());
                singer.setNew_album(source.readInt());
                singer.setNew_album_cnt(source.readInt());
                singer.setPic(source.readString());
                return singer;
            }

            @Override
            public Singer[] newArray(int size) {
                return new Singer[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeInt(music_num);
            dest.writeLong(listen);
            dest.writeString(like);
            dest.writeInt(new_album);
            dest.writeInt(new_album_cnt);
            dest.writeString(pic);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }
}
