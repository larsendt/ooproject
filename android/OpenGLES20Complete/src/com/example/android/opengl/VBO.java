package com.example.android.opengl;

import java.lang.Object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class VBO {

    private int program;
    private int vbo_vertices;
    private int ibo_elements;
    private int att_vertex;
    
    private static final int VERTEX_BYTE_SIZE = 12;
    private static final int INT_BYTE_SIZE = 4;
    private static final int ELEMENTS_IN_VERTEX = 3;
    
    private float vertices[];
    private int elements[];
    
    public VBO(int shaderprogram)
    {
    	
    	program = shaderprogram;
    	
    	String attribute_name = "vertex";
    	
    	att_vertex = GLES20.glGetAttribLocation(program, attribute_name);
    	if (att_vertex == -1){
    	}
    	IntBuffer ib = IntBuffer.allocate(2);
    	
    	GLES20.glGenBuffers(2, ib);
    	vbo_vertices = ib.get();
    	
    	ibo_elements = ib.get();
    	
    	
    	
    }
    
    public void setBuffers(float vertices[], int indices[])
    {
    	Log.d(MyGLRenderer.TAG, "setting buffers");
    	FloatBuffer fb = FloatBuffer.allocate(vertices.length);
    	
    	Log.d(MyGLRenderer.TAG, "Length of vertices is " + Integer.toString(vertices.length));

    	
    	fb.put(vertices);
    	fb.rewind();
    	
    	Log.d(MyGLRenderer.TAG, "binding vertex buffer");
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	Log.d(MyGLRenderer.TAG, "setting vertex buffer data");
    	
    	GLES20.glBufferData(
    			GLES20.GL_ARRAY_BUFFER,
    			vertices.length, 
    			fb, 
    			GLES20.GL_STATIC_DRAW
    	);
    	
    	IntBuffer ib = IntBuffer.allocate(indices.length);
    	
    	Log.d(MyGLRenderer.TAG, "Length of indices is " + Integer.toString(indices.length));
    	
    	ib.put(indices);
    	ib.rewind();
    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	
    	GLES20.glBufferData(
    			GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			indices.length,
    			ib,
    			GLES20.GL_STATIC_DRAW
    	);
    	
    }
    
    public void draw()
    {
    	GLES20.glEnableVertexAttribArray(att_vertex);
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	GLES20.glVertexAttribPointer(
    				att_vertex,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				0,
    				0
    	);
    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	
    	int size;
    	IntBuffer ib = IntBuffer.allocate(1);
    	GLES20.glGetBufferParameteriv(
    			GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			GLES20.GL_BUFFER_SIZE,
    			ib
    	);
    	
    	size = ib.get();
    	
    	GLES20.glDrawElements(
    			GLES20.GL_TRIANGLES,
    			size/4, 
    			GLES20.GL_UNSIGNED_INT, 
    			0
    	);
    	
    	GLES20.glDisableVertexAttribArray(att_vertex);
    	
    }
    
}

