package com.yuqf.fengmomusic.ui.entity;

/**
 * Created by Yuqf on 2017/2/28.
 */

/*    {
        "source": "8",
        "extend": "0",
        "name": "【多少开始发乎情多少结局止于礼",
        "sourceid": "2073674394",
        "pic": "http://img1.kwcdn.kuwo.cn/star/userpl2015/15/53/1482257882632_54513215_150.jpg",
        "isnew": "0",
        "disname": "【多少开始发乎情多少结局止于礼",
        "newcount": "0",
        "playcnt": "1608306",
        "info": "",
        "nodeid": "0",
        "digest": "8",
        "tag": "欧美#伤感",
        "rcm_type": "algorithm",
        "newreason": [
            {
                "desc": "和你近期收听的歌曲相似",
                "type": "txt"
            }
        ]
    }*/
public class GsonPersonalRecommendationItem {
    private int source;
    private String extend;
    private String name;
    private long sourceid;
    private String pic;
    private int isnew;
    private String disname;
    private int newcount;
    private int playcnt;
    private String info;
    private int nodeid;
    private String tag;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSourceid() {
        return sourceid;
    }

    public void setSourceid(int sourceid) {
        this.sourceid = sourceid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getIsnew() {
        return isnew;
    }

    public void setIsnew(int isnew) {
        this.isnew = isnew;
    }

    public String getDisname() {
        return disname;
    }

    public void setDisname(String disname) {
        this.disname = disname;
    }

    public int getNewcount() {
        return newcount;
    }

    public void setNewcount(int newcount) {
        this.newcount = newcount;
    }

    public int getPlaycnt() {
        return playcnt;
    }

    public void setPlaycnt(int playcnt) {
        this.playcnt = playcnt;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNodeid() {
        return nodeid;
    }

    public void setNodeid(int nodeid) {
        this.nodeid = nodeid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
