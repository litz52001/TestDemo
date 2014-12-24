package com.test.photowall;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.test.R;

public class PhotoAdapter extends ArrayAdapter<String> implements OnScrollListener{

	 /** 
     * 记录所有正在下载或等待下载的任务。 
     */  
	public Set<BitmapWorkerTask> taskCollection;
	 /** 
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。 
     */  
	public LruCache<String, Bitmap> mMemoryCache;
	/** 
     * GridView的实例 
     */  
    private GridView mPhotoWall;  
    /** 
     * 第一张可见图片的下标 
     */  
    private int mFirstVisibleItem;  
  
    /** 
     * 一屏有多少张图片可见 
     */  
    private int mVisibleItemCount;  
  
    /** 
     * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。 
     */  
    private boolean isFirstEnter = true;  
	
	
	
	@SuppressLint("NewApi")
	public PhotoAdapter(Context context, int textViewResourceId, String[] objects,GridView photoWall) {
		super(context, textViewResourceId, objects);
		 mPhotoWall = photoWall;  
		 taskCollection = new HashSet<BitmapWorkerTask>();  
		// 获取应用程序最大可用内存  
		 int maxMemory = (int)Runtime.getRuntime().maxMemory();
		 int cache = maxMemory / 8;
		 mMemoryCache = new LruCache<String, Bitmap>(cache){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		 };
		
		 mPhotoWall.setOnScrollListener(this);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String url = getItem(position);
		 Log.e("下载URL", url);
		View view = null;
		if(convertView == null)
		{
			view = LayoutInflater.from(getContext()).inflate(R.layout.photowall_item, null);
		}else{
			view  = convertView;
		}
		ImageView imageView = (ImageView)view.findViewById(R.id.photo);
		imageView.setTag(url);
		setImageView(url, imageView);  
		return view;
	}
	
	/** 
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存， 
     * 就给ImageView设置一张默认图片。 
     *  
     * @param imageUrl 
     *            图片的URL地址，用于作为LruCache的键。 
     * @param imageView 
     *            用于显示图片的控件。 
     */ 
	public void setImageView(String url,ImageView photo)
	{
		Bitmap bitmap = getBitmapFromMemoryCache(url);  
		if (bitmap != null) {  
			photo.setImageBitmap(bitmap);  
        } else {  
        	photo.setImageResource(R.drawable.refresh_btn);  
        } 
	}
	
	/** 
     * 将一张图片存储到LruCache中。 
     *  
     * @param key 
     *            LruCache的键，这里传入图片的URL地址。 
     * @param bitmap 
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。 
     */  
	public void addBitmapToMemoryCache(String key,Bitmap bitmap)
	{
		if(getBitmapFromMemoryCache(key) == null && bitmap != null)
			mMemoryCache.put(key, bitmap);
	}
	
	/** 
     * 从LruCache中获取一张图片，如果不存在就返回null。 
     *  
     * @param key 
     *            LruCache的键，这里传入图片的URL地址。 
     * @return 对应传入键的Bitmap对象，或者null。 
     */  
	public Bitmap getBitmapFromMemoryCache(String url)
	{
		return mMemoryCache.get(url);
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，  
        // 因此在这里为首次进入程序开启下载任务。 
		if(isFirstEnter && mVisibleItemCount > 0)
		{
			loadBitmaps(firstVisibleItem, visibleItemCount);  
            isFirstEnter = false; 
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE)
		{
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);  
		}else {
			cancelAllTasks();
		}
		
	}

	/** 
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象， 
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。 
     *  
     * @param firstVisibleItem 
     *            第一个可见的ImageView的下标 
     * @param visibleItemCount 
     *            屏幕中总共可见的元素数 
     */ 
	public void loadBitmaps(int first,int visibleItemCount)
	{
		try {
			for(int i = first;i < first+visibleItemCount;i++)
			{
				String urlString = Images.imageThumbUrls[i];
				Bitmap bitmap = getBitmapFromMemoryCache(urlString);
				if(bitmap == null)
				{
					BitmapWorkerTask task = new BitmapWorkerTask();
					taskCollection.add(task);
					task.execute(urlString);
				}else {
					ImageView imgView = (ImageView)mPhotoWall.findViewWithTag(urlString);
					if(imgView != null && bitmap != null)
					{
						imgView.setImageBitmap(bitmap);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /** 
     * 取消所有正在下载或等待下载的任务。 
     */  
    public void cancelAllTasks() {  
        if (taskCollection != null) {  
            for (BitmapWorkerTask task : taskCollection) {  
                task.cancel(false);  
            }  
        }  
    }  
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
	{

		String imgUrl ;
		@Override
		protected Bitmap doInBackground(String... params) {
			imgUrl = params[0];
			Bitmap bitmap = downloadBitmap(params[0]);  
			if(bitmap != null)
			{
				// 图片下载完成后缓存到LrcCache中  
                addBitmapToMemoryCache(params[0], bitmap); 
			}
			return bitmap;
		}
	}
	
	
	/** 
     * 建立HTTP请求，并获取Bitmap对象。 
     *  
     * @param imageUrl 
     *            图片的URL地址 
     * @return 解析后的Bitmap对象 
     */  
	private Bitmap downloadBitmap(String murl)
	{
		
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(murl);
			con = (HttpURLConnection)url.openConnection();
			con.setConnectTimeout(5 * 1000);  
            con.setReadTimeout(10 * 1000); 
            bitmap = BitmapFactory.decodeStream(con.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(con != null)
				con.disconnect();
		}
		return bitmap;
	}
	
}


















