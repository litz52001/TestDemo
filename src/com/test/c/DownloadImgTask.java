package com.test.c;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * 
 */
public class DownloadImgTask extends AsyncTask<String, Float, Bitmap> {
	private PorterDuffView pdView;

	public DownloadImgTask(PorterDuffView pdView) {
		this.pdView = pdView;
	}

	/** 下载准备工作。在UI线程中调用。 */
	@Override
	protected void onPreExecute() {
		 
	}

	/** 执行下载。在背景线程调用。 */
	@Override
	protected Bitmap doInBackground(String... params) {
		LogOut.out(this, "doInBackground: " + params[0]);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(params[0]);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
//			printHttpResponse(httpResponse);
			HttpEntity httpEntity = httpResponse.getEntity();
			long length = httpEntity.getContentLength(); 
			is = httpEntity.getContent();
			if (is != null) {
				baos = new ByteArrayOutputStream();
				byte[] buf = new byte[100];
				int read = -1;
				int count = 0;
				while ((read = is.read(buf)) != -1) {
					baos.write(buf, 0, read);
					count += read;
					publishProgress(count * 1.0f / length);
				}
				LogOut.out(this, "count=" + count + " length=" + length);
				byte[] data = baos.toByteArray();
				Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length);
				return bit;
			}               
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/** 更新下载进度。在UI线程调用。onProgressUpdate */
	@Override
	protected void onProgressUpdate(Float... progress) {
		pdView.setProgress(progress[0]);
	}

	/** 通知下载任务完成。在UI线程调用。 */
	@Override
	protected void onPostExecute(Bitmap bit) {
		pdView.setPorterDuffMode(false);
		pdView.setLoading(false);
		pdView.setImageBitmap(bit);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

//	private void printHttpResponse(HttpResponse httpResponse) {
//		Header[] headerArr = httpResponse.getAllHeaders();
//		for (int i = 0; i < headerArr.length; i++) {
//			Header header = headerArr[i];
//			LogOut.out(this, "name[" + header.getName() + "]value[" + header.getValue() + "]");
//		}
//		HttpParams params = httpResponse.getParams();
//		LogOut.out(this, String.valueOf(params));
//		LogOut.out(this, String.valueOf(httpResponse.getLocale()));
//	}
}