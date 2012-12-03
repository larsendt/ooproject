import struct
import vector

class Chunk(object):
    def __init__(self, triangles, x, y, texture_filename = ""):
        self.triangles = triangles
        self.texture_data = []
        self.x = x
        self.y = y

        vertex_set = {}
        for t in triangles:
            if t.v1 in vertex_set:
                vertex_set[t.v1].append(t.normals[0])
            else:
                vertex_set[t.v1] = [t.normals[0]]

        for vert, norms in vertex_set.items():
            smooth_norm = vector.Vec3(0, 0, 0)
            for n in norms:
                smooth_norm += n

            smooth_norm = smooth_norm.normalized()

            for index, t in enumerate(triangles):
                if t.v1 == vert:
                    t.normals[0] = smooth_norm
                    triangles[index] = t
                elif t.v2 == vert:
                    t.normals[1] = smooth_norm
                    triangles[index] = t
                elif t.v3 == vert:
                    t.normals[2] = smooth_norm
                    triangles[index] = t

        tmp_serial = []

        for t in triangles:
            tmp_serial += t.serial_data()

        self.serialized_data = struct.pack(">%sf" % len(tmp_serial), *tmp_serial)

    def serial_vertex_data(self):
        """Return the serialized, network-ready data
        Format: [vx,vy,vz,nx,ny,nz,...]
        v[x|y|z] and n[x|y|z] are network-byte-ordered 32-bit floats
        """
        return self.serialized_data

    def serial_texture_data(self):
        """Return the texture data for the chunk
        Format: RGBA, endian-agnostic
        """
        return self.texture_data 
