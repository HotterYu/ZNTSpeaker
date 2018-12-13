package com.znt.wifimodel.p;

import java.util.ArrayList;
import java.util.List;

import com.znt.wifimodel.m.NetWorkModel1;
import com.znt.wifimodel.v.INetWorkView;

import android.content.Context;
import android.net.wifi.ScanResult;

public class NetWorkPresenter
{
	private NetWorkModel1 netWorkModel = null;
	
	public NetWorkPresenter(Context activity, INetWorkView iNetWorkView)
	{
		netWorkModel = new NetWorkModel1(activity,iNetWorkView);
	}
	
	public void startConnectCurrentWifi()
	{
		netWorkModel.startConnectCurrentWifi();
	}
	public void startConnectCurrentWifiAndReset()
	{
		netWorkModel.startConnectCurrentWifiAndReset();
	}
	public void openWifi()
	{
		netWorkModel.openWifi();
	}
	
	public boolean isConnectRunning()
	{
		return netWorkModel.isConnectRunning();
	}
	
	public void startScanWifi()
	{
		//netWorkModel.startScanWifi();
	}
	public void connectWifi(String name, String pwd)
	{
		netWorkModel.connectWifi(name, pwd);
	}
	
	public void createWifiAp()
	{
		//netWorkModel.createWifiAp();
	}
	public void closeWifiAp(Context context)
	{
		//netWorkModel.closeWifiAp(context);
	}
	/*public String getWifiApName()
	{
		return netWorkModel.getWifiApName();
	}*/
	
	public boolean isWifiEnabled()
	{
		return netWorkModel.isWifiEnabled();
	}
	
	public void stopCheckSSID()
	{
		//netWorkModel.stopCheckSSID();
	}
	
	private int checkWifiConnectRunningCount = 0;
	public void checkWifiState()
	{
		
		if(isConnectRunning())
		{
			if(checkWifiConnectRunningCount >= 20)
			{
				checkWifiConnectRunningCount = 0;
				startReconnectWifi();
			}
			else
				checkWifiConnectRunningCount ++;
			return;
		}
		
		startReconnectWifi();	
			
	}
	
	private void startReconnectWifi()
	{
		netWorkModel.checkIfShouldConnectWifi();
	}
	
	
	public List<ScanResult> getWifiList()
	{
		//return netWorkModel.getWifiList();
		return new ArrayList<ScanResult>();
	}
	
}
