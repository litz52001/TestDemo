package com.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyToast 
{
	Context context=null;
	Object obj =null;
	public MyToast(Context context,String text)
	{
		this.context =context;
		Toast toast =Toast.makeText(context, text, 1);
		try {
			Field field = toast.getClass().getDeclaredField("mTN");
			field.setAccessible(true);
			obj =field.get(toast);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("AAA", "MyToast Exception--->"+e.toString());
		}
	}
	public void show()
	{           
		try {
			//android4.0以上就要以下处理
//			Field mNextViewField = obj.getClass().getDeclaredField("mNextView");
//	        mNextViewField.setAccessible(true);
//			LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View v = inflate.inflate(R.layout.ui_toast, null);			
//	        mNextViewField.set(obj, v);
			Method method =obj.getClass().getDeclaredMethod("show", null);
			method.invoke(obj, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("AAA", "show Exception--->"+e.toString());
			e.printStackTrace();
		}
	}
	public void hide()
	{
		try {
			Method method =obj.getClass().getDeclaredMethod("hide", null);
			method.invoke(obj, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("AAA", "hide Exception--->"+e.toString());
			e.printStackTrace();
		}
	}

}
