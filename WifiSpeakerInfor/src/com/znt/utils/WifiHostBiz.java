package com.znt.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/** 
 * WIFI热点业务�? 
 * @author wlh 
 * 
 */  
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
  
    /**判断热点�?启状�?*/  
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
  
    /** 
     * wifi热点�?�? 
     * @param enabled   true：打�?  false：关�? 
     * @return  true：成�?  false：失�? 
     */  
    public boolean setWifiApEnabled(boolean enabled) {  
        System.out.println(TAG + ":�?启热�?");  
        if (enabled) { // disable WiFi in any case  
            //wifi和热点不能同时打�?，所以打�?热点的时候需要关闭wifi  
            wifiManager.setWifiEnabled(false);  
            System.out.println(TAG + ":关闭wifi");  
        }else{  
            wifiManager.setWifiEnabled(true);  
        }  
        try {  
            //热点的配置类  
            WifiConfiguration apConfig = new WifiConfiguration();  
            //配置热点的名�?(可以在名字后面加点随机数�?么的)  
            apConfig.SSID = WIFI_HOST_SSID;  
            //配置热点的密�?  
            apConfig.preSharedKey = WIFI_HOST_PRESHARED_KEY;  
            //安全：WPA2_PSK  
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
            //通过反射调用设置热点  
            Method method = wifiManager.getClass().getMethod(  
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);  
            //返回热点打开状�??  
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);  
        } catch (Exception e) {  
            return false;  
        }  
    }  
}  
