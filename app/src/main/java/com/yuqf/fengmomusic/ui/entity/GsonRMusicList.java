package com.yuqf.fengmomusic.ui.entity;


//        "name": "酷我热歌榜",
//        "leader": "酷我热歌榜",
//        "term": "",
//        "info": "整合酷我人气最旺好听单曲",
//        "pic": "http://img1.kuwo.cn/star/mboxAlbum/BangPic/small/au_16_30.jpg",
//        "pub": "2016-08-19",
//        "num": "300",
//        "type": "music",
//        "musiclist": [
//        {
//        "id": "5237384",
//        "name": "逆流成河",
//        "artist": "金南玲",
//        "artistid": "72182",
//        "album": "来生",
//        "albumid": "303535",
//        "score100": "97",
//        "formats": "WMA96|AAC24|WMA128|MP3192|MP3H|AAC48|AL|MP3128|ALFLAC|MP4|MP4L|MV500|MV700",
//        "param": "逆流成河;金南玲;来生;2112385148;4218851748;MUSIC_5237384;2845908544;3879919432;MP3_5237384;1742356744;1057418640;MV_595866;1",
//        "ispoint": "1",
//        "mutiver": "0",
//        "pay": "0",
//        "online": "1",
//        "copyright": "0",
//        "rank_change": "0",
//        "isnew": "0",
//        "duration": "192",
//        "highest_rank": "1",
//        "score": "0",
//        "trend": "e0"
//        }
//        ]

import java.util.List;

//从排行榜获取音乐列表
public class GsonRMusicList {
    private String name;
    private String leader;
    private String term;
    private String info;
    private String pic;
    private String pub;
    private int num;
    private String type;
    private List<RMusic> musiclist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RMusic> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<RMusic> musiclist) {
        this.musiclist = musiclist;
    }

    public class RMusic {
        private int id;
        private String name;
        private String artist;
        private String artistid;
        private String album;
        private int albumid;
        private int score100;
        private String formats;
        private String param;
        private int ispoint;
        private String mutiver;
        private String pay;
        private String online;
        private String copyright;
        private String rank_change;
        private String isnew;
        private int duration;
        private String highest_rank;
        private String score;
        private String trend;

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

        public String getFormats() {
            return formats;
        }

        public void setFormats(String formats) {
            this.formats = formats;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public int getIspoint() {
            return ispoint;
        }

        public void setIspoint(int ispoint) {
            this.ispoint = ispoint;
        }

        public String getMutiver() {
            return mutiver;
        }

        public void setMutiver(String mutiver) {
            this.mutiver = mutiver;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getRank_change() {
            return rank_change;
        }

        public void setRank_change(String rank_change) {
            this.rank_change = rank_change;
        }

        public String getIsnew() {
            return isnew;
        }

        public void setIsnew(String isnew) {
            this.isnew = isnew;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getHighest_rank() {
            return highest_rank;
        }

        public void setHighest_rank(String highest_rank) {
            this.highest_rank = highest_rank;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }
}
