package com.yuqf.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ScalableImageButton extends ImageButton {
    private final String logTag = "ScalableImageButton";
    private float scaleXY = 1.5f;

    public ScalableImageButton(Context context) {
        super(context);
    }

    public ScalableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalableImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScalableImageButton, defStyleAttr, 0);
        scaleXY = typedArray.getFloat(R.styleable.ScalableImageButton_scale_xy, scaleXY);
        typedArray.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleX(scaleXY).scaleY(scaleXY).setDuration(200).start();
//                animate().scaleXBy(0.2f).scaleYBy(0.2f).setDuration(200).start();
                Log.d(logTag, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
//                animate().scaleXBy(-0.2f).scaleYBy(-0.2f).setDuration(200).start();
                Log.d(logTag, "ACTION_UP");
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                Log.d(logTag, "ACTION_BUTTON_PRESS");
                break;
            case MotionEvent.ACTION_BUTTON_RELEASE:
                Log.d(logTag, "ACTION_BUTTON_RELEASE");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(logTag, "ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.d(logTag, "ACTION_HOVER_ENTER");
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                Log.d(logTag, "ACTION_HOVER_EXIT");
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.d(logTag, "ACTION_HOVER_MOVE");
                break;
            case MotionEvent.ACTION_MASK:
                Log.d(logTag, "ACTION_MASK");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(logTag, "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.d(logTag, "ACTION_OUTSIDE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(logTag, "ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_INDEX_MASK:
                Log.d(logTag, "ACTION_POINTER_INDEX_MASK");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(logTag, "ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_SCROLL:
                Log.d(logTag, "ACTION_SCROLL");
                break;
            case MotionEvent.AXIS_BRAKE:
                Log.d(logTag, "AXIS_BRAKE");
                break;
            case MotionEvent.AXIS_DISTANCE:
                Log.d(logTag, "AXIS_DISTANCE");
                break;
        }

        return super.onTouchEvent(event);
    }
}
