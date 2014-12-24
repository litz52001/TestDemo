package com.test.photowall;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.test.R;


public class PhotoWallActiviy extends Activity {
	
	GridView mPhotoWall;
//	CachePhotoAdapter adapter;
	DisLruPhotoAdapter adapter;
	private FileUtils fileUtils;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photowall);  
		fileUtils = new FileUtils(this); 
		 mPhotoWall = (GridView) findViewById(R.id.photo_wall);  
//		 adapter = new CachePhotoAdapter(this, Images.imageThumbUrls, mPhotoWall);  
		 adapter = new DisLruPhotoAdapter(this,0, Images.imageThumbUrls, mPhotoWall); 
		 mPhotoWall.setAdapter(adapter); 
		 
		 
	}

	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        // 退出程序时结束所有的下载任务  
//        adapter.cancelAllTasks();  
        adapter.cancelTask();
    }  
	
	
	@Override  
	public boolean onCreateOptionsMenu(Menu menu) {  
	    super.onCreateOptionsMenu(menu);  
	    menu.add("删除手机中图片缓存");  
	        return super.onCreateOptionsMenu(menu);  
	    }  
	  
	    @Override  
	    public boolean onOptionsItemSelected(MenuItem item) {  
	        switch (item.getItemId()) {  
	        case 0:  
	            fileUtils.deleteFile();  
	            Toast.makeText(getApplication(), "清空缓存成功", Toast.LENGTH_SHORT).show();  
	        break;  
	    }  
	    return super.onOptionsItemSelected(item);  
	}  
	    
  }
    
