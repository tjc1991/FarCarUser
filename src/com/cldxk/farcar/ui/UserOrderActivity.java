package com.cldxk.farcar.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindCallback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter;
import com.cldxk.app.listview.XListView;
import com.cldxk.app.listview.XListView.IXListViewListener;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.user.R;

public class UserOrderActivity extends EBaseActivity implements IXListViewListener, OnClickListener,OnItemClickListener{
	
	private XListView list = null;
	
	private FromeAndGoAdapter orderAdapter = null;
	
	private ArrayList<YSOrderModel>listItems = null;
	
	private boolean PullRefresh = true;
	
	//语音合成
	private static String TAG = "tjc"; 	
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private String myphone="";
	
	//当前页号
	private int cur_page = 0;
	private int page_size = 0;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		cur_page = 0;
		page_size = 10;
		myphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
		
		initMyview();
	}
	
	public void initMyview(){
		
		list = (XListView) this.findViewById(R.id.msg_list);
		list.setPullRefreshEnable(PullRefresh);
		list.setPullLoadEnable(false);
		list.setXListViewListener(this);
		list.setOnItemClickListener(this);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("我的订单");
		
		listItems = new ArrayList<YSOrderModel>();
		//设置数据适配器
		orderAdapter = new FromeAndGoAdapter(this, listItems);
				
		list.setAdapter(orderAdapter);
		
		// /获取并更新数据
		GetCarMsgData();
		
								
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.fragmet_message;
	}

	//XLListView接口方法

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		getNewmsgData(cur_page);
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
	
	private void stoponLoad() {
		
		list.stopRefresh();
		list.stopLoadMore();	
	}
	
	public void GetCarMsgData(){
		
		//查询服务器获取数据
		BmobQuery query = new BmobQuery("ys_order");
		//按照时间降序
        query.order("-createdAt");
        
		//添加查询参数
		if(myphone != null){		
			//key /value
			query.addWhereContains("telePhone", myphone);
		}  
		
		//分页查询
		query.setLimit(page_size);
		query.setSkip(0);
		
		//执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(this, new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				if(arg0.length()<= 0){
					Toast.makeText(getApplicationContext(), "没有最新数据了", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(getApplicationContext(), "加载"+arg0.length()+""+"条订单", Toast.LENGTH_SHORT).show();
				//Log.i("tjc", arg0.toString());
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
				for (YSOrderModel ysOrderModel : orders) {
					
					listItems.add(ysOrderModel);
					Log.i("tjc", "--->price="+ysOrderModel.getOrderPrice());

				}
								
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
				//当前索引+1
				cur_page++;
								
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
	}
	
	public void getNewmsgData(final int page){
				
		BmobQuery query = new BmobQuery("ys_order");
		
		query.order("-createdAt");

		//添加查询参数
		if(myphone != null){		
			//key /value
			query.addWhereContains("telePhone", myphone);
		}   
		
		//分页查询
		query.setLimit(page_size);
		query.setSkip(page*page_size);
		
		query.findObjects(this, new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				if(arg0.length() <= 0)
				{
					Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();
					return;
				}else{
					Toast.makeText(getApplicationContext(), "加载"+arg0.length()+""+"条新订单", Toast.LENGTH_SHORT).show();	
				}
				Log.i("tjc", arg0.toString());
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
				for (YSOrderModel ysOrderModel : orders) {
					listItems.add(0,ysOrderModel);
					
					Log.i("tjc", "--->price="+ysOrderModel.getOrderPrice());

				}
				
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
				cur_page++;
				//Log.i("tjc", "-->"+cur_page+"");
				
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
				
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.fragment_actionbar_back:
			finish();
			break;	
			
			default :
				break;
		}		
				
	}

	//Listview 方法
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		// TODO Auto-generated method stub
		
		//Log.i(TAG, "--->"+pos+"");
		
		//传送订单详情到Activity
		Intent it = new Intent(UserOrderActivity.this,OrderDetailActivity.class);
		Bundle mBundle = new Bundle();   
	    mBundle.putSerializable("ysorder",listItems.get(pos-1)); 					    
	    it.putExtras(mBundle);   
		startActivity(it);
		
		//结束Activity
		finish();		
	}	
	
}
