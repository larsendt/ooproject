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
            d = json.loads(f.read())
        except IOError:
            d = {}
        except ValueError:
            d = {}
        else:
            f.close()

        d[key] = value
        f = open(self.filename, "w")
        f.write(json.dumps(d, indent=4))
        f.close()
        print "config set:", key, value

    def get(self, key, default_value=None, save_default=False):
        try:
            f = open(self.filename, "r")
        except IOError:
            f = open(self.filename, "w")
            f.write("")
            f.close()
            f = open(self.filename, "r")

        try:
            d = json.loads(f.read())
        except ValueError:
            if default_value is None:
                raise ValueError("Bad JSON file '%s'" % self.filename)
            elif save_default:
                self.set(key, default_value)
            return default_value

        f.close()

        try:
            value = d[key]
        except KeyError:
            if default_value is None:
                raise ValueError("Key '%s' was not in '%s'" % (key, self.filename))
            elif save_default:
                self.set(key, default_value)
            return default_value

        return value


        
