/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.opengl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class OpenGLES20Complete extends Activity {

    private MyGLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        //mGLView = new MyGLSurfaceView(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mGLView = (MyGLSurfaceView) findViewById(R.id.glsurface);
        if (mGLView == null){
        	Log.d("OO", "Surface grabbed from the layout was null");
        }
        
        Intent intent = getIntent();
        String serverName = intent.getStringExtra("com.example.android.opengl.serverName");
        Log.d("OO", serverName);
        //addContentView(mGLView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
    public void switchView(View view){
    	
    	mGLView.switchView();
    	
    }
    
    public void moveUp(View view){
    	
    	mGLView.moveUp();
    	
    }
    
public void moveDown(View view){
    	
    	mGLView.moveDown();
    	
    }


    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

