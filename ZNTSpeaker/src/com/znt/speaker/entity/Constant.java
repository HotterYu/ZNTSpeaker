
package com.znt.speaker.entity; 

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: Constant 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-21 下午3:32:20  
 */
public class Constant
{
	public static String WORK_DIR = "/ZNTSpeaker";
	public static String WIFI_HOT_PWD = "";
	public static String UUID_TAG = "_znt_ios_rrdg_sp";
	public static String IOS_TAG = "_znt_ios_rrdg_sp";
	public static String PKG_END = "znt_pkg_end";
	
	public static final String MEDIA_ADD = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_REMOVE = "android.intent.action.MEDIA_REMOVED";
	
	public static String PlayPermission = "0";
	public static String PlayRes = com.znt.diange.mina.entity.PlayRes.LOCAL;
	
	public static final int DB_VERSION = 1;
	public static final int LUCNHER_VERSION_CODE = 27;
	public static final int INSTALL_VERSION_CODE = 3;
	public static String DEVICE_MAC = "";
	
	public static int UPDATE_TYPE = 0;
	
	public volatile static int STATUS_CHECK_TIME_MAX = 12;
	
	public volatile static int CUR_ALL_MEDIA_COUNT = 0;
	public volatile static String GET_CUR_MEDIA_FROM_LOCAL_RESULT = "";
	
	public static boolean isBoxVersion = true;
	
	public static List<ScanResult> wifiList = new ArrayList<ScanResult>();

	public static String PLAN_GET_STATUS = "";
}
 
