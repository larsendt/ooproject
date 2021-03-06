import vector

class Triangle(object):
    def __init__(self, v1, v2, v3):
        self.v1 = v1
        self.v2 = v2
        self.v3 = v3

        self.compute_normal()

        self.vertices = [v1, v2, v3]
        self.normals = [self.facenormal, self.facenormal, self.facenormal]

    def compute_normal(self):
        n = vector.Vec3(0,0,0)
        v = self.v2-self.v1
        u = self.v3-self.v2

        n.x = u.y*v.z - u.z*v.y
        n.y = u.z*v.x - u.x*v.z
        n.z = u.x*v.y - u.y*v.x
        self.facenormal = n


    def serial_data(self):
        return [self.v1.x, self.v1.y, self.v1.z,
                self.normals[0].x, self.normals[0].y, self.normals[0].z,
                self.v1.x, self.v1.z,
                self.v2.x, self.v2.y, self.v2.z,
                self.normals[1].x, self.normals[1].y, self.normals[1].z,
                self.v2.x, self.v2.z,
                self.v3.x, self.v3.y, self.v3.z,
                self.normals[2].x, self.normals[2].y, self.normals[2].z,
                self.v3.x, self.v3.z,]
