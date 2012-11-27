package com.example.android.opengl;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.os.AsyncTask;
import android.util.Log;

public class DataFetcher extends AsyncTask<String, Integer, String> {

	private static String host = "192.168.1.149";
	private static int port = 5000;
	private static int HEADER_BYTE_COUNT = 4;

	@Override
	protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(new HttpGet("http://larsendt.com:1234/?x=0&y=0"));
        } catch (IOException e) {
            Log.d("FETCHER", "HttpResponse broke...");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "";
        }

        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                response.getEntity().writeTo(out);
            } catch(IOException e) {
                Log.d("FETCHER", "ByteArrayOutputStream broke when fetching chunk data");
                e.printStackTrace();
                return "";
            }
            String input_json = out.toString();
            Log.d("FETCHER", "response is" + input_json);
            input_json = input_json.substring(HEADER_BYTE_COUNT);
            return input_json;
        }
        else {
            try {
                response.getEntity().getContent().close();
            } catch(IOException e) {
                Log.d("FETCHER", "Response broke");
                e.printStackTrace();
            }
            Log.d("FETCHER", statusLine.getReasonPhrase());
        }

		Log.d("FETCHER", "Broke somewhere.");
		return null;
	}

	
}
