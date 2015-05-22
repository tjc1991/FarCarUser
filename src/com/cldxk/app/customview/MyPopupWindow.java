package com.cldxk.app.customview;

import java.util.Calendar;

import com.cldxk.farcar.user.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

public class MyPopupWindow extends PopupWindow{
	
	private Button btn_confirm, btn_cancel;
	private View mMenuView;
	private Context context = null;
	
	private AbstractWheel day_wheel = null;
	private AbstractWheel hour_wheel = null;
	private AbstractWheel mine_wheel = null;
	
	private String[] darstr = {"今天", "明天", "后天"};
	private String[] hourstr = {"0点", "1点", "2点","3点","4点",
    		"5点", "6点", "7点","8点","9点",
    		"10点", "11点", "12点","13点","14点",
    		"15点", "16点", "17点","18点","19点",
    		"20点", "21点", "22点","23点"};
	private String[] minestr = {"0分", "10分", "20分","30分","40分",
	"50分"};
	
	//private TimePicker picker = null;
	//private DatePicker datapicker = null;
	public MyPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		
		this.context = context;
		
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.time_popdialog, null);
		btn_cancel = (Button) mMenuView.findViewById(R.id.time_cancel_btn);
		btn_confirm = (Button) mMenuView.findViewById(R.id.time_confirm_btn);
		//取消按钮
		btn_cancel.setOnClickListener(itemsOnClick);
		//设置按钮监听
		btn_confirm.setOnClickListener(itemsOnClick);
		
        initWheel(R.id.time_day);
        initWheel(R.id.time_hour);
        initWheel(R.id.time_mine);
						
        // set current time
//        Calendar c = Calendar.getInstance();
//        int curHours = c.get(Calendar.HOUR_OF_DAY);
//        int curMinutes = c.get(Calendar.MINUTE);
 
		this.setContentView(mMenuView);
				
		 // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT); 
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(h/2+20);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // 设置SelectPicPopupWindow弹出窗体动画效果 
        
        this.setAnimationStyle(R.style.PopupAnimation);  
		
	}
	
	   /**
     * Initializes spinnerwheel
     * @param id the spinnerwheel wheel Id
     */
    @SuppressLint("ResourceAsColor")
	private void initWheel(int id) {
    	
    	day_wheel = (AbstractWheel) mMenuView.findViewById(R.id.time_day);
    hour_wheel = (AbstractWheel) mMenuView.findViewById(R.id.time_hour);
    mine_wheel = (AbstractWheel) mMenuView.findViewById(R.id.time_mine);
    ArrayWheelAdapter<String> dayAdapter = new ArrayWheelAdapter<String>(context, darstr);
    ArrayWheelAdapter<String> hourAdapter =  new ArrayWheelAdapter<String>(context, hourstr );
    ArrayWheelAdapter<String> mineAdapter =  new ArrayWheelAdapter<String>(context, minestr );
    
    dayAdapter.setItemResource(R.layout.wheel_text_centered);
    dayAdapter.setItemTextResource(R.id.text);
    day_wheel.setViewAdapter(dayAdapter);    
    day_wheel.setCyclic(false);
    
    hourAdapter.setItemResource(R.layout.wheel_text_centered);
    hourAdapter.setItemTextResource(R.id.text);
    hour_wheel.setViewAdapter(hourAdapter);    
    hour_wheel.setCyclic(false);
    
    mineAdapter.setItemResource(R.layout.wheel_text_centered);
    mineAdapter.setItemTextResource(R.id.text);
    mine_wheel.setViewAdapter(mineAdapter);    
    mine_wheel.setCyclic(false);
    
    
    	
    	
    	
//        AbstractWheel wheel = getWheel(id);
//        ArrayWheelAdapter<String> ampmAdapter = null;
//        switch (id) {
//		case R.id.time_day:
//	        ampmAdapter =
//            new ArrayWheelAdapter<String>(context, new String[] {"今天", "明天", "后天"});
//			break;
//			
//		case R.id.time_hour:
//	       ampmAdapter =
//            new ArrayWheelAdapter<String>(context, new String[] {"0点", "1点", "2点","3点","4点",
//            		"5点", "6点", "7点","8点","9点",
//            		"10点", "11点", "12点","13点","14点",
//            		"15点", "16点", "17点","18点","19点",
//            		"20点", "21点", "22点","23点"});
//			break;
//			
//		case R.id.time_min:
//	        ampmAdapter =
//            new ArrayWheelAdapter<String>(context, new String[] {"0分", "10分", "20分","30分","40分",
//            		"50分"});
//			break;
//
//		default:
//			break;
//		}
        
//        ampmAdapter.setItemResource(R.layout.wheel_text_centered);
//        ampmAdapter.setItemTextResource(R.id.text);
//        wheel.setViewAdapter(ampmAdapter);
//        
//        wheel.setCyclic(false);
        

    }
    
    /**
     * Returns spinnerwheel by Id
     * @param id the spinnerwheel Id
     * @return the spinnerwheel with passed Id
     */
//    private AbstractWheel getWheel(int id) {
//        return (AbstractWheel) mMenuView.findViewById(id);
//    }
    
    public String getChoiceTimeMsg(){
    	
    		String msg = darstr[ day_wheel.getCurrentItem()]+""
    				+hourstr[ hour_wheel.getCurrentItem()]+""+minestr[ mine_wheel.getCurrentItem()]+"";
    	
    		//Log.i("tjc", msg);
    		return msg;
    }
	
}
