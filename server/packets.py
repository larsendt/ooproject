import struct
import base64
import json

class Packet(object):
    def __init__(self, data):
        self.msg = data

    def data(self):
        return struct.pack(">L", len(self.msg)) + self.msg 

class ChunkPacket(Packet):
    def __init__(self, chunk):
        self.chunk = chunk

    def data(self):
        svd = base64.b64encode(self.chunk.serial_vertex_data())
        d = {
            "x":self.chunk.x,
            "y":self.chunk.y,
            "vertex_data":svd
        }
        js = json.dumps(d) 
        return struct.pack(">L", len(js)) + js

class ErrorPacket(Packet):
    def __init__(self, error_msg):
        self.msg = json.dumps({
            "type":"error",
            "message":error_msg,
            })

    def data(self):
        return struct.pack(">L", len(self.msg)) + self.msg

def dataFromRequest(request_data):
    d = json.loads(request_data)

    if d["type"] == "coords":
        if "x" in d and "y" in d:
            return int(d["x"]), int(d["y"])
        else:
            raise ValueError("Invalid coordinate request")
    else:
        raise ValueError("Unable to determine request type")
