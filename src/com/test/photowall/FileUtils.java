package com.test.photowall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	 /** 
     * sd卡的根目录 
     */  
	private final String mSdRootPath  = Environment.getExternalStorageDirectory().getPath(); 
	/** 
     * 手机的缓存根目录 
     */  
    private static String mDataRootPath = null;  
    /** 
     * 保存Image的目录名 
     */  
    private final static String FOLDER_NAME = "/AndroidImage";  

    public FileUtils(Context context) { 
    	mDataRootPath = context.getCacheDir().getParent();
    	Log.e("缓存地址：", mDataRootPath);
	}
    
    /** 
     * 获取储存Image的目录 有sd卡存储到sd卡，没有就存储到手机目录
     * @return 
     */  
    public String getStorageDirectory()
    {
    	return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? 
    			mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
//    	return mSdRootPath + FOLDER_NAME;
    }
    
    /** 
     * 保存Image的方法
     * @param fileName  
     * @param bitmap    
     * @throws IOException 
     */  
    public void saveBitmap(String fileName,Bitmap bitmap)throws IOException{
    	if(bitmap == null) return;
    	
    	String path = getStorageDirectory();
    	Log.e("path：", path);
    	File folderfile = new File(path);
    	if(!folderfile.exists())
    	{
    		folderfile.mkdir();
    	}
    	Log.e("保存地址：", path + File.separator + fileName);
    	File file = new File(path + File.separator + fileName);
    	file.createNewFile();
    	FileOutputStream fos = new FileOutputStream(file);
    	bitmap.compress(CompressFormat.JPEG, 100, fos);  
    	fos.flush();
    	fos.close();
    }
    
    /** 
     * 从手机或者sd卡获取Bitmap 
     * @param fileName 
     * @return 
     */ 
    public Bitmap getBitmap(String fileName) 
    {
    	return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }
    
    /** 
     * 判断文件是否存在 
     * @param fileName 
     * @return 
     */ 
    public boolean isFileExists(String fileName)
    {
    	return new File(getStorageDirectory() + File.separator + fileName).exists();  
    }
    
    /** 
     * 获取文件的大小 
     * @param fileName 
     * @return 
     */  
    public long getFileSize(String fileName) {  
        return new File(getStorageDirectory() + File.separator + fileName).length();  
    }  
    
    /** 
     * 删除SD卡或者手机的缓存图片和目录 
     */  
    public void deleteFile()
    {
    	File dirFile = new File(getStorageDirectory()); 
    	if(!dirFile.exists())
    	{
    		return;
    	}
    	if(dirFile.isDirectory())
    	{
    		String[] children = dirFile.list();
    		for(int i=0;i<children.length;i++)
    		{
    			new File(dirFile, children[i]).delete();  
    		}
    	}
    	dirFile.delete();  
    }
    
}
















