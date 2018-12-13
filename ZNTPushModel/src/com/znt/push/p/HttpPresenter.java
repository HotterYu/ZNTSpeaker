package com.znt.push.p;

import android.content.Context;

import com.znt.push.m.HttpRequestModel;
import com.znt.push.v.IHttpRequestView;

public class HttpPresenter
{
	private Context mContext = null;
	private HttpRequestModel httpRequestModel = null;
	
	public HttpPresenter(Context mContext, IHttpRequestView iHttpRequestView)
	{
		this.mContext = mContext;
		httpRequestModel = new HttpRequestModel(mContext, iHttpRequestView);
	}
	
	public boolean isRunning(int id)
	{
		return httpRequestModel.isRuning(id);
	}
	
	public void checkUpdate()
	{
		httpRequestModel.checkUpdate();
	}
	public void getDevStatus(String id, int playSeek, String playingSong, int playingSongType, String netInfo)
	{
		httpRequestModel.getDevStatus(playSeek, playingSong, playingSongType, id, netInfo);
	}
	
	public void cancle(int requestId)
	{
		httpRequestModel.cancelHttp(requestId);
	}
	
}
