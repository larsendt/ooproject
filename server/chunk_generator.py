import chunk, perlin

class ChunkGenerator(object):
    def __init__(self, dimensions = (10, 10), size = 1):
        self.size = size
        self.dimensions = dimensions

    def generateChunk(self, x, y):
        p = perlin.SimplexNoise()

        # generate the initial heightmap
        vertices = []
        for i in range(self.dimensions):
            arr = []
            for j in range(self.dimensions):
                xval = (x + i) / float(self.size)
                yval = (y + j) / float(self.size)
                h = p.noise2(xval, yval)
                arr.append([xval, yval, h])
            vertices.append(arr)
        
        # split the heightmap into triangles
        triangles = []
        tricount = ((self.dimensions - 1) ** 2) * 2

        for i in range(tricount):
            i = (tricount/2) % (self.dimensions-1)
            j = (tricount/2) / (self.dimensions-1)
          
            if tricount % 2 == 0:
                v1 = vertices[i][j]
                v2 = vertices[i+1][j]
                v3 = vertices[i][j+1]
            else:
                v1 = vertices[i+1][j]
                v2 = vertices[i+1][j+1]
                v3 = vertices[i][j+1]

            triangles += [v1, v2, v3]

       return Chunk(triangles) 



      
