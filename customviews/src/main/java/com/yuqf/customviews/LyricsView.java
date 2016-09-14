package com.yuqf.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.yuqf.customviews.Lyrics.LyricsItem;
import com.yuqf.customviews.Lyrics.LyricsParser;

import java.util.List;

public class LyricsView extends TextView {

    private final String logTag = "LyricsView";
    private List<LyricsItem> lyricsItems;
    private String lyrics;
    private Paint playingPaint;
    private Paint normalPaint;
    private int playingIndex;

    private int playingColor = Color.BLUE;
    private int playingSize = 35;
    private int normalColor = Color.WHITE;
    private int normalSize = 30;

    private final int LYRIC_SPACE = 50;

    private int startMovingIndex = -1;
    private final int TOUCH_DOWN_FLAG = 110;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TOUCH_DOWN_FLAG) {
                Log.d(logTag, String.format("MSG=====UP=================="));
                isTouchDown = false;
            }
            return false;
        }
    });

    public LyricsView(Context context) {
        this(context, null);
    }

    public LyricsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LyricsView, defStyleAttr, 0);
        playingColor = typedArray.getColor(R.styleable.LyricsView_playing_color, Color.parseColor("#FFA500"));
        playingSize = typedArray.getDimensionPixelSize(R.styleable.LyricsView_playing_size, playingSize);
        normalColor = typedArray.getColor(R.styleable.LyricsView_normal_color, normalColor);
        normalSize = typedArray.getDimensionPixelSize(R.styleable.LyricsView_normal_size, normalSize);
        typedArray.recycle();

        playingIndex = -1;

        playingPaint = new Paint();
        playingPaint.setAntiAlias(true);
        playingPaint.setColor(playingColor);
        playingPaint.setTextSize(playingSize);
        playingPaint.setTextAlign(Paint.Align.CENTER);

        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPaint.setColor(normalColor);
        normalPaint.setTextSize(normalSize);
        normalPaint.setTextAlign(Paint.Align.CENTER);
    }

    private int centerY;
    private int startY;

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        if (!TextUtils.isEmpty(text) && !text.equals(lyrics)) {
            playingIndex = -1;
            if (lyricsItems != null)
                lyricsItems.clear();
            oldDistance = 0;
            lyrics = text;
            lyricsItems = LyricsParser.getLyrics(lyrics);
        }
        if (TextUtils.isEmpty(lyrics)) {
            lyrics = null;
            playingIndex = -1;
            super.onDraw(canvas);
        } else {
//            Log.d(logTag, "playingIndex: " + String.valueOf(playingIndex) + "\nstartMovingIndex: " + String.valueOf(startMovingIndex));
            if (startMovingIndex == -1) {
//                startMovingIndex = (int) Math.ceil((getMeasuredHeight() / 2 - LYRIC_SPACE) / LYRIC_SPACE);
                startMovingIndex = (int) Math.ceil(getMeasuredHeight() / (2 * LYRIC_SPACE));
            }
            int centerX = getWidth() / 2;

            if (playingIndex < startMovingIndex) {
                setStartY();
                for (int i = 0; i < lyricsItems.size(); i++) {
                    Paint curPaint;
                    if (i == playingIndex) {
                        curPaint = playingPaint;
                    } else {
                        curPaint = normalPaint;
                    }
                    int height = startY + (1 + i) * LYRIC_SPACE;
                    canvas.drawText(lyricsItems.get(i).getLyric(), centerX, height, curPaint);
                    if (height + LYRIC_SPACE > getHeight()) {
                        break;
                    }
                }
            } else {
                centerY = getCenterY();
                canvas.drawText(lyricsItems.get(playingIndex).getLyric(), centerX, centerY, playingPaint);
                for (int i = playingIndex - 1; i >= 0; i--) {
                    int curY = centerY - LYRIC_SPACE * (playingIndex - i);
                    canvas.drawText(lyricsItems.get(i).getLyric(), centerX, curY, normalPaint);
                    if (curY - LYRIC_SPACE < 0) {
                        break;
                    }
                }
                for (int i = playingIndex + 1; i < lyricsItems.size(); i++) {
                    int curY = centerY + LYRIC_SPACE * (i - playingIndex);
                    canvas.drawText(lyricsItems.get(i).getLyric(), centerX, curY, normalPaint);
                    if (curY + LYRIC_SPACE > getHeight()) {
                        break;
                    }
                }
            }
        }
    }

    private int getCenterY() {
        Log.d(logTag, String.format("after---movedDistance: %f\n oldDistance:%d \n startY:%d\n", movedDistance, oldDistance, startY));
        movedDistance += oldDistance;
        if (isTouchDown) {
            if (movedDistance < 0) {
                int maxDistance = (lyricsItems.size() - playingIndex - 1) * LYRIC_SPACE - getHeight() / 2;
                if (maxDistance > 0)
                    if (maxDistance > (int) Math.abs(movedDistance))
                        return getHeight() / 2 + (int) movedDistance;
                    else
                        return getHeight() / 2 - maxDistance;

            } else {
                int maxDistance = (playingIndex + 1) * LYRIC_SPACE - getHeight() / 2;
                if (maxDistance > 0) {
                    if (maxDistance > movedDistance)
                        return getHeight() / 2 + (int) movedDistance;
                    else
                        return getHeight() / 2 + maxDistance;
                }
            }
        }

        return getHeight() / 2;
    }

    private void setStartY() {
        if (isTouchDown) {
            Log.d(logTag, String.format("before------movedDistance: %f\n oldDistance:%d \n startY:%d\n", movedDistance, oldDistance, startY));
            if (movedDistance < 0) {
                int maxMovedDis = lyricsItems.size() * LYRIC_SPACE - getHeight() - Math.abs(oldDistance);
                if (maxMovedDis > Math.abs(movedDistance)) {
                    startY = oldDistance + (int) movedDistance;
                } else
                    startY = oldDistance - maxMovedDis;
            } else {
                startY = oldDistance + (int) movedDistance;
                if (startY > 0)
                    startY = 0;
            }
        } else {
            oldDistance = 0;
            startY = 0;
        }
    }

//
//    private int getCenterY() {
//        if (isTouchDown) {
//            /**中心位置向上移动**/
//            if (movedDistance < 0) {
//                int maxDistance = (lyricsItems.size() - playingIndex - 1) * LYRIC_SPACE - getHeight() / 2;
//                if (maxDistance > 0)
//                    if (maxDistance > (int) Math.abs(movedDistance))
//                        return getHeight() / 2 + (int) movedDistance;
//                    else
//                        return getHeight() / 2 - maxDistance;
//
//            } else {
//                int maxDistance = playingIndex * LYRIC_SPACE - getHeight() / 2;
//                if (maxDistance > 0) {
//                    if (maxDistance > movedDistance)
//                        return getHeight() / 2 + (int) movedDistance;
//                    else
//                        return getHeight() / 2 + maxDistance;
//                }
//            }
//        }
//
//        return getHeight() / 2;
//    }

    /**
     * 获取当前播放的歌词索引
     * playedDuration：已播放时长，秒为单位
     **/
    public void setPlayingIndex(int playedDuration) {
        if (lyricsItems == null || lyricsItems.size() == 0) {
            playingIndex = -1;
            return;
        }

        int duration = playedDuration * 1000;
        for (int i = 0; i < lyricsItems.size(); i++) {
            if (lyricsItems.get(i).getStartTime() <= duration) {
                if ((i == lyricsItems.size() - 1) || (lyricsItems.get(i + 1).getStartTime() > duration)) {
                    playingIndex = i;
                    break;
                }
            }
        }
        if (!isTouchDown)
            invalidate();
    }

    private float movedDistance;
    private boolean isTouchDown;
    private float downY;
    private int oldDistance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(logTag, String.format("Down=========%f=========", downY));
                handler.removeMessages(TOUCH_DOWN_FLAG);
                isTouchDown = true;
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isTouchDown) {
                    movedDistance = event.getY() - downY;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.d(logTag, String.format("H_Moving=========%f=========", event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                Log.d(logTag, String.format("UP=========%f=========", event.getY()));
                if (playingIndex < startMovingIndex)
                    oldDistance = startY;
                else
                    oldDistance = centerY - getHeight() / 2;
                handler.sendEmptyMessageDelayed(TOUCH_DOWN_FLAG, 500);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(logTag, "============ACTION_CANCEL============");
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus)
            requestLayout();
    }
}
