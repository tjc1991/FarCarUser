package com.cldxk.app.config;

public class CldxkConfig {
	
	public static final String APP_URL = "http://www.cldxk.com/ystaxi/";
	
	public static final String API_UPLOADS = APP_URL + "Public/Uploads/";
	public static final String API_REGISTER = APP_URL
			+ "api/client.php" +"?tag=wemall_user_regist";
	public static final String API_LOGIN = APP_URL
			+ "api/client.php" +"?tag=wemall_login_check";
	public static final String API_UPLOAD_IMG = APP_URL
			+ "api/client.php" +"?tag=wemall_update_head";
	
	public static final String API_REPEAT_PWD = APP_URL
			+ "api/client.php" +"?tag=wemall_rec_passwd";
	
	public static final String API_UPLOAD_VERSON = APP_URL
			+ "api/updateclient.json";

}
