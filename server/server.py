#!/usr/bin/env python
import web
import config
import debug
import terrain
import json

urls = (
        "/", "terrain_server",
)

class terrain_server(object):
    def __init__(self):
        self.d = debug.Debug()
        self.terrain = terrain.Terrain()

    def GET(self):
        params = web.input()

        try:
            x = int(params.x)
            z = int(params.z)
        except AttributeError:
            return json.dumps({"type":"error", "error":"invalid coordinates given"})

        try:
            terrain_type = params.terrain_type
        except:
            print "no terrain type specified, defaulting to mountains"
            terrain_type = "mountains"

        print terrain_type

        try:
            compression = True if params.compression == "yes" else False
        except:
            compression = False

        return self.terrain.get_response_for(x, z, compression, terrain_type)

app = web.application(urls, globals())

if __name__ == "__main__":
    app.run()
