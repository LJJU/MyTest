package com.example.searchloadingdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 功能：放大镜搜索
 */

public class SearchView extends View {

    @IntDef({StateType.STATE_PRE, StateType.STATE_START, StateType.STATE_SEARCH, StateType.STATE_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StateType {
        int STATE_PRE = 0;     //当前动画状态：搜索前后
        int STATE_START = 1;   //当前动画状态：准备搜索
        int STATE_SEARCH = 2;  //当前动画状态：正在搜索
        int STATE_END = 3;     //当前动画状态：结束搜索
    }

    //默认参数
    private final int DURATION = 2000;            //动画周时长
    private final int COLOR_BLACK = Color.BLACK;  //画笔颜色
    private final int PADDING = 10;

    //View大小
    private int mWidth;             //宽度
    private int mHeight;            //高度
    private int mPadding = PADDING; //ViewPadding
    private boolean isOver;         //搜索是否结束

    //画笔参数
    private Paint mPaint;                     //画笔
    private int mPaintColor = COLOR_BLACK;    //画笔颜色

    //Path相关参数
    private PathMeasure mPathMeasure;   //用于截取部分Path
    private Path mPathCircle;           //圆形
    private Path mPathSrarch;           //放大镜

    //动画相关参数
    //ValueAnimator用于控制动画
    private ValueAnimator mStartAnimator;   //准备搜索动画
    private ValueAnimator mSearchAnimator;  //正在搜索动画
    private ValueAnimator mEndAnimator;     //搜索结束动画

    //ValueAnimator监听器
    private ValueAnimator.AnimatorUpdateListener updateListener;
    private Animator.AnimatorListener animatorListener;

    private float mAnimatorValue;       //当前动画时间
    private int mDuration = DURATION;   //动画周时长
    private Handler mHandler;           //动画控制器

    //当前动画状态
    private
    @StateType
    int mState = StateType.STATE_PRE;

    public SearchView(Context context, AttributeSet attrs) {
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
        //initPath();
        initListener();
        initAnimator(mDuration);
        initHandler();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);  //空心
        mPaint.setColor(mPaintColor);         //颜色
        mPaint.setStrokeWidth(15);            //粗细
        mPaint.setAntiAlias(true);            //平滑
        mPaint.setStrokeCap(Paint.Cap.ROUND); //笔刷的图形样式
    }

    /**
     * 初始化Path
     */
    private void initPath() {
        //为了获取当前View宽高,放在onSizeChanged中初始化
        //根据当前View宽高调整图标大小
        mPathCircle = new Path();
        mPathSrarch = new Path();

        //获取较小的边长
        int minSize = Math.min(mWidth, mHeight) - 2 * mPadding;

        //外部圆环
        float rfCircleR = minSize / 2;
        RectF rfCircle = new RectF(-rfCircleR, -rfCircleR, rfCircleR, rfCircleR);
        mPathCircle.addArc(rfCircle, 45, -359.9f);

        //内部圆环
        float rfSrarchCircleR = rfCircleR / 2;
        RectF rfSrarchCircle = new RectF(-rfSrarchCircleR, -rfSrarchCircleR, rfSrarchCircleR, rfSrarchCircleR);
        mPathSrarch.addArc(rfSrarchCircle, 45, 359.9f);

        //获取放大镜把手位置与外部圆环的焦点坐标
        float[] pos = new float[2];
        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mPathCircle, false);
        mPathMeasure.getPosTan(0, pos, null);
        mPathSrarch.lineTo(pos[0], pos[1]);
    }

    /**
     * 初始化动画监听器
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
                //更新动画
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
     * 以指定的动画周时长初始化ValueAnimator
     *
     * @param duration
     */
    private void initAnimator(int duration) {
        mStartAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        mSearchAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        mEndAnimator = ValueAnimator.ofFloat(1, 0).setDuration(duration);

        mStartAnimator.addUpdateListener(updateListener);
        mSearchAnimator.addUpdateListener(updateListener);
        mEndAnimator.addUpdateListener(updateListener);

        mStartAnimator.addListener(animatorListener);
        mSearchAnimator.addListener(animatorListener);
        mEndAnimator.addListener(animatorListener);
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mState) {
                    case StateType.STATE_START: {
                        //准备搜索动画结束->正在搜索动画
                        isOver = false;
                        mState = StateType.STATE_SEARCH;
                        mSearchAnimator.start();
                        break;
                    }
                    case StateType.STATE_SEARCH: {
                        if (isOver) {
                            //搜索结束
                            mState = StateType.STATE_END;
                            mEndAnimator.start();
                        } else {
                            //正在搜索
                            mSearchAnimator.start();
                        }
                        break;
                    }
                    case StateType.STATE_END: {
                        //结束搜索
                        mState = StateType.STATE_PRE;
                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearch(canvas);
    }

    /**
     * 绘制搜索动画
     *
     * @param canvas
     */
    private void drawSearch(Canvas canvas) {
        //移动画布到View中点
        canvas.translate(mWidth / 2, mHeight / 2);

        switch (mState) {
            default: {
            }
            case StateType.STATE_PRE: {
                //搜索前后
                canvas.drawPath(mPathSrarch, mPaint);
                break;
            }
            case StateType.STATE_START: {
                //准备搜索
                Path dstPath = new Path();
                mPathMeasure.setPath(mPathSrarch, false);
                float startD = mPathMeasure.getLength() * mAnimatorValue;
                float stopD = mPathMeasure.getLength();
                mPathMeasure.getSegment(startD, stopD, dstPath, true);
                canvas.drawPath(dstPath, mPaint);
                break;
            }
            case StateType.STATE_SEARCH: {
                //正在搜索
                Path dstPath = new Path();
                mPathMeasure.setPath(mPathCircle, false);
                float stopD = mPathMeasure.getLength() * mAnimatorValue;
                float startD = stopD - (Math.abs(1 - mAnimatorValue)) * 50f;
                mPathMeasure.getSegment(startD, stopD, dstPath, true);
                canvas.drawPath(dstPath, mPaint);
                break;
            }
            case StateType.STATE_END: {
                //结束搜索
                Path dstPath = new Path();
                mPathMeasure.setPath(mPathSrarch, false);
                float startD = mPathMeasure.getLength() * mAnimatorValue;
                float stopD = mPathMeasure.getLength();
                mPathMeasure.getSegment(startD, stopD, dstPath, true);
                canvas.drawPath(dstPath, mPaint);
                break;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //获取View大小
        mWidth = w;
        mHeight = h;

        initPath();
    }

    /** 以下为开放给用户的方法
     *  用于调整View状态 **/

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {
        mPaintColor = color;
        mPaint.setColor(mPaintColor);
    }

    /**
     * 设置动画运行一周所需的时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        mDuration = duration;
        initAnimator(mDuration);
    }

    /**
     * 设置padding
     *
     * @param padding
     */
    public void setPadding(int padding) {
        mPadding = mPadding;
    }

    /**
     * 启动搜索动画
     * 执行准备搜索动画->循环执行正在搜索动画
     */
    public void startSearchAnimation() {
        isOver = false;

        if (!mStartAnimator.isStarted() && !mSearchAnimator.isRunning() && !mEndAnimator.isRunning()) {
            mState = StateType.STATE_START;
            mStartAnimator.start();
        }
    }

    /**
     * 关闭搜索动画
     * 循环执行正在搜索动画->执行搜索完毕动画
     */
    public void stopSearchAnimation() {
        isOver = true;
    }
}
