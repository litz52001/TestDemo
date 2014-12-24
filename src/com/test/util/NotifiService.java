package com.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.test.MyApplication;
import com.test.R;

public class NotifiService extends IntentService {

	private String mUri = "http://www.yonghui.com.cn/apps/yhdear.apk";
	
	NotificationManager nm = MyApplication.getInstance().notificationManager;
	Notification notifi;
	Context mContext = MyApplication.getInstance().mContext;
	
	LocalBinder mBinder = new LocalBinder();
	
	public NotifiService() {
		super("NotifiService");
	}
	
	public NotifiService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String type = intent.getStringExtra("action");
		Log.d("intent", type);
		if (type.equals("btn1")) {
			 Toast.makeText(mContext, "btn1", 1000).show();
			 nm.cancelAll();
		}
		if (type.equals("btn2")) {
			 Toast.makeText(mContext, "btn2", 1000).show();
			 nm.cancelAll();
		}
		
	}
	
	
	public void download()
	{
		notifi = MyApplication.getInstance().notification;
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
					notifi.tickerText="下载完成,点击安装";
					notifi.contentView.setTextViewText(R.id.content_text, "下载完成,点击安装");
					notifi.contentView.setProgressBar(R.id.content_progress, 100, p, false);
					
					Intent intent = new Intent(Intent.ACTION_VIEW); 
					
					if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
						intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"test.apk")), "application/vnd.android.package-archive"); 
					}else{
						intent.setDataAndType(Uri.fromFile(new File(getFilesDir(),"test.apk")), "application/vnd.android.package-archive"); 
					}
					
					PendingIntent pi=PendingIntent.getActivity(mContext, 0, intent, 0);
					notifi.contentIntent=pi;
					notifi.flags=Notification.FLAG_AUTO_CANCEL;
					notifi.defaults=Notification.DEFAULT_VIBRATE;
					NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nm.notify(110, notifi);
				}else{
					notifi.contentView.setTextViewText(R.id.content_text, "正在下载"+"		"+p+"%");
					notifi.contentView.setProgressBar(R.id.content_progress, 100, p, false);
					NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					nm.notify(110, notifi);
				}
				
			}
			
		}.execute();
		
	}
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
		
	}
	
	/**
	 * 自定义绑定Service类，通过这里的getService得到Service，之后就可调用Service这里的方法了
	 */
	public class LocalBinder extends Binder {
		public NotifiService getService() {
			Log.d("playerService", "getService");
			return NotifiService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
