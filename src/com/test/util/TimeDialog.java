package com.test.util;

import java.util.Calendar;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.test.R;

public class TimeDialog extends Dialog {

	Context context;
	Button ok, cancel;
	private DatePicker date_picker;
	private Calendar calendar;
	private int my_year, my_month, my_day;
	String value = "";

	DialogClick dialogClick;

	public TimeDialog(Context context) {
		super(context, R.style.net_load_dialog);
		this.context = context;
		
		if(context instanceof DialogClick)
		{
			this.dialogClick = (DialogClick) context;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.times_dialog);
		initUI();
		setListener();
	}

	private void initUI() {
		date_picker = (DatePicker) findViewById(R.id.datepicker);
		ok = (Button) findViewById(R.id.ok);
		cancel = (Button) findViewById(R.id.cancel);
		// 设置时间为中国
		calendar = Calendar.getInstance(Locale.CHINA);
		// 获取日期
		my_year = calendar.get(Calendar.YEAR);
		my_month = calendar.get(Calendar.MONTH);
		my_day = calendar.get(Calendar.DAY_OF_MONTH);

		value = my_year + "-" + toStrings(my_month + 1) + "-"
				+ toStrings(my_day);
	}

	private void setListener() {
		// 日历控件
		date_picker.init(my_year, my_month, my_day,
				new OnDateChangedListener() {
					// 日期修改的单击事件
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						my_year = year;
						my_month = monthOfYear;
						my_day = dayOfMonth;
						// 显示时间
						value = my_year + "-" + toStrings(my_month + 1) + "-"
								+ toStrings(my_day);
					}
				});
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				System.out.println("value:" + value);
				if(dialogClick != null)
					dialogClick.onClick(value);
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				System.out.println("value:" + value);
				if(dialogClick != null)
					dialogClick.onDismiss();
			}
		});
	}

	private String toStrings(int s) {
		if (s < 10) {
			return "0" + s;
		} else {
			return s + "";
		}
	}
	
	public void setDialogClick(DialogClick onclick)
	{
		this.dialogClick = onclick;
	}

	public interface DialogClick {
		void onClick(String value);

		void onDismiss();
	}

}
