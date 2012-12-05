package com.example.android.opengl;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	
	private int program;
	
	public Shader(String vertCode, String fragCode)
	{
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertCode);
		int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode);
		
		program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);

		Log.d(MyGLRenderer.TAG, "Vertex compo log" + GLES20.glGetShaderInfoLog(vertexShader));
		Log.d(MyGLRenderer.TAG, "Fragment compo log" + GLES20.glGetShaderInfoLog(fragmentShader));
		Log.d(MyGLRenderer.TAG, "Program compo log" + GLES20.glGetProgramInfoLog(program));
		
	}
	
	public int getProgram()
	{
		return program;
	}
	
	public void useProgram(){
		GLES20.glUseProgram(program);
		
	}
	
	public void setUniform1i(String name, int value){
		int location = GLES20.glGetUniformLocation(program, name);
		
		GLES20.glUniform1i(location, value);
	}
	
	public void setUniform1f(String name, float value){
		int location = GLES20.glGetUniformLocation(program, name);
		GLES20.glUniform1f(location, value);
	}
	public void setUniform2f(String name, float v1, float v2){
		int location = GLES20.glGetUniformLocation(program, name);
		GLES20.glUniform2f(location, v1,v2);
	}
	public void setUniform3f(String name, float v1, float v2, float v3){
		int location = GLES20.glGetUniformLocation(program, name);
		GLES20.glUniform3f(location, v1,v2,v3);
	}
	public void setUniform4f(String name, float v1, float v2, float v3, float v4){
		int location = GLES20.glGetUniformLocation(program, name);
		GLES20.glUniform4f(location, v1,v2,v3,v4);
	}
	
	public void setUniformMatrix3fv(String name, float values[])
	{
		int location = GLES20.glGetUniformLocation(program, name);
		if (location == -1){
			Log.d(MyGLRenderer.TAG, "Location for " + name + " is invalid");
		}
		
		GLES20.glUniformMatrix3fv(location, 1, false, values, 0);
	}
	
	public void setUniformMatrix4fv(String name, float values[])
	{
		int location = GLES20.glGetUniformLocation(program, name);
		if (location == -1){
			Log.d(MyGLRenderer.TAG, "Location for " + name + " is invalid");
		}
		
		GLES20.glUniformMatrix4fv(location, 1, false, values, 0);
	}
	
	public void setMatrices(float[] mvMatrix, float[] pMatrix)
	{
		MyGLRenderer.checkGlError("Start of setmatrices");
		setUniformMatrix4fv("mvMatrix", mvMatrix);
		MyGLRenderer.checkGlError("mvMatrix setmatrix");
		setUniformMatrix4fv("pMatrix", pMatrix);
		MyGLRenderer.checkGlError("pMatrix setmatrix");
		
		
	}
	
	public void setMatrices(float[] mvMatrix, float[] pMatrix, float[] nMatrix){
		
		setUniformMatrix4fv("mvMatrix", mvMatrix);
		MyGLRenderer.checkGlError("mvMatrix setmatrix");
		setUniformMatrix4fv("pMatrix", pMatrix);
		MyGLRenderer.checkGlError("pMatrix setmatrix");
		setUniformMatrix3fv("nMatrix", nMatrix);
		MyGLRenderer.checkGlError("nMatrix setmatrix");
	}
	
	
	private int compileShader(int type, String shaderCode)
	{

        int shader= GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        MyGLRenderer.checkGlError("compileShader");
        return shader;
		
	}
	
}
