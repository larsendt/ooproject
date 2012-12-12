package com.larsendt.ObjectOriented;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Spinner;
import com.larsendt.ObjectOriented.R;

public class SetupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        Spinner terrainSpinner = (Spinner) findViewById(R.id.terrainSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.terrain_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        terrainSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_init, menu);
        return true;
    }
    
    public void startStreaming(View view){
    	Intent intent = new Intent(this, DisplayActivity.class);
    	EditText editText = (EditText) findViewById(R.id.editText1);
    	String message = editText.getText().toString();//"http://larsendt.com:1234";
    	intent.putExtra("com.larsendt.ObjectOriented.serverName", message);

        Spinner terrainSpinner = (Spinner) findViewById(R.id.terrainSpinner);
        String message2 = terrainSpinner.getSelectedItem().toString();
        intent.putExtra("com.larsendt.ObjectOriented.terrainType",message2);

    	startActivity(intent);
    }
}
