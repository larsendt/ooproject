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
    
    private static final int VERTEX_BYTE_SIZE = 12;
    private static final int INT_BYTE_SIZE = 4;
    private static final int FLOAT_BYTE_SIZE = 4;
    private static final int ELEMENTS_IN_VERTEX = 3;
    
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
    	MyGLRenderer.checkGlError("draw start");
    	
    	GLES20.glEnableVertexAttribArray(att_vertex);
        MyGLRenderer.checkGlError("att_vertex glEnableVertexAttribArray");
        GLES20.glEnableVertexAttribArray(att_normal);
        MyGLRenderer.checkGlError("att_normal glEnableVertexAttribArray");

    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	MyGLRenderer.checkGlError("vbo_vertex glBindBuffer");
    	
    	GLES20.glVertexAttribPointer(
    				att_vertex,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				3,
    				0
    	);
    	MyGLRenderer.checkGlError("att_vertex glVertexAttribPointer");

        GLES20.glVertexAttribPointer(
                att_normal,
                3,
                GLES20.GL_FLOAT,
                false,
                3,
                3
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
    	MyGLRenderer.checkGlError("glDisableVertexAttribArray");
    	
    }
    
}

