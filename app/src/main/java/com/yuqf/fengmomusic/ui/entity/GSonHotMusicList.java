package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

//{"album":"宝贝 妈妈",
// "albumid":"551768",
// "artist":"张靓颖",
// "artistid":"211",
// "copyright":"0",
// "duration":"150","
// formats":"WMA96|WMA128|MP3H|MP3192|MP3128|ALFLAC|AL|AAC48|AAC24|MV700|MV500|MP4L|MP4",
// "hasmv":"1",
// "id":"7060878",
// "is_point":"0",
// "muti_ver":"0",
// "name":"宝贝 妈妈(吉他版)",
// "online":"1",
// "params":"宝贝 妈妈(吉他版);张靓颖;宝贝 妈妈;1078517628;2887270191;MUSIC_7060878;1397106088;2270808779;7060878;3146650195;1265852076;MV_727892;1",
// "pay":"0","score100":"50"}
public class GSonHotMusicList {
    private int abstime;
    private int ctime;
    private int id ;
    private String info;
    private String ispub;
    private List<HotMusic> musiclist;

    public int getAbstime() {
        return abstime;
    }

    public void setAbstime(int abstime) {
        this.abstime = abstime;
    }

    public int getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIspub() {
        return ispub;
    }

    public void setIspub(String ispub) {
        this.ispub = ispub;
    }

    public List<HotMusic> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<HotMusic> musiclist) {
        this.musiclist = musiclist;
    }

    public class HotMusic
    {
        private String album;
        private int albumid;
        private String artist;
        private String artistid;
        private String copyright;
        private int duration;
        private String formats;
        private int hasmv;
        private int id;
        private int is_point;
        private String muti_ver;
        private String name;
        private String online;
        private String params;
        private String pay;
        private int score100;

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getAlbumid() {
            return albumid;
        }

        public void setAlbumid(int albumid) {
            this.albumid = albumid;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getArtistid() {
            return artistid;
        }

        public void setArtistid(String artistid) {
            this.artistid = artistid;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getFormats() {
            return formats;
        }

        public void setFormats(String formats) {
            this.formats = formats;
        }

        public int getHasmv() {
            return hasmv;
        }

        public void setHasmv(int hasmv) {
            this.hasmv = hasmv;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIs_point() {
            return is_point;
        }

        public void setIs_point(int is_point) {
            this.is_point = is_point;
        }

        public String getMuti_ver() {
            return muti_ver;
        }

        public void setMuti_ver(String muti_ver) {
            this.muti_ver = muti_ver;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public int getScore100() {
            return score100;
        }

        public void setScore100(int score100) {
            this.score100 = score100;
        }
    }
}
