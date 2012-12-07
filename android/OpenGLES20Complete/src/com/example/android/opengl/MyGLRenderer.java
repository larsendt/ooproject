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

package com.example.android.opengl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.android.opengl.DataFetcher.TaskStatus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.util.Log;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = "OO";
    private Shader meshShader;
    private Shader plainShader;
    private VBO lightball;
    private boolean m_hasChunk = false;
    DataFetcher m_dataFetcher;

    private String vertexShaderCode;
    private String fragmentShaderCode;

    private int texture;

    // Declare as volatile because we are updating it from another thread
    public volatile float mxAngle;
    public volatile float myAngle;
    public volatile float mY = 1;

    public volatile int view;
    
    private World w;


    private Context mContext;

    public void setContext(Context context){

        mContext = context;
    }

    public void initGL(){
    	
    	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        IntBuffer ib = IntBuffer.allocate(1);
        GLES20.glGenTextures(1, ib);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        checkGlError("gen texture");

        
        texture = ib.get();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        checkGlError("bind texture");

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap myBitmap;
        try {
            myBitmap = BitmapFactory.decodeStream( mContext.getResources().getAssets().open("rock.bmp"));
            if (myBitmap == null){
                Log.d(TAG, "ASDF");
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
        
    	
    }
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color

        Log.d(TAG, "==============\nStarting glstuff");

        view = 1;

        

        plainShader = loadShader(R.raw.plain_vs, R.raw.plain_fs);

        meshShader = loadShader(R.raw.mesh_vs, R.raw.mesh_fs);
        

        lightball = new VBO(plainShader.getProgram());

        //m_dataFetcher = new AsyncDataFetcher();
        //m_dataFetcher.execute("http://larsendt.com:1234/?x=0&z=0&compression=yes");
        
        m_dataFetcher = new DataFetcher();
        
        Matrix.setIdentityM(GLState.pMatrix, 0);
        Matrix.perspectiveM(GLState.pMatrix, 0, 60.0f, 1.0f, 1f,100.0f);

        initGL();
        
        float vertices[] = {
        		-.1f,.1f,-.1f,
        		-.1f,.1f,.1f,
        		.1f,.1f,.1f,
        		.1f,.1f,-.1f,
        		
        		-.1f,-.1f,-.1f,
        		-.1f,-.1f,.1f,
        		.1f,-.1f,.1f,
        		.1f,-.1f,-.1f
        };
        
        int indices[] = {
        		//top
        		
        		0,1,2,
        		0,3,2,
        		
        		//bottom
        		
        		4,5,6,
        		4,7,6,
        		
        		//left
        		
        		0,4,1,
        		1,5,4,
        		
        		//right
        		
        		2,6,3,
        		3,7,6,
        		
        		//front
        		
        		1,5,2,
        		2,6,5
        		
        		//back
        		
        		
        };
        
        lightball.setBuffers(vertices, indices);
        
        w = new World(m_dataFetcher, meshShader);
        w.loadAround(0, 0);
    }

    public void onDrawFrame(GL10 unused) {

        /*if(m_dataFetcher.getChunkStatus(0,0) == TaskStatus.DONE && !m_hasChunk ) {

            int SIZE_OF_VERTEX_PACKAGE = 8;
            float[] vertex_data = m_dataFetcher.getChunkData(0,0);

            int num_indices = vertex_data.length / SIZE_OF_VERTEX_PACKAGE;
            int[] index_data = new int[num_indices];

            for(int i = 0; i < index_data.length; i++) {
                index_data[i] = i;
            }

            vbo.setBuffers(vertex_data, index_data);
            MyGLRenderer.checkGlError("vbo setBuffers");
            m_hasChunk = true;
            Log.d(MyGLRenderer.TAG, "Vertex buffer complete");
        }*/

    	w.update();
    	
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLState.setMVIdentity();

        GLState.pushMVMatrix();


        if (view == 1){

            GLState.translate(0,0,-mY);
            GLState.rotate(mxAngle, 1f, 0, 0);
            GLState.rotate(myAngle, 0,1f,0);
            GLState.translate(-.5f,0,-.5f);
        }

        else if (view == 0){


            GLState.rotate(mxAngle, 1f, 0, 0);
            GLState.rotate(myAngle, 0,1f,0);
            GLState.translate(-.5f,-mY,-.5f);
            
        }

        float nlightPos[] = {0.5f,0f,.5f,1};
        
        GLState.pushMVMatrix();
        
        GLState.translate(nlightPos[0], nlightPos[1], nlightPos[2]);
        GLState.scale(.1f, .1f, .1f);
        plainShader.useProgram();
        plainShader.setMatrices(GLState.mvMatrix, GLState.pMatrix);
        lightball.draw();
        
        GLState.popMVMatrix();
        
        
        meshShader.useProgram();
        
        
        float lightPos[] = new float[4];
        
        Matrix.multiplyMV(lightPos, 0, GLState.mvMatrix, 0, nlightPos, 0);
        
        meshShader.setUniform3f("lightPos", lightPos[0],lightPos[1],lightPos[2]);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        
        w.draw();

        GLState.popMVMatrix();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.setIdentityM(GLState.pMatrix, 0);
        Matrix.perspectiveM(GLState.pMatrix, 0, 60.0f, ratio, .1f,100.0f);
    }

    private Shader loadShader(int vxresource, int fsresource){
    	try {
            Resources res = mContext.getResources();
            InputStream in = res.openRawResource(vxresource);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] tmpbuf = new byte[8192];
            int bytesRead;

            while((bytesRead = in.read(tmpbuf)) != -1) {
                out.write(tmpbuf, 0, bytesRead);
            }

            vertexShaderCode = out.toString("US-ASCII");
            Log.d(MyGLRenderer.TAG, vertexShaderCode);

            in = res.openRawResource(fsresource);
            out = new ByteArrayOutputStream();

            Log.d(MyGLRenderer.TAG, "================");

            while((bytesRead = in.read(tmpbuf)) != -1) {
                out.write(tmpbuf, 0, bytesRead);
            }

            fragmentShaderCode = out.toString("US-ASCII");
            Log.d(MyGLRenderer.TAG, fragmentShaderCode);


        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load shaders");
        }
    	
    	return new Shader(vertexShaderCode, fragmentShaderCode);
    	
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
