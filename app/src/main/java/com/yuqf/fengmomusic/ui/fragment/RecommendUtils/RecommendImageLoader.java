package com.yuqf.fengmomusic.ui.fragment.RecommendUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.yuqf.customviews.Banner.IImageLoader;
import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.MyApplication;
import com.yuqf.fengmomusic.utils.FileUtils;

import java.io.File;

/**
 * Created by Yuqf on 2017/1/4.
 */
public class RecommendImageLoader implements IImageLoader {

    public static RecommendImageLoader imageLoader;

    public static RecommendImageLoader getInstance() {
        if (imageLoader == null) {
            imageLoader = new RecommendImageLoader();
        }
        return imageLoader;
    }

    @Override
    public void loadImage(String url, final ImageView imageView) {
        final String imagePath = FileUtils.getRecommendPlayImagePath(url, false);
        File imageFile = new File(imagePath);
        if (imageFile.exists())
            imageView.setImageURI(Uri.fromFile(imageFile));
        else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            MyApplication.getPicasso().load(url).placeholder(R.drawable.downloading_animation).error(R.mipmap.ic_launcher).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    FileUtils.saveBitmap(bitmap, imagePath);
                }

                @Override
                public void onError() {

                }
            });
        }
    }
}
