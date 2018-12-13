package com.znt.wifimodel;


import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.znt.diange.mina.entity.WifiInfor;
import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.entity.WifiModelConstant;
import com.znt.wifimodel.p.NetWorkPresenter;
import com.znt.wifimodel.v.INetWorkView;

public class ZNTWifiService extends Service implements INetWorkView
{
	
	private String TAG = "ZNTWifiService";
	
    private ServiceBinder mBinder;
    
    private Context mContext = null;
    
    private NetWorkPresenter mNetWorkPresenter = null;

    public ZNTWifiService() 
    {
    	
    }
    
    private final String DEFAULT_WIFI_NAME = "DianYinGuanJia";
	private final String DEFAULT_WIFI_PWD = "zhunikeji";
    private void setDefaultWifi()
    {
    	DBManager.INSTANCE.insertWifi("cstorebp", "51060018");
		DBManager.INSTANCE.insertWifi("cstoreap", "51060018562");
		DBManager.INSTANCE.insertWifi("dianyin", "12345678");
		DBManager.INSTANCE.insertWifi(DEFAULT_WIFI_NAME, DEFAULT_WIFI_PWD);
    }
    private void showErrorLog(Exception e)
    {
    	Log.e(TAG, e.getMessage());
    }
    
    @Override
    public void onCreate() 
    {
    	// TODO Auto-generated method stub
    	super.onCreate();
    }
    
    @Override
    public void onDestroy() 
    {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
        if (mBinder == null)
            mBinder = new ServiceBinder();
        if(mContext == null)
        {
        	try 
        	{
            	mContext = getApplicationContext();
            	mNetWorkPresenter = new NetWorkPresenter(mContext, this);
            	
            	DBManager.init(mContext);
            	
            	setDefaultWifi();//保存默认的WIFI信息
    		}
        	catch (Exception e) 
        	{
    			// TODO: handle exception
        		showErrorLog(e);
    		}
        }
        
        return mBinder;
    }

    final RemoteCallbackList<ITaskCallback> mCallbacks = new RemoteCallbackList <ITaskCallback>();
    void callback(int val, String arg1, String arg2) 
    {   
        final int N = mCallbacks.beginBroadcast();  
        for (int i=0; i<N; i++)
        {   
            try 
            {  
                mCallbacks.getBroadcastItem(i).actionPerformed(val, arg1, arg2);   
            }  
            catch (Exception e)
            {   
                // The RemoteCallbackList will take care of removing   
                // the dead object for us.     
            }  
        }  
        mCallbacks.finishBroadcast();  
    }  
    
    class ServiceBinder extends IWifiAidlInterface.Stub 
    {

        public ServiceBinder() 
        {
            
        }

		@Override
		public void startConnectWifi(String wifiName, String wifiPwd) throws RemoteException 
		{
			// TODO Auto-generated method stub
			try 
			{
				DBManager.INSTANCE.insertWifi(wifiName, wifiPwd);
				
				/*int size = DBManager.INSTANCE.getWifiCount();
				List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();*/
				
				WifiLocalDataEntity.newInstance(mContext).updateWifi(wifiName, wifiPwd);
				mNetWorkPresenter.connectWifi(wifiName, wifiPwd);
				Log.e(TAG, "startConnectWifi -- >" + wifiName + "   " + wifiPwd);
			} 
			catch (Exception e)
			{
				// TODO: handle exception
				showErrorLog(e);
			}
		}

		@Override
		public void setWifiInfor(String wifiName, String wifiPwd) throws RemoteException
		{
			// TODO Auto-generated method stub
			try 
			{
				DBManager.INSTANCE.insertWifi(wifiName, wifiPwd);
				//LocalDataEntity.newInstance(mContext).updateWifi(wifiName, wifiPwd);
			} 
			catch (Exception e)
			{
				// TODO: handle exception
				showErrorLog(e);
			}
		}

		@Override
		public String getCurWifiName() throws RemoteException 
		{
			// TODO Auto-generated method stub
			try 
			{
				//List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();
				return WifiLocalDataEntity.newInstance(mContext).getWifiName();
			} 
			catch (Exception e) 
			{
				showErrorLog(e);
			}
			return null;
		}

		@Override
		public String getCurWifiPwd() throws RemoteException 
		{
			// TODO Auto-generated method stub
			
			try 
			{
				return WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
			} 
			catch (Exception e) 
			{
				showErrorLog(e);
			}
			return null;
		}

		@Override
		public void devStatusCheck(boolean isOnline) throws RemoteException 
		{
			// TODO Auto-generated method stub
			try 
			{
				if(!isOnline)
				{
					//掉线状态,检查是否需要扫描WIFI
					if(mNetWorkPresenter != null)
					{
						mNetWorkPresenter.checkWifiState();
					}
				}
				else
				{
					//在线状态，停止扫描WIFI
					if(mNetWorkPresenter != null)
						mNetWorkPresenter.stopCheckSSID();
				}
			} 
			catch (Exception e) 
			{
				showErrorLog(e);
			}
		}

		@Override
		public void registerCallback(ITaskCallback cb) throws RemoteException 
		{
			// TODO Auto-generated method stub
			if (cb != null) 
			{   
                mCallbacks.register(cb);  
            }  
		}

		@Override
		public void unregisterCallback(ITaskCallback cb) throws RemoteException 
		{
			if(cb != null) 
			{  
                mCallbacks.unregister(cb);  
            }  
		}
    }
    
    
	@Override
	public void createApStart() 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void createApFail() 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void createApSuccess() 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void connectWifiSatrt(String wifiName) 
	{
		// TODO Auto-generated method stub
		callback(WifiModelConstant.CALL_BACK_WIFI_CONNECT_START, wifiName, null);
	}
	@Override
	public void connectWifiFailed(String wifiName, String wifipwd) 
	{
		// TODO Auto-generated method stub
		callback(WifiModelConstant.CALL_BACK_WIFI_CONNECT_FAIL, wifiName, wifipwd);
	}
	@Override
	public void connectWifiSuccess(String wifiName, String wifipwd) 
	{
		// TODO Auto-generated method stub
		callback(WifiModelConstant.CALL_BACK_WIFI_CONNECT_SUCCESS, wifiName, wifipwd);
	}
}