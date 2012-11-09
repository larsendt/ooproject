class Vec2(object):
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vec2(self.x + other.x, self.y + other.y)

    def __sub__(self, other):
        return Vec2(self.x - other.x, self.y - other.y)

    def __mul__(self, other):
        return Vec2(self.x * other, self.y * other)

    def __neg__(self, other):
        self.x *=-1
        self.y *=-1

    def length(self):
        return sqrt((self.x**2 + self.y**2))

    def normalize(self):
        if (self.x == 0) and (self.y == 0):
            return
        l = self.length()
        return Vec2(self.x/l, self.y/l)     

class Vec3(object):
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z
        self.data = (x, y, z)

    def __add__(self, other):
        return Vec3(self.x + other.x, self.y + other.y, self.z + other.z)

    def __sub__(self, other):
        return Vec3(self.x - other.x, self.y - other.y, self.z - other.z)

    def dot(self, other):
        return (self.x * other.x) + (self.y * other.y) + (self.z * other.z)

    def cross(self, other):
        x = (self.y * other.z) - (other.y * self.z)
        y = (self.x * other.z) - (other.x * self.z)
        z = (self.x * other.y) - (other.x * self.y)
        return Vec3(x, y, z)

    def scale(self, scalar):
        return Vec3(self.x * scalar, self.y * scalar, self.z * scalar)

    def length(self):
        return math.sqrt((self.x**2) + (self.y**2))

    def normalized(self):
        l = self.length()
        return Vec2(self.x/l, self.y/l, self.z/l)



