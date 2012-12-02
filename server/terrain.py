import chunk_generator
import config
import filecache
import json
import base64

class Terrain(object):
    def __init__(self):
        self.conf = config.Config("terrain_server.conf")
        self.res = self.conf.get("chunk_resolution", default_value=50, save_default=True)
        self.size = self.conf.get("chunk_size", default_value=1, save_default=True)
        self.cache_size = self.conf.get("cache_size", default_value=1e9, save_default=True)
        self.cg = chunk_generator.ChunkGenerator(resolution = self.res, size = self.size)
        self.cache = filecache.Cache("terrain_cache", "terrain_server", self.cache_size)

    def get_response_for(self, x, y):
        if (x, y, self.res, self.size) in self.cache:
            print "chunk [x:%d, y:%d, res:%d, size:%d] was cached" % (x, y, self.res, self.size)
            return self.cache[(x, y, self.res, self.size)]
        else:
            print "generating new chunk for [x:%d, y:%d, res:%d, size:%d]" % (x, y, self.res, self.size)
            chunk = self.cg.generate_chunk(x, y)
            vertex_data = chunk.serial_vertex_data()
            encoded_data = base64.b64encode(vertex_data)
            jsonstr = json.dumps({"type":"chunk", 
                                  "x":x, "y":y, 
                                  "chunk_resolution": self.res,
                                  "chunk_size": self.size, 
                                  "vertex_data":encoded_data})
            self.cache[(x, y, self.res, self.size)] = jsonstr
            return jsonstr
