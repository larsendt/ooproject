import asyncore
import terrain
import packets
import config
import struct

class TerrainHandler(asyncore.dispatcher_with_send):
    def __init__(self, socket):
        asyncore.dispatcher_with_send.__init__(self, socket)

        self.conf = config.Config("terrain_server.conf")

        try:
            chunk_res = self.conf["chunk_res"]
        except KeyError:
            chunk_res = 50
            self.conf["chunk_res"] = chunk_res

        try:
            chunk_size = self.conf["chunk_size"]
        except KeyError:
            chunk_size = 0.8
            self.conf["chunk_size"] = chunk_size

        self.terrain = terrain.Terrain(chunk_res, chunk_size)

    def handle_read(self):
        request = ""
        try:
            expected_size = struct.unpack(">L", self.recv(4))[0]
        except:
            self.send(packets.ErrorPacket("Unrecognized request").data())
            return

        while 1:
            tmp = self.recv(8192)
            if not tmp: break
            request += tmp
            if len(request) >= expected_size: break

        try:
            x, y = self.parse_request(request)
        except ValueError as e:
            response = packets.ErrorPacket(repr(e))
        else:
            response = packets.ChunkPacket(self.terrain.get_chunk(x, y))

        self.send(response.data())

    def parse_request(self, request):
        return packets.dataFromRequest(request)
        

