import chunk
import perlin
import triangle
import vector
import config

DELTA = 0.0001

class VertexGenerator(object):
    def __init__(self, start_octave, end_octave, octave_step, base_height, height_range):
        self.conf = config.Config("terrain_server.conf")
        self.delta = self.conf.get("perlin_normal_delta", 1e-6, True)
        self.start_octave = start_octave
        self.end_octave = end_octave
        self.octave_step = octave_step
        self.base_height = base_height
        self.height_range = height_range
        self.p = perlin.SimplexNoise()

    def vert_and_norm(self, x, z, xoff, zoff):
        h = self.noise(x+xoff, z+zoff, self.start_octave, self.end_octave, self.octave_step)
        hdx = self.noise(x+xoff+self.delta, z+zoff, self.start_octave, self.end_octave, self.octave_step)
        hdz = self.noise(x+xoff, z+zoff+self.delta, self.start_octave, self.end_octave, self.octave_step)

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

    def noise(self, x, y, start_octave, end_octave, octave_step):
        v = 0
        for i in range(start_octave, end_octave, octave_step):
            mul = 0.5 * i
            v += self.p.noise2(x * (2 ** mul), y * (2 ** mul)) / (2 ** mul)
        return (v * self.height_range) + (self.base_height + 1.0)

class ChunkGenerator(object):
    def __init__(self):
        self.conf = config.Config("terrain_server.conf")
        self.size = self.conf.get("chunk_size", 1, True)
        self.resolution = self.conf.get("chunk_resolution", 50, True)
        self.mountains_params = self.conf.get("mountains", {"octave_start":0, "octave_end":6, "octave_step":1, "base_height":-0.5, "height_range":0.3}, True)
        self.plains_params = self.conf.get("plains", {"octave_start":-2, "octave_end":0, "octave_step":1, "base_height":-1.45, "height_range":0.1}, True)
        self.desert_params = self.conf.get("desert", {"octave_start":0, "octave_end":2, "octave_step":1, "base_height":-1.75, "height_range":0.1}, True)
        self.oceans_params = self.conf.get("oceans", {"octave_start":0, "octave_end":4, "octave_step":1, "base_height":-2.0, "height_range":0.1}, True)

        self.vx_gen = VertexGenerator(self.mountains_params["octave_start"], 
                self.mountains_params["octave_end"], 
                self.mountains_params["octave_step"], 
                self.mountains_params["base_height"],
                self.mountains_params["height_range"]) 

    def chunk_size(self):
        return self.size

    def set_terrain_type(self, terrain_type):
        if terrain_type == "mountains":
            params = self.mountains_params
        elif terrain_type == "plains":
            params = self.plains_params
        elif terrain_type == "oceans":
            params = self.oceans_params
        elif terrain_type == "desert":
            params = self.desert_params
        else:
            params = self.mountains_params

        self.vx_gen.start_octave = params["octave_start"]
        self.vx_gen.end_octave = params["octave_end"]
        self.vx_gen.octave_step = params["octave_step"]
        self.vx_gen.base_height = params["base_height"]
        self.vx_gen.height_range = params["height_range"]

    def chunk_res(self):
        return self.resolution

    def cache_attrs(self):
        return self.size, self.resolution, self.vx_gen.start_octave, self.vx_gen.end_octave, self.vx_gen.base_height, self.vx_gen.height_range, self.vx_gen.delta

    def generate_chunk(self, x, z):
        vertices = []
        normals = []
        for i in range(self.resolution):
            varr = []
            narr = []
            for j in range(self.resolution):
                xval = (x + i) * (float(self.size) / (self.resolution-2))
                zval = (z + j) * (float(self.size) / (self.resolution-2))
                vert, norm = self.vx_gen.vert_and_norm(xval, zval, x, z)
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



      
