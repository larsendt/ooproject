import chunk, perlin, triangle, vector

simplex = perlin.SimplexNoise()

def worldGen(x,y):
    tval = simplex.noise2((x-20)*.05 ,(y+50)*.05)
    rval = simplex.noise2(y*.05,x*.05)


    hval = simplex.noise2((y+50)*.1, (x+10)*.1)

    nval = simplex.noise2(x*.1, y*.1)

    val = tval*.6+rval*.1+hval*.3 + nval
    
    return val

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
                h = worldGen(x,z)
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
            triangles.append(triangle.Triangle(v3, v2, v1))

        return chunk.Chunk(triangles, x=x, y=z) 



      
