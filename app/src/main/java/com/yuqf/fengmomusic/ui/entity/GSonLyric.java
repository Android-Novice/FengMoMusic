package com.yuqf.fengmomusic.ui.entity;

//"charset":"utf8","content":"","fmt":"lrc","info":"OK","status":200
public class GSonLyric {
    private String charset;
    private String content;
    private String fmt;
    private String info;
    private int status;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

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
}
