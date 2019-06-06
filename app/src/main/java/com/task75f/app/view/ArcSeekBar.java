package com.task75f.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.task75f.app.R;

/**
 * ArcSeekBar
 *
 * @author Mahesh
 */
public class ArcSeekBar extends View {

    private float mStartValue = 50;
    private float mEndValue = 100;
    private float mProgress = 50;

    private float mStartAngle = -180;
    private float mEndAngle = 0;

    private boolean mIsShowMark = true;
    private boolean mIsShowProgress = true;

    private Paint mArcPaint;
    private Paint mLinePaint;
    private Paint mTextPaint;

    private float mMinSize;
    private float mArcWidth;
    private float mArcRadius;
    private float mCenter;
    private float mInnerRadius;

    private RectF mArcRectF;

    private float mArcWidthRate = 1;
    private static final int sArcTotalWidth = 240;

    private SweepGradient mSweepGradient;

    private int mStartColor = 0xFF33B5E5;
    private int mEndColor = 0xFFFF0000;

    private int[] mColors = new int[]{mStartColor, mEndColor};

    private float[] mPositions = null;

    private int mMarkColor = 0xff64646f;
    private int mProgressTextColor = 0xff64646f;
    private int mLineColor = 0xffdddddd;

    private int mMarkSize = 30;
    private int mProgressTextSize = 35;

    private OnSeekArcChangeListener mOnSeekArcChangeListener;

    public ArcSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (isInEditMode()) {
            return;
        }

        initAttr(context, attrs);
        initPaint();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderSeekArc);

            mStartValue = typedArray.getFloat(R.styleable.ShaderSeekArc_startValue, mStartValue);
            mEndValue = typedArray.getFloat(R.styleable.ShaderSeekArc_endValue, mEndValue);
            checkValueSet();

            mStartAngle = typedArray.getFloat(R.styleable.ShaderSeekArc_startAngle, mStartAngle);
            mEndAngle = typedArray.getFloat(R.styleable.ShaderSeekArc_endAngle, mEndAngle);
            checkAngleSet();

            mProgress = typedArray.getFloat(R.styleable.ShaderSeekArc_progress, mProgress);
            mProgress = checkProgressSet(mProgress);

            mArcWidthRate = typedArray.getFloat(R.styleable.ShaderSeekArc_arcWidthRate, mArcWidthRate);
            checkArcWidthRate();

            mStartColor = typedArray.getInt(R.styleable.ShaderSeekArc_startColor, mStartColor);
            mEndColor = typedArray.getInt(R.styleable.ShaderSeekArc_endColor, mEndColor);

            mIsShowMark = typedArray.getBoolean(R.styleable.ShaderSeekArc_showMark, mIsShowMark);
            mIsShowProgress = typedArray.getBoolean(R.styleable.ShaderSeekArc_showProgress, mIsShowProgress);

            mMarkSize = typedArray.getInt(R.styleable.ShaderSeekArc_markSize, mMarkSize);
            mMarkColor = typedArray.getInt(R.styleable.ShaderSeekArc_markColor, mMarkColor);

            mProgressTextSize = typedArray.getInt(R.styleable.ShaderSeekArc_progressTextSize, mProgressTextSize);
            mProgressTextColor = typedArray.getInt(R.styleable.ShaderSeekArc_progressTextColor, mProgressTextColor);

            mLineColor = typedArray.getInt(R.styleable.ShaderSeekArc_lineColor, mLineColor);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    private void initPaint() {
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(false);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(false);
        mLinePaint.setColor(mLineColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(0xff64646f);
    }

    private void initSize(int width, int height) {

        mMinSize = Math.min(width, height) / 600f;
        mCenter = 300 * mMinSize;
        mArcWidth = mArcWidthRate * sArcTotalWidth * mMinSize;
        mInnerRadius = (1 - mArcWidthRate) * sArcTotalWidth * mMinSize;
        mArcRadius = mInnerRadius + mArcWidth / 2;

        mArcRectF = new RectF(
                mCenter - mArcRadius,
                mCenter - mArcRadius,
                mCenter + mArcRadius,
                mCenter + mArcRadius);

        if (mPositions != null) {
            mSweepGradient = new SweepGradient(mCenter, mCenter, mColors, mPositions);
        } else {
            mSweepGradient = new SweepGradient(mCenter, mCenter, mColors, null);
        }

        Matrix matrix = new Matrix();
        matrix.preRotate(mStartAngle, mCenter, mCenter);
        mSweepGradient.setLocalMatrix(matrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int height = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = 600;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = 600;
        }

        initSize(width, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawArc(canvas);
        drawLine(canvas);
    }

    private void drawArc(Canvas canvas) {
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setShader(mSweepGradient);
        canvas.drawArc(
                mArcRectF,
                mStartAngle,
                getSweepAngle(mStartValue, mProgress),
                false,
                mArcPaint);

        mArcPaint.setShader(null);
        mArcPaint.setColor(Color.WHITE);
        canvas.drawArc(
                mArcRectF,
                mStartAngle + getSweepAngle(mStartValue, mProgress),
                getSweepAngle(mProgress, mEndValue),
                false, mArcPaint);
    }

    private float getSweepAngle(float from, float to) {
        return (to - from) / (mEndValue - mStartValue) * (mEndAngle - mStartAngle);
    }

    private void drawLine(Canvas canvas) {
        mLinePaint.setStrokeWidth(mMinSize * 3f);
        float rotate = mStartAngle + 90;
        canvas.rotate(rotate, mCenter, mCenter);

        mTextPaint.setTextSize(mMinSize * mMarkSize);
        mTextPaint.setColor(mMarkColor);

        float count = (mEndAngle - mStartAngle) / 3;

        for (int i = 0; i <= count; i++) {
            float top = mCenter - mInnerRadius - mArcWidth;
            if (i % 15 == 0) {
                top -= 20 * mMinSize;

                if (mIsShowMark) {
                    canvas.drawText(
                            (int) (Angle2Progress(i * 3 + mStartAngle)) + "",
                            mCenter - 15 * mMinSize,
                            top - 10 * mMinSize,
                            mTextPaint);
                }
            }
            canvas.drawLine(mCenter, mCenter - mInnerRadius + 0.5f,
                            mCenter, top,
                            mLinePaint);
            canvas.rotate(3, mCenter, mCenter);
            rotate += 3;
        }
        canvas.rotate(-rotate, mCenter, mCenter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                break;
            case MotionEvent.ACTION_MOVE:

                float x = event.getX();
                float y = event.getY();

                if (checkTouch(x, y)) {

                    double angle = Math.toDegrees(Math.atan2((y - mCenter), (x - mCenter)));

                    if (!checkAngle(angle)) {

                        return false;
                    } else {
                        float progress = Angle2Progress(angle);
                        setProgress(progress);
                        return true;
                    }
                } else {
                    return false;
                }

            case MotionEvent.ACTION_UP:
                onStopTrackingTouch();
                setPressed(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                setPressed(false);
                break;
        }

        return true;
    }

    private boolean checkTouch(float x, float y) {

        double distance = Math.sqrt((x - mCenter) * (x - mCenter)
                                    + (y - mCenter) * (y - mCenter));

        return distance >= mInnerRadius && distance <= (mInnerRadius + mArcRadius);
    }

    private boolean checkAngle(double angle) {
        if (mStartAngle <= -180) {
            return angle >= (mStartAngle + 360) || angle <= mEndAngle;
        } else {
            return angle >= mStartAngle & angle <= mEndAngle;
        }
    }

    public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
        mOnSeekArcChangeListener = l;
    }

    private void onStartTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStopTrackingTouch(this);
        }
    }

    private float Angle2Progress(double angle) {
        float progress;

        if (mStartAngle <= -180 && angle >= (mStartAngle + 360)) {
            progress = (float) (mStartValue + (angle - (mStartAngle + 360)) / (mEndAngle - mStartAngle) * (mEndValue - mStartValue));
        } else {
            progress = (float) (mEndValue - (mEndAngle - angle) / (mEndAngle - mStartAngle) * (mEndValue - mStartValue));
        }
        return progress;
    }

    public float getProgress() {
        checkProgressSet(mProgress);
        return mProgress;
    }

    public void setProgress(float progress) {
        checkProgressSet(progress);
        mProgress = progress;
        invalidate();
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onProgressChanged(this, progress);
        }
    }

    private float checkProgressSet(float progress) {
        if (progress >= mStartValue && progress <= mEndValue) {
            return progress;
        } else {
            throw new IllegalArgumentException("Progress is out of range!");
        }
    }

    private void checkValueSet() {
        if (mEndValue <= mStartValue) {
            throw new IllegalArgumentException("End value should large than the start value!");
        }
    }

    private void checkAngleSet() {
        if (mEndAngle <= mStartAngle) {
            throw new IllegalArgumentException("End angle should large than the start angle!");
        } else if (mEndAngle - mStartAngle > 360) {
            throw new IllegalArgumentException("Arc angle shall not exceed 360!");
        }
    }

    private void checkArcWidthRate() {
        mArcWidthRate = Math.min(mArcWidthRate, 0.7f);
        mArcWidthRate = Math.max(0.3f, mArcWidthRate);
    }

    public interface OnSeekArcChangeListener {

        void onProgressChanged(ArcSeekBar seekArc, float progress);

        void onStartTrackingTouch(ArcSeekBar seekArc);

        void onStopTrackingTouch(ArcSeekBar seekArc);
    }
}
