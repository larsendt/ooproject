package com.larsendt.ObjectOriented;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	
	private int program;
	
	public Shader(String vertCode, String fragCode)
	{
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertCode);
		int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode);

		program = GLES20.glCreateProgram();

        if(program == 0) {
            throw new RuntimeException("Could not create shader program");
        }

		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		GLES20.glLinkProgram(program);

        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if(status[0] == GLES20.GL_TRUE) {
            Log.d(WorldRenderer.TAG, "Linked shader program (id:" + program + ")");
        }
        else {
            throw new RuntimeException("Program link error:" + GLES20.glGetProgramInfoLog(program));
        }
	}

    public static Shader loadShaderFromResource(int vxresource, int fsresource, Context context){
        String vertexShaderCode;
        String fragmentShaderCode;
        try {
            Resources res = context.getResources();
            InputStream in = res.openRawResource(vxresource);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] tmpbuf = new byte[8192];
            int bytesRead;

            while((bytesRead = in.read(tmpbuf)) != -1) {
                out.write(tmpbuf, 0, bytesRead);
            }

            vertexShaderCode = out.toString("US-ASCII");

            in = res.openRawResource(fsresource);
            out = new ByteArrayOutputStream();

            while((bytesRead = in.read(tmpbuf)) != -1) {
                out.write(tmpbuf, 0, bytesRead);
            }

            fragmentShaderCode = out.toString("US-ASCII");

        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load shaders");
        }

        return new Shader(vertexShaderCode, fragmentShaderCode);

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
			Log.d(WorldRenderer.TAG, "Location for " + name + " is invalid");
		}
		
		GLES20.glUniformMatrix3fv(location, 1, false, values, 0);
	}
	
	public void setUniformMatrix4fv(String name, float values[])
	{
		int location = GLES20.glGetUniformLocation(program, name);
		if (location == -1){
			Log.d(WorldRenderer.TAG, "Location for " + name + " is invalid");
		}
		
		GLES20.glUniformMatrix4fv(location, 1, false, values, 0);
	}
	
	public void setMatrices(float[] mvMatrix, float[] pMatrix)
	{
		WorldRenderer.checkGlError("Start of setmatrices");
		setUniformMatrix4fv("mvMatrix", mvMatrix);
		WorldRenderer.checkGlError("mvMatrix setmatrix");
		setUniformMatrix4fv("pMatrix", pMatrix);
		WorldRenderer.checkGlError("pMatrix setmatrix");
		
		
	}
	
	public void setMatrices(float[] mvMatrix, float[] pMatrix, float[] nMatrix){
		
		setUniformMatrix4fv("mvMatrix", mvMatrix);
		WorldRenderer.checkGlError("mvMatrix setmatrix");
		setUniformMatrix4fv("pMatrix", pMatrix);
		WorldRenderer.checkGlError("pMatrix setmatrix");
		setUniformMatrix3fv("nMatrix", nMatrix);
		WorldRenderer.checkGlError("nMatrix setmatrix");
	}
	
	
	private int compileShader(int type, String shaderCode)
	{

        int shader= GLES20.glCreateShader(type);

        if(shader == 0) {
            throw new RuntimeException("Could not create shader");
        }
        else {

            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            int[] status = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
            if(status[0] == GLES20.GL_TRUE) {
                if(type == GLES20.GL_VERTEX_SHADER) {
                    Log.d(WorldRenderer.TAG, "Compiled vertex shader (id:" + shader +")");
                }
                else {
                    Log.d(WorldRenderer.TAG, "Compiled fragment shader (id:" + shader + ")");
                }
            }
            else {
                if(type == GLES20.GL_VERTEX_SHADER) {
                    throw new RuntimeException("Vertex shader compile error: " + GLES20.glGetShaderInfoLog(shader));
                }
                else {
                    throw new RuntimeException("Fragment shader compile error: " + GLES20.glGetShaderInfoLog(shader));
                }
            }
        }

        return shader;
		
	}
	
}
