package com.cldxk.app.farcar.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.cldxk.app.base.BaseFragment;
import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.contact.CityChoiceActivity;
import com.cldxk.app.customview.MyPopupWindow;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.app.utils.ConnectType;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.ui.OrderDetailActivity;
import com.cldxk.farcar.user.R;

public class YuYueFragment extends BaseFragment implements OnClickListener, OnGetRoutePlanResultListener{
	
	private View view = null;
	
	private TextView people_tx = null;
	private Button add_btn = null;
		
	private LinearLayout orderpannellv = null;
	private LinearLayout timelv = null;
	private Button prepare_btn = null;
	private TextView time_tx = null;
	private TextView from_tx = null;
	private TextView go_tx = null;
	
	MyPopupWindow menuWindow = null;
	
	private Button search_money_btn = null;
	private TextView pay_money = null;
	private TextView pay_yuyue_people = null;
	
	private List<DrivingRouteLine> route = null;
    //搜索相关
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    
    private Boolean isSearchOk = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		isSearchOk = false;
		
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_customer_order, container, false);
		initMyLayout();
		
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
		return view;
	}
	
	public void initMyLayout(){

		orderpannellv = (LinearLayout) view.findViewById(R.id.order_pannel);
		timelv = (LinearLayout) view.findViewById(R.id.time_lv);
		
		time_tx = (TextView) view.findViewById(R.id.tv_time);
		from_tx = (TextView) orderpannellv.findViewById(R.id.tv_from);
		go_tx = (TextView) orderpannellv.findViewById(R.id.tv_to);

		timelv.setOnClickListener(this);
		from_tx.setOnClickListener(this);
		go_tx.setOnClickListener(this);
		
		pay_money = (TextView) view.findViewById(R.id.pay_yuyue_total_money);
		pay_yuyue_people = (TextView) view.findViewById(R.id.pay_yuyue_people);
		
		people_tx = (TextView) view.findViewById(R.id.yuyue_people);
		//默认1个人
		people_tx.setText("1");
		add_btn = (Button) view.findViewById(R.id.add_yuyue_btn);
		add_btn.setOnClickListener(this);
		
		prepare_btn = (Button) view.findViewById(R.id.prepare_yuyue_btn);
		prepare_btn.setOnClickListener(this);
		
		
		search_money_btn = (Button) view.findViewById(R.id.yuyue_search_btn);
		search_money_btn.setOnClickListener(this);
		
		route = new ArrayList<DrivingRouteLine>();
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_from:
			ConnectType.setCity_type(ConnectType.Type_city_from);
			Intent it_from = new Intent(getActivity(), CityChoiceActivity.class);
			//it_from.putExtra("Type_city", ConnectType.Type_city_from);
			startActivityForResult(it_from, 0);
			break;
		case R.id.tv_to:
			ConnectType.setCity_type(ConnectType.Type_city_go);
			Intent it_go = new Intent(getActivity(), CityChoiceActivity.class);
			//it_go.putExtra("Type_city", ConnectType.Type_city_go);
			startActivityForResult(it_go, 0);
			break;
		case R.id.time_lv:
			if(null == menuWindow){
				
				menuWindow = new MyPopupWindow(getActivity(),this);  
			}
            //显示窗口  
            menuWindow.showAtLocation(time_tx, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
            //设置layout在PopupWindow中显示的位置  
			
			break;
		case R.id.prepare_yuyue_btn:
			//提交预约订单
			SubmitYuYueOrder();
			break;
			
		case R.id.yuyue_search_btn:
			SearchButtonProcess();
			break;
			
			//添加人数
		case R.id.add_yuyue_btn:
			final EditText edit = new EditText(getActivity());
			new AlertDialog.Builder(getActivity()).setTitle("请输入乘车人数").setIcon(R.drawable.ys_icon_maomi)
				.setView(edit).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String sstr = edit.getText().toString();
						//更新人数
						if(Utils.isNumeric(sstr))
						{							
							people_tx.setText(sstr);
							dialog.dismiss();
						}else{
							Toast.makeText(getActivity(), "请输入数字", Toast.LENGTH_SHORT).show();
						}						
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						dialog.dismiss();
						
					}
				}).show();
			break;
			
		case R.id.time_cancel_btn:
			menuWindow.dismiss();
			break;
			
		case R.id.time_confirm_btn:
			String tstr = menuWindow.getChoiceTimeMsg();
			if(null != tstr && ! TextUtils.isEmpty(tstr))
				time_tx.setText(tstr);
			menuWindow.dismiss();
			break;
			
		default:
			break;
		}	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == getActivity().RESULT_OK){
			String cityname = data.getExtras().getString("city");
			if(cityname != null){
				if(ConnectType.getCity_type() == ConnectType.Type_city_from){					
					from_tx.setText(cityname);
				}else if(ConnectType.getCity_type() == ConnectType.Type_city_go){					
					go_tx.setText(cityname);
				}
				ConnectType.setCity_type(0);
			}
		}
		
	}
	
    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void SearchButtonProcess() {
        //重置浏览节点的路线数据
        route = null;

        //设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", from_tx.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", go_tx.getText().toString());

        // 实际使用中请对起点终点城市进行正确的设定
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
    }	
	
	
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		// TODO Auto-generated method stub
		
		//自驾行驶
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) { 
            route = result.getRouteLines();
            
            //获取距离数据
	        if (route == null) {
	            return;
	        }
            
	        getAllDistance(route);
        }
		
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void getAllDistance(List<DrivingRouteLine> list){
		
		int dist =0;
		for (int i = 0; i < list.size(); i++) {
			DrivingRouteLine line = list.get(i);
			//获取节点数据
	        //获取节结果信息
	        LatLng nodeLocation = null;
	        String nodeTitle = null;
	        List<DrivingStep> steps = line.getAllStep();
	        for(int j=0;j<steps.size();j++){	        	
	        	Object step = steps.get(j);	        	
	        	if (step instanceof DrivingRouteLine.DrivingStep) {
	        		//nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
	        		//nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
	        		dist += ((DrivingRouteLine.DrivingStep) step).getDistance();
	        	}
	        }
		}
		//Toast.makeText(this, dist+"", Toast.LENGTH_LONG).show();
		pay_money.setText((int)(dist*0.3/1000)+"");
		pay_yuyue_people.setVisibility(View.VISIBLE);
		
		//查询标志职位
		isSearchOk = true;
	}
	
    @Override
    public void onDestroy() {
        mSearch.destroy();
        super.onDestroy();
    }
    
    public void SubmitYuYueOrder(){
    	
    	String yuyuemsg = time_tx.getText().toString();
		if(null != yuyuemsg && !TextUtils.isEmpty(yuyuemsg))
		{
			
		}else{
			
			Toast.makeText(getActivity(), "请先选择预约时间", Toast.LENGTH_SHORT);	
			return;
		}
		
		if(isSearchOk ==false)
		{
			Toast.makeText(getActivity(), "请先点击查询路费按钮", Toast.LENGTH_SHORT);
			return;
		}
		
		if(null != from_tx.getText() && null != go_tx.getText()){
			
			final String city_from = from_tx.getText().toString();
			final String city_go = go_tx.getText().toString();
			
			//添加订单信息到数据库
			final YSOrderModel ysorder = new YSOrderModel();
			ysorder.setCityFrom(city_from);
			ysorder.setCityDest(city_go);
			ysorder.setOrderStatues(YSOrderStatus.YSOrder_Normal);
			ysorder.setOrderType(YSOrderType.Others);
			ysorder.setOrderYuYueMsg(yuyuemsg);
			String people_num = people_tx.getText().toString();
			String every_money = pay_money.getText().toString();
			
			//Log.i("tjc", "--->2");
			
			if(!Utils.isNumeric(every_money))
			{
				//Log.i("tjc", "--->3");
				return;
			}
			
			if(!Utils.isNumeric(people_num))
			{
				//Log.i("tjc", "--->4");
				return;
			}
			long money = Integer.parseInt(people_num)*Integer.parseInt(every_money);
			ysorder.setOrderPrice(money);
			String phone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
			if(phone.length() ==0 || null == phone)
			{
				Toast.makeText(getActivity(), "用户名错误", Toast.LENGTH_SHORT).show();
				return;
			}	
			//Log.i("tjc", "--->5");
			ysorder.setTelePhone(phone);
			//ysorder.setOrderType(YSOrderType.Others);
			
			
			ysorder.save(getActivity(), new SaveListener() {
				
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();

				}
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					
					Toast.makeText(getActivity(), "消息发送成功", Toast.LENGTH_SHORT).show();
					
					//保存订单信息到本地数据库
					
					//传送订单详情到Activity
					Intent it = new Intent(getActivity(),OrderDetailActivity.class);
					Bundle mBundle = new Bundle(); 
				    mBundle.putSerializable("ysorder",ysorder); 					    
				    it.putExtras(mBundle); 
				    
				    
				    //Log.i("tjc", "--->"+ysorder.getCreatedAt());
//				    Log.i("tjc", "--->"+ysorder.getCityDest());
//				    Log.i("tjc", "--->"+ysorder.getOrderPrice()+"");
					startActivity(it);
					
					isSearchOk = false;
					
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
					Toast.makeText(getActivity(), "消息发送失败", Toast.LENGTH_SHORT).show();
				
				}
			});
				
		}
    }
    

}
