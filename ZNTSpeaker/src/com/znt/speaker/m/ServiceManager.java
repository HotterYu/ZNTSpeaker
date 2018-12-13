/*package com.znt.speaker.m;

import com.znt.wifimodel.IWifiAidlInterface;
import com.znt.wifimodel.ZNTWifiService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ServiceManager 
{

	private Context context = null;
	private IWifiAidlInterface mIWifiAidlInterface = null;
	
	
	public ServiceManager(Context context)
	{
		this.context = context;
	}
	
	
	private ServiceConnection mConn = new ServiceConnection() 
	{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) 
        {
        	mIWifiAidlInterface = IWifiAidlInterface.Stub.asInterface(iBinder);
            if (mIWifiAidlInterface != null)
            {
            	
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

    public void bindService() 
    {
        // UnBind
        unBindService();

        Intent intent = new Intent(context, ZNTWifiService.class);
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() 
    {
        // Service
    	IWifiAidlInterface service = mIWifiAidlInterface;
        mIWifiAidlInterface = null;
        if (service != null) 
        {
        	context.unbindService(mConn);
        }
    }
}
*/