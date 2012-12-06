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

	private HashMap<ChunkLookup, Chunk> map;
	private List<ChunkLookup> pending;
	private DataFetcher fetcher;
	private Shader shader;
	
	public World(DataFetcher df, Shader s) {
		map = new HashMap<ChunkLookup, Chunk>();
		pending = new LinkedList<ChunkLookup>();
		fetcher = df;
		shader = s;
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
		
		for (int i = -1; i < 2; i++){
			for (int j = -1; j < 2; j++){
				cl = new ChunkLookup(x+i,z+j);
				if (map.containsKey(cl)){
					continue;
				}
				fetcher.pushChunkRequest(cl.x, cl.z);
				Log.d("World", "Requested " +  Integer.toString(cl.x) + "/" + Integer.toString(cl.z) + " from datafetcher");
				pending.add(cl);
			}
		}
	}
	
	public void update(){
		
		checkForPending();
		
	}
	
	private void checkForPending(){
		for (int i = 0; i < pending.size(); i++){
			ChunkLookup cl = pending.get(i);
			TaskStatus status = fetcher.getChunkStatus(cl.x, cl.z);
			if (status==TaskStatus.DONE){
				
				
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
				Log.d("World", "Loaded " +  Integer.toString(c.x) + "/" + Integer.toString(c.z) + " into chunk");
				Log.d("World", Integer.toString(pending.size()) + " pending left");
			}
			else if (status == TaskStatus.NOSUCHCHUNK){
				Log.d("World", "Looks like the fetcher dropped " + Integer.toString(cl.x) + "/" + Integer.toString(cl.z));
				pending.remove(i);
			}
			
		}
	}
	
}
