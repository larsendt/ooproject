package com.larsendt.ObjectOriented;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class AsyncDataFetcher extends AsyncTask<String, Integer, String> {
    private float[] m_chunkData;

	@Override
	protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;

        Log.d(WorldRenderer.TAG, "Requesting url: " + params[0]);

        try {
            response = httpclient.execute(new HttpGet(params[0]));
        } catch (IOException e) {
            Log.d(WorldRenderer.TAG, "HttpResponse broke...");
            e.printStackTrace();
            return "";
        }

        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                response.getEntity().writeTo(out);
            } catch(IOException e) {
                Log.d(WorldRenderer.TAG, "ByteArrayOutputStream broke when fetching chunk data");
                e.printStackTrace();
                return "";
            }
            String input_json = out.toString();
            Log.d(WorldRenderer.TAG, "Got chunk data");
            return input_json;
        }
        else {
            try {
                response.getEntity().getContent().close();
            } catch(IOException e) {
                Log.d(WorldRenderer.TAG, "Response broke");
                e.printStackTrace();
            }
            Log.d(WorldRenderer.TAG, statusLine.getReasonPhrase());
        }
		Log.d(WorldRenderer.TAG, "Broke somewhere.");
		return null;
	}

    @Override
    protected void onPostExecute(String json_string) {
        JSONObject obj;
        try {
            obj = (JSONObject) new JSONTokener(json_string).nextValue();
        } catch (JSONException e) {
            Log.e(WorldRenderer.TAG, "Parsing json failed");
            e.printStackTrace();
            return;
        }

        Log.d(WorldRenderer.TAG, "Created JSON object");

        String type;
        try {
            type = obj.getString("type");
        } catch(JSONException e) {
            Log.e(WorldRenderer.TAG, "JSON object didn't have 'type'");
            e.printStackTrace();
            return;
        }

        Log.d(WorldRenderer.TAG, "Type was: " + type);
        if(type.equals("chunk")) {
            String chunk_string;
            try {
                chunk_string = obj.getString("vertex_data");
            } catch(JSONException e) {
                Log.e(WorldRenderer.TAG, "JSON object didn't have vertex data");
                e.printStackTrace();
                return;
            }

            byte[] decoded_data = Base64.decode(chunk_string, 0);
            Log.d(WorldRenderer.TAG, "Decoded chunk data from Base64");
            boolean compression;
            try {
                compression = obj.getBoolean("compression");
            } catch(JSONException e) {
                Log.d(WorldRenderer.TAG, "JSON object didn't have compression flag, assuming no compression");
                e.printStackTrace();
                compression = false;
                Log.d(WorldRenderer.TAG, "Assuming no compression (server didn't send compression flag");
            }

            if(compression) {
                Log.d(WorldRenderer.TAG, "Chunk data is compressed");
                Inflater inf = new Inflater();
                inf.setInput(decoded_data);
                int inflated_size;
                try {
                    inflated_size = obj.getInt("inflated_size");
                } catch(JSONException e) {
                    Log.e(WorldRenderer.TAG, "JSON object didn't have inflated size");
                    e.printStackTrace();
                    return;
                }

                byte[] decompressed_bytes = new byte[inflated_size];
                int sz;
                try {
                    sz = inf.inflate(decompressed_bytes);
                } catch(DataFormatException e) {
                    Log.e(WorldRenderer.TAG, "Failed to decompress vertex data");
                    e.printStackTrace();
                    return;
                }
                Log.d(WorldRenderer.TAG, "Decompressed chunk data");
                if(sz != inflated_size) {
                    Log.e(WorldRenderer.TAG, "Byte array decompressed to different size than expected");
                }
                decoded_data = decompressed_bytes;
            }
            else {
                Log.d(WorldRenderer.TAG, "Chunk data is not compressed");
            }

            Log.d(WorldRenderer.TAG, "Converting bytes to floats");
            ByteBuffer buf = ByteBuffer.wrap(decoded_data);
            buf.order(ByteOrder.BIG_ENDIAN);
            FloatBuffer vert_data = buf.asFloatBuffer();
            m_chunkData = new float[decoded_data.length/4];

            for(int i = 0; i < m_chunkData.length; i++) {
                m_chunkData[i] = vert_data.get(i);
            }
        }
        else {
            Log.e(WorldRenderer.TAG, "Got bad json data:" + json_string + "(type == " + type);
        }

        Log.d(WorldRenderer.TAG, "Done");
    }

	public float[] getChunkData() {
        return m_chunkData;
    }
}
