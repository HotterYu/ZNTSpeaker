package com.znt.push;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.znt.push.entity.PushModelConstant;
import com.znt.push.http.HttpRequestID;
import com.znt.push.p.HttpPresenter;
import com.znt.push.update.UpdateManager;
import com.znt.push.update.UpdateManager.SpaceCheckListener;
import com.znt.push.utils.StringUtils;
import com.znt.push.utils.SystemUtils;
import com.znt.push.v.IHttpRequestView;

import org.json.JSONObject;

import java.util.List;

public class ZNTPushService extends Service implements IHttpRequestView, SpaceCheckListener
{
    private ServiceBinder mBinder;
    
    private Context mContext = null;
    private HttpPresenter mHttpPresenter = null;
    private UpdateManager mUpdateManager = null;
    
    private  SpaceCheckListener mSpaceCheckListener = null;
    
    private boolean isStop = false;
    private boolean checkUpdateRunning = false;
    private int checkFailCount = 0;
	private int checkUpdateCount = 0;
	private final int MAX_CHECK_UPDATE_COUNT = 3;
	private long lastCheckUpdateTime = 0;
    
	public static final String ACTION = "com.znt.speaker.DEV_STATUS";
	
	private int checkTime = PushModelConstant.STATUS_CHECK_TIME_MAX;
	//private int checkTimeMax = CHECK_TIME_MAX;
	
	private void startCheckDevStatus()
	{
		isStop = false;
		new Thread(new CheckDevStatusTask()).start();
	}
	private void stopCheckDevStatus()
	{
		isStop = true;
	}
	
    public ZNTPushService() 
    {
    	
    }
    
    private boolean isProessRunning(Context context, String proessName) 
	{
			
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
 
		List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : lists){
			if(info.processName.equals(proessName)){
				isRunning = true;
			}
		}
		return isRunning;
	}
    
    private void checkMainProcess()
    {
    	if(!isProessRunning(getApplicationContext(), "com.znt.speaker"))
    	{
    		
    	}
    }

    
    private void showErrorLog(Exception e)
    {
    	Log.e("", e.getMessage());
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
            	
            	mHttpPresenter = new HttpPresenter(mContext, this);
            	
            	mSpaceCheckListener = this;
            	
            	startCheckDevStatus();
            	
    		}
        	catch (Exception e) 
        	{
    			// TODO: handle exception
        		showErrorLog(e);
    		}
        }
        
        return mBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) 
    {
    	// TODO Auto-generated method stub
    	//stopCheckDevStatus();
    	return super.onUnbind(intent);
    }
    
    
    private String devId = null;
    private int playSeek = 0;
    private int playingSongType = 0;
    private String playingSong = "";
    private String netInfo = "";
    private class CheckDevStatusTask implements Runnable
	{
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			while(true)
			{
				if(isStop)
					break;
				
				try 
				{
					Thread.sleep(1000);
					
					if(checkTime >= PushModelConstant.STATUS_CHECK_TIME_MAX)
					{
						if(TextUtils.isEmpty(devId))
						{
							callback(PushModelConstant.CALL_BACK_PUSH_NOT_REGISTER, null, null, null);
						}
						mHttpPresenter.getDevStatus(devId, playSeek, playingSong, playingSongType, netInfo);
						checkTime = 0;
						
					}
					else
					{
						checkTime ++;
						callback(PushModelConstant.CALL_BACK_PUSH_CHECK, checkTime + "", null, null);
					}
					
				} 
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

    final RemoteCallbackList<ITaskCallback> mCallbacks = new RemoteCallbackList <ITaskCallback>();
    void callback(int val, String arg1, String arg2, String arg3) 
    {   
    	try 
    	{
    		int N = mCallbacks.beginBroadcast();  
            if(N > 1)
            	N = N - 1;
            for (int i=0; i<N; i++)
            {   
                try 
                {  
                    mCallbacks.getBroadcastItem(i).actionPerformed(val, arg1, arg2, arg3);   
                }  
                catch (Exception e)
                {   
                    // The RemoteCallbackList will take care of removing   
                    // the dead object for us.     
                }  
            }  
            mCallbacks.finishBroadcast();
		} 
    	catch (Exception e) 
		{
			// TODO: handle exception
		}
    }  
    
    class ServiceBinder extends IPushAidlInterface.Stub 
    {

        public ServiceBinder() 
        {
            
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

		@Override
		public void putRequestParams(String fdevId, int fplayingSongType, String fplayingSong, String fnetInfo, boolean updateNow)
				throws RemoteException {
			// TODO Auto-generated method stub
			
			if(fplayingSong == null)
				fplayingSong = "";
			
			if(fnetInfo == null)
				fnetInfo = "";
			
			devId = fdevId;
			playingSongType = fplayingSongType;
			playingSong = fplayingSong;
			netInfo = fnetInfo;
			
			if(updateNow)
				mHttpPresenter.getDevStatus(devId, playSeek, playingSong, playingSongType, netInfo);
		}
    }

	@Override
	public void requestStart(int requestId) 
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			
		}
		else if(requestId == HttpRequestID.CHECK_UPDATE)
		{
			checkUpdateRunning = true;
		}
		else if(requestId == HttpRequestID.REGISTER)
		{

		}
	}
	@Override
	public void requestError(int requestId, String error) 
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			checkFailCount++;
			callback(PushModelConstant.CALL_BACK_PUSH_FAIL, checkFailCount + "", error, null);
		}
		else if(requestId == HttpRequestID.CHECK_UPDATE)
		{
			checkUpdateRunning = false;
		}
		else if(requestId == HttpRequestID.REGISTER)
		{
		}
	}
	private int updateCheckRunningCount = 0;
	@Override
	public void requestSuccess(String info, int requestId) 
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			try 
			{
				callback(PushModelConstant.CALL_BACK_PUSH_SUCCESS, info, "", null);

				checkFailCount = 0;
				
				if(checkUpdateCount < MAX_CHECK_UPDATE_COUNT)
				{
					updateCheckProcess(info);
				}
				else
				{
					if((System.currentTimeMillis() - lastCheckUpdateTime) >= 24 * 60 * 60 * 1000)
						checkUpdateCount = 0;
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(requestId == HttpRequestID.CHECK_UPDATE)
		{
			try 
			{
				checkUpdateCount ++;
			
				if(checkUpdateCount >= MAX_CHECK_UPDATE_COUNT)
					lastCheckUpdateTime = System.currentTimeMillis();
				
				checkUpdateRunning = false;
				JSONObject json = new JSONObject(info);
				
				String apkUrl = StringUtils.getInforFromJason(json, "url");
				
	          	if(mUpdateManager == null)
	          		mUpdateManager = new UpdateManager(mContext ,this,devId);
	          	mUpdateManager.setDevId(devId);
	          	mUpdateManager.setUpdateRunning();
	          	mUpdateManager.doApkInstall(apkUrl);
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	@Override
	public void requestNetWorkError() 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSpaceCheck(long size) 
	{
		// TODO Auto-generated method stub
		callback(PushModelConstant.CALL_BACK_PUSH_SPACE_CHECK, size + "", null, null);
	}
	
	private void updateCheckProcess(String info)
	{
		try 
		{
			if(mUpdateManager == null)
	      		mUpdateManager = new UpdateManager(mContext, mSpaceCheckListener,devId);
			JSONObject json = new JSONObject(info);
			if(json.has("sysLastVersionNum"))
			{
				String sysLastVersionNum  = json.getString("sysLastVersionNum");
				if(!TextUtils.isEmpty(sysLastVersionNum))
				{
					int lastVersion = Integer.parseInt(sysLastVersionNum);
					
					int curVersion = SystemUtils.getVersionCode(getApplicationContext());
					if(curVersion < lastVersion)
					{
						if(!checkUpdateRunning)
						{
							if(!mUpdateManager.isUpdateRunning())
							{
								checkUpdateRunning = true;
								mHttpPresenter.checkUpdate();
							}
							else
							{
								updateCheckRunningCount ++;
								if(updateCheckRunningCount >= 1000)
								{
									updateCheckRunningCount = 0;
									mUpdateManager.setUpdateFinished();
								}
							}
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		
	}
}