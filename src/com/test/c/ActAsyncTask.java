package com.test.c;

import com.test.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActAsyncTask extends Activity implements OnClickListener {
	private PorterDuffView pViewA, pViewB, pViewC, pViewD;
	public static final String[] STRING_ARR = {//
	"http://f.hiphotos.baidu.com/zhidao/pic/item/d53f8794a4c27d1e17ee023c1ad5ad6edcc43879.jpg", 
			"http://developer.android.com/images/home/design.png", 
			"http://f.hiphotos.baidu.com/zhidao/pic/item/d53f8794a4c27d1e17ee023c1ad5ad6edcc43879.jpg", 
			"http://developer.android.com/images/home/google-io.png" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.async_proter);

		pViewA = (PorterDuffView) findViewById(R.id.pViewA);
		pViewA.setOnClickListener(this);
		pViewB = (PorterDuffView) findViewById(R.id.pViewB);
		pViewB.setOnClickListener(this);
		pViewC = (PorterDuffView) findViewById(R.id.pViewC);
		pViewC.setOnClickListener(this);
		pViewD = (PorterDuffView) findViewById(R.id.pViewD);
		pViewD.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if (v instanceof PorterDuffView) {
			PorterDuffView pdView = (PorterDuffView) v;
			if (pdView.isLoading() == false) {
				DownloadImgTask task = new DownloadImgTask(pdView);
				if(pdView.getId() == pViewA.getId())
					task.execute(STRING_ARR[0]);
				if(pdView.getId() == pViewB.getId())
					task.execute(STRING_ARR[0]);
				if(pdView.getId() == pViewC.getId())
					task.execute(STRING_ARR[0]);
				if(pdView.getId() == pViewD.getId())
					task.execute(STRING_ARR[0]); 
				
				pdView.setPorterDuffMode(true);
				pdView.setLoading(true);
				pdView.setProgress(0);
				pdView.invalidate();
			}
		}
	}
}
