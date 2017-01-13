package com.yuqf.customviews.Banner;

import android.support.v4.view.ViewPager.PageTransformer;

import com.yuqf.customviews.Banner.Transformer.AccordionTransformer;
import com.yuqf.customviews.Banner.Transformer.BackgroundToForegroundTransformer;
import com.yuqf.customviews.Banner.Transformer.CubeInTransformer;
import com.yuqf.customviews.Banner.Transformer.CubeOutTransformer;
import com.yuqf.customviews.Banner.Transformer.DefaultTransformer;
import com.yuqf.customviews.Banner.Transformer.DepthPageTransformer;
import com.yuqf.customviews.Banner.Transformer.FlipHorizontalTransformer;
import com.yuqf.customviews.Banner.Transformer.FlipVerticalTransformer;
import com.yuqf.customviews.Banner.Transformer.ForegroundToBackgroundTransformer;
import com.yuqf.customviews.Banner.Transformer.RotateDownTransformer;
import com.yuqf.customviews.Banner.Transformer.RotateUpTransformer;
import com.yuqf.customviews.Banner.Transformer.ScaleInOutTransformer;
import com.yuqf.customviews.Banner.Transformer.StackTransformer;
import com.yuqf.customviews.Banner.Transformer.TabletTransformer;
import com.yuqf.customviews.Banner.Transformer.ZoomInTransformer;
import com.yuqf.customviews.Banner.Transformer.ZoomOutSlideTransformer;
import com.yuqf.customviews.Banner.Transformer.ZoomOutTranformer;

public class TransferAnimation {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}
