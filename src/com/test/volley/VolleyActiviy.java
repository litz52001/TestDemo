package com.test.volley;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.GsonRequest;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.XMLRequest;
import com.test.R;

public class VolleyActiviy extends Activity {
	RequestQueue mrq;
	ImageView img;
	Button btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.txtmain);
		mrq = Volley.newRequestQueue(getApplicationContext());
		
		img = (ImageView)findViewById(R.id.imageView1);
		btn = (Button)findViewById(R.id.btn1);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageCaCheLoader();
			}
		});
		
//		StringRequest();
//		ImageRequest();
//		XMLRespone();
		GsonRQ();
	}

	// 
	public void StringRequest() {
		
		StringRequest strRQ = new StringRequest("http://www.baidu.com",
				new Listener<String>() {
					@Override
					public void onResponse(String arg0) 
					{
						System.out.println(arg0);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) 
					{
						System.out.println(arg0.toString());
					}
				});
		mrq.add(strRQ);
	}

	//StringRequest POST 
	public void MethodStringRequest()
	{
		StringRequest sr = new StringRequest(Method.POST, "", new Listener<String>() {
			@Override
			public void onResponse(String arg0) {}}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {}})
		{
			//post 
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return super.getParams();
			}
		};
		mrq.add(sr);
	}
	
	//Json
	public void JSONRequest()
	{
		JsonObjectRequest jbr = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", null, 
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject arg0) {
						Log.e("成功", arg0.toString());
					}
				}, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						Log.e("error", arg0.toString());
					}
				});
		mrq.add(jbr);
	}
	
	//Image
	public void ImageRequest()
	{
		ImageRequest ir = new ImageRequest("http://developer.android.com/images/home/aw_dac.png", 
				new Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						((ImageView)findViewById(R.id.imageView1)).setImageBitmap(arg0);
					}
				}, 0, 0, Config.RGB_565, 
				new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						((ImageView)findViewById(R.id.imageView1)).setImageResource(R.drawable.ic_launcher);
					}
				});
		
		mrq.add(ir);
	}
	
	//ImageLoader  
	public void ImageLoader()
	{
		ImageLoader imgLoader = new ImageLoader(mrq, new ImageCache() {
			@Override
			public void putBitmap(String arg0, Bitmap arg1) {
			}
			@Override
			public Bitmap getBitmap(String arg0) {
				return null;
			}
		}); 
		
		ImageListener imgListener = com.android.volley.toolbox.ImageLoader.getImageListener(img, R.drawable.refresh_btn,R.drawable.ic_launcher);
		imgLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imgListener,50,50);
	}
	
	//ImageCaCheLoader   
	public void ImageCaCheLoader()
	{
		
		ImageLoader imgLoader = new ImageLoader(mrq, new BitmapCache());
		ImageListener imgListener = com.android.volley.toolbox.ImageLoader.getImageListener(img, R.drawable.refresh_btn,R.drawable.ic_launcher);
		imgLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imgListener,50,50);
		
	}
	
	//networkImageView
	public void networkImageView()
	{
		ImageLoader imgLoader = new ImageLoader(mrq, new BitmapCache());
		NetworkImageView networkImageView = (NetworkImageView) findViewById(R.id.network_image_view);  
		networkImageView.setDefaultImageResId(R.drawable.refresh_btn);
		networkImageView.setErrorImageResId(R.drawable.ic_launcher);
		networkImageView.setImageUrl("http://developer.android.com/images/home/aw_dac.png", imgLoader);
	}
	
	//xml
	public void XMLRespone()
	{
		XMLRequest xmlrp = new XMLRequest("http://flash.weather.com.cn/wmaps/xml/china.xml", 
				new Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						int eventType;
						try {
							eventType = response.getEventType();
							while(eventType != XmlPullParser.END_DOCUMENT)
							{
								switch (eventType) {
								case XmlPullParser.START_TAG:
									String name = response.getName();
									if(name.equals("city"))
									{
										String pname = response.getAttributeValue(0);
										 Log.d("TAG", "pName is " + pname);  
									}
									break;
								}
									eventType = response.next();
							}
						} catch (XmlPullParserException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error); 
					}
				});
		
		mrq.add(xmlrp);
		
	}
	
	/*gson
	{"weatherinfo":{"city":"北京","cityid":"101010100","temp":"19",
		"WD":"南风","WS":"2级","SD":"43%","WSE":"2","time":"19:45",
		"isRadar":"1","Radar":"JC_RADAR_AZ9010_JB"}} */
	public void GsonRQ()
	{
		GsonRequest<Weather> gsonRq = new GsonRequest<Weather>("http://www.weather.com.cn/data/sk/101010100.html",
				Weather.class,
				new Listener<Weather>() {
					@Override
					public void onResponse(Weather response) {
						// TODO Auto-generated method stub
						WeatherInfo weatherInfo = response.getWeatherinfo();  
						Log.e("天气", weatherInfo.toString());
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error); 
						
					}
				});
		mrq.add(gsonRq);
		
	}
	
	
}


























