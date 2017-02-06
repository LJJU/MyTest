package com.example.administrator.mytest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 功能：电风扇+叶子加载条
 * 来源：取自网上,林敬聚修改
 */
public class LeafLoading extends View {
    //默认参数
    private static final int WHITE_COLOR = 0xfffde399;   //淡白色
    private static final int ORANGE_COLOR = 0xffffa800;  //橙色
    private static final int LEFT_MARGIN = 9;            //进度条左边距：dp
    private static final int RIGHT_MARGIN = 25;          //进度条右边距：dp
    private static final int TOTAL_PROGRESS = 100;       //总进度
    private static final long LEAF_FLOAT_TIME = 3000;    //叶子飘动一个周期所花的时间
    private static final long LEAF_ROTATE_TIME = 2000;   //叶子旋转一周需要的时间
    private static final int MIDDLE_AMPLITUDE = 15;      //中等振幅大小
    private static final int AMPLITUDE_DISPARITY = 5;    //不同类型之间的振幅差距
    private static final int ICON_ROTATE_TIME = 3000;    //风扇旋转一周所需时间
    private static final int ICON_PADDING = 10;          //风扇图片padding

    //进度条相关参数
    private int mProgress = 0;              //当前进度
    private int mProgressWidth;             //当前进度条总宽度
    private int mCurrentProgressPosition;   //当前所在的绘制的进度条的位置

    private int mLeftMargin;                //进度条左边距
    private int mRightMargin;               //进度条右边距
    private int mArcRadius;                 //弧形的半径

    private int mArcRightLocation;          //矩形x坐标的起始点
    private RectF mWhiteRectF;              //进度条白色矩阵
    private RectF mOrangeRectF;             //进度条橘色矩阵
    private RectF mArcRectF;                //进度条左边圆弧矩阵

    //叶子相关参数
    private long mLeafFloatTime = LEAF_FLOAT_TIME;          //叶子飘动一个周期所花的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;        //叶子旋转一周需要的时间
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;        //中等振幅大小
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;  //振幅差

    private Bitmap mLeafBitmap;      //叶子图片资源
    private int mLeafWidth;          //叶子宽度
    private int mLeafHeight;         //叶子高度
    private int mAddTime;            //用于控制随机增加的时间不抱团
    private List<Leaf> mLeafInfos;   //记录叶子信息

    //风扇相关参数
    private long mIconRotateTime = ICON_ROTATE_TIME;    //风扇旋转一周所需时间
    private int mIconPadding = ICON_PADDING;            //风扇图片padding

    private Bitmap mIconBitmap;      //风扇图片资源
    private int mIconSize;           //风扇图片大小
    private RectF mIconSrcRect;      //风扇图片矩阵
    private RectF mIconDestRect;     //风扇缩放矩阵
    private long mIconStartTime;     //风扇开始时间

    //画笔相关参数
    private Paint mBitmapPaint;  //图片画笔
    private Paint mWhitePaint;   //颜色：白色画笔
    private Paint mOrangePaint;  //颜色：橘色画笔

    //背景相关参数
    private Bitmap mOuterBitmap;  //背景图片资源
    private int mOuterWidth;      //背景图片宽度
    private int mOuterHeight;     //背景图片高度
    private Rect mOuterSrcRect;   //图片矩阵
    private Rect mOuterDestRect;  //缩放矩阵

    public LeafLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        //获取进度条左右边距：dp转px
        mLeftMargin = UiUtils.dipToPx(context, LEFT_MARGIN);
        mRightMargin = UiUtils.dipToPx(context, RIGHT_MARGIN);
        mIconStartTime = System.currentTimeMillis();

        initBitmap();
        initPaint();

        LeafFactory leafFactory = new LeafFactory();
        mLeafInfos = leafFactory.generateLeafs();
    }

    /**
     * 加载图片资源
     */
    private void initBitmap() {
        Resources res = getResources();
        //叶子图片资源
        mLeafBitmap = ((BitmapDrawable) res.getDrawable(R.drawable.leaf)).getBitmap();
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();

        //背景图片资源
        mOuterBitmap = ((BitmapDrawable) res.getDrawable(R.drawable.leaf_kuang)).getBitmap();
        mOuterWidth = mOuterBitmap.getWidth();
        mOuterHeight = mOuterBitmap.getHeight();

        //风扇图片资源
        mIconBitmap = ((BitmapDrawable) res.getDrawable(R.drawable.fengshan)).getBitmap();
        mIconSize = Math.max(mIconBitmap.getWidth(), mIconBitmap.getHeight());
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制进度条和叶子
        drawProgressAndLeaf(canvas);

        //绘制背景
        canvas.drawBitmap(mOuterBitmap, mOuterSrcRect, mOuterDestRect, mBitmapPaint);

        //绘制风扇
        drawIcon(canvas);

        postInvalidate();
    }

    /**
     * 绘制风扇
     *
     * @param canvas
     */
    private void drawIcon(Canvas canvas) {
        canvas.save();

        long currentTime = System.currentTimeMillis();
        Matrix matrix = new Matrix();
        //风扇平移缩放
        matrix.setRectToRect(mIconSrcRect, mIconDestRect, Matrix.ScaleToFit.CENTER);
        //风扇旋转
        float rotateFraction = ((currentTime - mIconStartTime) % mIconRotateTime) / (float) mIconRotateTime;
        int angle = (int) (rotateFraction * 360);
        //计算旋转中心点坐标
        float transX = (mIconDestRect.right + mIconDestRect.left) / 2;
        float transY = (mIconDestRect.bottom + mIconDestRect.top) / 2;
        matrix.postRotate(angle, transX, transY);
        canvas.drawBitmap(mIconBitmap, matrix, mBitmapPaint);
        canvas.restore();
    }

    /**
     * 绘制进度条和叶子
     *
     * @param canvas
     */
    private void drawProgressAndLeaf(Canvas canvas) {
        if (mProgress > TOTAL_PROGRESS) {
            //如果当前进度超过最大进度,设为最大进度
            mProgress = TOTAL_PROGRESS;
        }

        //计算当前进度宽度
        mCurrentProgressPosition = mProgressWidth * mProgress / TOTAL_PROGRESS;
        if (mCurrentProgressPosition < mArcRadius) {
            //如果当前进度宽度小于圆弧半径
            //绘制白色部分
            canvas.drawArc(mArcRectF, 90, 180, false, mWhitePaint);
            mWhiteRectF.left = mArcRightLocation;
            canvas.drawRect(mWhiteRectF, mWhitePaint);

            //绘制叶子
            drawLeafs(canvas);

            //绘制橙色进度
            //计算单边角度：将弧度转换为角度
            int angle = (int) Math.toDegrees(Math.acos((mArcRadius - mCurrentProgressPosition) / (float) mArcRadius));
            int startAngle = 180 - angle;   //起始角度
            int sweepAngle = 2 * angle;     //扫过角度
            canvas.drawArc(mArcRectF, startAngle, sweepAngle, false, mOrangePaint);
        } else {
            //如果当前进度宽度大于圆弧半径
            //绘制白色进度
            mWhiteRectF.left = mCurrentProgressPosition;
            canvas.drawRect(mWhiteRectF, mWhitePaint);

            //绘制叶子
            drawLeafs(canvas);

            //绘制橙色进度
            canvas.drawArc(mArcRectF, 90, 180, false, mOrangePaint);
            mOrangeRectF.right = mCurrentProgressPosition;
            canvas.drawRect(mOrangeRectF, mOrangePaint);
        }
    }

    /**
     * 绘制叶子
     *
     * @param canvas
     */
    private void drawLeafs(Canvas canvas) {
        //判断旋转周期是否小于等于0
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        //当前系统时间
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                getLeafLocation(leaf, currentTime);
                canvas.save();

                //控制叶子运动
                Matrix matrix = new Matrix();
                //计算叶子位移
                float transX = mLeftMargin + leaf.x;
                float transY = mLeftMargin + leaf.y;
                matrix.postTranslate(transX, transY);
                //计算叶子旋转
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, transX
                        + mLeafWidth / 2, transY + mLeafHeight / 2);

                canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    /**
     * 计算叶子位置
     *
     * @param leaf
     * @param currentTime
     */
    private void getLeafLocation(Leaf leaf, long currentTime) {
        //与开始时间的时间间隔
        long intervalTime = currentTime - leaf.startTime;
        if (intervalTime < 0) {
            //如果当前时间小于开始时间
            return;
        } else if (intervalTime > mLeafFloatTime) {
            //如果时间间隔大于周期,重置叶子开始时间
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        //计算叶子位置
        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = mProgressWidth - mProgressWidth * fraction;
        leaf.y = getLocationY(leaf);
    }

    /**
     * 计算叶子纵坐标
     *
     * @param leaf
     * @return
     */
    private int getLocationY(Leaf leaf) {
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case StartType.LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case StartType.MIDDLE:
                a = mMiddleAmplitude;
                break;
            case StartType.BIG:
                // 大振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //计算进度条总长度
        mProgressWidth = w - mLeftMargin - mRightMargin;

        //计算背景图片大小及缩放大小
        mOuterSrcRect = new Rect(0, 0, mOuterWidth, mOuterHeight);
        mOuterDestRect = new Rect(0, 0, w, h);

        //计算圆弧半径
        mArcRadius = (h - 2 * mLeftMargin) / 2;

        //计算进度条矩阵
        mArcRectF = new RectF(mLeftMargin, mLeftMargin, mLeftMargin + 2 * mArcRadius,
                h - mLeftMargin);
        mWhiteRectF = new RectF(mLeftMargin + mCurrentProgressPosition, mLeftMargin,
                w - mRightMargin, h - mLeftMargin);
        mOrangeRectF = new RectF(mLeftMargin + mArcRadius, mLeftMargin,
                mCurrentProgressPosition, h - mLeftMargin);

        //计算矩阵起始坐标x
        mArcRightLocation = mLeftMargin + mArcRadius;

        //风扇矩阵
        mIconSrcRect = new RectF(0, 0, mIconSize, mIconSize);
        mIconDestRect = new RectF(w - h + mIconPadding, 0 + mIconPadding, w - mIconPadding, h - mIconPadding);
    }

    @IntDef(StartType.LITTLE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface StartType {
        int MIDDLE = 0;
        int LITTLE = 1;
        int BIG = 2;
    }

    /**
     * 记录叶子信息
     */
    private class Leaf {
        float x;                //叶子x坐标
        float y;                //叶子y坐标
        @StartType
        int type;         //控制叶子飘动的幅度
        int rotateAngle;        //旋转角度
        int rotateDirection;    // 旋转方向--0代表顺时针，1代表逆时针
        long startTime;         //起始时间(ms)
    }

    /**
     * 叶子加工厂
     */
    private class LeafFactory {
        private static final int MAX_LEAFS = 5;
        private Random random = new Random();

        /**
         * 生成叶子
         *
         * @return
         */
        public Leaf generateLeaf() {
            Leaf result = new Leaf();
            int randomType = random.nextInt(3);
            // 随时类型－ 随机振幅
            @StartType int type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    type = StartType.MIDDLE;
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            result.type = type;
            result.rotateAngle = random.nextInt(360);   // 随机起始的旋转角度
            result.rotateDirection = random.nextInt(2); // 随机旋转方向（顺时针或逆时针）
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            result.startTime = System.currentTimeMillis() + mAddTime;
            return result;
        }

        /**
         * 生成固定个数的叶子
         *
         * @return
         */
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        /**
         * 生成指定个数的叶子
         *
         * @return
         */
        public List<Leaf> generateLeafs(int count) {
            List<Leaf> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(generateLeaf());
            }
            return result;
        }
    }

    /**
     * 设置中等振幅
     *
     * @param amplitude
     */
    public void setMiddleAmplitude(int amplitude) {
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置振幅差
     *
     * @param disparity
     */
    public void setMplitudeDisparity(int disparity) {
        this.mAmplitudeDisparity = disparity;
    }

    /**
     * 获取中等振幅
     */
    public int getMiddleAmplitude() {
        return mMiddleAmplitude;
    }

    /**
     * 获取振幅差
     */
    public int getMplitudeDisparity() {
        return mAmplitudeDisparity;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress > TOTAL_PROGRESS ? TOTAL_PROGRESS : progress;
        postInvalidate();
    }

    /**
     * 设置叶子飘完一个周期所花的时间
     *
     * @param time
     */
    public void setLeafFloatTime(long time) {
        this.mLeafFloatTime = time;
    }

    /**
     * 设置叶子旋转一周所花的时间
     *
     * @param time
     */
    public void setLeafRotateTime(long time) {
        this.mLeafRotateTime = time;
    }

    /**
     * 获取叶子飘完一个周期所花的时间
     */
    public long getLeafFloatTime() {
        mLeafFloatTime = mLeafFloatTime == 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        return mLeafFloatTime;
    }

    /**
     * 获取叶子旋转一周所花的时间
     */
    public long getLeafRotateTime() {
        mLeafRotateTime = mLeafRotateTime == 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        return mLeafRotateTime;
    }

    /**
     * 设置电风扇padding
     *
     * @param mIconPadding
     */
    public void setmIconPadding(int mIconPadding) {
        this.mIconPadding = mIconPadding;
    }

    /**
     * 设置电风扇旋转周期
     *
     * @param mIconRotateTime
     */
    public void setmIconRotateTime(long mIconRotateTime) {
        this.mIconRotateTime = mIconRotateTime;
    }

    /**
     * 获取电风扇padding
     *
     * @return
     */
    public int getmIconPadding() {
        return mIconPadding;
    }

    /**
     * 获取电风扇旋转周期
     *
     * @return
     */
    public long getmIconRotateTime() {
        return mIconRotateTime;
    }
}
