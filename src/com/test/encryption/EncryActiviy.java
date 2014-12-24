package com.test.encryption;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.test.R;

/**
 * 
 * @author litz
 *
 */
public class EncryActiviy extends Activity {
	
	TextView tv;
	StringBuffer sb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_item);  
		 
		tv = (TextView)findViewById(R.id.tv);
		
		String key = "1234567890asdfghjkl";
		String data = "hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！" +
				"hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！" +
				"hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！" +
				"hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！hello你好！";
		
		String enData = DESUtil.encode(key, data);
		System.out.println("密文："+enData+" 长度："+enData.length());
		String deData = DESUtil.decode(key, enData);
		System.out.println("明文："+deData);
		
//        String msg = "使用3DES对数据进行加密";  
        System.out.println("【加密前】：" + data);  
        // 加密  
        byte[] secretArr = DES3Utils.encryptMode(data.getBytes());  
        String mm = DESUtil.byte2String(secretArr);
        System.out.println("【加密后】：" + mm+" 长度："+mm.length());  
        // 解密  
        byte[] myMsgArr = DES3Utils.decryptMode(secretArr);  
        System.out.println("【解密后】：" + new String(myMsgArr));  
        
        System.out.println("【AES加密前】：" + data);  
        String mm2 = AESUtil.encrypt(key, data);
        System.out.println("【AES加密后】：" + mm2+" 长度："+mm2.length());  
        String jm = AESUtil.decrypt(key, mm2);
        System.out.println("【AES解密后】：" + jm);  
	}
	

  }
    
