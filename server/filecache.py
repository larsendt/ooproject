import os
import json
import hashlib
import time
import util


def get_hash(item):
    return str(hex(hash(json.dumps(item)))).replace("-", "+")

def get_json_key(item):
    return str(type(item)) + str(item)

def get_size(start_path = '.'):
    total_size = 0
    for dirpath, dirnames, filenames in os.walk(start_path):
        for f in filenames:
            fp = os.path.join(dirpath, f)
            total_size += os.path.getsize(fp)
    return total_size


class Cache(object):
    """ On-disk key-value store """

    def __init__(self, directory, cache_name, max_cache_size = 1e9):
        self.directory = directory
        self.name = cache_name
        self.max_size = max_cache_size
        print "filecache max size: %s" % util.SI_byte_string(self.max_size)

        if not os.path.exists(self.directory):
            os.mkdir(self.directory)

        print "filecache current size: %s" % util.SI_byte_string(get_size(self.directory))

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
            
            # update the atime for the LRU
            obj[get_json_key(key)]["atime"] = time.time()
            return obj[get_json_key(key)]["value"]
        else:
            raise KeyError("Cache did not have value for key '%s'", key)

    def __setitem__(self, key, value):
        self.drop_lru()

        k = get_hash(key)
        fname = os.path.join(self.directory, k)

        obj = {}
        
        # deal with possible collision
        if os.path.exists(fname):
            f = open(fname, "r")
            obj = json.loads(f.read())
            f.close()

        # set the access time for LRU
        obj[get_json_key(key)] = {"atime":time.time(), "value":value}

        f = open(fname, "w")
        f.write(json.dumps(obj))
        f.close()

        
    # drop least recently used cache items until the total 
    # size of the dir is <= max_size
    def drop_lru(self):
        print "cache size:", util.SI_byte_string(get_size(self.directory))
        while get_size(self.directory) > self.max_size:
            oldest_file = None
            oldest_time = None
            oldest_key = None
            

            for f in os.listdir(self.directory):
                path = os.path.join(self.directory, f)
                f = open(path, "r")
                obj = json.loads(f.read())
                f.close()

                if (oldest_time is None) or (obj[obj.keys()[0]]["atime"] < oldest_time):
                    oldest_time = obj[obj.keys()[0]]["atime"]
                    oldest_file = path
                    oldest_key = obj.keys()[0]

            os.remove(oldest_file)
            print "dropped %s (%s), cache is now %s" % (oldest_file, oldest_key, util.SI_byte_string(get_size(self.directory)))




