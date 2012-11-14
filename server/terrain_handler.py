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
        print "handle_read"
        request = ""
        try:
            expected_size = int(self.recv(12))
            print "got packet with expected_size:", expected_size
        except:
            self.sendall(packets.ErrorPacket("Unrecognized request").data())
            return

        print "receiving data"
        while 1:
            tmp = self.recv(8192)
            if not tmp: break
            request += tmp
            if len(request) >= expected_size: break

        print "got request:", request
        try:
            x, y = self.parse_request(request)
        except ValueError as e:
            response = packets.ErrorPacket(repr(e))
        else:
            response = packets.ChunkPacket(self.terrain.get_chunk(x, y))

        print "sending response (%d bytes)" % len(response.data())
        self.sendall(response.data())
        print "done sending"

    def parse_request(self, request):
        return packets.dataFromRequest(request)
        

