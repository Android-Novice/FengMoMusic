package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

/**
 * Created by Yuqf on 2017/1/11.
 */
/*{"albumid":"1370516",
"id":"0",
"artistid":"195792",
"name":"凉凉",
"artist":"张碧晨;杨宗纬",
"lang":"国语&nbsp;",
"pic":"120/94/71/3387350512.jpg",
"info":"电视剧《三生三世十里桃花》片尾曲《凉凉》率先惊喜曝光，lt;br&gt;",
"pub":"2017-01-09",
"company":"海洋&nbsp;",
"pay":"0",
"tag":[{"type":"Category","cat1":"中国流行","cat2":"中国流行"},{"type":"Genre","cat1":"流行Pop","cat2":"流行Pop"}],
"songnum":"1",
"musiclist":[
{"name":"凉凉-(电视剧《三生三世十里桃花》片尾曲)",
"artist":"张碧晨&杨宗纬",
"artistid":"195792",
"id":"15249349",
"score":"4",
"score100":"76",
"formats":"WMA96|WMA128|MP3128|MP3192|MP3H|AAC48|AL|ALFLAC",
"playcnt":"37823",
"uploader":"",
"uptime":"",
"is_point":"0",
"muti_ver":"0",
"online":"1",
"pay":"0",
"copyright":"0",
"param":"凉凉-(电视剧《三生三世十里桃花》片尾曲);张碧晨&杨宗纬;凉凉;119462593;3136017620;MUSIC_15249349;490078574;2426173836;MP3_15249349;0;0;0;0"}]}*/
public class GSonRecommendAlbum {
    private String albumid;
    private String id;
    private String artistid;
    private String name;
    private String artist;
    private String lang;
    private String pic;
    private String info;
    private String pub;
    private String company;
    private int pay;
    private List<TagItem> tag;
    private int songnum;
    private List<MusicItem> musiclist;

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtistid() {
        return artistid;
    }

    public void setArtistid(String artistid) {
        this.artistid = artistid;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public List<TagItem> getTag() {
        return tag;
    }

    public void setTag(List<TagItem> tag) {
        this.tag = tag;
    }

    public int getSongnum() {
        return songnum;
    }

    public void setSongnum(int songnum) {
        this.songnum = songnum;
    }

    public List<MusicItem> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<MusicItem> musiclist) {
        this.musiclist = musiclist;
    }

    public class TagItem {
        private String type;
        private String cat1;
        private String cat2;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCat1() {
            return cat1;
        }

        public void setCat1(String cat1) {
            this.cat1 = cat1;
        }

        public String getCat2() {
            return cat2;
        }

        public void setCat2(String cat2) {
            this.cat2 = cat2;
        }
    }

    public class MusicItem {
        private String name;
        private String artist;
        private String artistid;
        private int id;
        private int score;
        private int score100;
        private String formats;
        private int playcnt;
        private String uploader;
        private String uptime;
        private String is_point;
        private String muti_ver;
        private String online;
        private int pay;
        private String copyright;
        private String param;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
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

        public int getPlaycnt() {
            return playcnt;
        }

        public void setPlaycnt(int playcnt) {
            this.playcnt = playcnt;
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

        public int getPay() {
            return pay;
        }

        public void setPay(int pay) {
            this.pay = pay;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }
    }
}
