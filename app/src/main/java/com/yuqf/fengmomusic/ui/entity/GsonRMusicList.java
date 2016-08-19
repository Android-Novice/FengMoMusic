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
    private List<Music> musiclist;

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

    public List<Music> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<Music> musiclist) {
        this.musiclist = musiclist;
    }
}
