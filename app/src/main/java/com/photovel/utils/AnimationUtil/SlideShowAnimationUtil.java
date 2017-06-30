package com.photovel.utils.AnimationUtil;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.InterpolatorRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by daybreak on 2017-06-29.
 */

public class SlideShowAnimationUtil extends Animation implements ViewPager.PageTransformer {
    private long startOffset;
    private long durationMillis;
    private long startTimeMillis;
    private float scale;
    private int repeatMode;
    private int repeatCount;


    public SlideShowAnimationUtil() {
    }

    public SlideShowAnimationUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public void setInterpolator(Context context, @AnimRes @InterpolatorRes int resID) {
        super.setInterpolator(context, resID);
    }

    @Override
    public void setInterpolator(Interpolator i) {
        super.setInterpolator(i);
    }

    @Override
    public void setStartOffset(long startOffset) {
        super.setStartOffset(startOffset);
    }

    @Override
    public void setDuration(long durationMillis) {
        super.setDuration(durationMillis);
    }

    @Override
    public void scaleCurrentDuration(float scale) {
        super.scaleCurrentDuration(scale);
    }

    @Override
    public void setStartTime(long startTimeMillis) {
        super.setStartTime(startTimeMillis);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void startNow() {
        super.startNow();
    }

    @Override
    public void setRepeatMode(int repeatMode) {
        super.setRepeatMode(repeatMode);
    }

    @Override
    public void setRepeatCount(int repeatCount) {
        super.setRepeatCount(repeatCount);
    }

    @Override
    public Interpolator getInterpolator() {
        return super.getInterpolator();
    }

    @Override
    public long getStartTime() {
        return super.getStartTime();
    }

    @Override
    public long getDuration() {
        return super.getDuration();
    }

    @Override
    public long getStartOffset() {
        return super.getStartOffset();
    }

    @Override
    public int getRepeatMode() {
        return super.getRepeatMode();
    }

    @Override
    public int getRepeatCount() {
        return super.getRepeatCount();
    }

    @Override
    public void setAnimationListener(AnimationListener listener) {
        super.setAnimationListener(listener);
    }

    @Override
    protected void ensureInterpolator() {
        super.ensureInterpolator();
    }

    @Override
    public long computeDurationHint() {
        return super.computeDurationHint();
    }

    @Override
    public boolean hasStarted() {
        return super.hasStarted();
    }

    @Override
    public boolean hasEnded() {
        return super.hasEnded();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        //화면이 왼쪽에 감춰져 있는 경우
        if(position < -1){

        }
    }
}
