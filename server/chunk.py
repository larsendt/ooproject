import struct
import vector

class Chunk(object):
    def __init__(self, triangles, x, y, texture_filename = ""):
        self.triangles = triangles
        self.texture_data = []
        self.x = x
        self.y = y

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
