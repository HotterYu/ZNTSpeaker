package com.znt.speaker.http.callback;

import com.znt.diange.mina.entity.CurPlanInfor;

public interface IGetCurPllanCallBack 
{
	public void requestStart(int requestId);
	public void requestFail(int requestId);
	public void requestSuccess(CurPlanInfor curPlanInfor, int requestId);
	
}
