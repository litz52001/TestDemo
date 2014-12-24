package com.test;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * @author litz
 *
 */
public class EActiviy extends Activity {
	
	 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bmain);  
		final MyToast toast = new MyToast(EActiviy.this, "我显显显显显");
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				 
			}
		});
		
	 
		
	}
	

  }
    
