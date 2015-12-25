package com.example.zhoushengming.demo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zhoushengming on 15/11/26.
 */
public class PercentView extends View {


    private int mMeasureHeigth;
    private int mMeasureWidth;

    private Paint mCirclePaint;
    private float mCircleXY;
    private float mRadius;

    private Paint mArcPaint;
    private RectF mArcRectF;
    private float mOldSweepAngle;
    private float mSweepAngle;
    private float mOldSweepValue = 0;
    private float mSweepValue = 66;

    private Paint mTextPaint;
    private String mShowText = "";
    private float mShowTextSize = 50;
    private String TAG = "MyCircleView";
    /**
     * 圆弧的颜色
     */
    private int arcColor = getResources().getColor(
            android.R.color.holo_blue_bright);

    /**
     * 中心圆的颜色
     */
    private int circleColor = getResources().getColor(
            android.R.color.holo_blue_bright);

    /**
     *  每次增长的偏移量
     */
    private float offset = 4;
    /**
     * 帧数
     */
    private int frame=50;

    public PercentView(Context context) {
        super(context);
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        mMeasureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mMeasureWidth, mMeasureHeigth);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initView();
//        Log.d(TAG, String.format("onDraw: mSweepAngle:%f,mSweepValue:%f, mOldSweepAngle:%f, mOldSweepValue:%f,mShowText:%s,mShowTextSize:%f",
//                mSweepAngle, mSweepValue, mOldSweepAngle, mOldSweepValue, mShowText, mShowTextSize));

        // 绘制圆
        canvas.drawCircle(mCircleXY, mCircleXY, mRadius, mCirclePaint);
        // 绘制弧线
        canvas.drawArc(mArcRectF, 270, mOldSweepAngle, false, mArcPaint);
        // 让圆弧颜色变浅变浅的
        mArcPaint.setColor(Color.argb(100, Color.red(arcColor), Color.green(arcColor), Color.blue(arcColor)));
        // 让圆弧变细
        //mArcPaint.setStrokeWidth(((float) mMeasureHeigth >= mMeasureWidth ? mMeasureWidth : mMeasureHeigth) * 0.01f);
        // 绘制最外边的圆环
        canvas.drawArc(mArcRectF, 270, 360, false, mArcPaint);
        // 绘制文字
        String text = String.format("%s%.2f%%", mShowText, mOldSweepValue);
        canvas.drawText(text, 0, text.length(),
                mCircleXY, mCircleXY + (mShowTextSize / 4), mTextPaint);
        if (mOldSweepAngle != mSweepAngle) {
            postInvalidateDelayed(1000/frame);
        }
    }

    private void initView() {
//        Log.d(TAG, "initView: ");
        if (mSweepValue > mOldSweepValue) {
            if (mSweepValue - mOldSweepValue > offset) {
                mOldSweepValue+= offset;
            } else {
                mOldSweepValue = mSweepValue;
            }
        } else if (mSweepValue < mOldSweepValue) {
            if (mSweepValue - mOldSweepValue < offset) {
                mOldSweepValue-= offset;
            } else {
                mOldSweepValue = mSweepValue;
            }
        }

        float length = 0;
        if (mMeasureHeigth >= mMeasureWidth) {
            length = mMeasureWidth;
        } else {
            length = mMeasureHeigth;
        }

        mCircleXY = length / 2;
        mRadius = (float) (length * 0.5 / 2);
        if (mCirclePaint == null) {
            mCirclePaint = new Paint();
        }
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(circleColor);

        if (mArcRectF == null) {
            mArcRectF = new RectF(
                    (float) (length * 0.1),
                    (float) (length * 0.1),
                    (float) (length * 0.9),
                    (float) (length * 0.9));
        }
        mOldSweepAngle = (mOldSweepValue / 100f) * 360f;
        mSweepAngle = (mSweepValue / 100f) * 360f;
        if (mArcPaint == null) {
            mArcPaint = new Paint();
        }
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(arcColor);
        mArcPaint.setStrokeWidth((float) (length * 0.1));
        mArcPaint.setStyle(Paint.Style.STROKE);
        if (mTextPaint == null) {
            mTextPaint = new Paint();
        }
        mTextPaint.setTextSize(mShowTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public PercentView setTextSize(float textSize) {
        if (textSize >= 0) {
            mShowTextSize = textSize;
        }
        this.invalidate();
        return this;
    }


    public float getTextSize() {
        return mShowTextSize;
    }

    public PercentView setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mShowText = text;
        }
        this.invalidate();
        return this;
    }

    public String getText() {
        return mShowText;
    }

    /**
     * 重新初始化控件
     */
    public PercentView forceInvalidate() {
        this.invalidate();
        return this;

    }

    public float getSweepValue() {
        Log.d(TAG, "getSweepValue: " + mSweepValue);
        return mSweepValue;
    }

    /**
     * 设置百分比值 0
     *
     * @param sweepValue 0~100
     */
    public PercentView setSweepValue(float sweepValue) {
        Log.d(TAG, "setSweepValue: " + sweepValue);
        mOldSweepValue = mSweepValue;
        mSweepValue = sweepValue;
        this.invalidate();
        return this;
    }

    public int getArcColor() {
        return arcColor;
    }

    /** 设置圆弧的颜色
     * @param arcColor
     * @return
     */
    public PercentView setArcColor(int arcColor) {
        this.arcColor = arcColor;
        this.invalidate();
        return this;
    }

    public int getCircleColor() {
        return circleColor;
    }

    /** 中心圆的颜色
     * @param circleColor
     * @return
     */
    public PercentView setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        this.invalidate();
        return this;
    }

    public float getOffset() {
        return offset;
    }

    /** 设置每帧动画的偏移量
     * @param offset
     * @return
     */
    public PercentView setOffset(float offset) {
        this.offset = offset;
        this.invalidate();
        return this;
    }


    /**
     * @return 动画帧数
     */
    public int getFrame() {
        return frame;
    }

    /** 设置动画帧数
     * @param frame
     * @return
     */
    public PercentView setFrame(int frame) {
        this.frame = frame;
        this.invalidate();
        return this;
    }
}
