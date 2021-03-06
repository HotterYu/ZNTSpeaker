package com.znt.speaker.prcmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.znt.speaker.v.INetWorkView;
import com.znt.wifimodel.IWifiAidlInterface;
import com.znt.wifimodel.IWifiCallback;
import com.znt.wifimodel.ZNTWifiService;

public class ZNTWifiServiceManager 
{

	private final String TAG = "ZNTWifiServiceManager";

	private Context context = null;
	private IWifiAidlInterface mIWifiAidlInterface = null;

	private INetWorkView mINetWorkView = null;


	public ZNTWifiServiceManager(Context context, INetWorkView mINetWorkView)
	{
		this.context = context;
		this.mINetWorkView = mINetWorkView;
		//bindService();
	}


	public boolean isBindSuccess()
	{
		return mIWifiAidlInterface != null;
	}


	public void devStatusCheck(boolean isOnline)
	{
		if (mIWifiAidlInterface != null)
		{
			try
			{
				mIWifiAidlInterface.devStatusCheck(isOnline);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			bindService();
	}

	public void startConnectCurWifi(String wifiName, String wifiPwd)
	{
		if (mIWifiAidlInterface != null)
		{
			try
			{
				//mIWifiAidlInterface.setWifiInfor(wifiName, wifiPwd);
				mIWifiAidlInterface.startConnectWifi(wifiName, wifiPwd);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			bindService();
	}

	public String getCurConnectWifiName()
	{

		try
		{
			if (mIWifiAidlInterface != null)
			{
				return mIWifiAidlInterface.getCurWifiName();
			}
			else
			{
				bindService();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}
	public String getCurConnectWifiPwd()
	{

		try
		{
			if (mIWifiAidlInterface != null)
			{
				return mIWifiAidlInterface.getCurWifiPwd();
			}
			else
			{
				bindService();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	private ServiceConnection mConn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		{
			mIWifiAidlInterface = IWifiAidlInterface.Stub.asInterface(iBinder);
			if (mIWifiAidlInterface != null)
			{
				try
				{
					mIWifiAidlInterface.registerCallback(mCallback);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
				Log.e("", "wifi service bind error!");
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mIWifiAidlInterface = null;
		}
	};

	private IWifiCallback mCallback = new IWifiCallback.Stub()
	{
		@Override
		public void actionPerformed(int id, String wifiName, String wifipwd) throws RemoteException
		{
			// TODO Auto-generated method stub
			if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_START)
			{
				mINetWorkView.connectWifiSatrt(wifiName);
				Log.d("", "wifi connect start");
			}
			else if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_FAIL)
			{
				mINetWorkView.connectWifiFailed(wifiName, wifipwd);
				Log.d("", "wifi connect fail");
			}
			else if(id == com.znt.wifimodel.entity.WifiModelConstant.CALL_BACK_WIFI_CONNECT_SUCCESS)
			{
				mINetWorkView.connectWifiSuccess(wifiName, wifipwd);
				Log.d("", "wifi connect success");
			}
		}
	};

	public void bindService()
	{
		try
		{
			// UnBind
			//unBindService();

			Intent intent = new Intent(context, ZNTWifiService.class);
			context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Log.e("", e.getMessage());
		}
	}

	public void unBindService()
	{
		// Service
		if (mIWifiAidlInterface != null)
		{
			context.unbindService(mConn);
			mIWifiAidlInterface = null;
		}
	}
}
