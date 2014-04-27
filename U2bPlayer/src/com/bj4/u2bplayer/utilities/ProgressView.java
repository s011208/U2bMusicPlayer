
package com.bj4.u2bplayer.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ProgressView extends View {
    private int mMeasureHeight = 0, mMeasureWidth = 0;
    private static final int DEFAULT_BACKGROUND = 0x66000000;
    private static final int DEFAULT_MAXPROGRESS = 100;
    private static final int DEFAULT_TEXT_SIZE = 100;
    private int mBackgroundColor = DEFAULT_BACKGROUND;
    private int mProgress = 0;
    private int mMaxProgress = DEFAULT_MAXPROGRESS;
    private Paint mBackgroundPaint = new Paint();
    private RectF mOval = new RectF();
    private float mStartAngle = 0, mSweepAngle = 0;
    private float mRadius = 0;
    private int mDefaultStartAngle = -90;
    private boolean mShowText = false;
    private Paint mTextPaint = new Paint();
    private int mTextSize = DEFAULT_TEXT_SIZE;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(DEFAULT_BACKGROUND);
        mBackgroundPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        setTextColor(Color.WHITE);
        setTextSize(DEFAULT_TEXT_SIZE);
        showText(true);
    }

    public void showText(boolean showText) {
        mShowText = showText;
    }

    public void setTextColor(int Color) {
        mTextPaint.setColor(Color);
    }

    public void setTextSize(int size) {
        mTextSize = size;
        mTextPaint.setTextSize(mTextSize);
    }

    public void setBackground(int color) {
        mBackgroundColor = color;
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public void setDefaultStartAngle(int angle) {
        mDefaultStartAngle = angle;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mStartAngle = ((360f / mMaxProgress) * mProgress) + mDefaultStartAngle;
        mSweepAngle = 360 - ((360f / mMaxProgress) * mProgress);
        postInvalidate();
    }

    public static double distance(double x1, double x2, double y1, double y2) {
        double distance = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return distance;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMeasureWidth == 0 || mMeasureHeight == 0) {
            mMeasureHeight = getMeasuredHeight();
            mMeasureWidth = getMeasuredWidth();
            mRadius = (float) distance(0, mMeasureWidth / 2, 0, mMeasureHeight / 2);
            mOval.left = -(mRadius * 2 - mMeasureWidth);
            mOval.right = Math.max(mRadius * 2, mMeasureWidth);
            mOval.top = -(mRadius * 2 - mMeasureHeight);
            mOval.bottom = Math.max(mRadius * 2, mMeasureHeight);
            return;
        }
        canvas.drawArc(mOval, mStartAngle, mSweepAngle, true, mBackgroundPaint);
        if (mShowText) {
            final String progress = mProgress + "";
            int xPos = (int) ((canvas.getWidth() / 2) - mTextPaint.measureText(progress) / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((mTextPaint.descent() + mTextPaint
                    .ascent()) / 2));
            canvas.drawText(progress, xPos, yPos, mTextPaint);
        }
    }
}
