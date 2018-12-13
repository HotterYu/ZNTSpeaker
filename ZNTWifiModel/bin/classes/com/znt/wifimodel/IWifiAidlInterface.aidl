
package com.znt.wifimodel;

// Declare any non-default types here with import statements

import com.znt.wifimodel.ITaskCallback; 
interface IWifiAidlInterface
{
    
    void startConnectWifi(String wifiName , String wifiPwd);
    void setWifiInfor(String wifiName , String wifiPwd);
    String getCurWifiName();
    String getCurWifiPwd();
    void devStatusCheck(boolean isOnline);
   
    void registerCallback(ITaskCallback cb);   
    void unregisterCallback(ITaskCallback cb);
}