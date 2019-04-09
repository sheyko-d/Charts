package com.dmytrosheiko.charts;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.io.IOException;
import java.io.InputStream;

public class Util {

    public static void Log(Object text) {
        int maxLogSize = 2000;
        for (int i = 0; i <= (text+"").length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > (text+"").length() ? (text+"").length() : end;


            Log.d("Charts", (text+"").substring(start, end));
        }
    }

    public static String loadJSONFromAsset(Context context, String path) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
