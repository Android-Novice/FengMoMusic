package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

//{'id':'947',
// 'name':'薛之谦',
// 'rank':'1',
// 'playcnt':'2861940',
// 'region_category':'1',
// 'info':'薛之谦，....',
// 'oname':'',
// 'birthday':'1983-07-17',
// 'birthplace':'中国上海',
// 'tall':'172cm',
// 'weight':'55kg',
// 'country':'中国',
// 'language':'国语;',
// 'gender':'男',
// 'constellation':'巨蟹座',
// 'similar':'乔任梁;刘维;君君;爱乐团;男才女貌;青鸟飞鱼;刘畊宏;张栋梁;言承旭',
// 'pic':'120/70/62/2990715719.jpg',
// 'musicnum':'266',
// 'albumnum':'15',
// 'mvnum':'120',
// 'tag':
// [
// {'type':'Category','cat1':'中国流行','cat2':'中国流行'},
// {'type':'Category','cat1':'现场','cat2':'选秀节目'},
// {'type':'Genre','cat1':'流行Pop','cat2':'流行Pop'},
// {'type':'Genre','cat1':'民谣Folk','cat2':'唱作歌手Singer&Songwriter'}]}
public class GSonSinger {
    private int id;
    private String name;
    private String rank;
    private String playcnt;
    private String region_category;
    private String info;
    private String onname;
    private String birthday;
    private String birthplace;
    private String tall;
    private String weight;
    private String country;
    private String language;
    private String gender;
    private String constellation;
    private String similar;
    private String pic;
    private String musicnum;
    private String albumnum;
    private String mvnum;
    private List<OtherItem> tag;

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPlaycnt() {
        return playcnt;
    }

    public void setPlaycnt(String playcnt) {
        this.playcnt = playcnt;
    }

    public String getRegion_category() {
        return region_category;
    }

    public void setRegion_category(String region_category) {
        this.region_category = region_category;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getOnname() {
        return onname;
    }

    public void setOnname(String onname) {
        this.onname = onname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getTall() {
        return tall;
    }

    public void setTall(String tall) {
        this.tall = tall;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public String getPic() {
        if (pic.startsWith("120"))
            pic = pic.replace("120", "240");
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getMusicnum() {
        return musicnum;
    }

    public void setMusicnum(String musicnum) {
        this.musicnum = musicnum;
    }

    public String getAlbumnum() {
        return albumnum;
    }

    public void setAlbumnum(String albumnum) {
        this.albumnum = albumnum;
    }

    public String getMvnum() {
        return mvnum;
    }

    public void setMvnum(String mvnum) {
        this.mvnum = mvnum;
    }

    public List<OtherItem> getTag() {
        return tag;
    }

    public void setTag(List<OtherItem> tag) {
        this.tag = tag;
    }

    class OtherItem {
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
}
