import os, json, hashlib


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

# drop least recently used cache items until the total 
# size of the dir is <= max_size
def drop_lru(directory, max_size):
    while get_size(directory) > max_size:
        oldest_file = None

        for f in os.listdir(directory):
            if (oldest_file is None) or (os.path.getmtime(os.path.join(directory, oldest_file)) > os.path.getmtime(os.path.join(directory, f))):
                oldest_file = f

        print "dropped %s from the cache, total size is now %d KB" % (os.path.join(directory, oldest_file), get_size(directory)/1000.0)
        os.remove(os.path.join(directory, oldest_file))

class Cache(object):
    """ On-disk key-value store """

    def __init__(self, directory, cache_name, max_cache_size = 1e9):
        self.directory = directory
        self.name = cache_name
        self.max_size = max_cache_size

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
        drop_lru(self.directory, self.max_size)

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

        
