package com.example.android.opengl;


import android.content.Context;
import android.opengl.Matrix;

public class Light {
    private float[] m_pos;
    private float[] m_mvPos;
    private float m_time;
    private Shader m_shader;
    private VBO m_vbo;

    public Light(Context context) {
        m_pos = new float[4];
        m_pos[0] = 0.0f;
        m_pos[1] = 1.0f;
        m_pos[2] = 0.5f;
        m_pos[3] = 1.0f;

        m_mvPos = new float[4];

        m_shader = Shader.loadShaderFromResource(R.raw.plain_vs, R.raw.plain_fs, context);
        m_vbo = ShapeUtils.cube(m_shader.getProgram());
    }

    public void draw() {
        GLState.pushMVMatrix();
        GLState.translate(m_pos[0], m_pos[1], m_pos[2]);
        GLState.scale(.01f, .01f, .01f);
        Matrix.multiplyMV(m_mvPos, 0, GLState.mvMatrix, 0, m_pos, 0);
        m_shader.useProgram();
        m_shader.setMatrices(GLState.mvMatrix, GLState.pMatrix);
        m_vbo.draw();
        GLState.popMVMatrix();
    }

    public float[] getBasePos() {
        return m_pos;
    }

    public float[] getMVPos() {
        return m_mvPos;
    }

    public void update() {
        m_time += 0.01;
        m_pos[0] = (float)Math.sin(m_time)*3.0f;
        m_pos[1] = (float)Math.cos(m_time)*3.0f;
    }
}
