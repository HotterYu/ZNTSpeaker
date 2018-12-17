package com.znt.speaker.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.znt.diange.mina.entity.DeviceStatusInfor;
import com.znt.speaker.R;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.dialog.MyAlertDialog;
import com.znt.speaker.dialog.MyProgressDialog;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.utils.FileUtils;
import com.znt.utils.MyToast;
import com.znt.utils.SystemUtils;

public class BaseActivity extends AppCompatActivity
{
	private MyToast myToast = null;
	private static MyProgressDialog mProgressDialog;
	private static MyAlertDialog myAlertDialog = null;
	private AudioManager mAudioManager = null;
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(null);
		
		myToast = new MyToast(getApplicationContext());
		mAudioManager = (AudioManager)getSystemService(getActivity().AUDIO_SERVICE);
		
		if(getLocalData().isInit())
		{
			File file = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR); 
			FileUtils.deleteFolder(file);
		}
		
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public String getCurrentSsid()
	{
		if(!SystemUtils.isNetConnected(getActivity()))
			return "";
		String ssid = SystemUtils.getConnectWifiSsid(getActivity());
		if(TextUtils.isEmpty(ssid))
			return "";
		return ssid.replace("\"","");
	}
	
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_BACK) 
		{
			dismissDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void doVolumeSet(DeviceStatusInfor devStatus)
	{
		int volume = devStatus.getVolume();
		int curVolume = getCurrentVolume();
		//int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
		if((volume >= 0) && (volume != curVolume))
		{
			setCurrentVolume(volume);
		}
		
		if(!TextUtils.isEmpty(devStatus.getVodFlag()))
		{
			Constant.PlayPermission = devStatus.getVodFlag();
			getLocalData().setPlayPermission(devStatus.getVodFlag());
		}
		
	}
	private int getCurrentVolume()
	{
		//int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
		return mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ); 
	}
	private void setCurrentVolume(int volume)
	{
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}
	public boolean isSpeakerphoneOn()
	{
		return mAudioManager.isSpeakerphoneOn();
	}
	
	public static final void showProgressDialog(Activity activity, String title,
			String message) 
	{
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在处理...";
		}
		if(mProgressDialog == null)
			mProgressDialog = new MyProgressDialog(activity, R.style.CustomDialog);
		mProgressDialog.setInfor(title, message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = (activity).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); 
			lp.height = (int)(display.getHeight()); 
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
	
	public final void showAlertDialog(Activity activity, OnClickListener listener, String title,
			String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		myAlertDialog.setOnClickListener(listener);
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); 
		lp.height = (int)(display.getHeight()); 
		myAlertDialog.getWindow().setAttributes(lp);
	}
	
	public final void showAlertDialog(Activity activity, String title,
			String message) 
	{
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		dismissDialog();
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //
		lp.height = (int)(display.getHeight()); //
		myAlertDialog.getWindow().setAttributes(lp);
	}

	public final void dismissDialog() 
	{
		if(getActivity() == null || getActivity().isFinishing())
			return;
		if (mProgressDialog != null && mProgressDialog.isShowing()) 
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (myAlertDialog != null && myAlertDialog.isShowing()) 
		{
			myAlertDialog.dismiss();
			myAlertDialog = null;
		}
	}
	
	public Activity getActivity()
	{
		return BaseActivity.this;
	}
	
	public LocalDataEntity getLocalData()
	{
		return LocalDataEntity.newInstance(getActivity());
	}
	
	public DBManager getDBManager()
	{
		return DBManager.INSTANCE;
	}
	
	public void showToast(int res)
	{
		myToast.show(res);
	}
	public void showToast(String res)
	{
		/*if(res != null)
		{
			myToast.show(res);
			//LogFactory.createLog().e(res);
		}*/
	}
	
	public boolean isProessRunning(Context context, String proessName) 
	{
		
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
 
		List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : lists)
		{
			if(info.processName.equals(proessName))
			{
				//Log.i("Service2进程", ""+info.processName);
				isRunning = true;
			}
		}
		
		return isRunning;
	}
	
	public boolean isServiceRunning(Context context, String serviceName)
	{
		
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> lists = am.getRunningServices(30);
		
		for (RunningServiceInfo info : lists) 
		{
			//判断服务
				if(info.service.getClassName().equals(serviceName))
				{
				Log.i("Service1进程", ""+info.service.getClassName());
				isRunning = true;
			}
		}
		
		
		return isRunning;
	}
}
