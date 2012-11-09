#!/usr/bin/env python

import OpenGL
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *

import sys
import time
import math
import random
import os
import struct
import socket
import vertex_buffer
import display_list

class GLWrapper(object):
    def __init__(self):
        glutInit(len(sys.argv), sys.argv)
        glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE)
        glutInitWindowSize(800, 600)
        glutCreateWindow('Dynamic FBM Warping')
        #glutFullScreen()
        glutDisplayFunc(self.draw)
        glutMotionFunc(self.mouse_drag)
        glutKeyboardFunc(self.keyboard)
        glutMouseFunc(self.mouse_press)
        glutReshapeFunc(self.reshape)
        glutIdleFunc(self.idle)
        glutSpecialFunc(self.special)

        print glGetString(GL_VERSION)
        
        glClearColor(0.0, 0.0, 0.0, 1.0)
        
        self.time = time.clock()
        self.screen_width = 1.0
        self.fps = 120
        self.idle_tick = 1.0/self.fps
        self.frames_drawn = 0
        self.second_timer = 0
        self.fullscreen = False
        self.scr_width = 800
        self.scr_height = 600
        self.xrotation = 0
        self.yrotation = 0

        host = "localhost"
        port = 5000
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.connect((host, port))
        s.sendall("0,0")

        sstring = s.recv(4)
        size = struct.unpack(">L", sstring)[0]

        data = ""
        while 1:
            tmp = s.recv(1024)
            if not tmp: break
            data += tmp
            if len(data) >= size: break

        print "final length:", len(data)
        print "expected size:", size
        s.close()

        vertex_data = struct.unpack(">%df" % (len(data)/4), data)
        #self.vbo = vertex_buffer.VertexBuffer(vertex_data)
        self.disp_list = display_list.DisplayList(vertex_data)
       
        glEnable(GL_LIGHTING)
        glEnable(GL_DEPTH_TEST)

    def begin(self):
        glutMainLoop()

    def idle(self):
        if (time.clock() - self.time) > self.idle_tick:
            self.time = time.clock()
            self.frames_drawn += 1
            if time.clock() - self.second_timer > 1:
                glutSetWindowTitle("Streaming Terrain Viewer : %d FPS" % self.frames_drawn)
                self.second_timer = time.clock()
                self.frames_drawn = 0
            glutPostRedisplay();
    
    def draw(self):
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)
        glLoadIdentity()

        pos = [3, 3, 3, 1]
        ambient = [0.5, 0.5, 0.5, 1.0]
        diffuse = [0.5, 0.5, 0.5, 1.0]
        specular = [0.1, 0.1, 0.1, 1.0]

        glEnable(GL_LIGHT0)
        glLightfv(GL_LIGHT0, GL_POSITION, pos)
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambient)
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse)
        glLightfv(GL_LIGHT0, GL_SPECULAR, specular)

        glRotatef(self.xrotation, 1, 0, 0)
        glRotatef(self.yrotation, 0, 1, 0)

        self.disp_list.draw()

        glutSwapBuffers();
    
    def reshape(self, width, height):
        if height > 0:
            self.screen_width = float(width)/height
        else:
            self.screen_width = 1.0

        glViewport(0,0, width,height)
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(-self.screen_width, self.screen_width, -1.0, 1.0, -1.0, 1.0)
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        
    def mouse_drag(self, x, y):
        pass
            
    def mouse_press(self, button, state, x, y):
        pass

    def special(self, key, x, y):
        if key == GLUT_KEY_LEFT:
            self.yrotation -= 10
        elif key == GLUT_KEY_RIGHT:
            self.yrotation += 10
        elif key == GLUT_KEY_UP:
            self.xrotation -= 10
        elif key == GLUT_KEY_DOWN:
            self.xrotation += 10
           
    def keyboard(self, key, x, y):
        if key == '\x1b': #escape key
            print "Quit"
            sys.exit(0)
        elif key == 'f':
            self.fullscreen = not self.fullscreen
            if self.fullscreen:
                glutFullScreen()
            else:
                glutReshapeWindow(self.scr_width, self.scr_height)
            
    
def main():
    print "Initializing OpenGL..."
    try:
        gl_wrapper = GLWrapper()
        gl_wrapper.begin()
    except Exception as excep:
        print excep
        sys.exit(1)
        
    
if __name__ == "__main__":
    main()
