package com.znt.speaker.prcmanager;

import java.util.List;

import org.json.JSONObject;

import com.znt.diange.mina.entity.DeviceStatusInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.push.IPushAidlInterface;
import com.znt.push.ITaskCallback;
import com.znt.push.ZNTPushService;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.utils.StringUtils;
import com.znt.push.v.IDevStatusView;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class ZNTPushServiceManager 
{

	private Application context = null;
	private IPushAidlInterface mIPushAidlInterface = null;
	
	private IDevStatusView mIDevStatusView = null;
	
	
	public ZNTPushServiceManager(Application context, IDevStatusView mIDevStatusView)
	{
		this.context = context;
		this.mIDevStatusView = mIDevStatusView;
	}
	
	public boolean isBindSuccess()
	{
		return mIPushAidlInterface != null;
	}
	
	public void putRequestParams(String devId,int playingSongType, String playingSong, String netInfo, boolean updateNow)
	{
		try 
		{	if(mIPushAidlInterface != null)
				mIPushAidlInterface.putRequestParams(devId,playingSongType, playingSong, netInfo, updateNow);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ServiceConnection mConn = new ServiceConnection() 
	{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) 
        {
        	mIPushAidlInterface = IPushAidlInterface.Stub.asInterface(iBinder);
            if (mIPushAidlInterface != null)
            {
            	try 
            	{
            		mIPushAidlInterface.registerCallback((com.znt.push.ITaskCallback) mCallback);
				} 
            	catch (Exception e) 
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else
                Log.e("", "push service bind error!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) 
        {
        	mIPushAidlInterface = null;
        }
    };
    
    private ITaskCallback mCallback = new ITaskCallback.Stub() 
    {

		@Override
		public void actionPerformed(int id, String arg1, String arg2, String arg3) throws RemoteException 
		{
			// TODO Auto-generated method stub
			if(id == PushModelConstant.CALL_BACK_PUSH_SUCCESS)
			{
				DeviceStatusInfor deviceStatusInfor = new DeviceStatusInfor();
				
				if(!TextUtils.isEmpty(arg1))
				{
					JSONObject json;
					try 
					{
						json = new JSONObject(arg1);
						deviceStatusInfor = new DeviceStatusInfor();
						String vodFlag = StringUtils.getInforFromJason(json, "vodFlag");
						String planId = StringUtils.getInforFromJason(json, "planId");
						String planTime = StringUtils.getInforFromJason(json, "planTime");
						String playStatus   = StringUtils.getInforFromJason(json, "playStatus  ");
						String lastMusicUpdate  = StringUtils.getInforFromJason(json, "lastMusicUpdate");
						String sysLastVersionNum  = StringUtils.getInforFromJason(json, "sysLastVersionNum");
						String pushStatus  = StringUtils.getInforFromJason(json, "pushStatus");
						String volume  = StringUtils.getInforFromJason(json, "volume");
						String downloadFlag  = StringUtils.getInforFromJason(json, "downloadFlag");
						String videoWhirl  = StringUtils.getInforFromJason(json, "videoWhirl");
						String wifiName  = StringUtils.getInforFromJason(json, "wifiName");
						String wifiPassword  = StringUtils.getInforFromJason(json, "wifiPassword");
						//String playingPos  = getInforFromJason(json, "playingPos");
						deviceStatusInfor.setLastVersionNum(sysLastVersionNum);
						deviceStatusInfor.setVodFlag(vodFlag);
						deviceStatusInfor.setMusicLastUpdate(lastMusicUpdate);
						deviceStatusInfor.setPlanId(planId);
						deviceStatusInfor.setPlanTime(planTime);
						deviceStatusInfor.setPlayStatus(playStatus);
						deviceStatusInfor.setPushStatus(pushStatus);
						//deviceStatusInfor.setPlayingPos(playingPos);
						deviceStatusInfor.setVolume(volume);
						deviceStatusInfor.setDownloadFlag(downloadFlag);
						deviceStatusInfor.setVideoWhirl(videoWhirl);
						
						deviceStatusInfor.setWifiName(wifiName);
						deviceStatusInfor.setWifiPwd(wifiPassword);
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mIDevStatusView.onPushSuccess(deviceStatusInfor);
				}
				else
					mIDevStatusView.onPushFail(0);
				
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_FAIL)
			{
				int failCount = 0;
				try 
				{
					if(!TextUtils.isEmpty(arg1))
						failCount = Integer.parseInt(arg1);
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				mIDevStatusView.onPushFail(failCount);
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_DEV_REGISTER)
			{
				mIDevStatusView.onRegisterCallBack();
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_SPACE_CHECK)
			{
				try
				{
					if(!TextUtils.isEmpty(arg1))
					{
						long size = Integer.parseInt(arg1);
						mIDevStatusView.onSpaceCheck(size);
					}
				} catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_CHECK)
			{
				try
				{
					if(!TextUtils.isEmpty(arg1))
					{
						int count = Integer.parseInt(arg1);
						mIDevStatusView.onPushCheck(count);
					}
				} catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
			else if(id == PushModelConstant.CALL_BACK_PUSH_NOT_REGISTER)
			{
				try
				{
					mIDevStatusView.onNotRegister();
				} catch (Exception e) 
				{
					// TODO: handle exception
				}
			}
		}  
        
    };   

    public void bindService() 
    {
    	try 
    	{
    		// UnBind
            unBindService();

            Intent intent = new Intent(context, ZNTPushService.class);
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
        if (mIPushAidlInterface != null) 
        {
        	context.unbindService(mConn);
        	mIPushAidlInterface = null;
        }
        
    }
    
}
