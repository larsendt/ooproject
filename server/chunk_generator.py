import chunk, perlin, triangle, vector

DELTA = 0.0001

def noise(p, x, y, octaves):
    v = 0
    for i in range(octaves):
        v += p.noise2(x * (2 ** i), y * (2 ** i)) / (2 * (i + 1))

    return v*0.5

class ChunkGenerator(object):
    def __init__(self, resolution = 10, size = 1, octaves = 2):
        self.size = size
        self.resolution = resolution
        self.perlin_octaves = octaves

    def generate_chunk(self, x, z):
        p = perlin.SimplexNoise()

        # generate the initial heightmap
        vertices = []
        normals = []
        for i in range(self.resolution):
            varr = []
            narr = []
            for j in range(self.resolution):
                xval = (x + i) * (float(self.size) / self.resolution)
                zval = (z + j) * (float(self.size) / self.resolution)
                h = noise(p, xval, zval, self.perlin_octaves)
                hdx = noise(p, xval+DELTA, zval, self.perlin_octaves)
                hdz = noise(p, xval, zval+DELTA, self.perlin_octaves)
    
                v1 = vector.Vec3(xval, h, zval)
                v2 = vector.Vec3(xval+DELTA, hdx, zval)
                v3 = vector.Vec3(xval, hdz, zval+DELTA)

                v = v2-v1
                u = v3-v2

                n = vector.Vec3(0, 0, 0)
                n.x = u.y*v.z - u.z*v.y
                n.y = u.z*v.x - u.x*v.z
                n.z = u.x*v.y - u.y*v.x

                varr.append(vector.Vec3(xval, h, zval))
                narr.append(n)

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



      
