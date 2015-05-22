package com.cldxk.plug.user;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.YSUserType;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.user.R;

public class RegistActivity extends EBaseActivity {
	private ViewGroup backbar;
	private int registstate = -1;
	private TextView wemall_phonenumber,
			wemall_register_button, wemall_agree_details;
	private EditText wemall_register_password,
			wemall_register_confirm_password, name;
	private CheckBox agree;
	private Handler handler = null;
	private ProgressBar registloadingBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		backbar = (ViewGroup) findViewById(R.id.title_left_layout_regist);
		wemall_phonenumber = (TextView) findViewById(R.id.wemall_register_phonenum);
		//wemall_register_name = (EditText) findViewById(R.id.wemall_register_name);
		wemall_register_password = (EditText) findViewById(R.id.wemall_register_password);
		name = (EditText) findViewById(R.id.wemall_register_name);
		//peoplecard = (EditText) findViewById(R.id.wemall_car_name);
		wemall_register_confirm_password = (EditText) findViewById(R.id.wemall_register_confirm_password);
		wemall_register_button = (TextView) findViewById(R.id.wemall_register_button);
		wemall_phonenumber.setText(bundle.getString("phone"));
		registloadingBar = (ProgressBar) findViewById(R.id.registloadingBar);
		agree = (CheckBox) findViewById(R.id.wemall_is_agree);
		this.wemall_agree_details = (TextView) findViewById(R.id.wemall_agree_details);
		wemall_agree_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegistActivity.this, GplActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.wemall_slide_in_right,
						R.anim.wemall_slide_out_left);
			}
		});
		backbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				RegistActivity.this.finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
		wemall_register_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 隐藏键盘
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				checkregist();
			}
		});
	}
	
	public void regist() {
		
		final String usertelephone = wemall_phonenumber.getText().toString();
		String userpwd = Utils.MD5(wemall_register_password.getText().toString());
		final String nickname = name.getText().toString();
		YSUser ysuser = new YSUser();
		ysuser.setUsername(usertelephone);//设置用户名
		ysuser.setPassword(userpwd);//设置密码
		ysuser.setUserNike(nickname);//设置用户昵称
		ysuser.setUserPower(YSUserType.User_Power_normal);//允许用户使用
		ysuser.setUserType(YSUserType.User_Ck);//设置用户类型,乘客
		//用户登录
		ysuser.signUp(getApplicationContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				//保存信息
				msharePreferenceUtil.saveSharedPreferences("userName", usertelephone);
				//保存用户昵称
				msharePreferenceUtil.saveSharedPreferences("userNick", nickname);
				
				//YSUser ys = (YSUser) YSUser.getCurrentUser(getApplicationContext());
				//保存用户Id
	    	   		//msharePreferenceUtil.saveSharedPreferences("userobjId", ys.getObjectId()+"");
				
				//YSUser ys = (YSUser) YSUser.getCurrentUser(getApplicationContext());
				//msharePreferenceUtil.saveSharedPreferences("userCar", usercard);
				
				Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
			}
		});		
		
		
		
		
		
		
		
		
//		RequestParams params = new RequestParams();
//		//参数传递方式
//		List<NameValuePair> values = new ArrayList<NameValuePair>(); 
//		values.add(new BasicNameValuePair("phone", wemall_phonenumber.getText().toString()));
//		values.add(new BasicNameValuePair("name", Utils.getBASE64(wemall_register_name.getText().toString())));
//		values.add(new BasicNameValuePair("passwd", wemall_register_password.getText().toString()));
//		params.addBodyParameter(values);
//		httpClient.send(HttpMethod.POST, CldxkConfig.API_REGISTER, params, new RequestCallBack<String>(){
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<String> arg0) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
//				finish();
//			}
//			
//			
//		});
	}	
	
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

	public void checkregist() {
		if (wemall_phonenumber.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "手机号为空,请填写", Toast.LENGTH_SHORT).show();

		} else if (wemall_register_password.getText().toString().length() == 0) {
			Toast.makeText(this, "密码呢?,切莫逗我玩", Toast.LENGTH_SHORT).show();
		} else if (wemall_register_password.getText().toString().length() < 6) {
			Toast.makeText(this, "密码有点短额...", Toast.LENGTH_SHORT).show();
		} else if (!(wemall_register_password.getText().toString()
				.equals(wemall_register_confirm_password.getText().toString()))) {
			Toast.makeText(this, "两次输入的密码好像不一样!", Toast.LENGTH_SHORT).show();
		} else if (!(agree.isChecked())) {
			Toast.makeText(this, "您必须同意注册条款才能继续", Toast.LENGTH_SHORT).show();
		}else if (name.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "用户名为空,请填写", Toast.LENGTH_SHORT).show();

		}else {
			registloadingBar.setVisibility(View.VISIBLE);
			regist();
		}

	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_user_register;
	}
}
