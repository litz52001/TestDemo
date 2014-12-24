package com.test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * 测试系统下载
 * @author litz
 *
 */
public class CActiviy extends Activity{// implements DialogClick
	
	private String mUri = "http://www.yonghui.com.cn/apps/yhdear.apk";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bmain);  
		 
		Button btn1 = (Button)findViewById(R.id.button1);
		Button btn2 = (Button)findViewById(R.id.button2);
		
		btn1.setText("系统下载");
		btn2.setText("时间框");
		
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Uri uri = Uri.parse(mUri);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{ 
				NotifiDown();
			}
		});
	}
	
	
	public void NotifiDown()
	{
    	//点击更新后,发通知
    	final Notification n=new Notification();
		n.icon=R.drawable.ic_launcher;
		n.tickerText="开始下载";
		n.contentView = new RemoteViews(getPackageName(),R.layout.item_notification);
		n.flags = Notification.FLAG_NO_CLEAR;
		
		Intent intent = new Intent(CActiviy.this, BActiviy.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(CActiviy.this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
		n.contentIntent = pendingIntent;		
		
		NotificationManager nm=(NotificationManager)CActiviy.this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(110, n); 
		
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				downLoadApk();
				return null;
			}
			
			void downLoadApk(){
				File file=null;
				
				if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){//如果SD卡存在
					file=new File(Environment.getExternalStorageDirectory(), "test.apk");
				}else{//internal storage
					file=new File(getFilesDir(),"test.apk");
				}
				
				try {
					URL url=new URL(mUri);
					HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
					int len=httpURLConnection.getContentLength();
					InputStream is=httpURLConnection.getInputStream();
					OutputStream os=new FileOutputStream(file, false);
					
					byte buffer[]=new byte[1024];
					int readSize=0;//每次读取的大小
					int totalSize=0;//累计读取的大小
					int temProgress=0;//上一次进度
					
					while((readSize=is.read(buffer))!=-1){
						os.write(buffer, 0, readSize);
						totalSize+=readSize;
						
						int progress=(totalSize*100/len);//实时进度
						
						if(progress!=temProgress){
							publishProgress(progress);
							temProgress=progress;
						}
						
					}
					
					is.close();
					os.close();
					httpURLConnection.disconnect();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				int p=values[0];
				
				if(p==100){
					n.tickerText="下载完成,点击安装";
					n.contentView.setTextViewText(R.id.content_text, "下载完成,点击安装");
					n.contentView.setProgressBar(R.id.content_progress, 100, p, false);
					
					Intent intent = new Intent(Intent.ACTION_VIEW); 
					
					if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
						intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"test.apk")), "application/vnd.android.package-archive"); 
					}else{
						intent.setDataAndType(Uri.fromFile(new File(getFilesDir(),"test.apk")), "application/vnd.android.package-archive"); 
					}
					
					PendingIntent pi=PendingIntent.getActivity(CActiviy.this, 0, intent, 0);
					n.contentIntent=pi;
					n.flags=Notification.FLAG_AUTO_CANCEL;
					n.defaults=Notification.DEFAULT_VIBRATE;
					NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nm.notify(110, n);
				}else{
					n.contentView.setTextViewText(R.id.content_text, "正在下载"+"		"+p+"%");
					n.contentView.setProgressBar(R.id.content_progress, 100, p, false);
					NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nm.notify(110, n);
				}
				
			}
			
		}.execute();
		
	}
	
 
  }
    
