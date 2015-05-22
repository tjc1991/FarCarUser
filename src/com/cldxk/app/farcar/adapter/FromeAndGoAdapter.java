package com.cldxk.app.farcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.user.R;

public class FromeAndGoAdapter extends BaseAdapter{
	
	private ArrayList<YSOrderModel> listItems = null;	
	//ArrayList<YSOrderModel>listItems = null;
	private Context mContext = null;
	
	//用于按钮点击事件回调接口
	//private Callback mCallback = null;
	

	public FromeAndGoAdapter( Context mContext,ArrayList<YSOrderModel> listItems) {
		super();
		this.mContext = mContext;
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<YSOrderModel> d) {
		this.listItems = d;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final ViewHolder holder;
        if(convertView == null){
	          //使用自定义的list_items作为Layout
	          convertView = LayoutInflater.from(mContext).inflate(R.layout.carmsg_list_layout, parent, false);
	          //使用减少findView的次数
			  holder = new ViewHolder();
			  holder.fromTv = ((TextView) convertView.findViewById(R.id.carmsg_from)) ;
			  holder.goTv = ((TextView) convertView.findViewById(R.id.carmsg_go));
			  holder.statusTv = ((TextView) convertView.findViewById(R.id.pay_status));	  
			  holder.orderTimeTv = ((TextView) convertView.findViewById(R.id.order_time));	
			  holder.yymsTv = ((TextView) convertView.findViewById(R.id.yuyue_msg_time));
			  holder.orderdate = ((TextView) convertView.findViewById(R.id.carmsg_date));
			  //设置标记
			  convertView.setTag(holder);
        }else{
      	  holder = (ViewHolder) convertView.getTag();
        }
		
		//填充数据
        YSOrderModel orderitem = (YSOrderModel)listItems.get(position);
        if (orderitem == null) {
            return null;
        }
        
        holder.goTv.setText(orderitem.getCityDest());
        holder.fromTv.setText(orderitem.getCityFrom());
        holder.orderTimeTv.setText(orderitem.getObjectId()+"");
        holder.orderdate.setText(orderitem.getCreatedAt());
        switch(orderitem.getOrderStatues()){        
	        case YSOrderStatus.YSOrder_Normal:
	        		holder.statusTv.setText("等待接单");
	        	break;
	        case YSOrderStatus.YSOrder_Select:
        			holder.statusTv.setText("已抢单,待支付");
        		break;
	        case YSOrderStatus.YSOrder_Pay:
        			holder.statusTv.setText("已支付");
        		break;
        		default :
        			break;
        	
        }
        switch(orderitem.getOrderType()){        
        case YSOrderType.Now:
        		holder.yymsTv.setText("");
        	break;
        case YSOrderType.Others:
    			holder.yymsTv.setText("预约时间: "+orderitem.getOrderYuYueMsg());
    		break;

    		default :
    			break;
    	
    }
               		
		return convertView;
	}
	
    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
		TextView fromTv;
		TextView goTv;
		TextView statusTv;
		TextView orderTimeTv;
		TextView yymsTv;
		TextView orderdate;
	}
	
}
