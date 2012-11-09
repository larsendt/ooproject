class Packet(object):
    def __init__(self, data):
        self.internal_data = data

    def data(self):
        return self.internal_data


class ChunkPacket(Packet):
    def __init__(self, chunk):
        self.chunk = chunk

    def data(self):
        return self.chunk.serial_vertex_data()

class ErrorPacket(Packet):
    def __init__(self, error_msg):
        self.msg = error_msg

    def data(self):
        return self.msg


