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
            case MotionEvent.ACTION_MOVE:
            	
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                //if (y > getHeight() / 2) {
                //  dx = dx * -1 ;
                //}

                // reverse direction of rotation to left of the mid-line
                //if (x < getWidth() / 2) {
                //  dy = dy * -1 ;
                //}

                mRenderer.mxAngle += (dy * Math.PI / 10.0f) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                mRenderer.myAngle += (dx * Math.PI / 10.0f) * TOUCH_SCALE_FACTOR;
                
        }
        
        

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
    public void switchView(){
    	
    	mRenderer.view = (mRenderer.view+1) % 2;
    	
    }
    
    public void moveUp(){
    	
    	mRenderer.mY +=.1;
    	
    }
    
    public void moveDown(){
    	
    	mRenderer.mY -=.1;
    	
    }
    
    public void run(){
    		
    }
}
