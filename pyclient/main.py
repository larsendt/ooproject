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
import urllib2
import vertex_buffer
import display_list
import json
import base64
import struct



def get_chunk_data(x, y, host, port):
        print "requesting chunk (%d, %d) from host %s:%d" % (x, y, host, port)

        start = time.time()
       
        f = urllib2.urlopen("http://%s:%d/?x=%d&y=%d" % (host, port, x, y))
        data = f.read()
        f.close()

        difftime = time.time() - start
        print "total time: %.1f seconds" % difftime
        print "average bandwidth: %.1f KB/s" % (len(data)/difftime/1000)

        obj = json.loads(data)

        if obj["type"] == "error":
            raise ValueError("Server error: " + obj["error"])
        else:
            return base64.b64decode(obj["vertex_data"])

class GLWrapper(object):
    def __init__(self, hostname, port, x, y):
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

        data = get_chunk_data(x, y, hostname, port)
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
    arg_hostname = "larsendt.com"
    arg_port = 1234
    print "Initializing OpenGL..."
    if (len(argv) == 5):
        arg_hostname = argv[1]
        arg_port = int(argv[2])
        arg_x = int(argv[3])
        arg_y = int(argv[4])
        print "Host: %s:%d" % (arg_hostname, arg_port)
    else:
        print "Usage: %s <hostname> <port> <x> <y>" % argv[0]
        return
    
    gl_wrapper = GLWrapper(arg_hostname, arg_port, arg_x, arg_y)
    gl_wrapper.begin()
        
    
if __name__ == "__main__":
    main(sys.argv)
