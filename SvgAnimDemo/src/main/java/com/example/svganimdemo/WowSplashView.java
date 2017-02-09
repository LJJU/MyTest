package com.example.svganimdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.example.svganimdemo.support.SvgPathParser;

/**
 * 功能：描边动画效果
 */

public class WowSplashView extends View {

    private final int STATE_STARTANIM = 0;    //描边动画
    private final int STATE_TRANSANIM = 1;    //透明度动画

    //View大小
    private int mWidth;   //宽度
    private int mHeight;  //高度

    //Paint
    private Paint mPaint;       //画笔
    private Matrix mMatrix;     //用于Path缩放

    //Path
    private Path mTowerPath;        //SVG转换的Path
    private Path mTowerDst;         //截取的路径
    private Path mTowerOut;         //轮廓
    private PathMeasure mMeasure;   //用于截取路径

    //白云Path
    private Path[] mCouldPaths;
    private int couldCount = 3;
    private float mCouldX[] = {0f, 100f, 350f};
    private float mCouldY[] = {100f, 110f, 90f};

    //ValueAnimator
    private ValueAnimator mStartAnimator;    //描边动画
    private ValueAnimator mTransAnimator;    //透明度动画

    private float mAnimatorValue;   //动画进度
    private int mAnimatorState;     //动画状态

    //动画监听器
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private Animator.AnimatorListener animatorListener;

    private Handler mHandler;
    private OnAnimFinishedListener animFinishedListener;

    public WowSplashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initPaint();
//        initPath();
        initListener();
        initAnimator();
        initHandler();

        mStartAnimator.start();
        mAnimatorState = STATE_STARTANIM;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);  //空心
        mPaint.setColor(Color.WHITE);         //颜色
        mPaint.setStrokeWidth(2);             //粗细
        mPaint.setAntiAlias(true);            //平滑
        mPaint.setStrokeCap(Paint.Cap.ROUND); //笔刷的图形样式
        mPaint.setTextSize(30);
    }

    /**
     * 初始化Path
     */
    private void initPath() {
        SvgPathParser spp = new SvgPathParser();
        mTowerPath = spp.parsePath(getResources().getString(R.string.path_00));

        //缩放Path适配屏幕大小
        mMatrix = new Matrix();
        mMatrix.postScale(mWidth / spp.getPathWidth(), mHeight / spp.getPathHeight());
        mTowerPath.transform(mMatrix);

        mTowerDst = new Path();
        mMeasure = new PathMeasure(mTowerPath, false);

        mTowerOut = new Path();
        mMeasure.getSegment(0, mMeasure.getLength(), mTowerOut, true);

        mCouldPaths = new Path[couldCount];
        for (int i = 0; i < mCouldPaths.length; i++) {
            mCouldPaths[i] = new Path();
        }
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        //描边与透明度动画
        mStartAnimator = ValueAnimator.ofFloat(0, 1).setDuration(3000);
        mTransAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1000);

        mStartAnimator.addUpdateListener(updateListener);
        mTransAnimator.addUpdateListener(updateListener);

        mStartAnimator.addListener(animatorListener);
        mTransAnimator.addListener(animatorListener);
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mAnimatorState) {
                    case STATE_STARTANIM: {
                        //描边动画完成，开始透明度动画
                        mTransAnimator.start();
                        mAnimatorState = STATE_TRANSANIM;
                        break;
                    }
                    case STATE_TRANSANIM: {
                        //动画结束
                        if (animFinishedListener != null) {
                            animFinishedListener.onAnimFinished();
                        }
                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        drawTower(canvas);
    }

    /**
     * 绘制描边动画以及透明度动画
     */
    private void drawTower(Canvas canvas) {
        //防止动画切换闪烁
        if (mAnimatorValue == 0) {
            mAnimatorValue = 0.001f;
        } else if (mAnimatorValue == 1) {
            mAnimatorValue = 0.999f;
        }

        switch (mAnimatorState) {
            case STATE_STARTANIM: {
                //绘制白云
                drawCould(canvas, mAnimatorValue);

                //描边动画
                mPaint.setStrokeWidth(3);
                mTowerDst.reset();
                float startD = 0;
                float stopD = mMeasure.getLength() * mAnimatorValue;
                mMeasure.getSegment(startD, stopD, mTowerDst, true);
                canvas.drawPath(mTowerDst, mPaint);
                break;
            }
            case STATE_TRANSANIM: {
                //绘制白云
                drawCould(canvas, 1);

                //透明度动画
                mPaint.setStrokeWidth(3);
                canvas.drawPath(mTowerOut, mPaint);
                mPaint.setStrokeWidth(2);
                int startAlpha = 150;
                int stopAlpha = 255;
                int alpha = (int) ((stopAlpha - startAlpha) * mAnimatorValue + startAlpha);
                mPaint.setAlpha(alpha);
                canvas.drawPath(mTowerPath, mPaint);
                break;
            }
        }
    }

    /**
     * 绘制白云动画
     *
     * @param canvas
     */
    private void drawCould(Canvas canvas, float animValue) {
        mPaint.setStrokeWidth(3);
        if (animValue < 0.5f) animValue = 0;
        else animValue -= 0.5f;

        for (int i = 0; i < mCouldPaths.length; i++) {
            setupCouldPath(mCouldPaths[i], i, animValue * 100, animValue * 400);
            canvas.drawPath(mCouldPaths[i], mPaint);
        }
        mPaint.setStrokeWidth(2);
    }

    /**
     * 初始化白云Path
     *
     * @param path
     * @param pos
     */
    private void setupCouldPath(Path path, int pos, float offsetX, float offsetY) {
        path.reset();
        path.moveTo(mCouldX[pos], mCouldY[pos]);
        path.lineTo(mCouldX[pos] + 30, mCouldY[pos]);
        path.quadTo(mCouldX[pos] + 30 + 30, mCouldY[pos] - 50, mCouldX[pos] + 30 + 60, mCouldY[pos]);
        path.lineTo(mCouldX[pos] + 30 + 60 + 30, mCouldY[pos]);
        Matrix matrix = new Matrix(mMatrix);
        matrix.postTranslate(offsetX, offsetY);
        path.transform(matrix);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        initPath();
    }

    /** 开放给用户的方法 **/

    /**
     * 动画完毕监听器
     */
    public interface OnAnimFinishedListener {
        void onAnimFinished();
    }

    /**
     * 设置动画完毕监听器
     *
     * @param listener
     */
    public void setOnAnimFinishedListener(OnAnimFinishedListener listener) {
        animFinishedListener = listener;
    }
}
