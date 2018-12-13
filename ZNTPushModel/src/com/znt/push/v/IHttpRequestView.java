package com.znt.push.v;

public interface IHttpRequestView 
{
	public void requestStart(int requestId);
	public void requestError(int requestId, String error);
	public void requestSuccess(String obj, int requestId);
	public void requestNetWorkError();
}
