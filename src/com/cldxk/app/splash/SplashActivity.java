package com.cldxk.app.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.user.R;

/**
 * 
 * 闪屏
 * 
 * @author liudewei
 *
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends EBaseActivity {
	private Handler mMainHandler = new Handler() {

		public void handleMessage(Message msg) {
			String userphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
			if(null != userphone && !TextUtils.isEmpty(userphone))
			{
				Intent it = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(it);
				finish();
			}else{
				Intent it = new Intent(SplashActivity.this, WelcomeActivity.class);
				startActivity(it);	
				finish();
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.wemall_splash);
		
//		@SuppressWarnings("unused")
//		SQLProcess chushihuaProcess=new SQLProcess(this);//初始化数据库,防止FC
//		// ////////////////////初始化第三方SDK组件///////////////////////////////////
		//短信验证SDK
		mMainHandler.sendEmptyMessageDelayed(0, 2000);
		
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.common_start_splash;
	}

}