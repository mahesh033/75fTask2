package com.task75f.app.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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

    //
    private static final int MAX = 180;
    private final float DENSITY = getContext().getResources().getDisplayMetrics().density;

    /**
     * Private variables
     */
    //Rectangle for the arc
    private RectF mArcRect = new RectF();

    //Paints required for drawing
    private Paint mArcPaint;
    private Paint mArcBackgroundPaint;
    private Paint mArcProgressPaint;
    private Paint mTickPaint;
    private Paint mTickProgressPaint;

    //Arc related dimens
    private int mArcRadius = 0;
    private int mArcWidth = 4;
    private int mArcProgressWidth = 4;
    private boolean mRoundedEdges = true;

    //Thumb Drawable
    private Drawable mThumb;

    //Thumb position related coordinates
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mAngleTextSize = 12;

    private int mTickOffset = -20;
    private int mTickLength = 10;
    private int mTickWidth = 4;
    private int mTickProgressWidth = 2;
    private int mAngle = 0;
    private boolean mTouchInside = true;
    private boolean mEnabled = true;
    private int mTickIntervals = 10;
    private double mTouchAngle = 0;
    private float mTouchIgnoreRadius;

    //Event listener
    private OnProtractorViewChangeListener mOnProtractorViewChangeListener = null;


    //Interface for event listener
    public interface OnProtractorViewChangeListener {

        void onProgressChanged(ArcSeekBar protractorView, int progress, boolean fromUser);

        void onStartTrackingTouch(ArcSeekBar protractorView);

        void onStopTrackingTouch(ArcSeekBar protractorView);
    }

    public ArcSeekBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.protractorViewStyle);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * init default attr form xml
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {

        final Resources res = getResources();

        int arcColor = res.getColor(R.color.default_white);
        int backgroundColor = res.getColor(R.color.default_blue);
        int arcProgressColor = res.getColor(R.color.default_white);
        int tickColor = res.getColor(R.color.default_white);
        int tickProgressColor = res.getColor(R.color.default_white);
        int thumbHalfHeight = 0;
        int thumbHalfWidth = 0;

        mThumb = res.getDrawable(R.drawable.thumb_selector);

        //Convert all default dimens to pixels for current density
        mArcWidth = (int) (mArcWidth * DENSITY);
        mArcProgressWidth = (int) (mArcProgressWidth * DENSITY);
        mAngleTextSize = (int) (mAngleTextSize * DENSITY);
        mTickOffset = (int) (mTickOffset * DENSITY);
        mTickLength = (int) (mTickLength * DENSITY);
        mTickWidth = (int) (mTickWidth * DENSITY);
        mTickProgressWidth = (int) (mTickProgressWidth * DENSITY);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProtractorView, defStyle, 0);
            Drawable thumb = array.getDrawable(R.styleable.ProtractorView_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }
            thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight);

            //Dimensions
            mArcProgressWidth = (int) array.getDimension(R.styleable.ProtractorView_progressWidth, mArcProgressWidth);
            mTickOffset = (int) array.getDimension(R.styleable.ProtractorView_tickOffset, mTickOffset);
            mTickLength = (int) array.getDimension(R.styleable.ProtractorView_tickLength, mTickLength);
            mArcWidth = (int) array.getDimension(R.styleable.ProtractorView_arcWidth, mArcWidth);

            //Integers
            mAngle = array.getInteger(R.styleable.ProtractorView_angle, mAngle);
            mTickIntervals = array.getInt(R.styleable.ProtractorView_tickIntervals, mTickIntervals);

            //Colors
            arcColor = array.getColor(R.styleable.ProtractorView_arcColor, arcColor);
            backgroundColor = array.getColor(R.styleable.ProtractorView_arcColor, backgroundColor);
            arcProgressColor = array.getColor(R.styleable.ProtractorView_arcProgressColor, arcProgressColor);
            tickColor = array.getColor(R.styleable.ProtractorView_tickColor, tickColor);
            tickProgressColor = array.getColor(R.styleable.ProtractorView_tickProgressColor, tickProgressColor);

            //Boolean
            mRoundedEdges = array.getBoolean(R.styleable.ProtractorView_roundEdges, mRoundedEdges);
            mEnabled = array.getBoolean(R.styleable.ProtractorView_enabled, mEnabled);
            mTouchInside = array.getBoolean(R.styleable.ProtractorView_touchInside, mTouchInside);

        }

        //Creating and configuring the paints as  required.
        mAngle = (mAngle > MAX) ? MAX : ((mAngle < 0) ? 0 : mAngle);

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(false);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mArcBackgroundPaint = new Paint();
        mArcBackgroundPaint.setColor(backgroundColor);
        mArcBackgroundPaint.setStyle(Paint.Style.FILL);

        mArcProgressPaint = new Paint();
        mArcProgressPaint.setColor(arcProgressColor);
        mArcProgressPaint.setAntiAlias(false);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeWidth(mArcProgressWidth);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mTickPaint = new Paint();
        mTickPaint.setColor(tickColor);
        mTickPaint.setAntiAlias(false);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mTickWidth);

        mTickProgressPaint = new Paint();
        mTickProgressPaint.setColor(tickProgressColor);
        mTickProgressPaint.setAntiAlias(false);
        mTickProgressPaint.setStyle(Paint.Style.STROKE);
        mTickProgressPaint.setStrokeWidth(mTickProgressWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(),
                                    heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(),
                                   widthMeasureSpec);
        int min = Math.min(width, height);
        //width = min;
        height = min / 2;

        float top;
        float left;
        int arcDiameter;

        int tickEndToArc = (mTickLength);

        arcDiameter = min - 2 * tickEndToArc;
        arcDiameter = (int) (arcDiameter - 2 * 20 * DENSITY);
        mArcRadius = arcDiameter / 2;

        top = height - (mArcRadius);
        left = width / 2 - mArcRadius;

        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mTranslateX = (int) mArcRect.centerX();
        mTranslateY = (int) mArcRect.centerY();

        int thumbAngle = mAngle;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
        setTouchInside(mTouchInside);
        setMeasuredDimension(width, height + tickEndToArc);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(1, -1, mArcRect.centerX(), mArcRect.centerY());
        canvas.drawArc(mArcRect, 0, MAX, false, mArcPaint);
        canvas.drawArc(mArcRect, 0, mAngle, false, mArcProgressPaint);
        canvas.drawArc(mArcRect, 0, MAX, false, mArcBackgroundPaint);

        canvas.restore();
        double slope, startTickX, startTickY, endTickX, endTickY, thetaInRadians;
        double radiusOffset = mArcRadius + mTickOffset;

        // to draw ticks
        for (int i = 360; i >= 180; i -= mTickIntervals) {
            canvas.save();

            //for tick
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mArcRect.centerX(), mArcRect.centerY());
            canvas.rotate(180);
            thetaInRadians = Math.toRadians(360 - i);
            slope = Math.tan(thetaInRadians);
            startTickX = (radiusOffset * Math.cos(thetaInRadians));
            startTickY = slope * startTickX;
            endTickX = startTickX + ((mTickLength) * Math.cos(thetaInRadians));
            endTickY = slope * endTickX;
            canvas.drawLine((float) startTickX, (float) startTickY, (float) endTickX, (float) endTickY, (mAngle <= 180 - i) ? mTickPaint : mTickProgressPaint);
            canvas.restore();
        }

        if (mEnabled) {
            // Draw the thumb nail
            canvas.save();
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            canvas.restore();
        }
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (ignoreTouch(event.getX(), event.getY())) {
                        return false;
                    }
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void onStartTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStopTrackingTouch(this);
        }
    }


    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius || touchRadius > (mArcRadius + mTickLength)) {
            ignore = true;
        }
        return ignore;
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        if (ignoreTouch || (int) mTouchAngle <= 49 || (int) mTouchAngle >= 101) {
            return;
        }
        setPressed(true);
        onProgressRefresh((int) mTouchAngle, true);
    }


    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        x = -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI));
        if (angle > 270) {
            angle = 0;
        } else if (angle > 180) {
            angle = 180;
        }
        return angle;
    }

    private void onProgressRefresh(int angle, boolean fromUser) {
        updateAngle(angle, fromUser);
    }

    private void updateAngle(int angle, boolean fromUser) {
        mAngle = (angle > MAX) ? MAX : (angle < 0) ? 0 : angle;

        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onProgressChanged(this, mAngle, fromUser);
        }
        updateThumbPosition();
        invalidate();
    }


    private void updateThumbPosition() {
        int thumbAngle = mAngle; //(int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    public boolean getTouchInside() {
        return mTouchInside;
    }

    public void setTouchInside(boolean isEnabled) {
        int thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = (float) (mArcRadius / 1.5);
        } else {
            mTouchIgnoreRadius = mArcRadius - Math.min(thumbHalfWidth, thumbHalfHeight);
        }
    }

    public void setOnProtractorViewChangeListener(OnProtractorViewChangeListener l) {
        mOnProtractorViewChangeListener = l;
    }

    public OnProtractorViewChangeListener getOnProtractorViewChangeListener() {
        return mOnProtractorViewChangeListener;
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        this.mAngle = angle;
        onProgressRefresh(mAngle, false);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        invalidate();
    }

    public void setArcBackgroundPaintColor(@ColorInt int color, int progress) {
        mArcBackgroundPaint.setColor(color);
        int r = 0;
        int g = 0;
        int b = 0;
        if (progress == 100) {
            r = 255;
            g = 0;
            b = 0;
        } else if (progress == 50) {
            r = 66;
            g = 134;
            b = 244;
        } else if (progress > 50) {
            r = (255 * progress) / 100;
            g = 0;
            b = (255 - 4 * (progress / 2));
        }
        mArcBackgroundPaint.setARGB(255, r, g, b);

        invalidate();
    }
}
