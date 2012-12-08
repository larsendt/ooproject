package com.example.android.opengl;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * User: dane
 * Date: 12/4/12
 * Time: 3:40 PM
 */

public class DataFetcher {
    private Map<Pair<Integer, Integer>, AsyncDataFetcher> m_fetcherMap;
    private String m_url;
    private final String chunkRequest = "/?x=%d&z=%d&compression=yes";
    
    public enum TaskStatus {
        NOSUCHCHUNK,
        PROCESSING,
        DONE,
    }
    
    

    public DataFetcher() {
        m_fetcherMap = new HashMap<Pair<Integer, Integer>, AsyncDataFetcher>();
    }

    public void pushChunkRequest(int x, int z) {
        Pair<Integer, Integer> key = new Pair<Integer, Integer>(x, z);
        if(!m_fetcherMap.containsKey(key)) {
            AsyncDataFetcher df = new AsyncDataFetcher();
            m_fetcherMap.put(key, df);
            df.execute(String.format(m_url + chunkRequest, x, z));
        }
    }

    public TaskStatus getChunkStatus(int x, int z) {
        Pair<Integer, Integer> key = new Pair<Integer, Integer>(x, z);
        TaskStatus ts;
        if(m_fetcherMap.containsKey(key)) {
            AsyncDataFetcher df = m_fetcherMap.get(key);

            if(df.getStatus() == AsyncTask.Status.FINISHED) {
                ts = TaskStatus.DONE;
            }
            else if(df.getStatus() == AsyncTask.Status.PENDING ||
                    df.getStatus() == AsyncTask.Status.RUNNING) {
                ts = TaskStatus.PROCESSING;
            }
            else {
                ts = TaskStatus.NOSUCHCHUNK;
            }
        }
        else {
            ts = TaskStatus.NOSUCHCHUNK;
        }
        return ts;
    }

    public float[] getChunkData(int x, int z) {
        float[] data = null;
        Pair<Integer, Integer> key = new Pair<Integer, Integer>(x, z);

        if(m_fetcherMap.containsKey(key) && m_fetcherMap.get(key).getStatus() == AsyncTask.Status.FINISHED) {
            AsyncDataFetcher df = m_fetcherMap.remove(key);
            data = df.getChunkData();
        }

        return data;
    }
    
    public void setServerName(String newUrl){
    	m_url = newUrl;
    }
}
