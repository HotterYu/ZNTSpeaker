
package com.jflavio1.wificonnectorsample;

// Declare any non-default types here with import statements

import com.jflavio1.wificonnectorsample.ITaskCallback; 
interface IWifiAidlInterface
{
    
    void startConnectWifi(String wifiName , String wifiPwd);
    void setWifiInfor(String wifiName , String wifiPwd);
    String getCurWifiName();
    String getCurWifiPwd();
    boolean isHasWifi(String wifiName , String wifiPwd);
    void devStatusCheck(boolean isOnline);
   
    void registerCallback(ITaskCallback cb);   
    void unregisterCallback(ITaskCallback cb);
}