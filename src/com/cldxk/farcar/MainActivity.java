package com.cldxk.farcar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cldxk.app.farcar.adapter.PagerAdapter;
import com.cldxk.app.farcar.fragment.NowCarFragment;
import com.cldxk.app.farcar.fragment.UserCenterFragment;
import com.cldxk.app.farcar.fragment.YuYueFragment;
import com.cldxk.farcar.user.R;

public class MainActivity extends FragmentActivity implements
OnPageChangeListener, OnCheckedChangeListener{
	
	private PagerAdapter pageadapter = null;
	private TextView titleText;
	private RadioButton navigationBtn[] = new RadioButton[3];
	// 页卡内容
	private ViewPager viewPager;
	// Tab页面列表
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	// 当前页面
	private NowCarFragment homefragment = null;
	private YuYueFragment messagefragment = null;
	private UserCenterFragment userfragment = null;
	
	private int index;	
	public static PagerAdapter mpAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		homefragment = new NowCarFragment();
		messagefragment = new YuYueFragment();
		userfragment = new UserCenterFragment();
		
		titleText = (TextView) findViewById(R.id.main_title_text);
		
		InitViewPager();
		
		navigationBtn[0] = (RadioButton) findViewById(R.id.type_tab_good);
		navigationBtn[1] = (RadioButton) findViewById(R.id.type_tab_cart);
		navigationBtn[2] = (RadioButton) findViewById(R.id.type_tab_user);
		navigationBtn[0].setChecked(true);// 初始化第一个按钮为选中
		titleText.setText("现在用车");
		for (int i = 0; i < navigationBtn.length; i++) {
			navigationBtn[i].setOnCheckedChangeListener(this);
		}

	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		viewPager.setOffscreenPageLimit(3);
		fragmentList.add(homefragment);
		fragmentList.add(messagefragment);
		fragmentList.add(userfragment);
		mpAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(mpAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	public PagerAdapter getAdapter() {
		return pageadapter;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		navigationBtn[arg0].setChecked(true);
	}

	/**
	 * 横竖屏切换
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		viewPager.setCurrentItem(index);
		super.onConfigurationChanged(newConfig);
	}

	/*
	 * 
	 * 页面切换监听器
	 */

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		HideKeyboard();
		int currentFragment = 0;
		if (!isChecked)
			return;
		switch (buttonView.getId()) {
		case R.id.type_tab_good:
			currentFragment = 0;
			titleText.setText("现在用车");
			break;
		case R.id.type_tab_cart:
			currentFragment = 1;
			titleText.setText("预约用车");
			break;
		case R.id.type_tab_user:
			currentFragment = 2;
			titleText.setText("个人中心");
			break;
		}
		viewPager.setCurrentItem(currentFragment);
	}

	// 切换到商品列表页面
	public void gotoshop() {
		viewPager.setCurrentItem(0);
		navigationBtn[0].isChecked();

	}

	// 切换到购物车页面
	public void gotocart() {
		viewPager.setCurrentItem(1);
		navigationBtn[1].isChecked();

	}
	
	public void HideKeyboard() {
		try {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}

	}
	


}
