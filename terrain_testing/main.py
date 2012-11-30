from PIL import Image
import perlin
import time
import math

simplex = perlin.SimplexNoise()

def worldGen(x,y):
	tval = simplex.noise2((x-20)*.05 ,(y+50)*.05)
	rval = simplex.noise2(y*.05,x*.05)


	hval = simplex.noise2((y+50)*.05, (x+10)*.05)

	nval = simplex.noise2(x*.005, y*.005)

	val = tval*.6+rval*.1+hval*.3 + nval
	
	return val

def main():
	im = Image.new("RGB", [512,512])
	pixels  = im.load()
	
	for i in xrange(512):
		for j in xrange(512):
			val = worldGen(i,j)
			val += .5
			val = (int)(val * 255 * .5)
			pixels[i,j] = (val,val,val)


	fname = "perlin%3.0f.png" % time.time()
	im.save(fname)



main()