#!/usr/bin/env python
import web
import config
import debug
import terrain
import json
import base64

urls = (
        "/(.*)", "terrain_data",
)

class terrain_data(object):
	def __init__(self):
		self.conf = config.Config("terrain_server.conf")
		self.d = debug.Debug()

		self.chunk_res = self.conf.get("chunk_resolution", default_value=50, save_default=True)
		self.chunk_size = self.conf.get("chunk_size", default_value=1, save_default=True)
		self.terrain = terrain.Terrain(self.chunk_res, self.chunk_size)

	def GET(self, coords):
		params = web.input()
		
		try:
			x = int(params.x)
			y = int(params.y)
		except AttributeError:
			return json.dumps({"type":"error", "error":"invalid coordinates given"})

		chunk = self.terrain.get_chunk(x, y)
		vertex_data = chunk.serial_vertex_data()
		encoded_data = base64.b64encode(vertex_data)
		return json.dumps({"type":"chunk", "x":x, "y":y, "vertex_data":encoded_data})

app = web.application(urls, globals())

if __name__ == "__main__":
    app.run()
