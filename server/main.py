#!/usr/bin/env python2

from terrain_handler import TerrainHandler
from tcp_server import TCPServer
import asyncore

serv = TCPServer("localhost", 5000, TerrainHandler)
asyncore.loop()

