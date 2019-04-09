package com.dmytrosheiko.charts;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class Chart2SurfaceView extends GLSurfaceView {

    private Chart2Renderer mRenderer;

    public Chart2SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Chart2SurfaceView(Context context) {
        super(context);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        mRenderer = (Chart2Renderer) renderer;
    }
}