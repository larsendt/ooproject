/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.larsendt.ObjectOriented;

import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.larsendt.ObjectOriented.R;


public class WorldRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = "OO";
    
    
    private Shader meshShader;
    private Light lightball;
    private DataFetcher m_dataFetcher;
    private Camera m_camera;

    private int texture1;
    private int texture2;
    private int texture3;
    private int texture4;

    private float m_xpos;
    private float m_zpos;
    private float m_y = 2.0f;

    public volatile int view;
    
    private World w;
    
    float pos;


    private Context mContext;

    public void setContext(Context context){
        mContext = context;
    }

    public void moveUp() {
        m_y += 0.3;
        m_camera.setHeight(m_y);
    }

    public void moveDown() {
        m_y -= 0.3;
        m_camera.setHeight(m_y);
    }

    public void drag(float dx, float dy) {
        if(view == 1) {
            m_xpos += dx;
            m_zpos += dy;
            m_camera.move(dx, dy);
        }
        else {
            m_camera.rotate(dx, dy);
        }
    }

    public void nextView() {
        view = (view+1) % 2;
    }

    public void initGL(){
    	
    	GLES20.glClearColor(0.3f, 0.5f, 0.7f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        texture1 = loadBitmap("rock.bmp");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        texture2 = loadBitmap("grass.bmp");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        texture3 = loadBitmap("sand.bmp");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        texture4 = loadBitmap("snow.bmp");
    }
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        view = 1;

        meshShader = Shader.loadShaderFromResource(R.raw.terrain_vs, R.raw.terrain_fs, mContext);

        lightball = new Light(mContext);

        m_dataFetcher = DisplayActivity.m_dataFetcher;

        m_camera = new Camera();
        m_camera.setHeight(m_y);
        
        Matrix.setIdentityM(GLState.pMatrix, 0);
        Matrix.perspectiveM(GLState.pMatrix, 0, 60.0f, 1.0f, 1f,100.0f);

        initGL();

        w = new World(m_dataFetcher, meshShader);
        w.loadAround(0, 0);
        
        pos = 0;
    }

    public void onDrawFrame(GL10 unused) {
    	
    	w.loadAround((int)Math.floor(m_camera.m_x), (int)Math.floor(m_camera.m_y));
    	w.update();
    	
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        m_camera.doTransformation();
        lightball.update();
        lightball.draw();
        meshShader.useProgram();

        float[] lightPos = lightball.getMVPos();
        meshShader.setUniform3f("lightPos", lightPos[0], lightPos[1], lightPos[2]);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture1);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture3);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture4);
        
        meshShader.setUniform1i("tex1", 0);
        meshShader.setUniform1i("tex2", 1);
        meshShader.setUniform1i("tex3", 2);
        meshShader.setUniform1i("tex4", 3);
        
        w.draw();

        GLState.popMVMatrix();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.setIdentityM(GLState.pMatrix, 0);
        Matrix.perspectiveM(GLState.pMatrix, 0, 60.0f, ratio, .1f,100.0f);
    }

    public int loadBitmap(String resourceName){
    	IntBuffer ib = IntBuffer.allocate(1);
        GLES20.glGenTextures(1, ib);
        
        checkGlError("gen texture");

        int textureHandle = ib.get();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        checkGlError("bind texture");

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap myBitmap;
        try {
            myBitmap = BitmapFactory.decodeStream( mContext.getResources().getAssets().open(resourceName));
            if (myBitmap == null){
                Log.d(TAG, "Bitmap wut");
            }
        } catch (IOException e) {
            // farts
            e.printStackTrace();
            myBitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);

            myBitmap.setPixel(0, 0, Color.WHITE);
            myBitmap.setPixel(0, 1, Color.CYAN);
            myBitmap.setPixel(1, 0, Color.CYAN);
            myBitmap.setPixel(1, 1, Color.WHITE);
        }
        
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, myBitmap, 0);

        checkGlError("TexImage");
        
        return textureHandle;
    }
    
    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + GLU.gluErrorString(error));
            //throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}
