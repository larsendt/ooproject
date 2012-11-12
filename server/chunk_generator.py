import chunk, perlin, triangle, vector

class ChunkGenerator(object):
    def __init__(self, dimensions = 10, size = 1):
        self.size = size
        self.dimensions = dimensions

    def generate_chunk(self, x, z):
        p = perlin.SimplexNoise()

        # generate the initial heightmap
        vertices = []
        for i in range(self.dimensions):
            arr = []
            for j in range(self.dimensions):
                xval = (x + i) * (float(self.size) / self.dimensions)
                zval = (z + j) * (float(self.size) / self.dimensions)
                h = p.noise2(xval, zval)
                arr.append(vector.Vec3(xval, h, zval))
            vertices.append(arr)
        
        # split the heightmap into triangles
        triangles = []
        tricount = ((self.dimensions - 1) ** 2) * 2

        for i in range(tricount):
            x = i % self.dimensions
            z = i / self.dimensions

            if (x >= (self.dimensions-1)) or (z >= (self.dimensions-1)):
                continue

            v1 = vertices[x][z]
            v2 = vertices[x+1][z]
            v3 = vertices[x][z+1]
            triangles.append(triangle.Triangle(v1, v2, v3))
            
            v1 = vertices[x][z+1]
            v2 = vertices[x+1][z+1]
            v3 = vertices[x+1][z]
            triangles.append(triangle.Triangle(v1, v2, v3))

        return chunk.Chunk(triangles, x=x, y=y) 



      
