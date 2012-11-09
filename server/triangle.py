import vector

class Triangle(object):
    def __init__(self, v1, v2, v3):
        self.v1 = v1
        self.v2 = v2
        self.v3 = v3

        self.facenormal = v1.cross(v2)
        self.vertices = [v1, v2, v3]
        self.normals = [self.facenormal, self.facenormal, self.facenormal]
