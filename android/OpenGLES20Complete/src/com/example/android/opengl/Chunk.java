package com.example.android.opengl;

public class Chunk {

	private MeshVBO drawable;
	public int x;
	public int z;
	
	public Chunk(float vertices[], int indices[], int shaderProgram) {

		drawable = new MeshVBO(shaderProgram);
		drawable.setBuffers(vertices, indices);
		
	}
	public void draw(){
		
		drawable.draw();
	}
	
	public void clear(){
		drawable.clearBuffers();
		
	}
}
