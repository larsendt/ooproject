#!/usr/bin/env python2

from terrain_handler import TerrainHandler
from tcp_server import TCPServer
import asyncore
import config

conf = config.Config("terrain_server.conf")

try:
    listen_host = conf["listen_host"]
except KeyError:
    listen_host = "0.0.0.0"
    conf["listen_host"] = listen_host

try:
    listen_port = conf["listen_port"]
except KeyError:
    listen_port = 5000
    conf["listen_port"] = listen_port


serv = TCPServer(listen_host, listen_port, TerrainHandler)
asyncore.loop()

