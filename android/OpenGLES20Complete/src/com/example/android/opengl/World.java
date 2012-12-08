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
import android.util.Pair;

import com.example.android.opengl.DataFetcher.TaskStatus;

public class World {

	private HashMap<Pair<Integer,Integer>, Chunk> map;
	private List<Pair<Integer,Integer>> pending;
	private Queue<Pair<Integer,Integer>> active;
	private DataFetcher fetcher;
	private Shader shader;
	
	public World(DataFetcher df, Shader s) {
		map = new HashMap<Pair<Integer,Integer>, Chunk>();
		active = new LinkedList<Pair<Integer,Integer>>();
		pending = new LinkedList<Pair<Integer,Integer>>();
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
		Pair<Integer, Integer> cl = new Pair<Integer, Integer>(x,z);
		
		for (int i = -1; i < 2; i++){
			for (int j = -1; j < 2; j++){
				cl = new Pair<Integer, Integer>(x+i,z+j);
				if (map.containsKey(cl)){
					continue;
				}
				if (pending.contains(cl)){
					continue;
				}
				fetcher.pushChunkRequest(cl.first, cl.second);
				Log.d("World", "Requested " +  Integer.toString(cl.first) + "/" + Integer.toString(cl.second) + " from datafetcher");
				pending.add(cl);
				
				if (active.size() > 18){
					Chunk c = map.remove(active.remove());
					c.clear();
				}
			}
		}
	}
	
	public void update(){
		
		checkForPending();
		
	}
	
	private void checkForPending(){
		for (int i = 0; i < pending.size(); i++){
			Pair<Integer, Integer> cl = pending.get(i);
			TaskStatus status = fetcher.getChunkStatus(cl.first, cl.second);
			if (status==TaskStatus.DONE){
				
				
				float data[] = fetcher.getChunkData(cl.first, cl.second);
				int num_indices = data.length/8;
				int indices[] = new int[num_indices];
				for (int j = 0; j < num_indices; j++){
					indices[j] = j;
				}
				
				Chunk c = new Chunk(data, indices, shader.getProgram());
				c.x = cl.first;
				c.z = cl.second;
				map.put(cl, c);
				active.add(cl);
				
				pending.remove(i);
				
				Log.d("World", "Loaded " +  Integer.toString(c.x) + "/" + Integer.toString(c.z) + " into chunk");
				Log.d("World", Integer.toString(pending.size()) + " pending left");
			}
			else if (status == TaskStatus.NOSUCHCHUNK){
				Log.d("World", "Looks like the fetcher dropped " + Integer.toString(cl.first) + "/" + Integer.toString(cl.second));
				pending.remove(i);
			}
			
		}
	}
	
}
