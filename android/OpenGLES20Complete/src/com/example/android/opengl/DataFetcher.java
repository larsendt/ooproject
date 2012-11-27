package com.example.android.opengl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
		PrintWriter out = null;
        DataInputStream in = null;
		
		try{
			Socket s = new Socket(host, port);
			s.setSoTimeout(5000);
			out = new PrintWriter(s.getOutputStream(), true);
            in = new DataInputStream(s.getInputStream());
		}
		catch (UnknownHostException e){
			e.printStackTrace();
			Log.d("FETCHER", "Can't connect to " + host + ", sorry!");
			return null;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
		
		
		
		JSONObject outjson = new JSONObject();
		
		try {
			
			outjson.put("type", "coords");
			outjson.put("x", 0);
			outjson.put("y", 0);
			
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String outstring = outjson.toString();
		
		int outlen = outstring.length();
		
		
		// the following is magic.
		byte[] lenbytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(outlen).array();
		
		String byte_header= Base64.encodeToString(lenbytes, Base64.DEFAULT);
		
		String tcp_request = byte_header + outstring;
		
		Log.d("FETCHER", tcp_request);
		
		out.write(tcp_request);
		out.flush();
		
		
		try {
			
			Log.d("FETCHER", "listening for data...");
			
			while (in.available() < HEADER_BYTE_COUNT){
				continue;
			}
			
			Log.d("FETCHER", "Waiting for byte length");
			
			byte[] header_bytes = new byte[4];
			in.readFully(header_bytes, 0, 4);
			
			ByteBuffer bb = ByteBuffer.wrap(header_bytes);
			
			int packet_bytes = bb.getInt();
			
			Log.d("FETCHER", "Packet size is " + Integer.toString(packet_bytes));
			
			byte[] data = new byte[packet_bytes];
			
			in.readFully(data, 0, packet_bytes);
			
			String input_json = new String(data, 0, packet_bytes);
			
			Log.d("FETCHER", "response is" + input_json);
			input_json = input_json.substring(HEADER_BYTE_COUNT);
			return input_json;
		} 
		catch (IOException e) {
			Log.d("FETCHER", e.toString());
			e.printStackTrace();
		}
		
		Log.d("FETCHER", "Broke somewhere.");
		return null;
	}

	
}
