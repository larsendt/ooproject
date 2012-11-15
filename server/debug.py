import config

class Debug(object):
    def __init__(self):
        conf = config.Config("terrain_server.conf")

        try:
            self.on = conf.get("debug_switch")
        except KeyError:
            self.on = True
            conf.set("debug_switch", True)

    def debug_print(self, message):
        if self.on:
            print message
