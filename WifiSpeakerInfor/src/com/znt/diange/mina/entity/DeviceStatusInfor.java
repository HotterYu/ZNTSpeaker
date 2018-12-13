package com.znt.diange.mina.entity;

import android.text.TextUtils;

public class DeviceStatusInfor 
{
	private String lastMusicUpdate = "";//鏇插簱鏇存柊鏃堕棿
	private String vodFlag = "";//0-涓嶅厑璁哥偣鎾� 1-鐐规挱鐩掑瓙鍐呯疆姝屾洸 2-鍙偣鎾涓夋柟姝屾洸
	private String lastBootTime  = "";//鐩掑瓙鏈�鍚庡惎鍔ㄦ椂闂�
	private String sysLastVersionNum = "";
	private String planId = "";
	private String planTime = "";
	private String pushStatus  = "";// 1-琛ㄧず鏈夋柊鐐规挱鐨勬瓕鏇�
	private String playStatus   = "";// 0-鎾斁 1-鏆傚仠
	private String playingPos = "0";
	private String volume = "0";
	private String videoWhirl = "";
	private String downloadFlag = "0";//0-涓嶄笅杞� 1-鍏佽涓嬭浇
	private String wifiName = "";
	private String wifiPassword = "";
	
	public void setLastBootTime(String lastBootTime)
	{
		this.lastBootTime = lastBootTime;
	}
	public String getLastBootTime()
	{
		return lastBootTime;
	}
	public void setWifiName(String wifiName)
	{
		this.wifiName = wifiName;
	}
	public String getWifiName()
	{
		return wifiName;
	}
	
	public void setWifiPwd(String wifiPassword)
	{
		this.wifiPassword = wifiPassword;
	}
	public String getWifiPwd()
	{
		return wifiPassword;
	}
	
	public void setVodFlag(String vodFlag)
	{
		this.vodFlag = vodFlag;
	}
	public String getVodFlag()
	{
		return vodFlag;
	}
	
	public void setVolume(String volume)
	{
		this.volume = volume;
	}
	public int getVolume()
	{
		if(!TextUtils.isEmpty(volume))
			return Integer.parseInt(volume);
		else
			return -1;
	}
	
	public void setDownloadFlag(String downloadFlag)
	{
		this.downloadFlag = downloadFlag;
	}
	public String getDownloadFlag()
	{
		return downloadFlag;
	}
	public boolean isDownloadEnable()
	{
		return downloadFlag.equals("1");
	}
	public boolean isDownloadStop()
	{
		return downloadFlag.equals("2");
	}
	
	public void setPlayingPos(String playingPos)
	{
		this.playingPos = playingPos;
	}
	public String getPlayingPos()
	{
		return playingPos;
	}
	
	public void setPushStatus(String pushStatus)
	{
		this.pushStatus = pushStatus;
	}
	public String getPushStatus()
	{
		return pushStatus;
	}
	
	public void setPlayStatus(String playStatus)
	{
		this.playStatus = playStatus;
	}
	public String getPlayStatus()
	{
		return playStatus;
	}
	public boolean isPlay()
	{
		return playStatus.equals("0");
	}
	public void setPlanId(String planId)
	{
		this.planId = planId;
	}
	public String getPlanId()
	{
		return planId;
	}
	public void setPlanTime(String planTime)
	{
		this.planTime = planTime;
	}
	public String getPlanTime()
	{
		return planTime;
	}
	
	public void setMusicLastUpdate(String lastMusicUpdate)
	{
		this.lastMusicUpdate = lastMusicUpdate;
	}
	public String getMusicLastUpdate()
	{
		return lastMusicUpdate;
	}
	
	public void setLastVersionNum(String sysLastVersionNum)
	{
		this.sysLastVersionNum  = sysLastVersionNum;
	}
	public String getLastVersionNum()
	{
		return sysLastVersionNum;
	}
	
	public void setVideoWhirl(String videoWhirl)
	{
		this.videoWhirl  = videoWhirl;
	}
	public String getVideoWhirl()
	{
		if(videoWhirl == null)
			videoWhirl = "";
		return videoWhirl;
	}
}
