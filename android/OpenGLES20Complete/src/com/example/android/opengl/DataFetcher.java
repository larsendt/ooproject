package com.example.android.opengl;

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
    private final String m_url = "http://larsendt.com:1234/?x=%d&z=%d&compression=yes";

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
            df.execute(String.format(m_url, x, z));
        }
    }

    public TaskStatus getChunkStatus(int x, int z) {
        Pair<Integer, Integer> key = new Pair<Integer, Integer>(x, z);
        if(m_fetcherMap.containsKey(key)) {
            return TaskStatus.PROCESSING;
        }
        else {
            return TaskStatus.NOSUCHCHUNK;
        }
    }

    public float[] getChunkData(int x, int z) {
        return new float[3];
    }
}
