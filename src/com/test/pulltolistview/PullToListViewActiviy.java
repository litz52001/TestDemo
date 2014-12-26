package com.test.pulltolistview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.test.R;
import com.test.pulltolistview.RefreshableView.PullToRefreshListener;

/**
 * 下拉更新
 * @author Administrator
 *
 */
public class PullToListViewActiviy extends Activity {

	RefreshableView refreshableView;
	ListView listView;
	ArrayAdapter<String> adapter;
	List<String> itemsList = new ArrayList<String>();
	
	int y = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pull_main);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		listView = (ListView) findViewById(R.id.list_view);
		
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
				
				y++;
				for (int i = 0; i < 10; i++) {
					itemsList.add(y + "第 "+i);
				}
				handler.sendEmptyMessage(0);
			}
		}, 0);
		
		for (int i = 0; i < 10; i++) {
			itemsList.add("第 "+i);
		}
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsList);
		listView.setAdapter(adapter);
		
	}
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
		};
	};

}
