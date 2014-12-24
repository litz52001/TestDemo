package com.test.util;


import com.test.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotifiReceiver extends BroadcastReceiver {

	MyApplication myApplication=MyApplication.getInstance();
	Context mContext = MyApplication.getInstance().mContext;
	
	public NotifiReceiver() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getStringExtra("action");
		if(action.equals("close"))
		{
			Toast.makeText(mContext, "close", 1000).show();
			myApplication.notificationManager.cancelAll();
			
		}
	}

}
