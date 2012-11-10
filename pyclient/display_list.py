from OpenGL.GL import *
from OpenGL.raw import GL

class DisplayList(object):
    def __init__(self, data):
        self.disp_list = glGenLists(1)
        glNewList(self.disp_list, GL_COMPILE)

        glBegin(GL_TRIANGLES)
        for i in range(0, len(data), 6):
            v1 = data[i+0]
            v2 = data[i+1]
            v3 = data[i+2]
            n1 = data[i+3]
            n2 = data[i+4]
            n3 = data[i+5]

            glNormal3f(n1, n2, n3)
            glVertex3f(v1, v2, v3)
        glEnd()
        
        glEndList()


    def draw(self):
        glColor3f(0.3, 0.35, 0.3)
        glCallList(self.disp_list)
