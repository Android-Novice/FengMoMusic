package com.yuqf.fengmomusic.ui.entity;

import android.text.TextUtils;

import java.util.List;

//"id":"2","name":"排行榜","disname":"排行榜","info":"0个列表","source":"5","sourceid":"",
//"pic":"http://img4.kwcdn.kuwo.cn/star/upload/10/10/1351577087466_.png",
//"like":"0","listen":"13459597","tips":"","isnew":"0","newcnt":"0",
//"extend":"","intro":"全球最权威的流行风向标 ","pc_extend":"","pic5":"null","pic2":"http://img1.kwcdn.kuwo.cn/star/upload/12/12/1360045316636_.png","child":
public class GsonRankingList {

    private int id;
    private String name;
    private String disname;
    private String info;
    private String source;
    private String sourceid;
    private String pic;
    private int like;
    private long listen;
    private String tips;
    private int isnew;
    private int newcnt;
    private String extend;
    private String intro;
    private String pc_extend;
    private String pic5;
    private String pic2;
    private List<ChildRanking> child;

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

    public String getDisname() {
        return disname;
    }

    public void setDisname(String disname) {
        this.disname = disname;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public long getListen() {
        return listen;
    }

    public void setListen(long listen) {
        this.listen = listen;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getIsnew() {
        return isnew;
    }

    public void setIsnew(int isnew) {
        this.isnew = isnew;
    }

    public int getNewcnt() {
        return newcnt;
    }

    public void setNewcnt(int newcnt) {
        this.newcnt = newcnt;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPc_extend() {
        return pc_extend;
    }

    public void setPc_extend(String pc_extend) {
        this.pc_extend = pc_extend;
    }

    public String getPic5() {
        return pic5;
    }

    public void setPic5(String pic5) {
        this.pic5 = pic5;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public List<ChildRanking> getChild() {
        return child;
    }

    public void setChild(List<ChildRanking> child) {
        this.child = child;
    }

    public class ChildRanking {
        private int id;
        private String name;
        private String disname;
        private String info;
        private String source;
        private String sourceid;
        private String pic;
        private int like;
        private long listen;
        private String tips;
        private int isnew;
        private int newcnt;
        private String extend;
        private String intro;
        private String pc_extend;
        private String pic5;
        private String pic2;
        private List<ChildRanking> child;

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

        public String getDisname() {
            return disname;
        }

        public void setDisname(String disname) {
            this.disname = disname;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSourceid() {
            return sourceid;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public long getListen() {
            return listen;
        }

        public void setListen(long listen) {
            this.listen = listen;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public int getIsnew() {
            return isnew;
        }

        public void setIsnew(int isnew) {
            this.isnew = isnew;
        }

        public int getNewcnt() {
            return newcnt;
        }

        public void setNewcnt(int newcnt) {
            this.newcnt = newcnt;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getPc_extend() {
            return pc_extend;
        }

        public void setPc_extend(String pc_extend) {
            this.pc_extend = pc_extend;
        }

        public String getPic5() {
            return pic5;
        }

        public void setPic5(String pic5) {
            this.pic5 = pic5;
        }

        public String getPic2() {
            return pic2;
        }

        public void setPic2(String pic2) {
            this.pic2 = pic2;
        }

        public List<ChildRanking> getChild() {
            return child;
        }

        public void setChild(List<ChildRanking> child) {
            this.child = child;
        }

        public String getPicPath() {
            String picPath = getPic2();
            if (TextUtils.isEmpty(picPath))
                picPath = getPic5();
            if (TextUtils.isEmpty(picPath))
                picPath = getPic();
            return picPath;
        }
//
//        public class GrandSonRanking {
//            private int id;
//            private String name;
//            private String disname;
//            private String info;
//            private String source;
//            private String sourceid;
//            private String pic;
//            private int like;
//            private long listen;
//            private String tips;
//            private int isnew;
//            private int newcnt;
//            private String extend;
//            private String intro;
//            private String pc_extend;
//            private String pic5;
//            private String pic2;
//            private List<String> child;
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public String getDisname() {
//                return disname;
//            }
//
//            public void setDisname(String disname) {
//                this.disname = disname;
//            }
//
//            public String getInfo() {
//                return info;
//            }
//
//            public void setInfo(String info) {
//                this.info = info;
//            }
//
//            public String getSource() {
//                return source;
//            }
//
//            public void setSource(String source) {
//                this.source = source;
//            }
//
//            public String getSourceid() {
//                return sourceid;
//            }
//
//            public void setSourceid(String sourceid) {
//                this.sourceid = sourceid;
//            }
//
//            public String getPic() {
//                return pic;
//            }
//
//            public void setPic(String pic) {
//                this.pic = pic;
//            }
//
//            public int getLike() {
//                return like;
//            }
//
//            public void setLike(int like) {
//                this.like = like;
//            }
//
//            public long getListen() {
//                return listen;
//            }
//
//            public void setListen(long listen) {
//                this.listen = listen;
//            }
//
//            public String getTips() {
//                return tips;
//            }
//
//            public void setTips(String tips) {
//                this.tips = tips;
//            }
//
//            public int getIsnew() {
//                return isnew;
//            }
//
//            public void setIsnew(int isnew) {
//                this.isnew = isnew;
//            }
//
//            public int getNewcnt() {
//                return newcnt;
//            }
//
//            public void setNewcnt(int newcnt) {
//                this.newcnt = newcnt;
//            }
//
//            public String getExtend() {
//                return extend;
//            }
//
//            public void setExtend(String extend) {
//                this.extend = extend;
//            }
//
//            public String getIntro() {
//                return intro;
//            }
//
//            public void setIntro(String intro) {
//                this.intro = intro;
//            }
//
//            public String getPc_extend() {
//                return pc_extend;
//            }
//
//            public void setPc_extend(String pc_extend) {
//                this.pc_extend = pc_extend;
//            }
//
//            public String getPic5() {
//                return pic5;
//            }
//
//            public void setPic5(String pic5) {
//                this.pic5 = pic5;
//            }
//
//            public String getPic2() {
//                return pic2;
//            }
//
//            public void setPic2(String pic2) {
//                this.pic2 = pic2;
//            }
//
//            public List<String> getChild() {
//                return child;
//            }
//
//            public void setChild(List<String> child) {
//                this.child = child;
//            }
//        }
    }

}
