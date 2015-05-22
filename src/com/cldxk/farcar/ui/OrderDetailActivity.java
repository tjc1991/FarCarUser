package com.cldxk.farcar.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.alibaba.fastjson.JSON;
import com.bmob.pay.tool.BmobPay;
import com.bmob.pay.tool.PayListener;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.model.YSWage;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.user.R;

public class OrderDetailActivity extends EBaseActivity implements OnClickListener{
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private TextView paystatus_tx = null;
	private TextView ordermoney_tx = null;
	private TextView othermoney_tx = null;
	private Button add_btn = null;
	
	private Button submit_btn = null;
	private Button cancel_btn = null;
	
	private LinearLayout paylv = null;
	private TextView cittyfrom_tx = null;
	private TextView citygo_tx = null;
	private TextView ordertime_tx = null;
	private TextView curmsg_tx = null;
	
	private TextView yuyuetime_tx = null;
	
	private RadioGroup pay_radiogroup = null;  
	private RadioButton ali_radio,wx_radio; 
	
	private int pay_way = 1;
	
	private YSOrderModel ysorder  = null;
	
	private String driverid = "";
	private float drivermoney = 0.0f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		driverid = "";
		drivermoney = 0.0f;
		
		pay_way = 1;//默认支付宝
		
		initMylayout();
		
		ysorder = (YSOrderModel) this.getIntent().getSerializableExtra("ysorder");
		
		if(null != ysorder)
		{
			Log.i("tjc", "--->"+"not null");
			
			Log.i("tjc", "--->price="+ysorder.getOrderPrice());

			
			cittyfrom_tx.setText(ysorder.getCityFrom());
			citygo_tx.setText(ysorder.getCityDest());
			ordertime_tx.setText(ysorder.getObjectId()+"");
			//设置订单预定时间
			if(ysorder.getOrderType() == YSOrderType.Others){
				yuyuetime_tx.setText("预约时间: "+ysorder.getOrderYuYueMsg());
			}
	        switch(ysorder.getOrderStatues()){        
	        case YSOrderStatus.YSOrder_Normal:
	        		paystatus_tx.setText("等待接单");
	        	break;
	        case YSOrderStatus.YSOrder_Select:
	        		paystatus_tx.setText("已抢单,待支付");
        		break;
	        case YSOrderStatus.YSOrder_Pay:
	        	    paystatus_tx.setText("已支付");
        		break;
        		default :
        			break;      	
	        }			
			ordermoney_tx.setText(ysorder.getOrderPrice()+"");
			//如果已经支付完毕,则隐藏支付控件
			if(ysorder.getOrderStatues() == YSOrderStatus.YSOrder_Pay){
				
				HidePayCompont();
			}
		}
		
	}
	
	public void initMylayout(){
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("订单详情");
		paylv = this.findLinearLayoutById(R.id.pay_order_pannel);
		
		paystatus_tx = (TextView) paylv.findViewById(R.id.pay_status);
		ordermoney_tx = this.findTextViewById(R.id.pay_order_money);
		othermoney_tx = this.findTextViewById(R.id.pay_order_other_money);
		add_btn = this.findButtonById(R.id.add_money_btn);
		add_btn.setOnClickListener(this);
		
		submit_btn = this.findButtonById(R.id.sublit_order);
		cancel_btn = this.findButtonById(R.id.cancel_order);
		submit_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		
		cittyfrom_tx = (TextView) paylv.findViewById(R.id.carmsg_from);
		citygo_tx = (TextView) paylv.findViewById(R.id.carmsg_go);
		ordertime_tx = (TextView) paylv.findViewById(R.id.order_time);
		yuyuetime_tx = (TextView) paylv.findViewById(R.id.yuyue_msg_time);
		curmsg_tx = (TextView) this.findViewById(R.id.pay_tmp);
		
		pay_radiogroup = (RadioGroup) this.findViewById(R.id.pay_radiogroup);
		ali_radio = (RadioButton) this.findViewById(R.id.ali_radio);
		wx_radio = (RadioButton) this.findViewById(R.id.wx_radio);
		
		pay_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                // TODO Auto-generated method stub  
                if(checkedId==ali_radio.getId())  
                {  
                  pay_way = 1;  
                  Log.i("tjc", "---->1");
                }else if(checkedId==wx_radio.getId())  
                {  
                		pay_way = 2;   
                		Log.i("tjc", "---->2");
                }
            }  
        });   
		
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_orderdetail;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
		case R.id.fragment_actionbar_back:
			finish();
			break;	
			
		case R.id.add_money_btn:
			addOtherMoney();
			break;
			
		case R.id.cancel_order:
			
			//取消订单
			UpdateMyOrderStatus(YSOrderStatus.YSOrder_Normal);
			break;
			
		case R.id.sublit_order:
			CheckMyOrderStatus();
			//updateDriverMoney();
//			if(pay_way == 1)
//			{				
//				//支付宝支付
//				
//				payAli();
//				
//			}else if(pay_way == 2)
//			{
//				payWeiXin();
//				
//			}
			
			break;
			
		default:
			break;
	}
		
	}
	
	public void addOtherMoney(){
		
		final EditText edit = new EditText(this);
		new AlertDialog.Builder(this).setTitle("请输入差补价(元)").setIcon(R.drawable.ys_icon_maomi)
			.setView(edit).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String sstr = edit.getText().toString();
					//更新人数
					if(Utils.isNumeric(sstr))
					{							
						othermoney_tx.setText(sstr+"");
						dialog.dismiss();
					}else{
						Toast.makeText(getApplicationContext(), "请输入数字", Toast.LENGTH_SHORT).show();
					}						
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					dialog.dismiss();
					
				}
			}).show();
	}
	
	/**
	 * 得到支付总费用
	 * @return
	 */
	@SuppressWarnings("null")
	public float getPayMoney(){
		
		//分2种情况,一种第一次支付,一种从订单中再次支付
		
		String other_money = othermoney_tx.getText().toString();
		//Log.i("tjc","as"+other_money+"");
		if(null != other_money && !TextUtils.isEmpty(other_money)){			
			if(Utils.isNumeric(other_money)){
				Log.i("tjc", "---->haha");
				//得到总费用
				return ysorder.getOrderPrice()+Integer.parseInt(other_money);
			}else{
				
				return ysorder.getOrderPrice();
			}
		}else{
			
			return ysorder.getOrderPrice();
		}
		
		//return 0.02f;
	}
	
	/**
	 * 支付宝支付
	 */
	public void payAli()
	{
		Log.i("tjc", getPayMoney()+"");
		new BmobPay(this).pay(getPayMoney(), "打车付费","坐车费用", new PayListener() {
			
			@Override
			public void unknow() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void succeed() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
				
				//刷新订单状态
				if(null != ysorder){
					paystatus_tx.setText("已支付");
					//UpdateMyOrderStatus(YSOrderStatus.YSOrder_Pay);
					updateDriverMoney();
				}
			}
			
			@Override
			public void orderId(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void fail(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 微信支付
	 */
	public void payWeiXin()
	{
		//微信支付
		new BmobPay(this).payByWX(getPayMoney(), "打车付费", new PayListener() {
			
			@Override
			public void unknow() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void succeed() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
				//刷新订单状态
				if(null != ysorder){
					paystatus_tx.setText("已支付");
					
					//更新司机钱包
					updateDriverMoney();					
				}
			}
			
			@Override
			public void orderId(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void fail(int code, String reson) {
				// TODO Auto-generated method stub
				
				// 当code为-2,意味着用户中断了操作
				// code为-3意味着没有安装BmobPlugin插件
				if (code == -3) {
					new AlertDialog.Builder(OrderDetailActivity.this)
							.setMessage(
									"监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)还是用支付宝支付")
							.setPositiveButton("安装",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											installBmobPayPlugin("BmobPayPlugin.apk");
											dialog.dismiss();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create().show();
				} else {
					Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
				}					
			
			}
		});
		
	}
	
	
	void installBmobPayPlugin(String fileName) {
		try {
			InputStream is = getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + fileName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file),
					"application/vnd.android.package-archive");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * 隐藏支付控件
	 */
	public void HidePayCompont()
	{
		othermoney_tx.setVisibility(View.GONE);
		add_btn.setVisibility(View.GONE);
		
		submit_btn.setVisibility(View.GONE);
		cancel_btn.setVisibility(View.GONE);
		
		curmsg_tx.setVisibility(View.GONE);
		
		pay_radiogroup.setVisibility(View.GONE);
		ali_radio.setVisibility(View.GONE);
		wx_radio.setVisibility(View.GONE);
		
	}
	
	/**
	 * 更新订单状态
	 * @param orderstatus
	 */
	public void UpdateMyOrderStatus(final int orderstatus){
		
		final ProgressDialog progressDialog =ProgressDialog.show(this, 
				"支付状态", "正在提交订单信息...");
		progressDialog.setCancelable(false);
		
		String orderId = ysorder.getObjectId();
		
		YSOrderModel updateorder = new YSOrderModel();
		if(orderstatus == YSOrderStatus.YSOrder_Normal){
			
			//设置价格
			updateorder.setOrderPrice(ysorder.getOrderPrice());
			//取消接单电话
			updateorder.setOrderGoPhone("");
		}
		updateorder.setOrderPrice(ysorder.getOrderPrice());
		updateorder.setOrderStatues(orderstatus);
		updateorder.update(this, orderId, new UpdateListener() {

		    @Override
		    public void onSuccess() {
		        // TODO Auto-generated method stub
		    		progressDialog.dismiss();
		    		if(orderstatus == YSOrderStatus.YSOrder_Pay){		    			
		    			//Toast.makeText(getApplicationContext(), "司机已成功收款", Toast.LENGTH_SHORT).show();
		    		
				    	//结束本次交易
				    	finish();
		    				    		
		    		}else if(orderstatus == YSOrderStatus.YSOrder_Normal){
		    			Toast.makeText(getApplicationContext(), "订单已取消", Toast.LENGTH_SHORT).show();
		    		}
				    				    				    		
		    }

		    @Override
		    public void onFailure(int code, String msg) {
		        // TODO Auto-generated method stub
		    		progressDialog.dismiss();
		    		if(orderstatus == YSOrderStatus.YSOrder_Pay){		    			
		    			Toast.makeText(getApplicationContext(), "司机收款失败", Toast.LENGTH_SHORT).show();
		    		}else if(orderstatus == YSOrderStatus.YSOrder_Normal){
		    			Toast.makeText(getApplicationContext(), "订单取消失败", Toast.LENGTH_SHORT).show();
		    		}
		    }
		});
	}
	
	
	/**
	 * 查询订单状态
	 * @param orderstatus
	 */
	public void CheckMyOrderStatus(){
		
//		final ProgressDialog progressDialog =ProgressDialog.show(this, 
//				"订单状态", "正在查询预定状态...");
//		progressDialog.setCancelable(false);
		
		String orderId = ysorder.getObjectId();
		
		Log.i("tjc", "orderId="+orderId+"");
		
		//查询服务器获取数据
		BmobQuery query = new BmobQuery("ys_order");
		query.addWhereContains("objectId", orderId);
		
		query.findObjects(this, new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//progressDialog.dismiss();
								
				if(arg0.length() == 1){
					
					com.alibaba.fastjson.JSONArray jsonarray = JSON.parseArray(arg0.toString());
					
					com.alibaba.fastjson.JSONObject jsonobj = jsonarray.getJSONObject(0);
					//订单状态
					int orderst = jsonobj.getIntValue("orderStatues");
					
					Log.i("tjc", "orderId="+orderst+"");
					
					if(orderst == YSOrderStatus.YSOrder_Select){						
						//支付
						paystatus_tx.setText("已抢单,待支付");	
						Log.i("tjc", "orderId="+ysorder.getOrderGoPhone()+"");

						getDriverId(ysorder.getOrderGoPhone());
																									
					}else if(orderst == YSOrderStatus.YSOrder_Normal){
						Toast.makeText(getApplicationContext(), "等待司机接单", Toast.LENGTH_SHORT).show();
					}else if(orderst == YSOrderStatus.YSOrder_Pay){
						Toast.makeText(getApplicationContext(), "已支付,无需重新支付", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				//progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		
		
//		query.getObject(getApplicationContext(), orderId, new GetListener<YSOrderModel>() {
//			
//			@Override
//			public void onSuccess(YSOrderModel mymodel) {
//				// TODO Auto-generated method stub
//				progressDialog.dismiss();
//				Log.i("tjc", "-->"+mymodel.getObjectId()+"");
//				Toast.makeText(getApplicationContext(), "查询成功", Toast.LENGTH_SHORT).show();
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//				progressDialog.dismiss();
//				Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
//				
//			}
//		});
//		

//		updateorder.update(this, orderId, new UpdateListener() {
//
//		    @Override
//		    public void onSuccess() {
//		        // TODO Auto-generated method stub
//		    		progressDialog.dismiss();
//		    		if(orderstatus == YSOrderStatus.YSOrder_Pay){		    			
//		    			Toast.makeText(getApplicationContext(), "司机已成功收款", Toast.LENGTH_SHORT).show();
//		    		}else if(orderstatus == YSOrderStatus.YSOrder_Normal){
//		    			Toast.makeText(getApplicationContext(), "订单已取消", Toast.LENGTH_SHORT).show();
//		    		}
//		    		
//		    		finish();
//				    				    				    		
//		    }
//
//		    @Override
//		    public void onFailure(int code, String msg) {
//		        // TODO Auto-generated method stub
//		    		progressDialog.dismiss();
//		    		if(orderstatus == YSOrderStatus.YSOrder_Pay){		    			
//		    			Toast.makeText(getApplicationContext(), "司机收款失败", Toast.LENGTH_SHORT).show();
//		    		}else if(orderstatus == YSOrderStatus.YSOrder_Normal){
//		    			Toast.makeText(getApplicationContext(), "订单取消失败", Toast.LENGTH_SHORT).show();
//		    		}
//		    }
//		});
	}
	
	
//	public void getDriverId(String dphone){
//		final ProgressDialog dialog = ProgressDialog.show(this, "转账给司机", "正在转账中...");
//		dialog.setCancelable(false);
//		BmobQuery<YSUser> query = new BmobQuery<YSUser>();
//		query.addWhereEqualTo("username", dphone);
//		query.findObjects(this, new FindListener<YSUser>() {
//		    @Override
//		    public void onSuccess(List<YSUser> object) {
//		        // TODO Auto-generated method stub
//		    	dialog.dismiss();
//		    		if(object.size() ==1){
//		    			driverid = object.get(0).getObjectId();
//		    			//drivermoney = object.get(0).getUserwage();
//		    			
//		    			
//		    			
//		    			
//		    			
//		    			
//		    			Log.i("tjc", "driverid="+driverid);
//		    			Log.i("tjc", "drivermoney="+drivermoney+"");
//		    			
//		    			
//		    			//完成支付
//					if(pay_way == 1)
//					{				
//						//支付宝支付							
//						payAli();												
//					}else if(pay_way == 2)
//					{
//						//微信
//						payWeiXin();
//					}														    			
//		    		}
//		    }
//		    @Override
//		    public void onError(int code, String msg) {
//		        // TODO Auto-generated method stub
//		    	dialog.dismiss();
//			       Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();
//
//		    }
//		});
//		
//	}
	
	public void getDriverId(String dphone){
		final ProgressDialog dialog = ProgressDialog.show(this, "转账给司机", "正在转账中...");
		dialog.setCancelable(false);
		BmobQuery<YSWage> query = new BmobQuery<YSWage>();
		query.addWhereEqualTo("userid", dphone);
		query.findObjects(this, new FindListener<YSWage>() {
		    @Override
		    public void onSuccess(List<YSWage> object) {
		        // TODO Auto-generated method stub
		    	dialog.dismiss();
		    		if(object.size() ==1){
		    			driverid = object.get(0).getObjectId();
		    			drivermoney = object.get(0).getUserwage();
		    					    					    					    				    			
		    			Log.i("tjc", "driverid="+driverid);
		    			Log.i("tjc", "drivermoney="+drivermoney+"");
		    			
		    			
		    			//完成支付
					if(pay_way == 1)
					{				
						//支付宝支付							
						payAli();												
					}else if(pay_way == 2)
					{
						//微信
						payWeiXin();
					}														    			
		    		}
		    }
		    @Override
		    public void onError(int code, String msg) {
		        // TODO Auto-generated method stub
		    	dialog.dismiss();
			       Toast.makeText(getApplicationContext(), "失败", Toast.LENGTH_SHORT).show();

		    }
		});
		
	}
	
	public void updateDriverMoney(){
		
		final ProgressDialog dialog = ProgressDialog.show(this, "转账给司机", "正在转账中...");
		dialog.setCancelable(false);
		
		YSWage newwage = new YSWage();
		float money = getPayMoney() + drivermoney;
		newwage.setUserwage(money);
				
		Log.i("tjc", "--->did="+driverid+"");
		
		//driverid = "16444ddc9a";
		newwage.update(this,driverid,new UpdateListener() {
		    @Override
		    public void onSuccess() {
		        // TODO Auto-generated method stub
		    		//在更新服务器二
		    	dialog.dismiss();
		        //Toast.makeText(getApplicationContext(),"转账成功", Toast.LENGTH_SHORT).show();

		    	UpdateMyOrderStatus(YSOrderStatus.YSOrder_Pay);
		    	
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        // TODO Auto-generated method stub
		     	dialog.dismiss();
		     	Log.i("tjc", "--->msg="+msg);
		        Toast.makeText(getApplicationContext(),"转账失败", Toast.LENGTH_SHORT).show();
		    }
		});		
		
	}

	
	
//	public void updateDriverMoney(){
//		
//		final ProgressDialog dialog = ProgressDialog.show(this, "转账给司机", "正在转账中...");
//		dialog.setCancelable(false);
//		
//		YSUser newUser = new YSUser();
//		float money = getPayMoney() + drivermoney;
//		//newUser.setUserwage(money);
//		
//		
//		Log.i("tjc", "--->did="+driverid+"");
//		
//		driverid = "16444ddc9a";
//		newUser.update(this,driverid,new UpdateListener() {
//		    @Override
//		    public void onSuccess() {
//		        // TODO Auto-generated method stub
//		    		//在更新服务器二
//		    	dialog.dismiss();
//		        Toast.makeText(getApplicationContext(),"转账成功", Toast.LENGTH_SHORT).show();
//
//		    	//UpdateMyOrderStatus(YSOrderStatus.YSOrder_Pay);
//		    	
//		    }
//		    @Override
//		    public void onFailure(int code, String msg) {
//		        // TODO Auto-generated method stub
//		     	dialog.dismiss();
//		     	Log.i("tjc", "--->msg="+msg);
//		        Toast.makeText(getApplicationContext(),"转账失败", Toast.LENGTH_SHORT).show();
//		    }
//		});		
//		
//	}
	

}

//4651a0510abc0954c6745b3e3586d69e
//111.27.252.255
