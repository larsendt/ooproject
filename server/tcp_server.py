import asyncore
import socket

class TCPServer(asyncore.dispatcher):
    def __init__(self, host, port, handler):
        asyncore.dispatcher.__init__(self)

        self.host = host
        self.port = port
        self.handler = handler 

        self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        self.set_reuse_addr()
        self.bind((host, port))

        print "listen on:", host, port
        self.listen(5)

    def handle_accept(self):
        pair = self.accept()
        if pair is None:
            pass
        else:
            sock, addr = pair
            print 'Incoming connection from %s' % repr(addr)
            handler = self.handler(sock)
