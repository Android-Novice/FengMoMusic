package com.yuqf.fengmomusic.media;

public interface MusicPlayerListener {

    /**
     * 准备开始播放
     **/
    void onPreparing(Music music);

    /**
     * 开始缓冲---播放在线音乐调用
     **/
    void onStartBuffering();

    /**
     * 缓冲结束---播放在线音乐调用
     **/
    void onEndBuffering();

    /**
     * 缓冲进度---播放在线音乐调用
     **/
    void onBufferingUpdate(Music music);

    /**
     * 准备完成，开始播放
     **/
    void onPrepared(Music music);

    void onPlayStateChanged();

    /**
     * 加载音乐封面完成---播放在线音乐时调用
     **/
    void onMusicCoverLoaded(Music music);

    /**
     * 播放时长变化
     **/
    void onPlayedDurationChanged(Music music);

    /**
     * 播放完成
     **/
    void onCompletion(Music music);

    /**
     * 出现错误
     **/
    void onError();

}
