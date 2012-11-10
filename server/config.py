import json

class Config(object):
    def __init__(self, filename):
        self.filename = filename

    def __setitem__(self, key, value):
        self.set(key, value)

    def __getitem__(self, key):
        return self.get(key)

    def set(self, key, value):
        try:
            f = open(self.filename, "r")
        except IOError:
            d = {}
        else:
            d = json.loads(f.read())
            f.close()

        d[key] = value
        f = open(self.filename, "w")
        f.write(json.dumps(d))
        f.close()
        print "set:", key, value

    def get(self, key):
        try:
            f = open(self.filename, "r")
        except IOError:
            raise KeyError("Config item '%s' was not found in '%s'" 
                    % (key, self.filename))

        d = json.loads(f.read())
        f.close()
        value = d[key]

        print "get:", key, value
        return value


        
