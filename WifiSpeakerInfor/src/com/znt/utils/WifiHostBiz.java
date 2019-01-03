package com.znt.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class WifiHostBiz {  
  
    private final String TAG = "WifiHostBiz";  
    private WifiManager wifiManager;  
    private String WIFI_HOST_SSID = "AndroidAP";  
    private String WIFI_HOST_PRESHARED_KEY = "12345678";// 密码必须大于8位数  
  
    public static WifiHostBiz INSTANCE = null;
    
    public static WifiHostBiz getInstance(Context context)
    {
    	if(INSTANCE == null)
    		INSTANCE = new WifiHostBiz(context);
    	return INSTANCE;
    }
    
    public WifiHostBiz(Context context) {  
        super();  
        //获取wifi管理服务  
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
    }  
  
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;  
    }  
  
    private WIFI_AP_STATE getWifiApState(){  
        int tmp;  
        try {  
            Method method = wifiManager.getClass().getMethod("getWifiApState");  
            tmp = ((Integer) method.invoke(wifiManager));  
            // Fix for Android 4  
            if (tmp > 10) {  
                tmp = tmp - 10;  
            }  
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;  
        }  
    }  
  
    public enum WIFI_AP_STATE {  
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED  
    }  
  


}  
