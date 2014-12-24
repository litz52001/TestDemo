package com.test.listitem;


import com.test.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * @author litz
 *
 */
public class TListActiviy extends Activity {
	
	 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);  
	 
		findViewById(R.id.tv1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TListActiviy.this, OneActivity.class));
			}
		});
	 
		findViewById(R.id.tv2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TListActiviy.this, TwoActivity.class));
			}
		});
	}
	

  }
    
