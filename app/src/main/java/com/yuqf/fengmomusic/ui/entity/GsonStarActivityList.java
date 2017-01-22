package com.yuqf.fengmomusic.ui.entity;

import java.util.List;

/**
 * Created by Yuqf on 2017/1/19.
 */
/*{"cacheTime":"2017-01-19 15:20:00",
"list":
[{"id":"0",
"source":"17",
"name":"野孩子&周云蓬全国剧场巡演",
"pic":"http://img1.kwcdn.kuwo.cn/star/upload/12/12/1479788574636_.jpg",
"sourceId":"http://weibo.com/1738434147/EiHIBwwrA?ref=home&type=like#_loginLayer_1479786811704"}*/

public class GsonStarActivityList {
    private String cacheTime;
    List<StartActivity> list;

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }

    public List<StartActivity> getList() {
        return list;
    }

    public void setList(List<StartActivity> list) {
        this.list = list;
    }

    public class StartActivity{
        private int id;
        private int source;
        private String name;
        private String pic;
        private String souceId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
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

        public String getSouceId() {
            return souceId;
        }

        public void setSouceId(String souceId) {
            this.souceId = souceId;
        }
    }
}
