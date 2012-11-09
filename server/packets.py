import struct

class Packet(object):
    def __init__(self, data):
        self.msg = data

    def data(self):
        return struct.pack(">L", len(self.msg)) + self.msg 

class ChunkPacket(Packet):
    def __init__(self, chunk):
        self.chunk = chunk

    def data(self):
        svd = self.chunk.serial_vertex_data()
        s = struct.pack(">L", len(svd)) + svd
        print "chunk data: %d bytes" % len(s)
        return s

class ErrorPacket(Packet):
    def __init__(self, error_msg):
        self.msg = error_msg

    def data(self):
        return struct.pack(">L", len(self.msg)) + self.msg


