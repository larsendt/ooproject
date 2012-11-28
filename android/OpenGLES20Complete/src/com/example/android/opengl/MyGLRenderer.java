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
    public volatile float mxAngle;
    public volatile float myAngle;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	
    	Log.d(TAG, "==============\nStarting glstuff");
    	
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        shader = new Shader(vertexShaderCode, fragmentShaderCode);
        checkGlError("Before vbo init");
        vbo = new VBO(shader.getProgram());

        
        int size = 30;
        float vertices[] = new float[3 * 3*size * size];
        int indices[] = new int[3 * size * size];
        int count = 0;
        int icount = 0;
        
        for (int i = 0; i < size; i ++){
        	for (int j = 0; j < size; j++){
        		vertices[count] = (i-size/2)*.1f;
        		vertices[count + 1] = (j-size/2)*.1f;
        		vertices[count + 2] = (float)Math.random();
        		count+=3;
        		
        		vertices[count] = ((i+1)-size/2)*.1f;
        		vertices[count + 1] = (j-size/2)*.1f;
        		vertices[count + 2] = (float)Math.random();
        		count+=3;
        		
        		vertices[count] = ((i+1) - size/2)*.1f;
        		vertices[count + 1] = ((j+1) - size/2)*.1f;
        		vertices[count + 2] = (float)Math.random();
        		count+=3;
        		
        		indices[icount] = icount;
        		icount++;
        		indices[icount] = icount;
        		icount++;
        		indices[icount] = icount;
        		icount++;
        	}
        }
        
        
        
        
        
        vbo.setBuffers(vertices, indices);
        MyGLRenderer.checkGlError("vbo setBuffers");
    }

    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        Matrix.setIdentityM(mvMatrix, 0);
        
        
        Matrix.translateM(mvMatrix, 0, mvMatrix, 0, 0.0f,0.0f,-10.0f);
        Matrix.rotateM(mvMatrix, 0, mvMatrix, 0, mxAngle, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mvMatrix, 0, mvMatrix, 0, myAngle, 0, 1.0f, 0.0f);
        

        // Draw triangle
        shader.useProgram();
        
        shader.setMatrices(mvMatrix, pMatrix);
        
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
