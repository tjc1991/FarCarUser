package com.cldxk.app.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.user.R;


public class CityChoiceActivity extends EBaseActivity implements SideBar.OnTouchingLetterChangedListener, 
										TextWatcher, OnItemClickListener, OnClickListener {
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private SideBar mSideBar;

	private TextView mDialog;

	private ListView mListView;

	private EditText mSearchInput;

	private CharacterParser characterParser;// 汉字转拼音

	private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类

	private List<PersonDto> sortDataList = new ArrayList<PersonDto>();
	
	private String JiLincity[][] = {			
			{"长春","c"},
			{"双阳","s"},
			{"农安","n"},
			{"九台","j"},
			{"德惠","d"},
			{"榆树市","y"},
			{"吉林市","j"},
			{"永吉","y"},
			{"桦甸","h"},
			{"蛟河","j"},
			{"舒兰","s"},
			{"延吉市","y"},
			{"汪清","w"},
			{"和龙","h"},
			{"安图","a"},
			{"敦化","g"},
			{"图们","t"},
			{"龙井市","l"},
			{"四平","s"},
			{"双辽","s"},
			{"公主岭","g"},
			{"通化","t"},
			{"集安","j"},
			{"白城","b"},
			{"通榆","t"},
			{"大安","t"},
			{"洮南","t"},
			{"镇赉","z"},
			{"辽源","l"},
			{"东辽","d"},
			{"东丰","d"},
			{"松原","s"},
			{"扶余","f"},
			{"乾安","q"},
			{"临江市","l"},
			{"靖宇","j"},
			{"长白山","c"},
			{"抚松","f"},
			{"白山","b"},
			{"珲春","h"},
			{"梅河口","m"},
			{"柳河","l"},
			{"辉南","h"},
			{"龙嘉机场","l"},
			{"伊通满族自治县","y"},
			{"龙井县","l"}
	};

	private SchoolFriendMemberListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_citychoice);
		mListView = (ListView) findViewById(R.id.school_friend_member);
		mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
		mDialog = (TextView) findViewById(R.id.school_friend_dialog);
		mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);

		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("城市选择");
		mSideBar.setTextView(mDialog);
		mSideBar.setOnTouchingLetterChangedListener(this);

		initData();
		mListView.setOnItemClickListener(this);

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		//初始化数据
		for(int i=0;i<JiLincity.length;i++){
			PersonDto cuser = new PersonDto();
			cuser.setName(JiLincity[i][0]);
			cuser.setSortLetters(JiLincity[i][1]);
			sortDataList.add(cuser);
		}

		fillData(sortDataList);
		// 根据a-z进行排序源数据
		Collections.sort(sortDataList, pinyinComparator);
		mAdapter = new SchoolFriendMemberListAdapter(this, sortDataList);
		mListView.setAdapter(mAdapter);
		mSearchInput.addTextChangedListener(this);

	}
	
	@Override
	public void onTouchingLetterChanged(String s) {
		int position = 0;
		// 该字母首次出现的位置
		if(mAdapter != null){
			position = mAdapter.getPositionForSection(s.charAt(0));
		}
		if (position != -1) {
			mListView.setSelection(position);
		}
	}
	

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString(), sortDataList);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr, List<PersonDto> list) {
		List<PersonDto> filterDateList = new ArrayList<PersonDto>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list;
		} else {
			filterDateList.clear();
			for (PersonDto sortModel : list) {
				String name = sortModel.getName();
				String suoxie = sortModel.getSuoxie();
				if (name.indexOf(filterStr.toString()) != -1 || suoxie.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		mAdapter.updateListView(filterDateList);
	}

	/**
	 * 填充数据
	 * 
	 * @param list
	 */
	private void fillData(List<PersonDto> list) {
		for (PersonDto cUserInfoDto : list) {
			if (cUserInfoDto != null && cUserInfoDto.getName() != null) {
				String pinyin = characterParser.getSelling(cUserInfoDto.getName());
				String suoxie = CharacterParser.getFirstSpell(cUserInfoDto.getName());

				cUserInfoDto.setSuoxie(suoxie);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				if ("1".equals(cUserInfoDto.getUtype())) {// 判断是否是管理员
					cUserInfoDto.setSortLetters("#");
				} else if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
					cUserInfoDto.setSortLetters(sortString);
				} else {
					cUserInfoDto.setSortLetters("#");
				}
			}
		}
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_citychoice;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), "选中"+position+"", Toast.LENGTH_SHORT).show();
		TextView c = (TextView) view.findViewById(R.id.title);
	    String playerChanged = c.getText().toString();
		Intent it = new Intent();
		it.putExtra("city", playerChanged);
		//设置结果码
		CityChoiceActivity.this.setResult(RESULT_OK,it);
		//关闭activity
		CityChoiceActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fragment_actionbar_back:
			finish();
			break;
		default:
			break;
		}
	}

}
