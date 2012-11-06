import struct

class Chunk(object):
    def __init__(self, vertex_data = None, texture_filename = ""):
        self.vertex_data = []
        self.normal_data = []
        self.texture_data = []
        self.serialized_data = None

        if vertex_data:
            self.set_vertex_data(vertex_data)

        if texture_filename:
            self.load_texture(texture_filename)

    def set_vertex_data(self, vertices):
        """Set the vertex data
        
        Vertices are assumed to be in triplets, defined in counter-clockwise order as
        viewed from the "outside" of the terrain

        Calculates the normals with the cross-product method. 
        """

        self.vertex_data = vertices

        for i in range(0, len(self.vertex_data), 3):
            a = self.vertex_data[i]
            b = self.vertex_data[i+1]

            # calculate the face normal using the cross product
            nx = (a[1] * b[2]) - (b[1] * a[2])
            ny = (b[0] * a[2]) - (a[0] * b[2])
            nz = (a[0] * b[1]) - (b[0] * a[1])

            # normals are per vertex, so store 3
            self.normal_data.append((nx, ny, nz))
            self.normal_data.append((nx, ny, nz))
            self.normal_data.append((nx, ny, nz))

        # serialize the data
        tmp_serial = []
        for i in range(len(self.vertex_data)):
            v = self.vertex_data[i]
            n = self.normal_data[i]           

            tmp_serial += v
            tmp_serial += n

        # serial data is an array of network-order
        # floating point numbers
        self.serialized_data = struct.pack("!f" * len(tmp_serial), *tmp_serial)

    def load_texture(self, filename):
        """Currently a stub"""
        pass

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
