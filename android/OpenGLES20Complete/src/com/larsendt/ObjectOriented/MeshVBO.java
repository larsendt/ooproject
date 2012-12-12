package com.larsendt.ObjectOriented;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;

public class MeshVBO extends VBO{

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
    
    public MeshVBO(int shaderprogram)
    {
    	super(shaderprogram);
    	WorldRenderer.checkGlError("VBO start");
    	program = shaderprogram;
    	
    	String attribute_name = "vertex";
    	
    	att_vertex = GLES20.glGetAttribLocation(program, attribute_name);
    	if (att_vertex == -1){
    	}
    	WorldRenderer.checkGlError("vertex glGetAttribLocation");

        att_normal = GLES20.glGetAttribLocation(program, "normal");
        if(att_normal == -1) {
        }
        WorldRenderer.checkGlError("normal glGetAttribLocation");

        att_txcoord = GLES20.glGetAttribLocation(program, "txcoord");
        if(att_txcoord == -1) {
        }
        WorldRenderer.checkGlError("txcoord glGetAttribLocation");
        
        IntBuffer ib = IntBuffer.allocate(2);
    	GLES20.glGenBuffers(2, ib);
    	vbo_vertices = ib.get();
    	
    	ibo_elements = ib.get();
    	
    	WorldRenderer.checkGlError("glGenBuffers");
    	
    	
    	
    }
    
    @Override
	public void setBuffers(float vertices[], int indices[])
    {
    	FloatBuffer fb = FloatBuffer.allocate(vertices.length);
    	

    	
    	fb.put(vertices);
    	fb.rewind();
    	
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	WorldRenderer.checkGlError("glBindBuffer");
    	
    	
    	GLES20.glBufferData(
    			GLES20.GL_ARRAY_BUFFER,
    			vertices.length * FLOAT_BYTE_SIZE, 
    			fb, 
    			GLES20.GL_STATIC_DRAW
    	);
    	
    	WorldRenderer.checkGlError("glBufferData");
    	
    	IntBuffer ib = IntBuffer.allocate(indices.length);
    	
    	
    	ib.put(indices);
    	ib.rewind();
    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	
    	WorldRenderer.checkGlError("glBindBuffer");
    	
    	GLES20.glBufferData(
    			GLES20.GL_ELEMENT_ARRAY_BUFFER,
    			indices.length * INT_BYTE_SIZE,
    			ib,
    			GLES20.GL_STATIC_DRAW
    	);
    	
    	elements_length = indices.length;
    	
    	WorldRenderer.checkGlError("glBufferData");
    	
    }
    
    public void clearBuffers(){
    	int buffers[] = { ibo_elements, vbo_vertices};
    	GLES20.glDeleteBuffers(2, buffers, 0);
    }
    
    @Override
	public void draw()
    {
    	WorldRenderer.checkGlError("draw start");
    	
    	GLES20.glEnableVertexAttribArray(att_vertex);
        GLES20.glEnableVertexAttribArray(att_normal);
        GLES20.glEnableVertexAttribArray(att_txcoord);

    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	WorldRenderer.checkGlError("vbo_vertex glBindBuffer");
    	
    	GLES20.glVertexAttribPointer(
    				att_vertex,
    				ELEMENTS_IN_VERTEX,
    				GLES20.GL_FLOAT,
    				false,
    				BYTES_IN_PACKAGE,
    				0
    	);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
    	
    	WorldRenderer.checkGlError("att_vertex glVertexAttribPointer");

        GLES20.glVertexAttribPointer(
                att_normal,
                ELEMENTS_IN_VERTEX,
                GLES20.GL_FLOAT,
                false,
                BYTES_IN_PACKAGE,
                3*FLOAT_BYTE_SIZE
        );
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_vertices);
        
        WorldRenderer.checkGlError("att_normal glVertexAttribPointer");
        
        GLES20.glVertexAttribPointer(
                att_txcoord,
                ELEMENTS_IN_TXCOORD,
                GLES20.GL_FLOAT,
                false,
                BYTES_IN_PACKAGE,
                6*FLOAT_BYTE_SIZE
        );
        WorldRenderer.checkGlError("att_normal glVertexAttribPointer");

    	
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_elements);
    	WorldRenderer.checkGlError("glBindBuffer");
    	
    	
    	
    	GLES20.glDrawElements(
    			GLES20.GL_TRIANGLES,
    			elements_length, 
    			GLES20.GL_UNSIGNED_INT, 
    			0
    	);
    	WorldRenderer.checkGlError("glDrawElements");
    	
    	GLES20.glDisableVertexAttribArray(att_vertex);
    	GLES20.glDisableVertexAttribArray(att_normal);
    	GLES20.glDisableVertexAttribArray(att_txcoord);
    	WorldRenderer.checkGlError("glDisableVertexAttribArray");
    	
    }
    
}