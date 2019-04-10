package com.dmytrosheiko.charts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Graph1 extends View {

    private Paint mPaint = new Paint();
    private Paint mLinesPaint = new Paint();
    private Paint mY0Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mThumbBackgroundPaint = new Paint();
    private Paint mThumbWrapperPaint = new Paint();
    private JSONObject mJson;
    private JSONArray mColumns;
    private JSONArray x;
    private JSONArray y0;
    private SimpleDateFormat mDateFormat;
    private static final int X_LABELS_COUNT = 7;
    private int mMarginLeft;
    private int mMarginRight;
    private int mXMarginTop;
    private int xLength;
    private Path mY0path;
    private Path mY0pathAll;
    private float mMinValue;
    private float mMaxValue;
    private float mWidth;
    private float mHeight;
    private float mGraphBottomMargin;
    private float mLineBottomMargin;
    private int mThumbHeight;
    private int mThumbMarginBottom;
    private int mThumbWidth;
    private NinePatchDrawable npd;
    private RectF mThumbRect;
    private Rect npdBounds;
    private int mMoveThumb = 0;
    private int mTouchOffset;

    private void init() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mWidth = metrics.widthPixels;
        mHeight = getResources().getDimension(R.dimen.graph_height);

        mGraphBottomMargin = Util.dpToPx(120, getContext());
        mLineBottomMargin = Util.dpToPx(116, getContext());
        mMarginLeft = Util.dpToPx(16, getContext());
        mMarginRight = Util.dpToPx(7, getContext());
        mXMarginTop = Util.dpToPx(88, getContext());
        mThumbHeight = Util.dpToPx(42, getContext());
        mThumbMarginBottom = Util.dpToPx(16, getContext());
        mTouchOffset = Util.dpToPx(24, getContext());
        mThumbWidth = (int) (mWidth - Util.dpToPx(32, getContext()) + 2);

        mThumbBackgroundPaint.setColor(Color.parseColor("#EDF5FB"));
        mThumbWrapperPaint.setColor(Color.parseColor("#FFFFFF"));

        mLinesPaint.setColor(Color.parseColor("#E5E7E9"));
        mLinesPaint.setStrokeWidth(Util.dpToPx(1, getContext()));

        mPaint.setColor(Color.parseColor("#828288"));
        mPaint.setTextSize(Util.spToPx(14, getContext()));

        mY0Paint.setStyle(Paint.Style.STROKE);
        mY0Paint.setStrokeWidth(Util.dpToPx(2, getContext()));
        mY0Paint.setColor(Color.parseColor("#F34C44"));

        mDateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
        try {
            mJson = new JSONObject(Util.loadJSONFromAsset(getContext(), "contest/1/overview.json"));
            //Util.Log(mJson.getJSONArray("columns").getJSONArray(0).toString());
            mColumns = mJson.getJSONArray("columns");
            x = mColumns.getJSONArray(0);
            y0 = mColumns.getJSONArray(1);
            x.remove(0);
            y0.remove(0);
            xLength = x.length();

            mY0path = new Path();
            mY0pathAll = new Path();
            mMinValue = -1;
            mMaxValue = -1;
            for (int i = 0; i < y0.length(); i++) {
                if (mMinValue == -1 || y0.getInt(i) < mMinValue) {
                    mMinValue = y0.getInt(i);
                }
                if (mMaxValue == -1 || y0.getInt(i) > mMinValue) {
                    mMaxValue = y0.getInt(i);
                }
            }
            int num = 0;
            int mGraphHeight = (int) (mHeight - mGraphBottomMargin);
            mY0path.moveTo(0, mHeight - ((y0.getInt(0) - mMinValue) * mGraphHeight / mMaxValue) - mGraphBottomMargin);
            mY0pathAll.moveTo(mMarginLeft + 2, mHeight - ((y0.getInt(0) - mMinValue) * mThumbHeight / mMaxValue) - mThumbMarginBottom);
            for (int i = 1; i <= y0.length(); i++) {
                mY0path.lineTo(i * mWidth / y0.length(), mHeight - ((y0.getInt(i) - mMinValue) * mGraphHeight / mMaxValue) - mGraphBottomMargin);
                mY0pathAll.lineTo(i * (mWidth - mMarginLeft * 2) / y0.length() + mMarginLeft, mHeight - ((y0.getInt(i) - mMinValue) * (mThumbHeight - 40) / mMaxValue) - mThumbMarginBottom - 15);
                num = num + 2;
            }
        } catch (JSONException e) {
            Util.Log("Error: " + e);
        }

        npd = (NinePatchDrawable) getResources().getDrawable(R.drawable.thumb);
        npdBounds = new Rect(mMarginLeft - 2 + mResizeLeftOffset, (int) mHeight - mThumbMarginBottom - mThumbHeight,
                mMarginLeft + mThumbWidth - mResizeRightOffset, (int) mHeight - mThumbMarginBottom);
        npd.setAlpha(200);

        mThumbRect = new RectF(mMarginLeft, mHeight - mThumbMarginBottom - mThumbHeight,
                mWidth - mMarginLeft, mHeight - mThumbMarginBottom);
    }

    public Graph1(Context context) {
        super(context);
        init();
    }

    public Graph1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, mHeight - mLineBottomMargin, getWidth(), mHeight - mLineBottomMargin, mLinesPaint);

        try {
            // Draw X
            for (int i = 0; i < X_LABELS_COUNT; i++) {
                int pos = i * xLength / (X_LABELS_COUNT - 1);
                if (pos >= xLength) {
                    pos--;
                }
                canvas.drawText(mDateFormat.format(x.getLong(pos)),
                        i * (getWidth() - mMarginRight) / X_LABELS_COUNT + mMarginLeft,
                        getHeight() - mXMarginTop, mPaint);
            }
        } catch (JSONException e) {
            Util.Log("Json error: " + e);
        }

        // Draw Y1
        canvas.drawPath(mY0path, mY0Paint);

        canvas.drawRoundRect(mThumbRect, 10, 10, mThumbBackgroundPaint);

        canvas.save();
        canvas.translate(mMoveThumb, 0);
        canvas.drawRect(new RectF(mMarginLeft + 20 + mResizeLeftOffset, (int) mHeight - mThumbMarginBottom - mThumbHeight,
                mMarginLeft + mThumbWidth - 20 + mResizeRightOffset, (int) mHeight - mThumbMarginBottom), mThumbWrapperPaint);


        canvas.restore();

        // Draw Y1 All
        canvas.drawPath(mY0pathAll, mY0Paint);

        canvas.save();
        canvas.translate(mMoveThumb, 0);

        // Draw thumb

        npd.setBounds(npdBounds);
        npd.draw(canvas);
    }

    int mDownX = 0;
    int mDownY = 0;
    private boolean mDragThumb = false;
    private boolean mResizeLeftThumb = false;
    private boolean mResizeRightThumb = false;
    private int mResizeLeftOffset = 0;
    private int mResizeRightOffset = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownY = (int) event.getY();

            if (Rect.intersects(new Rect(npdBounds.left + mMoveThumb, npdBounds.top, npdBounds.left + mMoveThumb, npdBounds.bottom), new Rect((int) event.getX() - mTouchOffset, mDownY - mTouchOffset, (int) event.getX() + mTouchOffset, mDownY + mTouchOffset))) {
                mResizeLeftThumb = true;
                mDownX = (int) event.getX() - mResizeLeftOffset;
            } else if (Rect.intersects(new Rect(npdBounds.right + mMoveThumb, npdBounds.top, npdBounds.right + mMoveThumb, npdBounds.bottom), new Rect((int) event.getX() - mTouchOffset, mDownY - mTouchOffset, (int) event.getX() + mTouchOffset, mDownY + mTouchOffset))) {
                mResizeRightThumb = true;
                mDownX = (int) event.getX() - mResizeRightOffset;
            } else if (Rect.intersects(npdBounds, new Rect((int) event.getX() - mMoveThumb, mDownY - mTouchOffset, (int) event.getX() - mMoveThumb, mDownY + mTouchOffset))) {
                mDragThumb = true;
                mDownX = (int) event.getX() - mMoveThumb;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            int delta = x - mDownX;
            if (mDragThumb) {

                mMoveThumb = delta;
                if (npdBounds.left + mMoveThumb < mMarginLeft) {
                    mMoveThumb = -mResizeLeftOffset;
                }
            } else if (mResizeLeftThumb) {
                mResizeLeftOffset = delta;
                Util.Log("npdBounds left = " + (npdBounds.left));
                Util.Log("should be at least = " + mMarginLeft);
            } else if (mResizeRightThumb) {
                Util.Log("resize right");
                mResizeRightOffset = delta;
                /*if (mResizeRightOffset < 0) {
                    mResizeRightOffset = 0;
                }*/
            }

            if (mDragThumb || mResizeLeftThumb || mResizeRightThumb) {
                npdBounds = new Rect(mMarginLeft - 2 + mResizeLeftOffset, (int) mHeight - mThumbMarginBottom - mThumbHeight,
                        mMarginLeft + mThumbWidth - mResizeRightOffset, (int) mHeight - mThumbMarginBottom);
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mDragThumb = false;
            mResizeLeftThumb = false;
            mResizeRightThumb = false;
        }
        return true;
    }
}