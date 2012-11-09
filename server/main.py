#!/usr/bin/env python2

from echo_handler import EchoHandler
from tcp_server import TCPServer
import asyncore

serv = TCPServer("localhost", 5000, EchoHandler)
asyncore.loop()

