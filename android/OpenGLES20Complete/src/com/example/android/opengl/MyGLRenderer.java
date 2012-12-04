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

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Stack;

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
import android.os.AsyncTask;
import android.util.Log;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final String TAG = "OO";
    private Shader shader;
    private VBO vbo;
    private boolean m_hasChunk = false;
    AsyncDataFetcher m_dataFetcher;

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 mvMatrix;" +
        "uniform mat4 pMatrix;" +
        "uniform mat3 nMatrix;" +
        "attribute vec3 vertex;" +
        "attribute vec3 normal;" +
        "attribute vec2 txcoord;" +
        "varying vec2 f_txcoord;" +
        "varying vec3 f_lightPos;" +
        "varying vec3 f_normal;" +
        "varying vec3 f_vertex;" +


        "void main() {" +
        "	gl_PointSize = 3.0;" +
        "	f_txcoord = txcoord;" +
        "	f_normal = normalize(nMatrix*normal);" +
        "	f_vertex = vec3(mvMatrix * vec4(vertex, 1.0));" +
        "	vec3 lightPos = vec3(mvMatrix * vec4(0.0,.5,0.0,1.0));" +
        "	f_lightPos = lightPos;" +

        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = pMatrix * mvMatrix * vec4(vertex,1.0);" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform sampler2D tex;" +
        "varying vec2 f_txcoord;" +
        "varying vec3 f_lightPos;" +
        "varying vec3 f_normal;" +
        "varying vec3 f_vertex;" +
        
        "void main() {" +
        "	vec3 L = normalize(f_lightPos - f_vertex); " +
        "	vec3 E = normalize(-f_vertex);" +
        "	vec3 R = normalize(-reflect(L,f_normal));" +
        "	vec3 ambient = vec3(.0,.0,.0);" +
        "	vec3 diffuse = vec3(.6) * max(dot(L, f_normal), 0.0);" +
        "	diffuse = clamp(diffuse, 0.0,1.0);" +
        "	vec3 specular = vec3(.15)*pow(max(dot(R,E),0.0), .3 * 30.0);" +
        "	specular = clamp(specular, 0.0,1.0);" +
        
        "	vec4 color = texture2D(tex, f_txcoord*.1);" +
        "	vec3 intensity = vec3(color) * diffuse;" +
        "	gl_FragColor = vec4(ambient+intensity+specular, 1.0);" +
        "}";

    private float[] pMatrix = new float[16];
    private float[] mvMatrix = new float[16];
    private float[] nMatrix = new float[9];
    private float[] nInvertMatrix = new float[16];
    private float[] nInvertTransposeMatrix = new float[16];
    private float[] tmpMatrix = new float[16];
    
    private Stack<float[]> projectionStack = new Stack<float[]>();
    private Stack<float[]> modelviewStack = new Stack<float[]>();
    
    private int texture;
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mxAngle;
    public volatile float myAngle;
    public volatile float mY = 1;
    
    public volatile int view;
    
    
    private Context mContext;

    public void setContext(Context context){
    	
    	mContext = context;
    }
    
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	
    	Log.d(TAG, "==============\nStarting glstuff");
    	
    	view = 1;
    	
        GLES20.glClearColor(0.0f, 0.0f, 0.3f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        shader = new Shader(vertexShaderCode, fragmentShaderCode);

        vbo = new VBO(shader.getProgram());
        
        m_dataFetcher = new AsyncDataFetcher();
        m_dataFetcher.execute("banana");
        
        Matrix.setIdentityM(pMatrix, 0);
        Matrix.perspectiveM(pMatrix, 0, 60.0f, 1.0f, 1f,100.0f);
        
        
        
        Bitmap myBitmap;
		try {
			myBitmap = BitmapFactory.decodeStream( mContext.getResources().getAssets().open("rock.bmp"));
			if (myBitmap == null){
				Log.d(TAG, "ASDF");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// farts
			e.printStackTrace();
			myBitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
	        
	        myBitmap.setPixel(0, 0, Color.WHITE);
	        myBitmap.setPixel(0, 1, Color.CYAN);
	        myBitmap.setPixel(1, 0, Color.CYAN);
	        myBitmap.setPixel(1, 1, Color.WHITE);
		}
        

        IntBuffer ib = IntBuffer.allocate(1);
        
        GLES20.glGenTextures(1, ib);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        checkGlError("gen texture");
        
        texture = ib.get();
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        
        checkGlError("bind texture");
        
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, myBitmap, 0);
        
        checkGlError("TexImage");
        
    }

    public void onDrawFrame(GL10 unused) {

        if(m_dataFetcher.getStatus() == AsyncTask.Status.FINISHED && !m_hasChunk ) {
        	
        	int SIZE_OF_VERTEX_PACKAGE = 8;
        	
        	float[] vertex_data = m_dataFetcher.getVertexData();
        	
        	int num_indices = vertex_data.length / SIZE_OF_VERTEX_PACKAGE;
        	
            int[] index_data = new int[num_indices];

            for (int i = 0; i < vertex_data.length; i+=SIZE_OF_VERTEX_PACKAGE){
            	//Log.d(TAG, Float.toString(vertex_data[i+3]) + "/" + Float.toString(vertex_data[i+3]) + "/" + Float.toString(vertex_data[i+3]));
            	
            }
            
            for(int i = 0; i < index_data.length; i++) {
                index_data[i] = i;
            }

            vbo.setBuffers(vertex_data, index_data);
            MyGLRenderer.checkGlError("vbo setBuffers");
            m_hasChunk = true;
            Log.d(MyGLRenderer.TAG, "Vertex buffer complete");
        }

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        Matrix.setIdentityM(mvMatrix, 0);
        
        pushMVMatrix();
        
        
        if (view == 1){
        
        Matrix.translateM(mvMatrix, 0, 0.0f, 0.0f, -mY);
        
        Matrix.rotateM(mvMatrix, 0, mxAngle, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mvMatrix, 0, myAngle, 0, 1.0f, 0.0f);
        Matrix.translateM(mvMatrix, 0, -2.0f,0.0f, -2.0f);
        }
        
        else if (view == 0){
        	
            
            Matrix.rotateM(mvMatrix, 0, mxAngle, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(mvMatrix, 0, myAngle, 0, 1.0f, 0.0f);
            
            Matrix.translateM(mvMatrix, 0, -2.0f,-mY, -2.0f);
        }
        
        
        shader.useProgram();
        
        setNMatrix();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        
        checkGlError("bind texture");

        
        checkGlError("active texture");
        
        shader.setUniform1i("tex", 0);
        
        shader.setMatrices(mvMatrix, pMatrix, nMatrix);
        vbo.draw();
        
        popMVMatrix();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.setIdentityM(pMatrix, 0);
        Matrix.perspectiveM(pMatrix, 0, 60.0f, ratio, .1f,100.0f);
    }
    
    private void setNMatrix(){
    	
    	Matrix.invertM(nInvertMatrix, 0, mvMatrix, 0);
        Matrix.transposeM(nInvertTransposeMatrix, 0,nInvertMatrix, 0);
        
        nMatrix[0] = nInvertTransposeMatrix[0];
        nMatrix[1] = nInvertTransposeMatrix[1];
        nMatrix[2] = nInvertTransposeMatrix[2];
        
        nMatrix[3] = nInvertTransposeMatrix[4];
        nMatrix[4] = nInvertTransposeMatrix[5];
        nMatrix[5] = nInvertTransposeMatrix[6];
        
        nMatrix[6] = nInvertTransposeMatrix[8];
        nMatrix[7] = nInvertTransposeMatrix[9];
        nMatrix[8] = nInvertTransposeMatrix[10];
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
    
    private void pushMVMatrix(){
    	modelviewStack.push(mvMatrix);
    }
    
    private void popMVMatrix(){
    	mvMatrix = Arrays.copyOf(modelviewStack.pop(), 16);
    }
    
    private void pushPMatrix(){
    	projectionStack.push(pMatrix);
    }
    private void popPMatrix(){
    	pMatrix = Arrays.copyOf(projectionStack.pop(), 16);
    }
}
