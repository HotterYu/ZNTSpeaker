package com.znt.diange.mina.entity;

public class WifiInfor 
{
	public String wifiName = "";
	public String wifiPwd = "";
	public String wifiStatus = "";
	public int reconnectCount = 0;
	
	public void setWifiName(String wifiName)
	{
		this.wifiName = wifiName;
	}
	public String getWifiName()
	{
		return wifiName;
	}
	
	public void setWifiPwd(String wifiPwd)
	{
		this.wifiPwd = wifiPwd;
	}
	public String getWifiPwd()
	{
		return wifiPwd;
	}
	
	public void setWifiStatus(String wifiStatus)
	{
		this.wifiStatus = wifiStatus;
	}
	public String getWifiStatus()
	{
		return wifiStatus;
	}
	
	public void setConnectCount(int connectCount)
	{
		this.reconnectCount = connectCount;
	}
	public int getConnectCount()
	{
		return reconnectCount;
	}
	
}
