package com.znt.speaker.p;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.http.callback.IGetCurPllanCallBack;
import com.znt.speaker.m.HttpRequestModel;
import com.znt.speaker.prcmanager.ZNTWifiServiceManager;
import com.znt.speaker.v.IHttpRequestView;

public class HttpPresenter
{
	
	private Activity activity = null;
	
	private HttpRequestModel httpRequestModel = null;
	private IHttpRequestView iHttpRequestView = null;
	private ZNTWifiServiceManager mZNTWifiServiceManager = null;
	
	public HttpPresenter(Activity activity, IHttpRequestView iHttpRequestView)
	{
		httpRequestModel = new HttpRequestModel(activity, iHttpRequestView);
		this.iHttpRequestView = iHttpRequestView;
		this.activity = activity;
		
	}
	
	public void setZNTWifiServiceManager(ZNTWifiServiceManager mZNTWifiServiceManager)
	{
		this.mZNTWifiServiceManager = mZNTWifiServiceManager;
	}
	
	public boolean isRunning(int id)
	{
		return httpRequestModel.isRuning(id);
	}
	
	private volatile boolean isCurPlanRunning = false;
	private int getCurPlanRunningCount = 0;
	/**
	 * 获取当前播放计划
	 */
	public void getCurPlan()
	{
		if(isCurPlanRunning && getCurPlanRunningCount <= 3)
		{
			getCurPlanRunningCount ++;
			Log.e("", "get cur plan is running!");
			return;
		}
		getCurPlanRunningCount = 0;
 		isCurPlanRunning = true;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				String uid = LocalDataEntity.newInstance(activity).getDeviceCode();
				if(TextUtils.isEmpty(uid))
					uid = "1";
				params.put("terminalId", uid);
				httpRequestModel.getCurPlan(params, new IGetCurPllanCallBack() 
				{
					@Override
					public void requestSuccess(CurPlanInfor curPlanInfor, int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = false;
						iHttpRequestView.requestSuccess(curPlanInfor, requestId);
					}
					
					@Override
					public void requestStart(int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = true;
						iHttpRequestView.requestStart(requestId);
					}
					
					@Override
					public void requestFail(int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = false;
						iHttpRequestView.requestError(requestId, "request error");
					}
				});
			}
		}).start();
	}
	/**
	 * 注册设备
	 */
	public void register()
	{
		String devCode = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(TextUtils.isEmpty(devCode))
			httpRequestModel.register();
		else
			updateSpeakerInfor();
	}
	
	/*
	 *	更新设备信息
	 */
	public void updateSpeakerInfor()
	{
		String wifiName = "";
		String wifiPassword = "";
		if(mZNTWifiServiceManager != null)
		{
			wifiName = mZNTWifiServiceManager.getCurConnectWifiName();
			wifiPassword = mZNTWifiServiceManager.getCurConnectWifiPwd();
		}
		
		httpRequestModel.updateDeviceInfor(wifiName, wifiPassword);
	}
	public void updateWifiInfor(String wifiName, String wifiPassword, String result)
	{
		httpRequestModel.updateWifiInfor(wifiName, wifiPassword, result);
	}
	
	/**
	 * 检查更新
	 */
	public void checkUpdate()
	{
		httpRequestModel.checkUpdate();
	}
	/**
	 * 检查设备状态
	 */
	public void getDevStatus(int playSeek, String playingSong, int playingSongType, String netSpeed)
	{
		String id = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(TextUtils.isEmpty(id))
			httpRequestModel.register();
		else
			httpRequestModel.getDevStatus(playSeek, playingSong, playingSongType, netSpeed, id);
	}
	/**
	 * 获取推送的歌曲列表
	 */
	public void getPushMusics()
	{
		httpRequestModel.getPushMusics();
	}
	
	/**
	 * 获取当前播放列表
	 * @param 
	 */
	public void getPlanMusics()
	{
		httpRequestModel.getPlanMusics();
	}
	
	/**
	 * 初始化设备
	 * @param 
	 */
	public void initTerminal()
	{
		httpRequestModel.initTerminal();
	}
	
	/**
	 * 
	 * @param 
	 */
	public void getCurTime()
	{
		httpRequestModel.getCurTime();
	}
	
	/**
	 * 
	 * @param 
	 */
	public void getCurMusicPos()
	{
		httpRequestModel.getCurMusicPos();
	}
	
	/**
	 * 更新播放位置
	 * @param playingPos
	 * @param playingMusicInfoId
	 */
	public void updateCurPos(int playingPos, String playingMusicInfoId)
	{
		httpRequestModel.updateCurPos(playingPos, playingMusicInfoId);
	}
	
	
	public void bindSpeaker(String devName, String activateCode)
	{
		
		
		String id = LocalDataEntity.newInstance(activity).getDeviceCode();
		
		if(TextUtils.isEmpty(devName))
			devName = "店音_" + id;
		/*Map<String, String> params = new HashMap<String, String>();
		
		
		params.put("id", id);
		params.put("memberName", memberName);
		params.put("name", devName);*/
		//Log.e("", "*************************bind device id-->" + id + "  account-->"+memberName + " devName-->" + devName );
		httpRequestModel.bindSpeaker(devName, activateCode);
	}
	
	public void cancle(int requestId)
	{
		httpRequestModel.cancelHttp(requestId);
	}
}
