package com.test.photowall;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.test.R;
import com.test.photowall.ImageDownLoader.onImageLoaderListener;

/**
 * 使用 LruCache 和  保存文件
 * @author Administrator
 *
 */

public class CachePhotoAdapter extends BaseAdapter  implements OnScrollListener{

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
	
    private String[] imageThumbUrls;
    private Context context;
    
    /** 
     * Image 下载器 
     */  
    private ImageDownLoader mImageDownLoader;  
	
	public CachePhotoAdapter(Context context, String[] objects,GridView photoWall) {
		 this.mPhotoWall = photoWall;  
		 this.context = context;
		 this.imageThumbUrls = objects;
		
		 mImageDownLoader = new ImageDownLoader(context);
		 mPhotoWall.setOnScrollListener(this);
	}
	
	  @Override  
    public int getCount() {  
        return imageThumbUrls.length;  
    }  
  
    @Override  
    public String getItem(int position) {  
        return imageThumbUrls[position];  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }   
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String url = getItem(position);
		 Log.e("下载URL", url);
		View view = null;
		if(convertView == null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.photowall_item, parent,false);
		}else{
			view  = convertView;
		}
		ImageView imageView = (ImageView)view.findViewById(R.id.photo);
		imageView.setTag(url);
		
		 //==============去掉下面这几行试试是什么效果==============
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(url.replaceAll("[^\\w]", ""));
		if(bitmap != null)
		{
			imageView.setImageBitmap(bitmap);  
		}else{
			imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.refresh_btn));
		}
		
		return view;
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
		if(scrollState == SCROLL_STATE_IDLE)//未滚动
		{
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);  
		}else {
			cancelTask();
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
		Bitmap bitmap = null;  
		
		for(int i = first;i < first + visibleItemCount;i++)
		{
			String urlString = imageThumbUrls[i];
			final ImageView imgView = (ImageView)mPhotoWall.findViewWithTag(urlString);
			bitmap  = mImageDownLoader.downloadImage(urlString, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if(imgView != null && bitmap != null){  
						imgView.setImageBitmap(bitmap);  
					}
				}
			});
			
		}
	}
	 
	/** 
     * 取消下载任务 
     */  
    public void cancelTask(){  
        mImageDownLoader.cancelTask();  
    }  
	
}


















