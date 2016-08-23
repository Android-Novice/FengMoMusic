package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

//'total':'267','pn':'0','rn':'20','artist':'薛之谦','musiclist':[{'"musicrid":"6468891",
//"name":"演员",
//        "artist":"薛之谦",
//        "artistid":"947",
//        "album":"绅士",
//        "albumid":"503766",
//        "score100":"95",
//        "playcnt":"515118",
//        "formats":"WMA96|WMA128|MP3128|MP3192|MP3H|AAC48|AL|MV500|MV700|EXMV500|EXMV700|MP4|MP4L|EXMP4|EXMP4L",
//        "new":"0",
//        "nsig1":"3147030699",
//        "nsig2":"3966394200",
//        "mp3sig1":"495851287",
//        "mp3sig2":"723903978",
//        "mkvnsig1":"1544112812",
//        "mkvnsig2":"1912707522",
//        "hasecho":"1",
//        "haskdatx":"1",
//        "uploader":"",
//        "uptime":"",
//        "is_point":"1",
//        "muti_ver":"0",
//        "online":"1",
//        "pay":"0",
//        "COPYRIGHT":"0",
//        "mp3rid":"6468891",
//        "mkvrid":"594696"
public class GsonSMusicList {

    private int total;
    private int pn;
    private int rn;
    private String artist;
    private List<SMusic> musiclist;

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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<SMusic> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<SMusic> musiclist) {
        this.musiclist = musiclist;
    }

    public class SMusic {

        private int musicrid;
        private String name;
        private String artist;
        private String artistid;
        private String album;
        private int albumid;
        private int score100;
        private long playcnt;
        private String formats;
        private String _new;
        private String nsig1;
        private String nsig2;
        private String mp3sig1;
        private String mp3sig2;
        private String mkvsig1;
        private String mkvsig2;
        private String hasecho;
        private String haskdatx;
        private String uploader;
        private String uptime;
        private String is_point;
        private String muti_ver;
        private String online;
        private String pay;
        private String COPYRIGHT;
        private String mp3rid;
        private String mkvrid;

        public int getMusicrid() {
            return musicrid;
        }

        public void setMusicrid(int musicrid) {
            this.musicrid = musicrid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public int getScore100() {
            return score100;
        }

        public void setScore100(int score100) {
            this.score100 = score100;
        }

        public long getPlaycnt() {
            return playcnt;
        }

        public void setPlaycnt(long playcnt) {
            this.playcnt = playcnt;
        }

        public String getFormats() {
            return formats;
        }

        public void setFormats(String formats) {
            this.formats = formats;
        }

        public String get_new() {
            return _new;
        }

        public void set_new(String _new) {
            this._new = _new;
        }

        public String getNsig1() {
            return nsig1;
        }

        public void setNsig1(String nsig1) {
            this.nsig1 = nsig1;
        }

        public String getNsig2() {
            return nsig2;
        }

        public void setNsig2(String nsig2) {
            this.nsig2 = nsig2;
        }

        public String getMp3sig1() {
            return mp3sig1;
        }

        public void setMp3sig1(String mp3sig1) {
            this.mp3sig1 = mp3sig1;
        }

        public String getMp3sig2() {
            return mp3sig2;
        }

        public void setMp3sig2(String mp3sig2) {
            this.mp3sig2 = mp3sig2;
        }

        public String getMkvsig1() {
            return mkvsig1;
        }

        public void setMkvsig1(String mkvsig1) {
            this.mkvsig1 = mkvsig1;
        }

        public String getMkvsig2() {
            return mkvsig2;
        }

        public void setMkvsig2(String mkvsig2) {
            this.mkvsig2 = mkvsig2;
        }

        public String getHasecho() {
            return hasecho;
        }

        public void setHasecho(String hasecho) {
            this.hasecho = hasecho;
        }

        public String getHaskdatx() {
            return haskdatx;
        }

        public void setHaskdatx(String haskdatx) {
            this.haskdatx = haskdatx;
        }

        public String getUploader() {
            return uploader;
        }

        public void setUploader(String uploader) {
            this.uploader = uploader;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }

        public String getIs_point() {
            return is_point;
        }

        public void setIs_point(String is_point) {
            this.is_point = is_point;
        }

        public String getMuti_ver() {
            return muti_ver;
        }

        public void setMuti_ver(String muti_ver) {
            this.muti_ver = muti_ver;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getCOPYRIGHT() {
            return COPYRIGHT;
        }

        public void setCOPYRIGHT(String COPYRIGHT) {
            this.COPYRIGHT = COPYRIGHT;
        }

        public String getMp3rid() {
            return mp3rid;
        }

        public void setMp3rid(String mp3rid) {
            this.mp3rid = mp3rid;
        }

        public String getMkvrid() {
            return mkvrid;
        }

        public void setMkvrid(String mkvrid) {
            this.mkvrid = mkvrid;
        }
    }
}
