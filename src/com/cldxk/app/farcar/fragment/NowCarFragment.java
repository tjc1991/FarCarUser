package com.cldxk.app.farcar.fragment;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.cldxk.app.base.BaseFragment;
import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.contact.CityChoiceActivity;
import com.cldxk.app.customview.CustomProgressDialog;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.app.utils.ConnectType;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.ui.OrderDetailActivity;
import com.cldxk.farcar.user.R;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class NowCarFragment extends BaseFragment implements OnClickListener,OnGetRoutePlanResultListener{
	
	private View view = null;
	
	private TextView people_tx = null;
	private Button add_btn = null;
	
	private TextView cityFrom_btn = null;
	private TextView cityGo_btn = null;
	private Button call_btn = null;
	private Button search_money_btn = null;
	
	private TextView pay_money = null;
	private TextView pay_people = null;
	
	private CustomProgressDialog dialog = null;

	
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
		view = inflater.inflate(R.layout.cityandpay_layout, container, false);
		initMyLayout();
		
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
		return view;
	}
	
	public void initMyLayout(){
		cityFrom_btn = (TextView) view.findViewById(R.id.city_from_btn);
		cityGo_btn = (TextView) view.findViewById(R.id.city_go_btn);
		call_btn = (Button) view.findViewById(R.id.call_car_now);
		call_btn.setOnClickListener(this);
		cityFrom_btn.setOnClickListener(this);
		cityGo_btn.setOnClickListener(this);
		//back_btn.setOnClickListener(this);
		
		pay_money = (TextView) view.findViewById(R.id.pay_total_money);
		pay_people = (TextView) view.findViewById(R.id.pay_people);
		
		people_tx = (TextView) view.findViewById(R.id.people_number);
		//默认1个人
		people_tx.setText("1");
		add_btn = (Button) view.findViewById(R.id.add_people_btn);
		add_btn.setOnClickListener(this);
		
		search_money_btn = (Button) view.findViewById(R.id.search_money);
		search_money_btn.setOnClickListener(this);
		
		route = new ArrayList<DrivingRouteLine>();

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//pay_money.setText("");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.city_from_btn:
			ConnectType.setCity_type(ConnectType.Type_city_from);
			Intent it = new Intent(getActivity(), CityChoiceActivity.class);
			startActivityForResult(it, 0);
			break;
		case R.id.city_go_btn:
			ConnectType.setCity_type(ConnectType.Type_city_go);
			Intent it1 = new Intent(getActivity(), CityChoiceActivity.class);
			startActivityForResult(it1, 0);
			break;
		case R.id.call_car_now:
			
			if(isSearchOk ==false)
			{
				Toast.makeText(getActivity(), "请先点击查询路费按钮", Toast.LENGTH_SHORT);
				return;
			}
			
			if(null != cityFrom_btn.getText() && null != cityGo_btn.getText()){
				
				final String city_from = cityFrom_btn.getText().toString();
				final String city_go = cityGo_btn.getText().toString();
				
				//添加订单信息到数据库
				final YSOrderModel ysorder = new YSOrderModel();
				ysorder.setCityFrom(city_from);
				ysorder.setCityDest(city_go);
				ysorder.setOrderStatues(YSOrderStatus.YSOrder_Normal);
				ysorder.setOrderType(YSOrderType.Now);
				ysorder.setOrderYuYueMsg("");
				ysorder.setOrderGoPhone("");
				String people_num = people_tx.getText().toString();
				String every_money = pay_money.getText().toString();
				
				if(!Utils.isNumeric(every_money))
				{
					return;
				}
				
				if(!Utils.isNumeric(people_num))
				{
					return;
				}
				long money = Integer.parseInt(people_num)*Integer.parseInt(every_money);
				ysorder.setOrderPrice(money);
				
				Log.i("tjc", "-->p="+ysorder.getOrderPrice()+"");
				
				String phone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
				if(phone.length() ==0 || null == phone)
				{
					Toast.makeText(getActivity(), "用户名错误", Toast.LENGTH_SHORT).show();
					return;
				}				
				ysorder.setTelePhone(phone);
				ysorder.setOrderType(YSOrderType.Now);
				
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
//					    Log.i("tjc", "--->"+ysorder.getCityDest());
//					    Log.i("tjc", "--->"+ysorder.getOrderPrice()+"");
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
			break;
			
		case R.id.search_money:
			SearchButtonProcess();
			break;
		
			//添加人数
		case R.id.add_people_btn:
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
					cityFrom_btn.setText(cityname);
				}else if(ConnectType.getCity_type() == ConnectType.Type_city_go){					
					cityGo_btn.setText(cityname);
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
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", cityFrom_btn.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", cityGo_btn.getText().toString());

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
		pay_people.setVisibility(View.VISIBLE);
		
		//查询标志职位
		isSearchOk = true;
	}
	
    @Override
    public void onDestroy() {
        mSearch.destroy();
        super.onDestroy();
    }
	
	/**
	 * 显示美团进度对话框
	 * @param v
	 */
	public void showmeidialog(){
		dialog =new CustomProgressDialog(getActivity(), "正在发布中....",R.anim.frame);
		dialog.show();
	}
	
	public void closemeidialog(){
		if(dialog != null)
		dialog.dismiss();
	}	
	
}
