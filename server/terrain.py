import chunk_generator
import filecache
import json
import base64
import zlib
import time

class Terrain(object):
    def __init__(self):
        self.cg = chunk_generator.ChunkGenerator()
        self.cache = filecache.Cache("terrain_cache")

    def get_response_for(self, x, y, compression, terrain_type):
        self.cg.set_terrain_type(terrain_type)
        cache_key = (x, y, compression) + self.cg.cache_attrs()
        if cache_key in self.cache:
            print "chunk %s was cached" % str(cache_key)
            return self.cache[cache_key]
        else:
            print "generating new chunk for %s" % str(cache_key)

            start = time.time()
            chunk = self.cg.generate_chunk(x, y)

            vertex_data = chunk.serial_vertex_data()
            vertex_data_size = len(vertex_data)

            if compression:
                vertex_data = zlib.compress(vertex_data)

            encoded_data = base64.b64encode(vertex_data)
            jsonstr = json.dumps({"type":"chunk", 
                                  "x":x, "y":y, 
                                  "chunk_resolution": self.cg.chunk_res(),
                                  "chunk_size": self.cg.chunk_size(), 
                                  "vertex_data":encoded_data,
                                  "compression":compression,
                                  "inflated_size":vertex_data_size})
            self.cache[cache_key] = jsonstr

            end = time.time()
            print "chunk complete (%.1f seconds)" % (end-start)
            return jsonstr
