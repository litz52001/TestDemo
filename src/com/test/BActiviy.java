package com.test;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class BActiviy extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bmain);  
		final MyToast toast = new MyToast(BActiviy.this, "我显显显显显");
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				new MyToast(BActiviy.this, "我显显显显显").show();
			}
		});
		
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{ 
				toast.hide();
			}
		});
		
	}

  }
    
