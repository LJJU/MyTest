package com.example.svganimdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 功能：融云动画
 */

public class WowView extends View {

    //View宽高
    private int mWidth;
    private int mHeight;

    //Paint相关参数
    private PorterDuffXfermode mMode;
    private Paint mPaint;       //白云图片画笔
    private Paint mBackPaint;   //背景图片画笔

    //背景Bitmap相关参数
    private Bitmap mBackBitmap;   //背景
    private int mBackWidth;       //背景图片宽度
    private int mBackHeight;      //背景图片高度
    private Rect mBackSrcRect;    //背景矩阵
    private Rect mBackDstRect;    //背景缩放矩阵

    //白云Bitmap相关参数
    private Bitmap mCloudBitmap;   //白云
    private int mCloudWidth;       //白云图片宽度
    private int mCloudHeight;      //白云图片高度
    private Matrix mCloudMatrix;   //白云缩放Matrix

    //ValueAnimator
    private ValueAnimator mAnim;   //融云动画
    private float mAnimValue;      //动画进度

    public WowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        initPaint();
        initBitmap();
        initAnim();
        initMatrix();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setXfermode(mMode);

        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setDither(true);
        mBackPaint.setFilterBitmap(true);
    }

    /**
     * 初始化Bitmap
     */
    private void initBitmap() {
        mBackBitmap = null;

        mCloudBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wow_splash_shade);
        mCloudWidth = mCloudBitmap.getWidth();
        mCloudHeight = mCloudBitmap.getHeight();
    }

    /**
     * 初始化动画参数
     */
    private void initAnim() {
        mAnim = ValueAnimator.ofFloat(0, 1).setDuration(2000);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 初始化Matrix
     */
    private void initMatrix() {
        mCloudMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        if (mBackBitmap != null) {
            int startAlpha = 200;
            int stopAlpha = 150;
            int alpha = (int) (startAlpha - (startAlpha - stopAlpha) * mAnimValue);
            mBackPaint.setAlpha(alpha);
            canvas.drawBitmap(mBackBitmap, mBackSrcRect, mBackDstRect, mBackPaint);
        }

        //绘制白云
        float scale = 1f + mAnimValue * 5f;
        mCloudMatrix.reset();
        mCloudMatrix.postScale(scale, scale, mCloudWidth / 2, mCloudHeight / 2);
        canvas.drawBitmap(mCloudBitmap, mCloudMatrix, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mBackSrcRect = new Rect(0, 0, mBackWidth, mBackHeight);
        mBackDstRect = new Rect(0, 0, mWidth, mHeight);
    }

    /**
     * 设置背景图片
     *
     * @param bitmap
     */
    public void setBackBitmap(Bitmap bitmap) {
        if (bitmap == null) return;
        mBackBitmap = bitmap;
        mBackWidth = mBackBitmap.getWidth();
        mBackHeight = mBackBitmap.getHeight();
        invalidate();
    }

    /**
     * 启动动画
     *
     * @param bitmap
     */
    public void startAnimator(Bitmap bitmap) {
        setBackBitmap(bitmap);
        mAnim.start();
    }
}
