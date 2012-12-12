package com.example.android.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public Context myContext;
    
    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
        mRenderer.setContext(context);
        
        

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
    }

    private final float TOUCH_SCALE_FACTOR = 1/400.0f;
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
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRenderer.drag(-dx*TOUCH_SCALE_FACTOR, -dy*TOUCH_SCALE_FACTOR);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
    public void switchView(){
        mRenderer.nextView();
    }
    
    public void moveUp(){
    	mRenderer.moveUp();
    }
    
    public void moveDown(){
    	mRenderer.moveDown();
    }
    
    public void run(){
    		
    }
}
