package com.example.android.opengl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Vector;

import android.util.Log;

import com.example.android.opengl.DataFetcher.TaskStatus;

public class World {

	private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 mvMatrix;" +
	        "uniform mat4 pMatrix;" +
	        "uniform mat3 nMatrix;" +
	        "attribute vec3 vertex;" +
	        "attribute vec3 normal;" +
	        "attribute vec2 txcoord;" +
	        "varying vec2 f_txcoord;" +
	        "varying vec3 f_lightPos;" +
	        "varying vec3 f_normal;" +
	        "varying vec3 f_vertex;" +


	        "void main() {" +
	        "	gl_PointSize = 3.0;" +
	        "	f_txcoord = txcoord;" +
	        "	f_normal = normalize(nMatrix*normal);" +
	        "	f_vertex = vec3(mvMatrix * vec4(vertex, 1.0));" +
	        "	vec3 lightPos = vec3(mvMatrix * vec4(0.0,.5,0.0,1.0));" +
	        "	f_lightPos = lightPos;" +

	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = pMatrix * mvMatrix * vec4(vertex,1.0);" +
	        "}";

	    private final String fragmentShaderCode =
	        "precision mediump float;" +
	        "uniform sampler2D tex;" +
	        "varying vec2 f_txcoord;" +
	        "varying vec3 f_lightPos;" +
	        "varying vec3 f_normal;" +
	        "varying vec3 f_vertex;" +
	        
	        "void main() {" +
	        "	vec3 L = normalize(f_lightPos - f_vertex); " +
	        "	vec3 E = normalize(-f_vertex);" +
	        "	vec3 R = normalize(-reflect(L,f_normal));" +
	        "	vec3 ambient = vec3(.0,.0,.0);" +
	        "	vec3 diffuse = vec3(.6) * max(dot(L, f_normal), 0.0);" +
	        "	diffuse = clamp(diffuse, 0.0,1.0);" +
	        "	vec3 specular = vec3(.15)*pow(max(dot(R,E),0.0), .3 * 30.0);" +
	        "	specular = clamp(specular, 0.0,1.0);" +
	        
	        "	vec4 color = texture2D(tex, f_txcoord*.1);" +
	        "	vec3 intensity = vec3(color) * diffuse;" +
	        "	gl_FragColor = vec4(ambient+intensity+specular, 1.0);" +
	        "}";
	
	private HashMap<ChunkLookup, Chunk> map;
	private List<ChunkLookup> pending;
	private DataFetcher fetcher;
	private Shader shader;
	
	public World(DataFetcher df, Shader s) {
		map = new HashMap<ChunkLookup, Chunk>();
		pending = new LinkedList<ChunkLookup>();
		fetcher = df;
		shader = s;
		shader = new Shader(vertexShaderCode, fragmentShaderCode);
	}

	public void draw(){
		
		Collection<Chunk> v = map.values();
		
		Iterator it = v.iterator();
		
		while (it.hasNext()){
			
			Chunk c = (Chunk)it.next();
			GLState.pushMVMatrix();
			GLState.translate(c.x, 0, c.z);
			GLState.setNMatrix();
			shader.setUniform1i("tex", 0);
			shader.setMatrices(GLState.mvMatrix, GLState.pMatrix, GLState.nMatrix);
			c.draw();

			GLState.popMVMatrix();
			
		}
		
	}
	public void loadAround(int x, int z){
		ChunkLookup cl = new ChunkLookup(x,z);
		if (map.containsKey(cl)){
			return;
		}
		fetcher.pushChunkRequest(x, z);
		Log.d("OO", "Requested " +  Integer.toString(cl.x) + "/" + Integer.toString(cl.z) + " from datafetcher");
		pending.add(cl);
	}
	
	public void update(){
		
		checkForPending();
		
	}
	
	private void checkForPending(){
		for (int i = 0; i < pending.size(); i++){
			ChunkLookup cl = pending.get(i);
			if (fetcher.getChunkStatus(cl.x, cl.z)==TaskStatus.DONE){
				Log.d("OO", "Loading chunk data" +  Integer.toString(cl.x) + "/" + Integer.toString(cl.z) + "into chunk");
				float data[] = fetcher.getChunkData(cl.x, cl.z);
				int num_indices = data.length/8;
				int indices[] = new int[num_indices];
				for (int j = 0; j < num_indices; j++){
					indices[j] = j;
				}
				
				Chunk c = new Chunk(data, indices, shader.getProgram());
				c.x = cl.x;
				c.z = cl.z;
				map.put(cl, c);
				
				pending.remove(i);
			}
			
		}
	}
	
}
