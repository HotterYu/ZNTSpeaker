package com.znt.download;


import java.io.File;
import java.util.List;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.download.entity.FileDownLoadManager;
import com.znt.permission.PermissionUtil;
import com.znt.utils.StorageUtils;

public class ZNTDownloadService extends Service
{
	
	private String TAG = "ZNTWifiService";
	
    private ServiceBinder mBinder;
    
    private Context mContext = null;
    
    public static final int CALL_BACK_DOWNLOAD_CHECK_SPACE = 300;
    public static final int CALL_BACK_DOWNLOAD_INSERT_RECORD = 301;
    public static final int CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE = 302;
    
    public ZNTDownloadService() 
    {
    	
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

            	//DBManager.init(mContext);
            	FileDownLoadManager.init(new IDownloadListener() 
            	{
					@Override
					public void onDownloadSpaceCheck(long size) 
					{
						// TODO Auto-generated method stub
						callback(CALL_BACK_DOWNLOAD_CHECK_SPACE, size+"", null, null);
					}
					
					@Override
					public void onDownloadRecordInsert(SongInfor songInfor, long modifyTime) 
					{
						// TODO Auto-generated method stub
						if(songInfor != null)
							callback(CALL_BACK_DOWNLOAD_INSERT_RECORD, songInfor.getMediaName(), songInfor.getMediaUrl(), modifyTime+"");
					}

					@Override
					public void onRemoveLargeSize(String url) {
						// TODO Auto-generated method stub
						
						callback(CALL_BACK_DOWNLOAD_REMOVE_LARGE_SIZE, url, null, null);
					}
				});
    		}
        	catch (Exception e) 
        	{
    			// TODO: handle exception
        		showErrorLog(e);
    		}
        }
        
        return mBinder;
    }

    final RemoteCallbackList<IDownloadCallback> mCallbacks = new RemoteCallbackList <IDownloadCallback>();
    void callback(int val, String arg1, String arg2, String arg3) 
    {   
        final int N = mCallbacks.beginBroadcast();  
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
    
    class ServiceBinder extends IDownloadAidlInterface.Stub 
    {

        public ServiceBinder() 
        {
            
        }

		@Override
		public void registerCallback(IDownloadCallback cb) throws RemoteException 
		{
			// TODO Auto-generated method stub
			if (cb != null) 
			{   
                mCallbacks.register(cb);  
            }  
		}

		@Override
		public void unregisterCallback(IDownloadCallback cb) throws RemoteException 
		{
			if(cb != null) 
			{  
                mCallbacks.unregister(cb);  
            }  
		}

		@Override
		public void addSongInfor(SongInfor infor) throws RemoteException 
		{
			// TODO Auto-generated method stub
			FileDownLoadManager.INSTANCE.addDownloadSong(infor);
			Log.e(TAG, "addSongInfor");
		}

		@Override
		public void addSongInfors(List<SongInfor> infors)
				throws RemoteException 
				{
			// TODO Auto-generated method stub
			FileDownLoadManager.INSTANCE.addDownloadSongs(infors);
			Log.e(TAG, "addbSongInfors");
		}

        @Override
        public void updateSaveDir(String dir) throws RemoteException {
            //FileDownLoadManager.INSTANCE.updateSaveDir(dir);
            //STORAGE_DIR = dir;
            updateStorageeDir();
        }
    }

    public static volatile String STORAGE_DIR = "";
    private void updateStorageeDir()
    {
        List<StorageUtils.Volume> V = StorageUtils.getVolume(getApplicationContext());
        String innerDir = Environment.getExternalStorageDirectory() + "";
        //List<String> dirs = SystemUtils.getStorageDirectoriesArrayList();
        for(int i=0;i<V.size();i++)
        {
            StorageUtils.Volume temp = V.get(i);
            File file = new File(temp.getPath());
            if(!file.getAbsolutePath().equals(innerDir) && file.exists())
            {
                STORAGE_DIR = temp.getPath();
                break;
            }
            else
            {
                Log.e("","");
            }
        }
    }

}