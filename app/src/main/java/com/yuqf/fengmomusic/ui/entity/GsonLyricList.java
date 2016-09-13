package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

//{"info":"OK","status":200,"proposal":"15670545","keyword":"认真的雪薛之谦","candidates":
//[{"soundname":"","krctype":2,"nickname":"","originame":"","accesskey":"4DE6A8C1356CB77C4B98F9378F30ECCF",
// "origiuid":"0","score":60,"hitlayer":7,"duration":269000,"sounduid":"0","transname":"",
// "uid":"1000000001","transuid":"0","song":"认真的雪","id":"15670545","adjust":0,"singer":"薛之谦","language":""}
public class GsonLyricList {
    private String info;
    private int status;
    private int proposal;
    private String keyword;
    private List<LyricInfo> candidates;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProposal() {
        return proposal;
    }

    public void setProposal(int proposal) {
        this.proposal = proposal;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<LyricInfo> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<LyricInfo> candidates) {
        this.candidates = candidates;
    }

    public class LyricInfo {
        private String soundname;
        private int krctype;
        private String nickname;
        private String originame;
        private String accesskey;
        private String origiuid;
        private int score;
        private int hitlayer;
        private int duration;
        private int sounduid;
        private String transname;
        private int uid;
        private int transuid;
        private String song;
        private int id;
        private int adjust;
        private String singer;
        private String language;

        public String getSoundname() {
            return soundname;
        }

        public void setSoundname(String soundname) {
            this.soundname = soundname;
        }

        public int getKrctype() {
            return krctype;
        }

        public void setKrctype(int krctype) {
            this.krctype = krctype;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOriginame() {
            return originame;
        }

        public void setOriginame(String originame) {
            this.originame = originame;
        }

        public String getAccesskey() {
            return accesskey;
        }

        public void setAccesskey(String accesskey) {
            this.accesskey = accesskey;
        }

        public String getOrigiuid() {
            return origiuid;
        }

        public void setOrigiuid(String origiuid) {
            this.origiuid = origiuid;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getHitlayer() {
            return hitlayer;
        }

        public void setHitlayer(int hitlayer) {
            this.hitlayer = hitlayer;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getSounduid() {
            return sounduid;
        }

        public void setSounduid(int sounduid) {
            this.sounduid = sounduid;
        }

        public String getTransname() {
            return transname;
        }

        public void setTransname(String transname) {
            this.transname = transname;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getTransuid() {
            return transuid;
        }

        public void setTransuid(int transuid) {
            this.transuid = transuid;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAdjust() {
            return adjust;
        }

        public void setAdjust(int adjust) {
            this.adjust = adjust;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
