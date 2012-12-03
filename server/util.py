
def SI_byte_string(size):
    if size >= 1e15:
        return "%.1f PB" % (size/1e15)
    elif size >= 1e12:
        return "%.1f TB" % (size/1e12)
    elif size >= 1e9:
        return "%.1f GB" % (size/1e9)
    elif size >= 1e6:
        return "%.1f MB" % (size/1e6)
    elif size >= 1e3:
        return "%.1f KB" % (size/1e3)
    else:
        return "%.1f Bytes" % size
