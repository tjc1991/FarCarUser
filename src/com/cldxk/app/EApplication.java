package com.cldxk.app;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

import com.baidu.mapapi.SDKInitializer;
import com.bmob.pay.tool.BmobPay;
import com.cldxk.app.utils.SharePreferenceUtil;
import com.lidroid.xutils.HttpUtils;

import android.app.Activity;
import android.app.Application;

public class EApplication extends Application {
	
	private HashMap<String, WeakReference<Activity>> activities = new HashMap<String, WeakReference<Activity>>();
	
	//HttpUtils单例
	public static HttpUtils gHttpClient = null;
	
	//SharePreference单例
	public static SharePreferenceUtil msharePreferenceUtil = null;
	
	public static HttpUtils getHttpClient() {
		return gHttpClient;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.gHttpClient = new HttpUtils();
		this.msharePreferenceUtil = new SharePreferenceUtil(getApplicationContext());
		
		//初始化短信验证SMSSDK
		SMSSDK.initSDK(this, "646269f92187", "c7a629b66396f141c1f3b63aaca8fb78");
	
        
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        
        //初始化Bmob
        Bmob.initialize(this, "a3f680ed82bd9eae507e81f0aab567f2");
        
        //BmobPay.init(this,"a3f680ed82bd9eae507e81f0aab567f2");
        
	}
		
	@Override
	public void onTerminate() {		 
		super.onTerminate();
	}
	
	///////////////////////////////////////////////
	public synchronized void putActivity(String clzName,Activity activity)
	{
		WeakReference<Activity> rf = new WeakReference<Activity>(activity);
		activities.put(clzName, rf);
		
	}
	
	
	public synchronized Activity getActivity(String clzName)
	{
		
		WeakReference<Activity> rf = activities.get(clzName);
		if(null == rf)
		{
			return null;
		}
		Activity activity = rf.get();
		
		if(null == activity)
		{
			activities.remove(clzName);
		}
		
		return activity;
	}

	public static SharePreferenceUtil getMsharePreferenceUtil() {
		return msharePreferenceUtil;
	}

	public static void setMsharePreferenceUtil(
			SharePreferenceUtil msharePreferenceUtil) {
		EApplication.msharePreferenceUtil = msharePreferenceUtil;
	}

}
