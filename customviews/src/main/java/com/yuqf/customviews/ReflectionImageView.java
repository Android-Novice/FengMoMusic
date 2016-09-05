package com.yuqf.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ReflectionImageView extends ImageView {

    /**
     * 是否绘制原图，
     * 如果不绘制原图，就只绘制倒影，
     * 当绘制原图时，倒影可以绘制的高度是根据View的高度和原图的高度控制的，有可能绘制不出来图片的倒影
     **/
    private boolean drawSrc = false;
    /**
     * 倒影和原图之间的间隙
     **/
    private int reflectionGap = 20;
    private int gapColor = Color.BLACK;
    /**
     * 倒影的宽度，和View的宽度、图片宽度有关系，
     * 当图片太宽时，会先把图片压缩到和View一样的宽度，
     * 当图片的宽度小时，直接绘制到View水平居中位置
     **/
    private int reflectionWidth;
    /**
     * 倒影的高度，这个和View本身的高度、图片的高度有关系，
     * 另外高度截取是从下面往上面截取的
     **/
    private int reflectionHeight;

    private int rotateDegree;
    private Bitmap reflectionImg;
    private Paint gapPaint;

    public ReflectionImageView(Context context) {
        this(context, null);
    }

    public ReflectionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProperties(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReflectionImageView, defStyleAttr, 0);
        drawSrc = typedArray.getBoolean(R.styleable.ReflectionImageView_draw_src, false);
        reflectionGap = typedArray.getDimensionPixelSize(R.styleable.ReflectionImageView_reflection_gap, 4);
        gapColor = typedArray.getColor(R.styleable.ReflectionImageView_reflection_gap_color, Color.BLACK);
        rotateDegree = typedArray.getInt(R.styleable.ReflectionImageView_reflection_rotate_degree, 0);
        typedArray.recycle();
    }

    private void initPaints() {
        gapPaint = new Paint();
        gapPaint.setAlpha(122);
        gapPaint.setColor(gapColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;
        Bitmap srcBmp = ((BitmapDrawable) drawable).getBitmap();
        reflectionWidth = srcBmp.getWidth();
        int srcHeight = srcBmp.getHeight();
        int viewWidth = getWidth();
        if (reflectionWidth > viewWidth) {
            srcBmp = Bitmap.createScaledBitmap(srcBmp, viewWidth, srcHeight * viewWidth / reflectionWidth, false);
            reflectionWidth = viewWidth;
        }
        if (reflectionImg == null)
            reflectionImg = getReflectionBmp(srcBmp);
        canvas.drawBitmap(reflectionImg, (viewWidth - reflectionImg.getWidth()) / 2, 0, null);
    }

    private Bitmap getReflectionBmp(Bitmap srcBmp) {
        Bitmap bitmap = srcBmp.copy(Bitmap.Config.ARGB_8888, false);

        reflectionHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preRotate(rotateDegree);
        matrix.preScale(1, -1);
        Bitmap reflectBmp = Bitmap.createBitmap(bitmap, 0, 0, reflectionWidth, reflectionHeight, matrix, false);

        Rect reflectRect;
        Bitmap dstBmp;
        Canvas canvas;
        if (drawSrc) {
            dstBmp = Bitmap.createBitmap(reflectionWidth, bitmap.getHeight() + reflectionGap + reflectionHeight, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(dstBmp);
            canvas.drawBitmap(bitmap, 0, 0, null);
            if (reflectionGap > 0) {
                canvas.drawRect(0, bitmap.getHeight(), reflectionWidth, bitmap.getHeight() + reflectionGap, gapPaint);
            }
            reflectRect = new Rect(0, bitmap.getHeight() + reflectionGap, reflectionWidth, bitmap.getHeight() + reflectionGap + reflectionHeight);
        } else {
            dstBmp = Bitmap.createBitmap(reflectionWidth, reflectionHeight + reflectionGap, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(dstBmp);
            if (reflectionGap > 0) {
                canvas.drawRect(0, 0, reflectionWidth, reflectionGap, gapPaint);
            }
            reflectRect = new Rect(0, reflectionGap, reflectionWidth, reflectionGap + reflectionHeight);
        }
        canvas.drawBitmap(reflectBmp, reflectRect.left, reflectRect.top, null);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        LinearGradient linearGradient = new LinearGradient(
                reflectRect.left,
                reflectRect.top,
                reflectRect.left,
                reflectRect.bottom,
                0x70ffffff,
                0x00ffffff,
                Shader.TileMode.MIRROR);
        paint.setShader(linearGradient);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(reflectRect, paint);
        return dstBmp;
    }
}
