import os, json, hashlib


def get_hash(item):
    return str(hex(hash(json.dumps(item)))).replace("-", "+")

def get_json_key(item):
    return str(type(item)) + str(item)

class Cache(object):
    """ On-disk key-value store """

    def __init__(self, directory, cache_name):
        self.directory = directory
        self.name = cache_name

        if not os.path.exists(self.directory):
            os.mkdir(self.directory)

    def __contains__(self, key):
        k = get_hash(key)
        fname = os.path.join(self.directory, k)

        if os.path.exists(fname):
            f = open(fname, "r")
            obj = json.loads(f.read())
            f.close()
            return get_json_key(key) in obj
        else:
            return False

    def __getitem__(self, key):
        k = get_hash(key)
        fname = os.path.join(self.directory, k)
        
        if os.path.exists(fname):
            f = open(fname, "r")
            obj = json.loads(f.read())
            f.close()
            
            # there probably will only ever be one chunk per file,
            # but do this incase of collisions (the price of hashing)
            return obj[get_json_key(key)]
        else:
            raise KeyError("Cache did not have value for key '%s'", key)

    def __setitem__(self, key, value):
        k = get_hash(key)
        fname = os.path.join(self.directory, k)

        obj = {}
        
        # deal with possible collision
        if os.path.exists(fname):
            f = open(fname, "r")
            obj = json.loads(f.read())
            f.close()

        obj[get_json_key(key)] = value

        f = open(fname, "w")
        f.write(json.dumps(obj))
        f.close()

        
