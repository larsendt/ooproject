uniform mat4 mvMatrix;
uniform mat4 pMatrix;
uniform mat3 nMatrix;
attribute vec3 vertex;
attribute vec3 normal;
attribute vec2 txcoord;
varying vec2 f_txcoord;
varying vec3 f_lightPos;
varying vec3 f_normal;
varying vec3 f_vertex;


void main() 
{
    gl_PointSize = 3.0;
    f_txcoord = txcoord;
    f_normal = normalize(nMatrix*normal);
    f_vertex = vec3(mvMatrix * vec4(vertex, 1.0));
    vec3 lightPos = vec3(mvMatrix * vec4(0.0,.5,0.0,1.0));
    f_lightPos = lightPos;
    gl_Position = pMatrix * mvMatrix * vec4(vertex,1.0);
};
