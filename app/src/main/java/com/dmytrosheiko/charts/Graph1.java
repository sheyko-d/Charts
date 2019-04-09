package com.dmytrosheiko.charts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Graph1 extends View {

    private Paint mPaint = new Paint();
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
    private float mMinValue;
    private float mMaxValue;
    private float[] mY0Lines;
    private float mGraphHeight;

    private void init() {
        mGraphHeight = getResources().getDimension(R.dimen.graph_height);

        mPaint.setColor(Color.parseColor("#7D7D83"));
        mPaint.setTextSize(Util.spToPx(14, getContext()));
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
            mY0path.moveTo(0, y0.getLong(0));
            mMinValue = -1;
            mMaxValue = -1;
            for (int i = 0; i < y0.length(); i++) {
                if (mMinValue == -1 || y0.getLong(i) < mMinValue) {
                    mMinValue = y0.getLong(i);
                }
                if (mMaxValue == -1 || y0.getLong(i) > mMinValue) {
                    mMaxValue = y0.getLong(i);
                }
            }
            mY0Lines = new float[y0.length() * 2];
            int num = 0;
            for (int i = 1; i <= y0.length(); i++) {
                //mY0path.lineTo(i * mScreenWidth / y0.length(), 10);
                mY0Lines[num] = /*(float) (i * getWidth() / y0.length())*/i * 10.7f;
                mY0Lines[num + 1] =  y0.getLong(i) - mMinValue;
                num = num + 2;
            }
        } catch (JSONException e) {
            Util.Log("Error: " + e);
        }

        mMarginLeft = Util.dpToPx(16, getContext());
        mMarginRight = Util.dpToPx(7, getContext());
        mXMarginTop = Util.dpToPx(48, getContext());
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
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);
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

            // Draw Y1
            canvas.drawLines(mY0Lines, mPaint);
        } catch (JSONException e) {
            Util.Log("Json error: " + e);
        }
    }

}