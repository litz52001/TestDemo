package com.test.volley;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache{

	/**最核心的类是LruCache (此类在android-support-v4的包中提供) 。
	这个类非常适合用来缓存图片，它的主要算法原理是把最近使用的对象用强引用存储在 LinkedHashMap 中，
	并且把最近最少使用的对象在缓存值达到预设定值之前从内存中移除。*/
	LruCache<String, Bitmap> mCache;
	
	
	public BitmapCache() {
		// 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。  
	    // LruCache通过构造函数传入缓存值，以KB为单位。
//		int cacheSize = 10*1024*1024;
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
		// 使用最大可用内存值的1/8作为缓存的大小。
		int cacheSize = maxMemory / 8;  
		mCache = new LruCache<String, Bitmap>(cacheSize){
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
//				return bitmap.getRowBytes() * bitmap.getHeight();
				return bitmap.getByteCount() / 1024;  
			}
		};
		 
	}

	@Override
	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

}
