import chunk_generator
import config
import filecache
import json
import base64
import zlib
import time

class Terrain(object):
    def __init__(self):
        self.conf = config.Config("terrain_server.conf")
        self.res = self.conf.get("chunk_resolution", default_value=50, save_default=True)
        self.size = self.conf.get("chunk_size", default_value=1, save_default=True)
        self.octaves = self.conf.get("chunk_octaves", default_value=3, save_default=True)
        self.cache_size = self.conf.get("cache_size", default_value=1e9, save_default=True)
        self.cg = chunk_generator.ChunkGenerator(resolution = self.res, 
                size = self.size, octaves = self.octaves)
        self.cache = filecache.Cache("terrain_cache", "terrain_server", self.cache_size)

    def get_response_for(self, x, y, compression):
        if (x, y, self.res, self.size, compression, self.octaves) in self.cache:
            print "chunk [x:%d, y:%d, res:%d, size:%d, compression:%s, octaves:%d] was cached" % (x, y, self.res, self.size, compression, self.octaves)
            return self.cache[(x, y, self.res, self.size, compression, self.octaves)]
        else:
            print "generating new chunk for [x:%d, y:%d, res:%d, size:%d, compression:%s, octaves:%d]" % (x, y, self.res, self.size, compression, self.octaves)

            start = time.time()
            chunk = self.cg.generate_chunk(x, y)

            vertex_data = chunk.serial_vertex_data()

            if compression:
                vertex_data = zlib.compress(vertex_data)

            encoded_data = base64.b64encode(vertex_data)
            jsonstr = json.dumps({"type":"chunk", 
                                  "x":x, "y":y, 
                                  "chunk_resolution": self.res,
                                  "chunk_size": self.size, 
                                  "vertex_data":encoded_data,
                                  "compression":compression})
            self.cache[(x, y, self.res, self.size, compression)] = jsonstr

            end = time.time()
            print "chunk complete (%.1f seconds)" % (end-start)
            return jsonstr
