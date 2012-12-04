import chunk
import perlin
import triangle
import vector
import config

DELTA = 0.0001

class VertexGenerator(object):
    def __init__(self):
        self.conf = config.Config("terrain_server.conf")
        self.octaves = self.conf.get("perlin_octaves", 3, True)
        self.height = self.conf.get("perlin_height", 0.25, True)
        self.delta = self.conf.get("perlin_normal_delta", 1e-6, True)
        self.p = perlin.SimplexNoise()

    def vert_and_norm(self, x, z):
        h = self.noise(x, z, self.octaves)
        hdx = self.noise(x+self.delta, z, self.octaves)
        hdz = self.noise(x, z+self.delta, self.octaves)

        v1 = vector.Vec3(x, h, z)
        v2 = vector.Vec3(x+self.delta, hdx, z)
        v3 = vector.Vec3(x, hdz, z+self.delta)

        v = v2-v1
        u = v3-v2

        n = vector.Vec3(0, 0, 0)
        n.x = u.y*v.z - u.z*v.y
        n.y = u.z*v.x - u.x*v.z
        n.z = u.x*v.y - u.y*v.x

        return v1, n

    def noise(self, x, y, octaves):
        v = 0
        for i in range(octaves):
            v += self.p.noise2(x * (2 ** i), y * (2 ** i)) / (2 ** i)
        return v*self.height


class ChunkGenerator(object):
    def __init__(self):
        self.conf = config.Config("terrain_server.conf")
        self.size = self.conf.get("chunk_size", 1, True)
        self.resolution = self.conf.get("chunk_resolution", 50, True)
        self.vx_gen = VertexGenerator() 

    def chunk_size(self):
        return self.size

    def chunk_res(self):
        return self.resolution

    def cache_attrs(self):
        return self.size, self.resolution, self.vx_gen.octaves, self.vx_gen.height, self.vx_gen.delta

    def generate_chunk(self, x, z):
        vertices = []
        normals = []
        for i in range(self.resolution):
            varr = []
            narr = []
            for j in range(self.resolution):
                xval = (x + i) * (float(self.size) / self.resolution)
                zval = (z + j) * (float(self.size) / self.resolution)
                vert, norm = self.vx_gen.vert_and_norm(xval, zval)
                varr.append(vert)
                narr.append(norm)

            vertices.append(varr)
            normals.append(narr)
        
        # split the heightmap into triangles
        triangles = []
        tricount = ((self.resolution - 1) ** 2) * 2

        for i in range(tricount):
            x = i % self.resolution
            z = i / self.resolution

            if (x >= (self.resolution-1)) or (z >= (self.resolution-1)):
                continue

            v1 = vertices[x][z]
            v2 = vertices[x+1][z]
            v3 = vertices[x][z+1]
            t = triangle.Triangle(v1, v2, v3)
            t.normals[0] = normals[x][z]
            t.normals[1] = normals[x+1][z]
            t.normals[2] = normals[x][z+1]
            triangles.append(t)
            
            v1 = vertices[x][z+1]
            v2 = vertices[x+1][z+1]
            v3 = vertices[x+1][z]
            t = triangle.Triangle(v1, v2, v3)
            t.normals[0] = normals[x][z+1]
            t.normals[1] = normals[x+1][z+1]
            t.normals[2] = normals[x+1][z]
            triangles.append(t)

        return chunk.Chunk(triangles, x=x, y=z) 



      
