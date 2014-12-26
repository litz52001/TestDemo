package com.test.photowall;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.test.R;

/**
 * 使用LruCache 和  DisLruCache缓存
 * @author litz 
 *
 */
public class DisLruPhotoAdapter extends ArrayAdapter<String> {

	 /** 
     * 记录所有正在下载或等待下载的任务。 
     */  
	public Set<BitmapWorkerTask> taskCollection;
	 /** 
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。 
     */  
	public LruCache<String, Bitmap> mMemoryCache;
	/** 
     * 图片硬盘缓存核心类。 
     */  
    private DiskLruCache mDiskLruCache;  
	/** 
     * GridView的实例 
     */  
    private GridView mPhotoWall; 
    
	
	
	
	public DisLruPhotoAdapter(Context context, int textViewResourceId, String[] objects,GridView photoWall) {
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
		
		try {  
            // 获取图片缓存路径  
            File cacheDir = getDiskCacheDir(context, "thumb");  
            if (!cacheDir.exists()) {  
                cacheDir.mkdirs();  
            }  
            // 创建DiskLruCache实例，初始化缓存数据  
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		 
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
	public void setImageView(String url,ImageView imgView)
	{
		Bitmap bitmap = getBitmapFromMemoryCache(url);  
		if (bitmap == null) {  
			BitmapWorkerTask task = new BitmapWorkerTask();  
            taskCollection.add(task);  
            task.execute(url); 
        } else {  
        	if (imgView != null && bitmap != null)
        		imgView.setImageResource(R.drawable.refresh_btn);  
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
	
	 /** 
     * 取消所有正在下载或等待下载的任务。 
     */  
    public void cancelTask() {  
        if (taskCollection != null) {  
            for (BitmapWorkerTask task : taskCollection) {  
                task.cancel(false);  
            }  
        }  
    }  
    
    /** 
     * 根据传入的uniqueName获取硬盘缓存的路径地址。 
     */  
    public File getDiskCacheDir(Context context, String uniqueName) {  
        String cachePath;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return new File(cachePath + File.separator + uniqueName);  
    }  
  
    /** 
     * 获取当前应用程序的版本号。 
     */  
    public int getAppVersion(Context context) {  
        try {  
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);  
            return info.versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return 1;  
    }  
	
    /** 
     * 使用MD5算法对传入的key进行加密并返回。 
     */  
    public String hashKeyForDisk(String key) {  
        String cacheKey;  
        try {  
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");  
            mDigest.update(key.getBytes());  
            cacheKey = bytesToHexString(mDigest.digest());  
        } catch (NoSuchAlgorithmException e) {  
            cacheKey = String.valueOf(key.hashCode());  
        }  
        return cacheKey;  
    }  
    
    /** 
     * 将缓存记录同步到journal文件中。 
     */  
    public void fluchCache() {  
        if (mDiskLruCache != null) {  
            try {  
                mDiskLruCache.flush();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    private String bytesToHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < bytes.length; i++) {  
            String hex = Integer.toHexString(0xFF & bytes[i]);  
            if (hex.length() == 1) {  
                sb.append('0');  
            }  
            sb.append(hex);  
        }  
        return sb.toString();  
    }  
    
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
	{

		String imgUrl ;
		@Override
		protected Bitmap doInBackground(String... params) {
			
			imgUrl = params[0];
			FileDescriptor fileDescriptor = null;  
            FileInputStream fileInputStream = null;  
            Snapshot snapShot = null; 
            try {
            	String key = hashKeyForDisk(imgUrl);
				snapShot = mDiskLruCache.get(key);
				if(snapShot == null)
				{
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if(editor != null)
					{
						// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存  
						OutputStream outputStream = editor.newOutputStream(0);
						if(downloadUrlToStream(imgUrl, outputStream))
						{
							editor.commit();
						}else{
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存  
					snapShot = mDiskLruCache.get(key);
				} 
				if (snapShot != null) {  
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);  
                    fileDescriptor = fileInputStream.getFD();  
                }  
				
				// 将缓存数据解析成Bitmap对象  
                Bitmap bitmap = null;  
                if(fileDescriptor != null)
                {
                	bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);  
                }
                if (bitmap != null) {  
                    // 将Bitmap对象添加到内存缓存当中  
                    addBitmapToMemoryCache(params[0], bitmap);  
                } 
                return bitmap;  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {  
                if (fileDescriptor == null && fileInputStream != null) {  
                    try {  
                        fileInputStream.close();  
                    } catch (IOException e) {  
                    }  
                }  
            }  
            return null;
		}
		
		 @Override  
	     protected void onPostExecute(Bitmap bitmap) {  
	         super.onPostExecute(bitmap);  
	         // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。  
	         ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imgUrl);  
	         if (imageView != null && bitmap != null) {  
	             imageView.setImageBitmap(bitmap);  
	         }  
	         taskCollection.remove(this);  
	     }
	}
	
	 
	
	/** 
     * 建立HTTP请求，并获取Bitmap对象。 
     *  
     * @param imageUrl 
     *            图片的URL地址 
     * @return 解析后的Bitmap对象 
     */  
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {  
        HttpURLConnection urlConnection = null;  
        BufferedOutputStream out = null;  
        BufferedInputStream in = null;  
        try {  
            final URL url = new URL(urlString);  
            urlConnection = (HttpURLConnection) url.openConnection();  
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);  
            out = new BufferedOutputStream(outputStream, 8 * 1024);  
            int b;  
            while ((b = in.read()) != -1) {  
                out.write(b);  
            }  
            return true;  
        } catch (final IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (urlConnection != null) {  
                urlConnection.disconnect();  
            }  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (final IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return false;  
    }  

 
}


















