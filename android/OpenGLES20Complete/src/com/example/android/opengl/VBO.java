package com.example.android.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class VBO {

    private int program;
    private int vbo_vertices;
    private int ibo_elements;
    private int att_vertex;
    private int att_normal;
    private int att_txcoord;
    
    private static final int VERTEX_BYTE_SIZE = 12;
    private static final int INT_BYTE_SIZE = 4;
    private static final int FLOAT_BYTE_SIZE = 4;
    private static final int ELEMENTS_IN_VERTEX = 3;
    private static final int ELEMENTS_IN_TXCOORD = 2;
    private static final int ELEMENTS_IN_PACKAGE = 8;
    private static final int BYTES_IN_PACKAGE = ELEMENTS_IN_PACKAGE * FLOAT_BYTE_SIZE;
    
    private int elements_length;
    
    public VBO(int shaderprogram)
    {
    	MyGLRenderer.checkGlError("VBO start");
    	program = shaderprogram;
    	
    	String attribute_name = "vertex";
    	
    	att_vertex = GLES20.glGetAttribLocation(program, attribute_name);
    	if (att_vertex == -1){
    	}
    	MyGLRenderer.checkGlError("vertex glGetAttribLocation");

        att_normal = GLES20.glGetAttribLocation(program, "normal");
        if(att_normal == -1) {
        }
        MyGLRenderer.checkGlError("normal glGetAttribLocation");

        att_txcoord = GLES20.glGetAttribLocation(program, "txcoord");
        if(att_txcoord == -1) {
        }
        MyGLRenderer.checkGlError("txcoord glGetAttribLocation");
        
        IntBuffer ib = IntBuffer.allocate(2);
    	GLES20.glGenBuffers(2, ib);
    	vbo_vertices = ib.get();
    	
    	ibo_elements = ib.get();
    	
    	MyGLRenderer.checkGlError("glGenBuffers");
    	
    	
    	
    }
    
    public void setBuffers(float vertices[], int indices[])
    {
    	FloatBuffer fb = FloatBuffer.allocate(vertices.length);
    	

    	
    	fb.put(vertices);
    	fb.rewind();
    	
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	MyGLRenderer.checkGlError("glBindBuffer");
    	
    	
    	GLES20.glBufferData(
    			GLES20.GL_ARRAY_BUFFER,
    			vertices.length * FLOAT_BYTE_SIZE, 
    			fb, 
    			GLES20.GL_STATIC_DRAW
    	);
    	
    	MyGLRenderer.checkGlError("glBufferData");
    	
    	IntBuffer ib = IntBuffer.allocate(indices.length);
    	
    	
    	ib.put(indices);
    	ib.rewind();
    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	
    	MyGLRenderer.checkGlError("glBindBuffer");
    	
    	GLES20.glBufferData(
    			GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			indices.length * INT_BYTE_SIZE,
    			ib,
    			GLES20.GL_STATIC_DRAW
    	);
    	
    	elements_length = indices.length;
    	
    	MyGLRenderer.checkGlError("glBufferData");
    	
    }
    
    public void draw()
    {
    	Log.d(MyGLRenderer.TAG, "asdf");
    	MyGLRenderer.checkGlError("draw start");
    	
    	GLES20.glEnableVertexAttribArray(att_vertex);
        GLES20.glEnableVertexAttribArray(att_normal);
        GLES20.glEnableVertexAttribArray(att_txcoord);

    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	MyGLRenderer.checkGlError("vbo_vertex glBindBuffer");
    	
    	GLES20.glVertexAttribPointer(
    				att_vertex,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				BYTES_IN_PACKAGE,
    				0
    	);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	MyGLRenderer.checkGlError("att_vertex glVertexAttribPointer");

        GLES20.glVertexAttribPointer(
                att_normal,
                ELEMENTS_IN_VERTEX,
                GLES20.GL_FLOAT,
                false,
                BYTES_IN_PACKAGE,
                3*FLOAT_BYTE_SIZE
        );
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
        
        MyGLRenderer.checkGlError("att_normal glVertexAttribPointer");
        
        GLES20.glVertexAttribPointer(
                att_txcoord,
                ELEMENTS_IN_TXCOORD,
                GLES20.GL_FLOAT,
                false,
                BYTES_IN_PACKAGE,
                6*FLOAT_BYTE_SIZE
        );
        MyGLRenderer.checkGlError("att_normal glVertexAttribPointer");

    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	MyGLRenderer.checkGlError("glBindBuffer");
    	
    	
    	
    	GLES20.glDrawElements(
    			GLES20.GL_TRIANGLES,
    			elements_length, 
    			GLES20.GL_UNSIGNED_INT, 
    			0
    	);
    	MyGLRenderer.checkGlError("glDrawElements");
    	
    	GLES20.glDisableVertexAttribArray(att_vertex);
    	GLES20.glDisableVertexAttribArray(att_normal);
    	GLES20.glDisableVertexAttribArray(att_txcoord);
    	MyGLRenderer.checkGlError("glDisableVertexAttribArray");
    	
    }
    
}

