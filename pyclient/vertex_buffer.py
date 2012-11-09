from OpenGL.GL import *
from OpenGL.raw import GL
import numpy

class VertexBuffer(object):
    def __init__(self, data):
        self.array = numpy.array(data, dtype='float32')
        self.array_size = len(self.array)
        self.buffer = glGenBuffers(1) 
        glBindBuffer(GL_ARRAY_BUFFER, self.buffer)
        glBufferData(GL_ARRAY_BUFFER, self.array_size, self.array, GL_STATIC_DRAW)

    def __del__(self):
        glDeleteBuffers(1, GL.GLuint(self.buffer))

    def draw(self):
        glBindBuffer(GL_ARRAY_BUFFER_ARB, self.buffer)

        glEnableClientState(GL_VERTEX_ARRAY)
        glVertexPointer(3, GL_FLOAT, 6*4, 0)

        glEnableClientState(GL_NORMAL_ARRAY)
        glNormalPointer(GL_FLOAT, 6*4, 3*4)

        glDrawArrays(GL_TRIANGLES, 0, self.array_size/6)

        glDisableClientState(GL_VERTEX_ARRAY)
        glDisableClientState(GL_NORMAL_ARRAY)
