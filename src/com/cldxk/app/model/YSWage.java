package com.cldxk.app.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class YSWage extends BmobObject implements Serializable{
	
	/**
	 * 序列化传输
	 */
	private static final long serialVersionUID = 1L;

	//用户当前余额
	private float userwage;
	
	//一对一指向司机用户
	private String userid;
	
	//设置数据库表名
//	public YSWage(){
//			this.setTableName("ys_wage");
//		}
//	

	public float getUserwage() {
		return userwage;
	}

	public void setUserwage(float userwage) {
		this.userwage = userwage;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}

	

}
