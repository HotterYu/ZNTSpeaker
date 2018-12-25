
package com.znt.push.entity; 

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: Constant 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-21 下午3:32:20  
 */
public class PushModelConstant
{
	public static String WORK_DIR = "/ZNTSpeaker";
	public static String WIFI_HOT_PWD = "";
	public static String UUID_TAG = "_znt_ios_rrdg_sp";
	public static String IOS_TAG = "_znt_ios_rrdg_sp";
	public static String PKG_END = "znt_pkg_end";
	
	public static final String MEDIA_ADD = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_REMOVE = "android.intent.action.MEDIA_REMOVED";
	
	public static String PlayPermission = "0";
	
	public static final int DB_VERSION = 1;
	public static final int LUCNHER_VERSION_CODE = 27;
	public static final int INSTALL_VERSION_CODE = 3;
	public static String DEVICE_MAC = "";
	
	public static int UPDATE_TYPE = 0;
	
	public volatile static int STATUS_CHECK_TIME_MAX = 8;
	
	public static long minRemainSize = 1024 * 1024 * 300;
	
	public static boolean isBoxVersion = true;
	
	public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
	

	public static final int CALL_BACK_WIFI_CONNECT_START = 1000;
	public static final int CALL_BACK_WIFI_CONNECT_FAIL = 1001;
	public static final int CALL_BACK_WIFI_CONNECT_SUCCESS = 1002;
	
	public static final int CALL_BACK_PUSH_SUCCESS = 1003;
	public static final int CALL_BACK_PUSH_FAIL = 1004;
	public static final int CALL_BACK_PUSH_NOT_REGISTER = 1005;
	public static final int CALL_BACK_PUSH_SPACE_CHECK = 1006;
	public static final int CALL_BACK_PUSH_CHECK = 1007;
	public static final int CALL_BACK_PUSH_DEV_REGISTER = 1008;
}
 
