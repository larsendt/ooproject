import chunk, perlin, triangle, vector

class ChunkGenerator(object):
    def __init__(self, dimensions = 10, size = 1):
        self.size = size
        self.dimensions = dimensions

    def generate_chunk(self, x, y):
        p = perlin.SimplexNoise()

        # generate the initial heightmap
        vertices = []
        for i in range(self.dimensions):
            arr = []
            for j in range(self.dimensions):
                xval = (x + i) * (float(self.size) / self.dimensions)
                yval = (y + j) * (float(self.size) / self.dimensions)
                h = p.noise2(xval, yval)
                arr.append(vector.Vec3(xval, yval, h))
            vertices.append(arr)
        
        # split the heightmap into triangles
        triangles = []
        tricount = ((self.dimensions - 1) ** 2) * 2

        for i in range(tricount):
            x = i % self.dimensions
            y = i / self.dimensions

            if (x >= (self.dimensions-1)) or (y >= (self.dimensions-1)):
                continue

            if tricount % 2 == 0:
                v1 = vertices[x][y]
                v2 = vertices[x+1][y]
                v3 = vertices[x][y+1]
            else:
                v1 = vertices[x+1][y]
                v2 = vertices[x+1][y+1]
                v3 = vertices[x][y+1]

            triangles.append(triangle.Triangle(v1, v2, v3))

        return chunk.Chunk(triangles) 



      
