package com.example.android.opengl;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class InitActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_init, menu);
        return true;
    }
    
    public void startStreaming(View view){
    	Intent intent = new Intent(this, OpenGLES20Complete.class);
    	EditText editText = (EditText) findViewById(R.id.editText1);
    	String message = editText.getText().toString();//"http://larsendt.com:1234";
    	intent.putExtra("com.example.android.opengl.serverName", message);
    	startActivity(intent);
    }
}
