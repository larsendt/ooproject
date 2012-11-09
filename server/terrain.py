import chunk_generator

class Terrain(object):
    def __init__(self, resolution, units_per_chunk):
        self.res = resolution
        self.upc = units_per_chunk
        self.cg = chunk_generator.ChunkGenerator(dimensions = self.res, size = self.upc)
        self.cache = {}

    def get_chunk(self, x, y):
        if (x, y) in self.cache:
            return self.cache[(x, y)]
        else:
            chunk = self.cg.generate_chunk(x, y)
            self.cache[(x, y)] = chunk
            return chunk
