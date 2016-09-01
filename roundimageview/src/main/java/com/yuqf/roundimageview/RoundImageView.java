package com.yuqf.roundimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
    private int shadowRadius = 4;
    private int shadowColor = Color.RED;
    private int borderWidth = 5;
    private int borderColor = Color.WHITE;
    private Paint borderPaint;
    private Paint paint;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        borderWidth = Math.round(typedArray.getDimension(R.styleable.RoundImageView_border_color, 5));
        borderColor = typedArray.getColor(R.styleable.RoundImageView_border_color, Color.WHITE);
        shadowRadius = Math.round(typedArray.getDimension(R.styleable.RoundImageView_shadow_width, 4));
        shadowColor = typedArray.getColor(R.styleable.RoundImageView_shadow_color, Color.BLACK);
        typedArray.recycle();

        setPaints();
    }

    private void setPaints() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        borderPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
        this.setLayerType(LAYER_TYPE_HARDWARE, borderPaint);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap srcBmp = ((BitmapDrawable) drawable).getBitmap();
        srcBmp = getScaleBitmap(srcBmp);
        int canvasSize = getWidth();
        if (canvasSize > getHeight())
            canvasSize = getHeight();

        int startX = (getWidth() - canvasSize) / 2;
        int startY = (getHeight() - canvasSize) / 2;

        Bitmap dstBitmap = getShadowBitmap(srcBmp, canvasSize);
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

        int imageRadius = canvasSize - shadowRadius * 2 - borderWidth * 2;
        canvas.drawCircle(canvasSize / 2, canvasSize / 2, imageRadius / 2 + borderWidth, borderPaint);

        Bitmap dstBitmap = getRoundBitmap(bitmap, imageRadius);
        canvas.drawBitmap(dstBitmap, borderWidth + shadowRadius, borderWidth + shadowRadius, null);

        return shadowBmp;
    }

    private Bitmap getRoundBitmap(Bitmap bitmap, int imageRadius) {
        if (bitmap.getWidth() != imageRadius)
            bitmap = Bitmap.createScaledBitmap(bitmap, imageRadius, imageRadius, false);

        Bitmap dstBitmap = Bitmap.createBitmap(imageRadius, imageRadius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(borderColor);

        canvas.drawCircle(imageRadius / 2, imageRadius / 2, imageRadius / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return dstBitmap;
    }

    private Bitmap getScaleBitmap(Bitmap srcBmp) {
        Bitmap copyBmp = srcBmp.copy(Bitmap.Config.ARGB_8888, true);
        int bmpWidth = copyBmp.getWidth();
        int bmpHeight = copyBmp.getHeight();
        if (bmpWidth > bmpHeight) {
            copyBmp = Bitmap.createBitmap(copyBmp, (bmpWidth - bmpHeight) / 2, 0, bmpHeight, bmpHeight);
        } else {
            copyBmp = Bitmap.createBitmap(copyBmp, 0, (bmpHeight - bmpWidth) / 2, bmpWidth, bmpWidth);
        }
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
}
