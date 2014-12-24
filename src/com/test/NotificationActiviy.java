package com.test;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.test.util.NotifiReceiver;
import com.test.util.NotifiService;
import com.test.util.NotifiService.LocalBinder;

/**
 * 通知栏
 * @author litz
 */
public class NotificationActiviy extends Activity {
	
	private NotificationManager nm;
	private Notification notifi;
	private Notification notifi2;
	private NotificationCompat.Builder nBuilder;
	private NotificationCompat.Builder builder2;
	private NotifiService notifiService;
	
	private int notifID = 1000212;
	private Context mContext;
	
	private String COVER_CLICK_ACTION = "btn";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dmain);  
		
		initLocation();
		setOnclick();
	}
	
	public void initLocation()
	{
		mContext = this;
		nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		MyApplication.getInstance().notificationManager = nm; 
		MyApplication.getInstance().mContext = this;
		
		//绑定服务
		Intent i=new Intent(this,NotifiService.class);
		bindService(i, serConn, Context.BIND_AUTO_CREATE);
	}
	
	public void setOnclick()
	{
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				showNotif();
			}
		});
		
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{ 
				nm.cancel(notifID);
			}
		});
		
		findViewById(R.id.down).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				downNitofi();
			}
		});
	}
	
	//通知栏下载安装
	public void downNitofi()
	{
		builder2 = new NotificationCompat.Builder(mContext);
		builder2.setTicker("我要下载了");
		builder2.setWhen(System.currentTimeMillis());
		builder2.setPriority(NotificationCompat.PRIORITY_MAX);
		builder2.setSmallIcon(R.drawable.ic_launcher);   
		builder2.setOngoing(true);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.item_notification);
		
		Intent intent = new Intent(this, this.getClass()); 
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder2.setContentIntent(pIntent);
		
		notifi2 = builder2.build();
		notifi2.contentView = views;
		MyApplication.getInstance().notification = notifi2;
				
//		nm.notify(1021, notifi2);
		notifiService.download();
	}	
	
	public void showNotif()
	{
		
		nBuilder = new NotificationCompat.Builder(mContext);
		nBuilder.setTicker("我发消息了");
//		nBuilder.setAutoCancel(true);//设置这个标志当用户单击面板就可以让通知将自动取消  
		nBuilder.setOngoing(true);//ture，设置他为一个正在进行的通知。滑动不消除
		nBuilder.setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间  
		nBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//设置该通知优先级
//		nBuilder.setContentTitle("我是同志栏");
//		nBuilder.setContentText("内容");
		nBuilder.setSmallIcon(R.drawable.ic_launcher);//必须填写不然出不出来    //设置通知小ICON              
		nBuilder.setDefaults(Notification.DEFAULT_ALL);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合  
//		Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_launcher);
//		nBuilder.setLargeIcon(largeIcon);//大图标
		
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.d_notification);
		 
		notifi = nBuilder.build();
		if(android.os.Build.VERSION.SDK_INT < 16)
		{
			notifi.contentView = views; 
		}else{
			notifi.bigContentView  = views;//只对Android4.1+之后的设备才支持
		} 
		
		//通知栏跳转
		//new Intent(this,this.getClass())保证了点击通知栏里的通知可以回到该Activity
		//但是，假如该Activity还在后台运行，并没有运行，通知事件响应后，系统会自动结束该Activity，
		//然后再重新启动Activity，这不是我们要的。
		//解决方法为：在manifest.xml文件中找到该Activity，添加属性android:launchMode="singleTask“。
		//这个属性很明显，就是只允许有一个该Activity运行，如果正在运行，则只能切换到当前运行的Activity，
		//而不能重新启动Activity。
		Intent intent = new Intent(this, this.getClass()); 
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		nBuilder.setContentIntent(pIntent);
		
		//一般情况
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(COVER_CLICK_ACTION);
//		registerReceiver(onClickReceiver, filter);
//		views.setOnClickPendingIntent(R.id.close, getBtn());
		Intent intent3 = new Intent(this,NotifiReceiver.class);
		intent3.putExtra("action","close");
		intent3.setAction("com.test.NotifiReceiver");
		PendingIntent pd3 = PendingIntent.getBroadcast(this, 0, intent3,PendingIntent.FLAG_CANCEL_CURRENT);
		views.setOnClickPendingIntent(R.id.close, pd3);
		
		//多个按钮情况
		Intent intt1 = new Intent(this,NotifiService.class); 
		intt1.putExtra("action", "btn1");
		PendingIntent prepi = PendingIntent.getService(this, 1, intt1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.button1, prepi);//----设置对应的按钮ID监控
		
		Intent intent2 = new Intent(this,NotifiService.class); 
		intent2.putExtra("action", "btn2");
		PendingIntent prep2 = PendingIntent.getService(this, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.button2, prep2);//----设置对应的按钮ID监控
		
		nm.notify(notifID, notifi);
	}
	
	
	ServiceConnection serConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			notifiService = ((LocalBinder)service).getService();
			
		}
	}; 
	
	BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(COVER_CLICK_ACTION)) {
					Toast.makeText(mContext, "btn", 1000).show();
					nm.cancelAll();
	                System.exit(0);
				}
		}
	};
	
	private PendingIntent getBtn()
	{
		Intent buttonIntent = new Intent(COVER_CLICK_ACTION);
		buttonIntent.setAction(COVER_CLICK_ACTION);
		PendingIntent pendButtonIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
		return pendButtonIntent;
	}
	
	 
	
}
    
