package com.dmytrosheiko.charts;/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
    private GLSurfaceView mGLView1;
    private GLSurfaceView mGLView2;
    private Chart1Renderer mRenderer1;
    private Chart2Renderer mRenderer2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGraph1();
        initGraph2();
    }

    private void initGraph1() {
        mGLView1 = findViewById(R.id.graph1);
        // Create an OpenGL ES 2.0 context.
        mGLView1.setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer1 = new Chart1Renderer();
        mGLView1.setRenderer(mRenderer1);
        // Render the view only when there is a change in the drawing data
        mGLView1.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void initGraph2() {
        mGLView2 = findViewById(R.id.graph2);
        // Create an OpenGL ES 2.0 context.
        mGLView2.setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer2 = new Chart2Renderer();
        mGLView2.setRenderer(mRenderer2);
        // Render the view only when there is a change in the drawing data
        mGLView2.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        if (mGLView1 != null) {
            mGLView1.onPause();
        }
        if (mGLView2 != null) {
            mGLView2.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        if (mGLView1 != null) {
            mGLView1.onResume();
        }
        if (mGLView2 != null) {
            mGLView2.onResume();
        }
    }
}