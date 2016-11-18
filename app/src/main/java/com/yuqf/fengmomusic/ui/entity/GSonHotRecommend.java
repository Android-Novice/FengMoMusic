package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

public class GSonHotRecommend {

    private HotRecommendSecond data;
    private String msg;
    private String msgs;
    private int status;

    public HotRecommendSecond getData() {
        return data;
    }

    public void setData(HotRecommendSecond data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgs() {
        return msgs;
    }

    public void setMsgs(String msgs) {
        this.msgs = msgs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class HotRecommendSecond {
        private int total;
        private int rn;
        private int pn;
        private HotRecommendList playList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getRn() {
            return rn;
        }

        public void setRn(int rn) {
            this.rn = rn;
        }

        public int getPn() {
            return pn;
        }

        public void setPn(int pn) {
            this.pn = pn;
        }

        public HotRecommendList getPlayList() {
            return playList;
        }

        public void setPlayList(HotRecommendList playList) {
            this.playList = playList;
        }

        public class HotRecommendList {
            private String cacheTime;
            private List<HotRecommendItem> list;

            public String getCacheTime() {
                return cacheTime;
            }

            public void setCacheTime(String cacheTime) {
                this.cacheTime = cacheTime;
            }

            public List<HotRecommendItem> getList() {
                return list;
            }

            public void setList(List<HotRecommendItem> list) {
                this.list = list;
            }

            public class HotRecommendItem {
                private String inFo;
                private int nodeId;
                private String disName;
                private int source;
                private int isNew;
                private String extend;
                private String name;
                private String pic;
                private int newCount;
                private int sourceId;

                public String getInFo() {
                    return inFo;
                }

                public void setInFo(String inFo) {
                    this.inFo = inFo;
                }

                public int getNodeId() {
                    return nodeId;
                }

                public void setNodeId(int nodeId) {
                    this.nodeId = nodeId;
                }

                public String getDisName() {
                    return disName;
                }

                public void setDisName(String disName) {
                    this.disName = disName;
                }

                public int getSource() {
                    return source;
                }

                public void setSource(int source) {
                    this.source = source;
                }

                public int getIsNew() {
                    return isNew;
                }

                public void setIsNew(int isNew) {
                    this.isNew = isNew;
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

                public String getPic() {
                    return pic;
                }

                public void setPic(String pic) {
                    this.pic = pic;
                }

                public int getNewCount() {
                    return newCount;
                }

                public void setNewCount(int newCount) {
                    this.newCount = newCount;
                }

                public int getSourceId() {
                    return sourceId;
                }

                public void setSourceId(int sourceId) {
                    this.sourceId = sourceId;
                }
            }
        }
    }
}
