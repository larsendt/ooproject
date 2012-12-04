import config

class Debug(object):
    def __init__(self):
        conf = config.Config("terrain_server.conf")
        self.on = conf.get("config_switch", default_value=True, save_default=True)

    def debug_print(self, message):
        if self.on:
            print message
