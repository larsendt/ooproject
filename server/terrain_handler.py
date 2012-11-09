import asyncore
import terrain
import packets

class TerrainHandler(asyncore.dispatcher_with_send):
    def __init__(self, socket):
        asyncore.dispatcher_with_send.__init__(self, socket)
        self.terrain = terrain.Terrain(100, 2)

    def handle_read(self):
        request = self.recv(8192)
        response_packet = None

        try:
            x, y = self.parse_request(request)
        except ValueError:
            response = packets.ErrorPacket("Invalid request")
        else:
            response = packets.ChunkPacket(self.terrain.get_chunk(x, y))

        self.send(response.data())

    def parse_request(self, request):
        l = request.split(",")

        if len(l) != 2:
            raise ValueError("Invalid request")

        try:
            coords = int(l[0]), int(l[1])
        except:
            raise ValueError("Unable to convert coords to integers")
        else:
            print "coords requested:", coords

        return coords

