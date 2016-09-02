package com.yuqf.roundimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgressBar extends View {

    /**
     * 内圆半径
     **/
    private int innerRadius;
    /**
     * 进度条的条宽
     **/
    private int barWidth = 40;
    /**
     * 进度条的颜色
     */
    private int barColor = Color.BLACK;
    /**
     * 是否展示进度条背景槽
     **/
    private boolean showBackgroundArc = false;
    /**
     * 当展示进度条背景槽的时候，背景槽比进度条多出来的宽
     **/
    private int stretchWidth = 10;
    /**
     * 当展示进度条背景槽的时候，背景槽的颜色
     **/
    private int backgroundArcColor = Color.RED;
    /**
     * 是否展示进度条的外圈圆形，
     * 如果为true，radius长为【innerRadius + barWidth】，
     * 如果为false，radius则为【innerRadius+1】；
     * 当showBackgroundArc值为true时，这个不生效
     **/
    private IOCirclePosition ioCirclePosition = IOCirclePosition.Center;
    /**
     * 如果展示外圈圆或者内圈圆，画笔的宽
     **/
    private int ioCircleWidth = 1;
    private int ioCircleColor = Color.BLUE;

    /**
     * 当前进度
     **/
    private int progress = 20;

    private Paint outerPaint;
    private Paint barPaint;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initProperties(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar, defStyleAttr, 0);

        barWidth = typedArray.getDimensionPixelSize(R.styleable.RoundProgressBar_bar_width, 20);
        barColor = typedArray.getColor(R.styleable.RoundProgressBar_bar_color, Color.WHITE);
        showBackgroundArc = typedArray.getBoolean(R.styleable.RoundProgressBar_show_background_arc, true);
        backgroundArcColor = typedArray.getColor(R.styleable.RoundProgressBar_arc_background_color, Color.BLACK);
        stretchWidth = typedArray.getDimensionPixelSize(R.styleable.RoundProgressBar_arc_stretch_width, 2);
        int positionType = typedArray.getInt(R.styleable.RoundProgressBar_io_circle_position, -1);
        switch (positionType) {
            case -1:
                ioCirclePosition = IOCirclePosition.Outer;
                break;
            case -2:
                ioCirclePosition = IOCirclePosition.Inner;
                break;
            case -3:
                ioCirclePosition = IOCirclePosition.Center;
                break;
        }
        ioCircleColor = typedArray.getColor(R.styleable.RoundProgressBar_io_circle_color, Color.BLUE);
        ioCircleWidth = typedArray.getDimensionPixelSize(R.styleable.RoundProgressBar_io_circle_width, 1);
        progress = typedArray.getInt(R.styleable.RoundProgressBar_progress, 0);
        typedArray.recycle();
    }

    private void initPaints() {
        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setStyle(Paint.Style.STROKE);
        if (showBackgroundArc) {
            outerPaint.setAlpha(120);
            outerPaint.setColor(backgroundArcColor);
            outerPaint.setStrokeWidth(barWidth + stretchWidth * 2);
        } else {
            outerPaint.setColor(ioCircleColor);
            outerPaint.setStrokeWidth(ioCircleWidth);
        }

        barPaint = new Paint();
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeCap(Paint.Cap.ROUND);
        barPaint.setStrokeWidth(barWidth);
        barPaint.setColor(barColor);

        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int outerRadius = getWidth();
        if (getWidth() > getHeight())
            outerRadius = getHeight();

        int startX = (getWidth() - outerRadius) / 2;
        int startY = (getHeight() - outerRadius) / 2;

        innerRadius = outerRadius - barWidth;
        if (showBackgroundArc)
            innerRadius -= stretchWidth;
        if (innerRadius <= 0) return;

        float outR = (float) outerRadius / 2;
        float barR = (float) barWidth / 2;
        RectF barRect;
        if (showBackgroundArc) {
            canvas.drawCircle(startX + outR, startY + outR, outR - (float) (barWidth + stretchWidth * 2) / 2, outerPaint);
            barRect = new RectF(startX + stretchWidth + barR, startY + stretchWidth + barR, innerRadius + barR, innerRadius + barR);
        } else {
            float ioCircleRadius = outR - (float) ioCircleWidth / 2;
            switch (ioCirclePosition) {
                case Center:
                    ioCircleRadius -= (float) (barWidth - 1) / 2;
                    break;
                case Inner:
                    ioCircleRadius -= barWidth - 1;
                    break;
                case Outer:
                    ioCircleRadius -= 0;
                    break;
            }
            canvas.drawCircle(startX + outR - (float) ioCircleWidth / 2, startY + outR - (float) ioCircleWidth / 2, ioCircleRadius, outerPaint);
            barRect = new RectF(startX + barR, startY + barR, outerRadius - barR, outerRadius - barR);
        }
        canvas.drawArc(barRect, -90, (float) progress * 360 / 100, false, barPaint);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    enum IOCirclePosition {
        Outer,
        Inner,
        Center,
    }
}
