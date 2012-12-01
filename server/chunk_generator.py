import chunk, perlin, triangle, vector

class ChunkGenerator(object):
    def __init__(self, resolution = 10, size = 1):
        self.size = size
        self.resolution = resolution

    def generate_chunk(self, x, z):
        p = perlin.SimplexNoise()

        # generate the initial heightmap
        vertices = []
        for i in range(self.resolution):
            arr = []
            for j in range(self.resolution):
                xval = (x + i) * (float(self.size) / self.resolution)
                zval = (z + j) * (float(self.size) / self.resolution)
                h = p.noise2(xval, zval)*.1 + p.noise2((xval+5)*3, (zval+10)*3)*.2
                arr.append(vector.Vec3(xval, h, zval))
            vertices.append(arr)
        
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
            triangles.append(triangle.Triangle(v1, v2, v3))
            
            v1 = vertices[x][z+1]
            v2 = vertices[x+1][z+1]
            v3 = vertices[x+1][z]
            triangles.append(triangle.Triangle(v3, v2, v1))

        return chunk.Chunk(triangles, x=x, y=z) 



      
