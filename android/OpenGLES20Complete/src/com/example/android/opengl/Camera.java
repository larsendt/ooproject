package com.example.android.opengl;

import android.util.Log;

public class Camera {
    private float m_height;
    private float m_x;
    private float m_y;

	public Camera() {
		m_height = 1.0f;
	}

    public void doTransformation() {
        GLState.setMVIdentity();
        GLState.pushMVMatrix();
        GLState.translate(0.0f, 0.0f, -m_height);
        GLState.translate(m_x, -m_y, 0.0f);
        GLState.rotate(75, 1.0f, 0.0f, 0.0f);
    }

    public void setHeight(float height) {
        m_height = height;
    }

    public void move(float dx, float dy) {
        m_x += dx * Math.abs(m_height * 0.5);
        m_y += dy * Math.abs(m_height * 0.5);
    }
}
