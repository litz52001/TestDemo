package com.test.gson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.test.R;


public class JsonActivity extends Activity {
	
	StringBuffer sb = new StringBuffer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txtmain);  
		try {
			InputStream jis = getResources().getAssets().open("json.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(jis,"UTF-8"));
			String readLine = "";
			while ((readLine = br.readLine())!= null) {
				sb.append(readLine);
			}
			
			jis.close();
			br.close();
			
			Gson gson = new Gson(); 
			Status status = gson.fromJson(sb.toString(), Status.class);  
			Log.e("解析之后的数据", status.toString());
			 
			String json = gson.toJson(status);
			Log.e("组装之后的数据", json);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

  }
    
