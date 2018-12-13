package com.znt.push.v;

import com.znt.diange.mina.entity.DeviceStatusInfor;

public interface IDevStatusView 
{
	public void onPushSuccess(DeviceStatusInfor devStatusInfor);
	public void onPushFail(int count);
	public void onPushCheck(int count);
	public void onNotRegister();
	public void onRegisterCallBack();
	public void onSpaceCheck(long size);
}
