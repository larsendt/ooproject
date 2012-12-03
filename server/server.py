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
            y = int(params.y)
        except AttributeError:
            return json.dumps({"type":"error", "error":"invalid coordinates given"})

        try:
            compression = True if params.compression == "yes" else False
        except AttributeError:
            compression = False

        return self.terrain.get_response_for(x, y, compression)

app = web.application(urls, globals())

if __name__ == "__main__":
    app.run()
