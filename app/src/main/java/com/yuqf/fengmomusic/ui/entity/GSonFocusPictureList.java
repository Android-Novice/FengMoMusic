package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

/**
 * Created by Yuqf on 2017/1/3.
 */

public class GSonFocusPictureList {
    private String cacheTime;
    private List<FocusPictureItem> list;

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }

    public List<FocusPictureItem> getList() {
        return list;
    }

    public void setList(List<FocusPictureItem> list) {
        this.list = list;
    }

    public class FocusPictureItem {
        private String inFo;
        private int nodeId;
        private int source;
        private String disName;
        private String extend;
        private int isNew;
        private String name;
        private String pic;
        private int newCount;
        private String sourceId;

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

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public String getDisName() {
            return disName;
        }

        public void setDisName(String disName) {
            this.disName = disName;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

        public int getIsNew() {
            return isNew;
        }

        public void setIsNew(int isNew) {
            this.isNew = isNew;
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

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }
    }
}
