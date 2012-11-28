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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = "OO";
    private Shader shader;
    private VBO vbo;

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 mvMatrix;" +
        "uniform mat4 pMatrix;" +
        "attribute vec3 vertex;" +

        "void main() {" +
        "	gl_PointSize = 5.0;" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = pMatrix * mvMatrix * vec4(vertex,1);" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "void main() {" +
        "  gl_FragColor = vec4(1,1,1,1);" +
        "}";
    
    private final float[] pMatrix = new float[16];
    private final float[] mvMatrix = new float[16];

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	
    	Log.d(TAG, "Starting glstuff");
    	
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        shader = new Shader(vertexShaderCode, fragmentShaderCode);
        Log.d(TAG, Integer.toString(shader.getProgram()));
        checkGlError("Before vbo init");
        vbo = new VBO(shader.getProgram());

        int divs = 20;
        float vertices[] = new float[3*3*divs];
        int indices[] = new int[3*divs];
        int count = 0;
        int icount = 0;
        
        for (int i = 0; i < divs; i ++){
        	double ratio = i/((double)divs);
        	double radians = ratio * 2.0 * Math.PI;
	        vertices[count] = (float)Math.cos(radians);
	        vertices[count+1] = (float)Math.sin(radians);
	        vertices[count+2] = 0;
	        count+=3;
	        
	        indices[icount] = icount;
	        icount++;
	        
	        ratio = (i+1)/((double)divs);
	        radians = ratio * 2.0 * Math.PI;
	        vertices[count] = (float)Math.cos(radians);
	        vertices[count+1] = (float)Math.sin(radians);
	        vertices[count+2] = 0;
	        count+=3;
	        
	        indices[icount] = icount;
	        icount++;
	        
	        vertices[count] = 0;
	        vertices[count+1] = 0;
	        vertices[count+2] = 0;
	        count+=3;
	        
	        indices[icount] = icount;
	        icount++;
        		
        }
        
        
        vbo.setBuffers(vertices, indices);
        MyGLRenderer.checkGlError("vbo setBuffers");
    }

    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        Matrix.setIdentityM(mvMatrix, 0);
        
        Matrix.translateM(mvMatrix, 0, mvMatrix, 0, 0.0f,0.0f,-10.0f);
        Matrix.rotateM(mvMatrix, 0, mvMatrix, 0, mAngle, 0, 0, 1.0f);

        // Draw triangle
        MyGLRenderer.checkGlError("start of draw");
        shader.useProgram();
        
        shader.setMatrices(mvMatrix, pMatrix);
        MyGLRenderer.checkGlError("shader.setMatrices");
        
        MyGLRenderer.checkGlError("shader.useProgram");
        vbo.draw();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.setIdentityM(pMatrix, 0);
        Matrix.perspectiveM(pMatrix, 0, 45, ratio, .1f,100);

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
