package com.cldxk.plug.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.user.R;

public class GplActivity extends EBaseActivity {
	private ViewGroup back;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.back = (ViewGroup) findViewById(R.id.title_left_layout_gpl);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_gpl;
	}
}
