package com.example.android.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class MeshVBO {

    private int program;
    private int vbo_packages;
    private int ibo_elements;
    private int att_vertex;
    private int att_normal;
    
    private static final int VERTEX_BYTE_SIZE = 12;
    private static final int INT_BYTE_SIZE = 4;
    private static final int FLOAT_BYTE_SIZE = 4;
    private static final int ELEMENTS_IN_VERTEX = 3;
    
    private int elements_length;
    
    public MeshVBO(int shaderprogram)
    {
    	MyGLRenderer.checkGlError("VBO start");
    	program = shaderprogram;
    	
    	String attribute_name = "vertex";
    	
    	att_vertex = GLES20.glGetAttribLocation(program, attribute_name);
    	if (att_vertex == -1){
    	}
    	
    	attribute_name = "normal";
    	
    	att_normal = GLES20.glGetAttribLocation(program, attribute_name);
    	if (att_normal == -1){
    	}

    	IntBuffer ib = IntBuffer.allocate(2);
    	
    	GLES20.glGenBuffers(2, ib);
    	vbo_packages = ib.get();
    	
    	ibo_elements = ib.get();
    	
    	MyGLRenderer.checkGlError("glGenBuffers");
    	
    	
    	
    }
    
    public void setBuffers(float packages[], int indices[])
    {
    	FloatBuffer fb = FloatBuffer.allocate(packages.length);
    	

    	//
    	//
    	// Packages Layout:
    	// 	VERTEX: float,float,float
    	//	NORMAL: float,float,float
    	//
    	//
    	fb.put(packages);
    	fb.rewind();
    	
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_packages);
    	
    	MyGLRenderer.checkGlError("glBindBuffer");
    	
    	
    	GLES20.glBufferData(
    			GLES20.GL_ARRAY_BUFFER,
    			packages.length * FLOAT_BYTE_SIZE, 
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
    	
    	GLES20.glEnableVertexAttribArray(att_vertex);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_packages);
    	
    	GLES20.glVertexAttribPointer(
    				att_vertex,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				FLOAT_BYTE_SIZE * 3,
    				0
    	);
    	
    	GLES20.glEnableVertexAttribArray(att_normal);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_packages);
    	
    	GLES20.glVertexAttribPointer(
    				att_normal,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				FLOAT_BYTE_SIZE * 3,
    				FLOAT_BYTE_SIZE * 3
    	);
    	
    	
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
    	MyGLRenderer.checkGlError("glDisableVertexAttribArray");
    	
    }
    
}