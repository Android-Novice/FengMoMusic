package com.yuqf.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
    /*Round: 圆角；Circle：圆形*/
    private int shadowRadius = 4;
    private int shadowColor = Color.RED;
    private int borderWidth = 5;
    private int borderColor = Color.WHITE;
    private int backColor = Color.LTGRAY;
    private Paint borderPaint;
    private Paint paint;
    private Paint backPaint;
    private Bitmap roundImage;
    private Drawable oldDrawable;
    private Bitmap dstBitmap;
    private int startX;
    private int startY;
    private final String logTag = "RoundImageView";
    private boolean isCircle;
    private int roundRadius;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_border_width, 5);
        borderColor = typedArray.getColor(R.styleable.RoundImageView_border_color, Color.WHITE);
        shadowRadius = typedArray.getLayoutDimension(R.styleable.RoundImageView_shadow_width, 4);
        shadowColor = typedArray.getColor(R.styleable.RoundImageView_shadow_color, Color.BLACK);
        backColor = typedArray.getColor(R.styleable.RoundImageView_back_color, Color.LTGRAY);
        isCircle = typedArray.getBoolean(R.styleable.RoundImageView_is_circle, true);
        roundRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_round_radius, 5);
        typedArray.recycle();

        setPaints();
    }

    private void setPaints() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        if (!isInEditMode() && isCircle) {
            borderPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
            this.setLayerType(LAYER_TYPE_HARDWARE, borderPaint);
        }

        paint = new Paint();
        paint.setAntiAlias(true);

        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setColor(backColor);
    }

    public int getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
        invalidate();
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        invalidate();
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (oldDrawable != drawable) {
            oldDrawable = drawable;
            Log.d("RoundImageView", "onDraw....\n");
            Bitmap srcBmp = ((BitmapDrawable) drawable).getBitmap();
            srcBmp = getScaleBitmap(srcBmp);
            int canvasSize = getWidth();
            if (canvasSize > getHeight())
                canvasSize = getHeight();
            startX = (getWidth() - canvasSize) / 2;
            startY = (getHeight() - canvasSize) / 2;
            dstBitmap = getShadowBitmap(srcBmp, canvasSize);
        }
        if (dstBitmap != null)
            canvas.drawBitmap(dstBitmap, startX, startY, null);

        //这个也是一种绘制圆形图片的方法，但是这个有点问题没有解决
//        int imageRadius = canvasSize - shadowRadius * 2 - borderWidth * 2;
//        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(srcBmp, imageRadius, imageRadius, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        paint.setShader(shader);
//        canvas.drawCircle(startX + borderWidth + shadowRadius + imageRadius / 2, startY + borderWidth + shadowRadius + imageRadius / 2, imageRadius / 2, paint);
    }

    private Bitmap getShadowBitmap(Bitmap bitmap, int canvasSize) {

        Bitmap shadowBmp = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shadowBmp);

        int imageRadius;
        if (isCircle) {
            /*如果是圆形，就绘制边框、阴影、背景色，否则都不绘制*/
            imageRadius = canvasSize - shadowRadius * 2 - borderWidth * 2;
            canvas.drawCircle(canvasSize / 2, canvasSize / 2, imageRadius / 2 + borderWidth, borderPaint);

            canvas.drawCircle(canvasSize / 2, canvasSize / 2, imageRadius / 2, backPaint);
        } else {
            imageRadius = canvasSize;
        }

        Bitmap dstBitmap = getRoundBitmap(bitmap, imageRadius);
        if (isCircle) {
            canvas.drawBitmap(dstBitmap, borderWidth + shadowRadius, borderWidth + shadowRadius, null);
        } else {
            canvas.drawBitmap(dstBitmap, 0, 0, null);
        }
        return shadowBmp;
    }

    private Bitmap getRoundBitmap(Bitmap bitmap, int imageRadius) {
        if (bitmap.getWidth() != imageRadius) {
            bitmap = Bitmap.createScaledBitmap(bitmap, imageRadius, imageRadius, false);
        }
        Log.d(logTag, "RoundBmp Size: " + "/n Width:" + bitmap.getWidth() + "\n Height:" + bitmap.getHeight() + "\nrealRadius:" + imageRadius);
        Bitmap dstBitmap = Bitmap.createBitmap(imageRadius, imageRadius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(borderColor);

        if (isCircle) {
            canvas.drawCircle(imageRadius / 2, imageRadius / 2, imageRadius / 2, paint);
        } else {
            if (roundRadius > imageRadius / 2)
                roundRadius = imageRadius / 2;
            canvas.drawRoundRect(new RectF(0, 0, imageRadius, imageRadius), roundRadius, roundRadius, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        roundImage = dstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        return dstBitmap;
    }

    private Bitmap getScaleBitmap(Bitmap srcBmp) {
        Bitmap copyBmp = srcBmp.copy(Bitmap.Config.ARGB_8888, true);
        int bmpWidth = copyBmp.getWidth();
        int bmpHeight = copyBmp.getHeight();
        Log.d(logTag, "SrcBmp Size: " + "/n Width:" + bmpWidth + "\n Height:" + bmpHeight);

        if (bmpWidth == bmpHeight) {
            return copyBmp;
        }
        if (bmpWidth > bmpHeight) {
            copyBmp = Bitmap.createBitmap(copyBmp, (bmpWidth - bmpHeight) / 2, 0, bmpHeight, bmpHeight);
        } else {
            copyBmp = Bitmap.createBitmap(copyBmp, 0, (bmpHeight - bmpWidth) / 2, bmpWidth, bmpWidth);
        }

        Log.d(logTag, "ScaleBmp Size: " + "/n Width:" + bmpWidth + "\n Height:" + bmpHeight);
        return copyBmp;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            requestLayout();
        }
    }

    public Bitmap getRoundImage() {
        return roundImage;
    }
}
