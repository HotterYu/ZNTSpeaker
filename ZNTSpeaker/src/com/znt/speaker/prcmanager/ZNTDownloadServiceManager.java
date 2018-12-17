package com.znt.speaker.prcmanager;

import java.util.List;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.download.IDownloadAidlInterface;
import com.znt.download.IDownloadCallback;
import com.znt.download.ZNTDownloadService;

public class ZNTDownloadServiceManager 
{

	private Application context = null;
	private IDownloadAidlInterface mIDownloadAidlInterface = null;
	
	private DownlaodCallBack mDownlaodCallBack = null;
	
	public interface DownlaodCallBack
	{
		public void onDownloadSpaceCheck(String size);
		public void onDownloadRecordInsert(String mediaName, String mediaUrl, String modifyTime);
		void onRemoveLargeSize(String url);
	}
	
	public ZNTDownloadServiceManager(Application context,DownlaodCallBack mDownlaodCallBack)
	{
		this.context = context;
		this.mDownlaodCallBack = mDownlaodCallBack;
	}
	
	public void addSonginfor(SongInfor songInfor)
	{
		try 
		{
			mIDownloadAidlInterface.addSongInfor(songInfor);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSonginfor(List<SongInfor> songInfor)
	{
		try 
		{
			mIDownloadAidlInterface.addSongInfors(songInfor);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateSaveDir(String dir)
	{
		try
		{
			mIDownloadAidlInterface.updateSaveDir(dir);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isBindSuccess()
	{
		return mIDownloadAidlInterface != null;
	}
	
	private ServiceConnection mConn = new ServiceConnection() 
	{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) 
        {
        	mIDownloadAidlInterface = IDownloadAidlInterface.Stub.asInterface(iBinder);
            if (mIDownloadAidlInterface != null)
            {
            	try 
            	{
            		mIDownloadAidlInterface.registerCallback( (com.znt.download.IDownloadCallback) mCallback);
				} 
            	catch (Exception e) 
            	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else
                Log.e("", "download service bind error!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) 
        {
        	mIDownloadAidlInterface = null;
        }
    };
    
    private IDownloadCallback mCallback = new IDownloadCallback.Stub() 
    {

		@Override
		public void actionPerformed(int id, String arg1, String arg2, String arg3) throws RemoteException 
		{
			// TODO Auto-generated method stub
			if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_CHECK_SPACE)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onDownloadSpaceCheck(arg1);
				
			}
			else if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_INSERT_RECORD)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onDownloadRecordInsert(arg1, arg2,arg3);
			}
			else if(id == ZNTDownloadService.CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE)
			{
				if(mDownlaodCallBack != null)
					mDownlaodCallBack.onRemoveLargeSize(arg1);
			}
		}  
		
    };   

    public void bindService() 
    {
    	try 
    	{
    		// UnBind
            unBindService();

            Intent intent = new Intent(context, ZNTDownloadService.class);
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
        if (mIDownloadAidlInterface != null) 
        {
        	context.unbindService(mConn);
        	mIDownloadAidlInterface = null;
        }
        
    }
    
}
