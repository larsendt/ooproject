package com.larsendt.ObjectOriented;

public class ShapeUtils {

	public ShapeUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static VBO sphere(int shaderProgram, int horizontal_div_num, int vertical_div_num){
		
		
		
		float vertices[] = new float[3 * horizontal_div_num * vertical_div_num];
		int indices[] = new int[horizontal_div_num * vertical_div_num];
		int count = 0;
		int icount = 0;
		
		for (int i = 0; i < horizontal_div_num; i ++){
			
			float theta = i * (float)Math.PI / horizontal_div_num;
			
			float sinTheta = (float)Math.sin(theta);
			float cosTheta = (float)Math.cos(theta);
			
			for (int j = 0; j < vertical_div_num; j++){
				
				float phi = j * 2 * (float)Math.PI / vertical_div_num;
				float sinPhi = (float)Math.sin(phi);
				float cosPhi = (float) Math.cos(phi);
				
				float x = cosPhi * sinTheta;
				float y = cosTheta;
				float z = sinPhi * sinTheta;
				
				vertices[count] = x;
				vertices[count+1] = y;
				vertices[count+2] = z;
				count +=3;
				
				indices[icount] = icount;
				icount++;
				
				
			}
		}
		VBO v = new VBO(shaderProgram);
		v.setBuffers(vertices, indices);
		return v;
		
	}
	
	public static VBO cube(int shaderProgram){
		float vertices[] = {
        		-1f,1f,-1f,
        		-1f,1f,1f,
        		1f,1f,1f,
        		1f,1f,-1f,
        		
        		-1f,-1f,-1f,
        		-1f,-1f,1f,
        		1f,-1f,1f,
        		1f,-1f,-1f
        };
        
        int indices[] = {
        		//top
        		
        		0,1,2,
        		0,3,2,
        		
        		//bottom
        		
        		4,5,6,
        		4,7,6,
        		
        		//left
        		
        		0,4,1,
        		1,5,4,
        		
        		//right
        		
        		2,6,3,
        		3,7,6,
        		
        		//front
        		
        		1,5,2,
        		2,6,5
        		
        		//back
        		
        		
        };
		VBO v = new VBO(shaderProgram);
		v.setBuffers(vertices, indices);
		return v;
	}

}
