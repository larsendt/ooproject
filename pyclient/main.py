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

def get_chunk_data(x, y, host, port):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.connect((host, port))
        
        print "connected to: %s:%d" % (host, port)
        print "requesting chunk: (%d, %d)" % (x, y)

        s.sendall("%d,%d" % (x, y))

        sstring = s.recv(4)
        expected_size = struct.unpack(">L", sstring)[0]
        start = time.time()
        print "expecting: %.1f KB" % (expected_size/1000.0)

        data = ""
        
        while 1:
            tmp = s.recv(16384)
            if not tmp: break
            data += tmp
            if len(data) >= expected_size: break

        print "final size: %.1f KB" % (len(data)/1000.0)

        if len(data) != expected_size:
            print "WARNING: size mismatch! expected %d bytes, got %d bytes" % (expected_size, len(data))

        difftime = time.time() - start
        print "total time: %.1f seconds" % difftime
        print "average bandwidth: %.1f KB/s" % (len(data)/difftime/1000)
        s.close()

        return data

class GLWrapper(object):
    def __init__(self, hostname, port):
        #glutInit(len(sys.argv), sys.argv)
        glutInit(1, sys.argv[0])
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
        self.light_rotation = 0

        data = get_chunk_data(0, 0, hostname, port)
        vertex_data = struct.unpack(">%df" % (len(data)/4), data)
        #self.vbo = vertex_buffer.VertexBuffer(vertex_data)
        self.disp_list00 = display_list.DisplayList(vertex_data)

        glEnable(GL_LIGHTING)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_COLOR_MATERIAL)
        glEnable(GL_NORMALIZE)
        glEnable(GL_BLEND)

    def begin(self):
        glutMainLoop()

    def idle(self):
        if (time.clock() - self.time) > self.idle_tick:
            self.time = time.clock()
            self.frames_drawn += 1
            self.light_rotation += 0.5
            if time.clock() - self.second_timer > 1:
                glutSetWindowTitle("Streaming Terrain Viewer : %d FPS" % self.frames_drawn)
                self.second_timer = time.clock()
                self.frames_drawn = 0
            glutPostRedisplay();
    
    def draw(self):
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)
        glLoadIdentity()

        pos = [3, 1, 3, 1]
        ambient = [0.5, 0.5, 0.5, 1.0]
        diffuse = [1.0, 1.0, 1.0, 1.0]
        specular = [0.3, 0.3, 0.3, 1.0]
        
        glRotatef(self.xrotation, 1, 0, 0)
        glRotatef(self.yrotation, 0, 1, 0)

        glPushMatrix()
        glRotatef(self.light_rotation, 0, 1, 0)
        glEnable(GL_LIGHT0)
        glLightfv(GL_LIGHT0, GL_POSITION, pos)
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambient)
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse)
        glLightfv(GL_LIGHT0, GL_SPECULAR, specular)
        glPopMatrix()

        glTranslatef(-0.4, 0.0, -0.4)
        self.disp_list00.draw()
        
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
            print "quitting"
            sys.exit(0)
        elif key == 'f':
            self.fullscreen = not self.fullscreen
            if self.fullscreen:
                glutFullScreen()
            else:
                glutReshapeWindow(self.scr_width, self.scr_height)
            
    
def main(argv):
    print "Initializing OpenGL..."
    if (len(argv) == 2):
        arg_hostname = argv[1]
        print "Hostname argument: " + arg_hostname
    else:
        arg_hostname = "larsendt.com"
        print "No hostname argument provided - using " + arg_hostname
    
    gl_wrapper = GLWrapper(arg_hostname, 5000)
    gl_wrapper.begin()
        
    
if __name__ == "__main__":
    main(sys.argv)
