package com.znt.speaker.v;

public interface INetWorkView 
{
	public void createApStart();
	public void createApFail();
	public void createApSuccess();
	
	public void connectWifiSatrt(String wifiName);
	public void connectWifiFailed(String wifiName, String wifipwd);
	public void connectWifiSuccess(String wifiName, String wifipwd);
}
