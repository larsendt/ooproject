package com.example.android.opengl;

public class Chunk {

	private VBO drawable;
	public int x;
	public int z;
	
	public Chunk(float vertices[], int indices[], int shaderProgram) {

		drawable = new VBO(shaderProgram);
		drawable.setBuffers(vertices, indices);
		
	}
	public void draw(){
		
		drawable.draw();
	}
}
