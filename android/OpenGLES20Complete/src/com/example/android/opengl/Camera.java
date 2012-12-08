package com.example.android.opengl;

import android.util.Log;

public class Camera {
    private float m_height;
    public float m_x;
    public float m_y;
    private float m_xRotation;
    private float m_yRotation;
    private float m_xMomentum;
    private float m_yMomentum;
    private final float m_friction = 0.003f;

	public Camera() {
		m_height = 1.0f;
        m_xMomentum = 0.0f;
        m_yMomentum = 0.0f;
        m_xRotation = -90;
        m_yRotation = 0;
	}

    public void doTransformation() {
        GLState.setMVIdentity();
        GLState.pushMVMatrix();
        GLState.rotate(-m_xRotation, 1.0f, 0.0f, 0.0f);
        GLState.rotate(-m_yRotation, 0.0f, 1.0f, 0.0f);
        GLState.translate(-m_x, -m_height, -m_y);
    }

    public void setHeight(float height) {
        m_height = height;
    }

    public void move(float dx, float dy) {
        float sdx = dx * (float) Math.abs((m_height + 1.0) * 0.5);
        float sdy = dy * (float) Math.abs((m_height + 1.0) * 0.5);
        m_x += (sdx *  Math.cos(Math.toRadians(-m_yRotation)));
        m_y += (sdx *  Math.sin(Math.toRadians(-m_yRotation)));
        m_x += (sdy * -Math.sin(Math.toRadians(-m_yRotation)));
        m_y += (sdy *  Math.cos(Math.toRadians(-m_yRotation)));

        //m_xMomentum = clamp(dx, -0.5f, 0.5f);
        //m_yMomentum = clamp(dy, -0.5f, 0.5f);
    }

    public void rotate(float dx, float dy) {
        m_xRotation += dy * 50.0f;
        m_yRotation += dx * 50.0f;

        m_xRotation = clamp(m_xRotation, -90, 90);
    }

    public void update() {
        if(m_xMomentum > 0.0f) {
            m_xMomentum -= m_friction;
            m_xMomentum = Math.max(m_xMomentum, 0.0f);
            m_x += m_xMomentum;
        }
        else if(m_xMomentum < 0.0f) {
            m_xMomentum += m_friction;
            m_xMomentum = Math.min(m_xMomentum, 0.0f);
            m_x += m_xMomentum;
        }

        if(m_yMomentum > 0.0f) {
            m_yMomentum -= m_friction;
            m_yMomentum = Math.max(m_yMomentum, 0.0f);
            m_y += m_yMomentum;
        }
        else if(m_yMomentum < 0.0f) {
            m_yMomentum += m_friction;
            m_yMomentum = Math.min(m_yMomentum, 0.0f);
            m_y += m_yMomentum;
        }
    }

    private float clamp(float val, float min, float max) {
        if(val > max) {
            return max;
        }
        else if(val < min) {
            return min;
        }
        else
        {
            return val;
        }
    }
}
