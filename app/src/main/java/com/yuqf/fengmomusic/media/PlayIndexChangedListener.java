package com.yuqf.fengmomusic.media;

public interface PlayIndexChangedListener {

    /**
     * 播放歌曲索引切换
     **/
    void onPlayingIndexChange(Music music, int curPosition, int oldPosition);
}
