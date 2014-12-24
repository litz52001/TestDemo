package com.test.listitem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.R;

public class OneActivity extends Activity {

	private ListView list1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one);
		list1 = (ListView) findViewById(R.id.list1);
		list1.setAdapter(new MyAdapter(this));
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 20;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.activity_list_item, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.tv);
			tv.setText(position+"");
			return convertView;
		}

	}

}