package com.znt.speaker.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;
import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.diange.mina.entity.DeviceInfor;
import com.znt.diange.mina.entity.DeviceStatusInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.StaticIpInfor;
import com.znt.push.entity.PushModelConstant;
import com.znt.push.v.IDevStatusView;
import com.znt.speaker.R;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.entity.PlayListFilter;
import com.znt.speaker.factory.CurPlanFactory;
import com.znt.speaker.factory.LocationFactory;
import com.znt.speaker.factory.MediaScanFactory;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.factory.UIManager.OnBindDeviceListener;
import com.znt.speaker.factory.UIManager.OnPlayerControllListener;
import com.znt.speaker.factory.UIManager.OnSetWifiListener;
import com.znt.speaker.http.HttpRequestID;
import com.znt.speaker.m.HttpRequestModel;
import com.znt.speaker.m.UsbHelper;
import com.znt.speaker.p.HttpPresenter;
import com.znt.speaker.p.MusicPlayPresenter;
import com.znt.speaker.p.MusicPlayPresenter.OnMediaPlayCallBack;
import com.znt.speaker.p.SDCardMountPresenter;
import com.znt.speaker.permission.PermissionHelper;
import com.znt.speaker.permission.PermissionInterface;
import com.znt.speaker.player.UpdateTimer;
import com.znt.speaker.prcmanager.ZNTDownloadServiceManager;
import com.znt.speaker.prcmanager.ZNTDownloadServiceManager.DownlaodCallBack;
import com.znt.speaker.prcmanager.ZNTPushServiceManager;
import com.znt.speaker.prcmanager.ZNTWifiServiceManager;
import com.znt.speaker.receiver.USBBroadCastReceiver;
import com.znt.speaker.v.IHttpRequestView;
import com.znt.speaker.v.IMusicReceiverView;
import com.znt.speaker.v.INetWorkView;
import com.znt.speaker.v.ISDCardMountView;
import com.znt.utils.DateUtils;
import com.znt.utils.MacUtils;
import com.znt.utils.NetWorkUtils;
import com.znt.utils.ShellUtils;
import com.znt.utils.SystemUtils;
import com.znt.utils.ViewUtils;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends BaseActivity implements IHttpRequestView, INetWorkView, 
														IDevStatusView, ISDCardMountView,
														IMusicReceiverView, OnSetWifiListener
														,OnPlayerControllListener
														,OnBindDeviceListener
														,DownlaodCallBack
														,OnMediaPlayCallBack
														,PermissionInterface
{
	
	private UIManager mUIManager;
	
	private HttpPresenter httpPresenter = null;
	private MusicPlayPresenter musicPlayPresenter = null;
	
	private ZNTPushServiceManager mZNTPushServiceManager = null; 
	private ZNTWifiServiceManager mZNTWifiServiceManager = null; 
	private ZNTDownloadServiceManager mZNTDownloadServiceManager = null;
	
	private LocationFactory locationFactory = null;
	private CurPlanFactory curPlanFactory = null;

	private PermissionHelper mPermissionHelper;
	
	private SDCardMountPresenter mSDCardMountPresenter = null;
	
	private DeviceInfor deviceInforRecv = null;
	
	private DeviceStatusInfor curDeviceStatusInfor = null;
	
	private UpdateTimer mUpdateTimer = null;
	
	private volatile boolean isNetWorkInit = true;
	private volatile boolean isGetCurPlanFinished = false;
	private boolean isGetCurPlanRunning = false;
	private volatile boolean isTimeSynFinished = false;
	private volatile boolean isRegisterFinished = false;
	private volatile boolean isFirstGetPlan = true;
	private boolean isOnline = false;
	
	private final int MSG_UPDATE_POSITION = 100;
	private final int MSG_UPDATE_TIME = 101;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{  
            case MSG_UPDATE_POSITION:  
            	try 
        		{
        			updatePlayTime();
        		} 
        		catch (Exception e) 
        		{
        			// TODO: handle exception
        		}
                break; 
            case MSG_UPDATE_TIME:  
            	try 
        		{
            		//mUIManager.showNetSpead(getCurMem());
        			mUIManager.showCurTime();
        			getLocalData().setLastServerTime(mUIManager.getCurTime());
        			checkProcess();
        		} 
        		catch (Exception e) 
        		{
        			// TODO: handle exception
        		}
                break; 
			}  
			
			super.handleMessage(msg); 
		};
	};
	
	private void updatePlayTime()
	{
		int pos = musicPlayPresenter.getCurPosition();
		mUIManager.setSeekbarProgress(pos);
		
		if(mUIManager.getMaxProgress() == 0)
		{
			int duration = musicPlayPresenter.getDuration();
			mUIManager.setSeekbarMax(duration);
			mUIManager.setTotalTime(duration);
		}
	}
	
	private String getCurMem()
	{
		//ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    //最大分配内存
	    //int memory = activityManager.getMemoryClass();
	    //最大分配内存获取方法2
		float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
	    //当前分配的总内存
		float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
	    //剩余内存
		float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
	    
	    DecimalFormat decimalFormat = new DecimalFormat("0.0");
	    
	    return "m:" + decimalFormat.format(maxMemory) + "/" + decimalFormat.format(totalMemory) + "/" + decimalFormat.format(freeMemory);
	}
	
	public static void toggleGPS(Context context){
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.music_player_layout);
		
		DBManager.init(getApplicationContext());//初始化数据库
		
		mUIManager = new UIManager(getActivity());
		mUIManager.setOnSetWifiListener(this);
		mUIManager.setOnBindDeviceListener(this);
		mUIManager.setOnPlayerControllListener(this);
		mUIManager.showDeviceInfor();
		
		/*long localTime = LocalDataEntity.newInstance(getActivity()).getLastServerTime();
		if(localTime > 0)
			mUIManager.setCurTime(localTime);*/
		
		/*RefWatcher refWatcher = MApplication.getRefWatcher(this);
	    refWatcher.watch(this);*/
		//toggleGPS(getApplicationContext());
	    //设置系统时区
		try 
		{
			AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		    mAlarmManager.setTimeZone("GMT+08:00");
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}

		//mPermissionHelper = new PermissionHelper(this, this);
		//mPermissionHelper.requestPermissions();
		
		mUpdateTimer = new UpdateTimer(getApplicationContext());
		mUpdateTimer.setHandler(mHandler, MSG_UPDATE_TIME);
		mUpdateTimer.setTimeInterval(1000);
		mUpdateTimer.startTimer();
		
		httpPresenter = new HttpPresenter(getActivity(), this);
		locationFactory = new LocationFactory(getActivity());
		musicPlayPresenter = new MusicPlayPresenter(getActivity(), mUIManager, this);
		
		mSDCardMountPresenter = new SDCardMountPresenter(getActivity(), this);
		mSDCardMountPresenter.registerStorageMount();
		
		startWifiService();
		startPushService();
		startDownloadService();
		
		httpPresenter.setZNTWifiServiceManager(mZNTWifiServiceManager);
		
		//延迟5秒开启，主要是805的盒子问题
		mHandler.postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				initSqlite();
				initData();
				if(getCurrentSsid() != null && getCurrentSsid().equals("DianYinGuanJia"))
					Constant.STATUS_CHECK_TIME_MAX = 18;
			}
		}, 5000);
	}
	
	/**
	 * 初始化数据库
	 */
	private void initSqlite()
	{
		if(getLocalData().isInit())
		{
			getLocalData().setIsInit(false);
			DBManager.INSTANCE.deleteDbFile();
			DBManager.INSTANCE.openDatabase();
		}
		else
		{
			int dbVersion = LocalDataEntity.newInstance(getActivity()).getDbVersion();
			if(dbVersion < Constant.DB_VERSION)
			{
				DBManager.INSTANCE.deleteDbFile();
				DBManager.INSTANCE.openDatabase();
				LocalDataEntity.newInstance(getActivity()).setDbVersion(Constant.DB_VERSION);
			}
		}
		
		//初始化计划相关的本地配置信息
		getLocalData().setPlanId("");
		getLocalData().setMusicUpdateTime("");
		getLocalData().setPlanTime("");
	}
	
	/**
	 * 初始化数据
	 */
	private void initData()
	{
		Constant.PlayPermission = LocalDataEntity.newInstance(getActivity()).getPlayPermission();
		Constant.PlayRes = LocalDataEntity.newInstance(getActivity()).getPlayRes();
	
		if(SystemUtils.isNetConnected(getActivity()))
			httpPresenter.register();
		
		MediaScanFactory.getInstance(getActivity()).scanLocalMedias();
		
		//updateManager.readLuncherFile();//更新Launcher
		
		
		curPlanFactory = new CurPlanFactory(getActivity(), mUIManager);
		
		mUIManager.showPrepareLoadView(true);
		
		DBManager.INSTANCE.deleteAllSong();
		
		updateParamsForPush(false);
		
		String code = getLocalData().getDeviceCode();
		if(!TextUtils.isEmpty(code))
		{
			ShellUtils.setCode(code);
		}
		
		musicPlayPresenter.getMusicPlayEngineImpl().getMediaPlayer().setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
			
			@Override
			public void onVideoSizeChanged(MediaPlayer mp, int mVideoWidth, int mVideoHeight) {
				// TODO Auto-generated method stub
				
				String degree = getLocalData().getVideoWhirl();
				
				if(mUIManager.getTextureView() != null)
				{
					//mUIManager.getTextureView().updateVideoSize(100, 300);
					mUIManager.getTextureView().updateVideoSize(mVideoWidth, mVideoHeight);
					
					if(degree.equals("0"))
						mUIManager.getTextureView().setRotation(0);
					if(degree.equals("1"))
						mUIManager.getTextureView().setRotation(90.0f);
					if(degree.equals("2"))
						mUIManager.getTextureView().setRotation(180.0f);
					if(degree.equals("3"))
						mUIManager.getTextureView().setRotation(270.0f);
					else//调整大小和位置
						mUIManager.getTextureView().setVideoShow(degree);
					
				}
			}
		});

        mUsbHelper = new UsbHelper(getApplicationContext(), new USBBroadCastReceiver.UsbListener() {
            @Override
            public void insertUsb(UsbDevice device_add) {
                Log.e("","");
                updateSaveDir();
            }

            @Override
            public void removeUsb(UsbDevice device_remove) {
                Log.e("","");
                updateSaveDir();
            }

            @Override
            public void getReadUsbPermission(UsbDevice usbDevice) {
                Log.e("","");
            }

            @Override
            public void failedReadUsb(UsbDevice usbDevice) {
                Log.e("","");
            }
        });
        updateSaveDir();
        UsbFile mUsbFile = mUsbHelper.getCurrentFolder();
        UsbMassStorageDevice[] mUsbFiles = mUsbHelper.getDeviceList();
        //mUsbHelper.getUsbFolderFileList()
		tryGetUsbPermission();
	}

	private UsbHelper mUsbHelper = null;
	private void updateSaveDir()
    {
        String dir = "";
        UsbMassStorageDevice[] mUsbFiles = mUsbHelper.getDeviceList();
        if(mUsbFiles.length > 0)
        {
            UsbDevice temp = mUsbFiles[0].getUsbDevice();
            dir = temp.getDeviceName();
        }
        else
            dir = Environment.getExternalStorageDirectory() + "";

        mZNTDownloadServiceManager.updateSaveDir(dir);
    }

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private UsbManager mUsbManager = null;
	private void tryGetUsbPermission(){
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbPermissionActionReceiver, filter);

		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

		//here do emulation to ask all connected usb device for permission
		for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
			//add some conditional check if necessary
			//if(isWeCaredUsbDevice(usbDevice)){
			if(mUsbManager.hasPermission(usbDevice)){
				//if has already got permission, just goto connect it
				//that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
				//and also choose option: not ask again
				afterGetUsbPermission(usbDevice);
			}else{
				//this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
				mUsbManager.requestPermission(usbDevice, mPermissionIntent);
			}
			//}
		}
	}
	private void afterGetUsbPermission(UsbDevice usbDevice){
		//call method to set up device communication
		//Toast.makeText(this, String.valueOf("Got permission for usb device: " + usbDevice), Toast.LENGTH_LONG).show();
		//Toast.makeText(this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();
		Log.e("","");
		//doYourOpenUsbDevice(usbDevice);
	}
	private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						//user choose YES for your previously popup window asking for grant perssion for this usb device
						if(null != usbDevice){
							afterGetUsbPermission(usbDevice);
						}
					}
					else {
						//user choose NO for your previously popup window asking for grant perssion for this usb device
						Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};

    private void startWifiService()
	{
		try 
		{
			destroyWifiService();
			mZNTWifiServiceManager = new ZNTWifiServiceManager(getApplication(), this);
			mUIManager.setZNTWifiServiceManager(mZNTWifiServiceManager);
			mZNTWifiServiceManager.bindService();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	private void startPushService()
	{
		try 
		{
			destroyPushService();
			mZNTPushServiceManager = new ZNTPushServiceManager(getApplication(), this);
			mZNTPushServiceManager.bindService();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	private void startDownloadService()
	{
		try 
		{
			destroyDownloadService();
			mZNTDownloadServiceManager = new ZNTDownloadServiceManager(getApplication(), this);
			mZNTDownloadServiceManager.bindService();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	
	private void destroyWifiService()
	{
		try 
		{
			if(mZNTWifiServiceManager != null)
			{
				mZNTWifiServiceManager.unBindService();
				mZNTWifiServiceManager = null;
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			mZNTWifiServiceManager = null;
		}
	}
	private void destroyPushService()
	{
		try 
		{
			if(mZNTPushServiceManager != null)
			{
				mZNTPushServiceManager.unBindService();
				mZNTPushServiceManager = null;
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			mZNTPushServiceManager = null;
		}
	}
	private void destroyDownloadService()
	{
		try 
		{
			if(mZNTDownloadServiceManager != null)
			{
				mZNTDownloadServiceManager.unBindService();
				mZNTDownloadServiceManager = null;
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			mZNTDownloadServiceManager = null;
		}
	}
	
	private String WIFI_PROCESS = "com.znt.speaker:ZNTWifiService";
	private String PUSH_PROCESS = "com.znt.speaker:ZNTPushService";
	private String DOWNLOAD_PROCESS = "com.znt.speaker:ZNTDownloadService";
	private int checkProcessCount = 2;
	private void checkProcess()
	{
		if(checkProcessCount > 60 * 60)
		{
			
			checkProcessCount = 0;
			
			checkRebootDevice();
			
			if(!isProessRunning(getApplicationContext(), PUSH_PROCESS))
			{
				startPushService();
			}
			if(!isProessRunning(getApplicationContext(), WIFI_PROCESS))
			{
				startWifiService();
			}
			if(!isProessRunning(getApplicationContext(), DOWNLOAD_PROCESS))
			{
				startDownloadService();
			}
		}
		else
			checkProcessCount ++;
		
		
	}
	
	private void doWifiConnectSusscess(String wifiName, String wifipwd)
	{
		isNetWorkInit = false;
		
		/*if(!NetWorkUtils.checkEthernet(getActivity()))
		*/httpPresenter.register();
		locationFactory.startLocation();
		
		mHandler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				dismissDialog();
				showToast("网络连接成功");
			}
		});
	}
	
    private void doSystemSetFinish()
    {
    	if(deviceInforRecv != null)
    	{
    		getLocalData().setDeviceInfor(deviceInforRecv);
    		
    		locationFactory.startLocation();
    		httpPresenter.register();
    	}
    }
    //FIXME
    private void doSystemSet(DeviceInfor devInfor)
	{
    	if(!TextUtils.isEmpty(devInfor.getWifiName()))
		{
			isNetWorkInit = true;
			mZNTWifiServiceManager.startConnectCurWifi(devInfor.getWifiName(), devInfor.getWifiPwd());
		}
	}
    
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		mSDCardMountPresenter.unregisterStorageReceiver();
		//FileDownLoadManager.getInstance().cancelAll();
		
		mUpdateTimer.stopTimer();
		
		closeAll();
		
		super.onDestroy();
	}
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		if(mUIManager.getWifiView().isShown())
		{
			mUIManager.getWifiView().setVisibility(View.GONE);
		}
		else
		{
			//moveTaskToBack(true);  
			//FileDownLoadManager.getInstance().cancelAll();
			closeAll();
		}
	}
	
	String mac = "";
	private String getNetInfo()
	{

		try
		{
			int localPlanCount = DBManager.INSTANCE.getLocalPlanCount();
			String netType = "";
			//String ip = SystemUtils.getIP();
			if(NetWorkUtils.checkEthernet(getApplicationContext()))
				netType = getResources().getString(R.string.network_type_wired);
			else
				netType = getResources().getString(R.string.network_type_wireless);
			String space = SystemUtils.getAvailabeMemorySize();
			int rebootCount = LocalDataEntity.newInstance(getApplicationContext()).getRebootCount();
			
			if(TextUtils.isEmpty(mac))
				mac = MacUtils.getMac(getApplicationContext());

			int rotation = SystemUtils.getScreenRotation(getApplicationContext());
			return space + " " + netType + "  " + rotation + "   " + SystemUtils.getIP()+ "  mac地址:"+ mac + "  " + rebootCount + "   localPlanCount:"+localPlanCount + "  PlanStatus:" + Constant.PLAN_GET_STATUS ;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			return e.getMessage();
		}
		
	}
	
	private void doGetDevSuccessProcess(DeviceStatusInfor deviceStatusInfor)
	{
		
		curDeviceStatusInfor = deviceStatusInfor;
		
		if(!isRegisterFinished)
		{
			httpPresenter.register();
			return;
		}
		
		if(synTimeProcess())
		{
			//同步服务期时间
			return;
		}
		
		if(doGetPushMusic(curDeviceStatusInfor))
		{
			//获取推送歌曲列表
			return;
		}
		
		doPlanProcess();
		
		checkWifiAndConnectWifi(curDeviceStatusInfor);
		
		doVolumeSet(curDeviceStatusInfor);//设置音量
		
		doPlayType(curDeviceStatusInfor);//设置播放模式
		
		doSetVideoWhirl(curDeviceStatusInfor);//设置屏幕方向
		
		updateParamsForPush(false);
		
		checkPlanWhenException();
		
	}
	
	private int checkPlanWhenExceptionCount = 0;
	private void checkPlanWhenException()
	{
		if(checkPlanWhenExceptionCount <= 3)
		{
			checkPlanWhenExceptionCount ++;
		}
		else 
		{
			checkPlanWhenExceptionCount = 0;
			if(musicPlayPresenter.isPlayListNone() && isOnline)
			{
				if(DBManager.INSTANCE.isCurTimeHasPlayList(mUIManager.getCurTime()))
				{
					//播放列表为空，并且当前时段有播放计划，重新获取播放计划
					isGetCurPlanRunning = false;
					isFirstGetPlan = true;
					getCurPlan();
				}
			}
		}
	}
	
	private void updateParamsForPush(boolean updateNow)
	{
		if(musicPlayPresenter != null)
		{
			String terminalId = LocalDataEntity.newInstance(getActivity()).getDeviceCode();
			mZNTPushServiceManager.putRequestParams(terminalId, musicPlayPresenter.getCurPlaySongType()
					, musicPlayPresenter.getCurPlaySongName(), getNetInfo(), updateNow);
		}
	}
	
	private boolean synTimeProcess()
	{
		if((mUIManager.getCurTime() == 0) || !isTimeSynFinished)
		{
			httpPresenter.initTerminal();
			return true;
		}
		return false;
	}
	
	private void doPlanProcess()
	{
		if(!isGetCurPlanFinished)
		{
			getCurPlan();
			return;
		}
		String planTime = curDeviceStatusInfor.getPlanTime();
		String musicUpdateTime = curDeviceStatusInfor.getMusicLastUpdate();
		if(TextUtils.isEmpty(planTime))
		{
			//停止播放音乐，但是在有计划更新的时候，可以在闲时下载歌曲到本地
			musicPlayPresenter.handleProcessWhenNonePlayTime();
			String localMusicUpdateTime = getLocalData().getMusicUpdateTime();
			//String localPlanTime = getLocalData().getPlayTime();
			if(!TextUtils.isEmpty(musicUpdateTime) && !musicUpdateTime.equals(localMusicUpdateTime))
			{
				getCurPlan();
			}
			return;
		}
		
		String planId = curDeviceStatusInfor.getPlanId();
		String localPlanId = getLocalData().getPlayId();
		if((TextUtils.isEmpty(localPlanId) || !localPlanId.equals(planId)))
		{
			getCurPlan();
		}
		else
		{
			String localMusicUpdateTime = getLocalData().getMusicUpdateTime();
			String localPlanTime = getLocalData().getPlayTime();
			if(!TextUtils.isEmpty(musicUpdateTime) && !musicUpdateTime.equals(localMusicUpdateTime))
			{
				getCurPlan();
			}
			else if(!TextUtils.isEmpty(planTime) && !planTime.equals(localPlanTime))
			{
				getCurPlan();
				//getCurPlayMusicsFromLocal(false);
			}
			/*else
			{
				if(musicPlayPresenter.isPlayListNone())
				{
					getCurMusics();
				}
			}*/
		}
	}
	
	private int curPlanRuninngCount = 0;
	private void getCurPlan()
	{
		if(!isGetCurPlanRunning)
		{
			isGetCurPlanRunning = true;
			
			if(isFirstGetPlan)
			{
				curPlanRuninngCount = 0;
				isFirstGetPlan = false;
				getLocalData().setPlanTime("");
				httpPresenter.getCurPlan();
			}
			else
			{
				int getPlanInternalTime = (int) (Math.random() * 60);
				mHandler.postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						curPlanRuninngCount = 0;
						getLocalData().setPlanTime("");
						httpPresenter.getCurPlan();
					}
				}, getPlanInternalTime * 1000);
			}
		}
		else
		{
			curPlanRuninngCount ++;
			if(curPlanRuninngCount > 8)
			{
				curPlanRuninngCount = 0;
				isGetCurPlanRunning = false;
			}
		}
	}
	private void getCurMusics()
	{
		httpPresenter.getPlanMusics();
	}
	
	private void checkWifiAndConnectWifi(DeviceStatusInfor devStatus)
	{
		String wifiName = devStatus.getWifiName();
		if(!TextUtils.isEmpty(wifiName))
		{
			String localWifiName = mZNTWifiServiceManager.getCurConnectWifiName();
			String localWifiPwd = mZNTWifiServiceManager.getCurConnectWifiPwd();
			if(TextUtils.isEmpty(localWifiName)
					|| !localWifiName.equals(wifiName)
					|| !localWifiPwd.equals(devStatus.getWifiPwd()))//服务器 wifi有更新
			{
				
				if(!mZNTWifiServiceManager.isWifiExist(wifiName, devStatus.getWifiPwd()))
				{
					localWifiName = wifiName;
					
					String wifiPwd = devStatus.getWifiPwd();
					if(deviceInforRecv == null)
						deviceInforRecv = new DeviceInfor();
					deviceInforRecv.setWifiName(wifiName);
					deviceInforRecv.setWifiPwd(wifiPwd);
					doSystemSet(deviceInforRecv);
				}
				else
				{
					Log.e("", "该WIFI已经存在wifi数据库中，等待扫描连接即可");
					httpPresenter.updateWifiInfor(wifiName, devStatus.getWifiPwd(), HttpRequestModel.WIFI_CONFIG_EXIST);
				}
			}
		}
	}
	
	private boolean doGetPushMusic(DeviceStatusInfor devStatus)
	{
		String pushStatus = devStatus.getPushStatus();
		if(pushStatus.equals("1"))
		{
			httpPresenter.getPushMusics();
			return true;
		}
		
		return false;
	}
	private void doPlayType(DeviceStatusInfor devStatus)
	{
		//0顺序循环，1随机循环
		String localDownLoadFlag = getLocalData().getDownloadFlag();
		String downloadFlag = devStatus.getDownloadFlag();
		if(!localDownLoadFlag.equals(downloadFlag))
		{
			getLocalData().setDownloadFlag(downloadFlag);
		}
		
	}
	private void doSetVideoWhirl(DeviceStatusInfor devStatus)
	{
		String videoWhirl = devStatus.getVideoWhirl();
		if(!videoWhirl.equals(getLocalData().getVideoWhirl()))
		{
			getLocalData().setVideoWhirl(videoWhirl);
			//mUIManager.setSurfaceViewOritation(videoWhirl);
			//mUIManager.getTextureView().updateVideoSize(224, 756);
			
			//setOritation();
			musicPlayPresenter.startPlaySongForce();
			/*if(videoWhirl.equals("4") 
				||videoWhirl.equals("5") 
				||videoWhirl.equals("6") 
				||videoWhirl.equals("7"))
			{
				mUIManager.getTextureView().setLocation(videoWhirl);
			}
			else
			{
				musicPlayPresenter.startPlaySongForce();
			}*/
		}
	}
	private void doGetPlayListProcess(List<SongInfor> tempList)
	{
		
		if(tempList.size() > 0)
		{

			PlayListFilter mPlayListFilter = new PlayListFilter();
			mPlayListFilter.setSrcList(tempList);

			doImageProecess(mPlayListFilter);

			musicPlayPresenter.addPlayList(mPlayListFilter.getMediaList());
			musicPlayPresenter.startPlaySong();
		}
		
		dismissDialog();
		
		//getCurPlan();//获取播放计划
		
	}

	private void doImageProecess(final PlayListFilter mPlayListFilter)
	{

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mUIManager.updateImgList(mPlayListFilter);
			}
		});
	}
	
	public void closeAll()
	{
		if(mUIManager != null)
		{
			mUIManager.stop();
		}
		
		showProgressDialog(getActivity(), null, "正在退出...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				if(locationFactory != null)
					locationFactory.stopLocation();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						
						DBManager.INSTANCE.close();
						
						//stopService(new Intent(mContext, DMSService.class));
						
						mUIManager.unInit();
						musicPlayPresenter.closePlayer();
						//netWorkPresenter.stopCheckSSID();
						
						dismissDialog();
						System.exit(0);
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onReceive(Intent intent) 
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) 
		{
			
			if(isNetWorkInit)
        	{
				isNetWorkInit = false;
				return;
        	}
			
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();  
            if(info != null && info.isAvailable()) 
            {
            	if(info.getType() == ConnectivityManager.TYPE_ETHERNET)
            		showToast("有线网络连接成功");
            	else if(info.getType() == ConnectivityManager.TYPE_WIFI)
            		showToast("无线网络连接成功");
            } 
		}
	}

	@Override
	public void onMediaChange(boolean isAdd, String path) 
	{
		// TODO Auto-generated method stub
		if(isAdd)
		{
			//U盘拔插状态
			StaticIpInfor staticIpInfor = NetWorkUtils.getWifiSetInfoFromUsb(path);
			if(staticIpInfor == null)
			{
				showToast("未检测到IP信息或者IP信息解析失败");
			}
			else
			{
				Intent intent = getPackageManager().getLaunchIntentForPackage("com.znt.install");
				if(intent != null)
				{
					intent.putExtra("ip_set_addr", staticIpInfor.getIp());
					intent.putExtra("ip_set_gateway", staticIpInfor.getGateway());
					intent.putExtra("ip_set_dns1", staticIpInfor.getDns1());
					intent.putExtra("ip_set_dns2", staticIpInfor.getDns2());
					startActivity(intent);
				}
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
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showToast("WIFI热点创建失败");
			}
		});
	}

	@Override
	public void createApSuccess() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void connectWifiSatrt(final String wifiName)
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				//showProgressDialog(getActivity(), null, "正在连接wifi-->" + wifiName);
			}
		});
	}

	@Override
	public void connectWifiFailed(String wifiName, String wifipwd) 
	{
		// TODO Auto-generated method stub
		dismissDialog();
		/*if(deviceInforRecv != null && wifiName.contains(deviceInforRecv.getWifiName()))
			httpPresenter.updateWifiInfor(wifiName, wifipwd, false);*/
	}

	
	@Override
	public void connectWifiSuccess(String wifiName, String wifipwd) 
	{
		doWifiConnectSusscess(wifiName, wifipwd);
		
		if(deviceInforRecv != null && wifiName.contains(deviceInforRecv.getWifiName()))
		{
			httpPresenter.updateWifiInfor(wifiName, wifipwd, HttpRequestModel.WIFI_CONFIG_SUCCESS);
		}
	}

	@Override
	public void requestStart(int requestId)
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			Log.d("", "正在获取设备状态...");
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			mHandler.post(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					showProgressDialog(getActivity(), null, "正在获取播放列表...");
				}
			});
		}
		else if(requestId == HttpRequestID.GET_CUR_PLAN)
		{

			mHandler.post(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					showProgressDialog(getActivity(), null, "正在获取播放计划...");
				}
			});
		}
		else if(requestId == HttpRequestID.BIND_SPEAKER)
		{
			mHandler.post(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					showProgressDialog(getActivity(), null, "正在绑定设备...");
				}
			});
		}
	}

	@Override
	public void requestError(int requestId, String error) 
	{
		// TODO Auto-generated method stub
		mHandler.post(new Runnable()
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			getLocalData().setPlanId("");
		}
		else if(requestId == HttpRequestID.BIND_SPEAKER)
		{
			showToast("设备绑定失败-->"+error);
		}
		else if(requestId == HttpRequestID.INIT_TERMINAL)
		{
			isTimeSynFinished = false;
			showToast("时间同步失败-->"+error);
		}
		else if(requestId == HttpRequestID.GET_CUR_PLAN)
		{
			isGetCurPlanRunning = false;
		}
	}
	@Override
	public void requestSuccess(Object obj, int requestId)
	{
		// TODO Auto-generated method stub
		
		mHandler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});
		
		if(requestId == HttpRequestID.GET_CUR_PLAN)
		{
			CurPlanInfor curPlanInfor = (CurPlanInfor)obj;
			if(curPlanInfor != null)
			{
				curPlanFactory.setCurPlanInfor(curPlanInfor);
				List<SongInfor> tempList = DBManager.INSTANCE.getAllPlanMusics(DBManager.FILE_TYPE_ONLIE);
				mZNTDownloadServiceManager.addSonginfor(tempList);
				
			}
			
			getCurPlayMusicsFromLocal(false);
			
			if(curDeviceStatusInfor != null)
			{
				String planId = curDeviceStatusInfor.getPlanId();
				String musicUpdateTime = curDeviceStatusInfor.getMusicLastUpdate();
				String planTime = curDeviceStatusInfor.getPlanTime();

				getLocalData().setPlanId(planId);
				getLocalData().setMusicUpdateTime(musicUpdateTime);
				getLocalData().setPlanTime(planTime);
			}
			isGetCurPlanRunning = false;
			isGetCurPlanFinished = true;
			
		}
		else if(requestId == HttpRequestID.INIT_TERMINAL)
		{
			String systemTime = (String) obj;
			if(!TextUtils.isEmpty(systemTime))
			{
				mUIManager.setCurTime(Long.parseLong(systemTime));
				//devStatusPresenter.updateLocalServerTime();
				getLocalData().setLastServerTime(mUIManager.getCurTime());
				getLocalData().setLastRebootTime(mUIManager.getCurTime());
				
			}
			mUIManager.showDeviceInfor();
			musicPlayPresenter.startPlaySong();
			
			isTimeSynFinished = true;
			
		}
		else if(requestId == HttpRequestID.REGISTER || requestId == HttpRequestID.UPDATE_SPEAKER_INFOR   || requestId == HttpRequestID.UPDATE_WIFI_INFOR)
		{
			isRegisterFinished = true;
			httpPresenter.initTerminal();
			mUIManager.showDeviceInfor();
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			List<SongInfor> tempList = (List<SongInfor>)obj;
			if(tempList != null)
			{
				//FileDownLoadManager.getInstance().addDownloadSongs(tempList);
				doGetPlayListProcess(tempList);
			}
			else
				requestError(requestId, "no data");
		}
		else if(requestId == HttpRequestID.GET_PUSH_MUSIC)
		{
			musicPlayPresenter.startPlaySong();
		}
		else if(requestId == HttpRequestID.BIND_SPEAKER)
		{
			showToast("绑定成功");
		}
	}
	
	private void getCurPlayMusicsFromLocal(boolean isOffline )
	{
		List<SongInfor> tempList = curPlanFactory.getCurPlanMusics(isOffline);
		if(tempList != null)
		{
			doGetPlayListProcess(tempList);
		}
		/*else
			requestError(requestId, "no data");*/
	}

	@Override
	public void requestNetWorkError() 
	{
		// TODO Auto-generated method stub
		mHandler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});
	}

	private void checkRebootDevice()
	{
		try
		{
			if(isOver24Hours())//判断上次关机时间是不是超过24小时
			{
				String curHour = DateUtils.getHour(mUIManager.getCurTime());
				//String s3 = DateUtils.getHour(System.currentTimeMillis());
				if(!TextUtils.isEmpty(curHour))
				{
					int curHourInt = Integer.parseInt(curHour);
					if((curHourInt == 2) || (curHourInt == 3) || (curHourInt == 4))//凌晨
					{
						//更新关机时间
						getLocalData().setLastRebootTime(mUIManager.getCurTime());
						getLocalData().increaseRebootCount();
						ShellUtils.reboot();
					}
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	
	private int checkRebootStatusCount = 0;
	private boolean isOver24Hours()
	{
		
		long lastRebootTime = getLocalData().getLastRebootTime();
		if(lastRebootTime <= 0)
			return false;
		long curTime = mUIManager.getCurTime();
		if((curTime - lastRebootTime) >= 24 * 60 * 60 * 1000)
			return true;
		return false;
	}

	@Override
	public void OnWifiSetStart(String wifiName, String wifiPwd) 
	{
		// TODO Auto-generated method stub
		if(deviceInforRecv == null)
			deviceInforRecv = new DeviceInfor();
		deviceInforRecv.setWifiName(wifiName);
		deviceInforRecv.setWifiPwd(wifiPwd);
		doSystemSet(deviceInforRecv);
	}

	@Override
	public void onClickPause() 
	{
		// TODO Auto-generated method stub
		musicPlayPresenter.mPlayerEngineImpl.pause();
	}

	@Override
	public void onClickPlay() 
	{
		// TODO Auto-generated method stub
		musicPlayPresenter.mPlayerEngineImpl.play();
	}

	@Override
	public void onClickSeek(int pos) 
	{
		// TODO Auto-generated method stub
		musicPlayPresenter.mPlayerEngineImpl.skipTo(pos);
	}

	@Override
	public void onDeviceBind(String name, String account) 
	{
		// TODO Auto-generated method stub
		httpPresenter.bindSpeaker(name, account);
	}

	@Override
	public void onPushSuccess(DeviceStatusInfor devStatusInfor) 
	{
		// TODO Auto-generated method stub
		Log.e("", "onPushSuccess");
		isOnline = true;
		mZNTWifiServiceManager.devStatusCheck(true);
		doGetDevSuccessProcess(devStatusInfor);
	}

	@Override
	public void onPushFail(int count) 
	{
		// TODO Auto-generated method stub
		Log.e("", "onPushFail");
		isOnline = false;
		getCurPlayMusicsFromLocal(true);
		
		mZNTWifiServiceManager.devStatusCheck(false);
	}

	@Override
	public void onNotRegister() 
	{
		// TODO Auto-generated method stub
		if(!isRegisterFinished)
			httpPresenter.register();
		else
			updateParamsForPush(false);
	}

	@Override
	public void onSpaceCheck(long size) 
	{
		// TODO Auto-generated method stub
		Log.e("", "onSpaceCheck");
		DBManager.INSTANCE.checkAndReleaseSpace(size);
	}

	@Override
	public void onPushCheck(int count)
	{
		// TODO Auto-generated method stub
		
		if(count >= PushModelConstant.STATUS_CHECK_TIME_MAX - 1)
		{
			if(checkRebootStatusCount < 10)
			{
				checkRebootStatusCount ++;
			}
			else
			{
				checkRebootStatusCount = 0;
				checkRebootDevice();
			}
		}
		
		musicPlayPresenter.checkDelay();
		//update UI
		ViewUtils.sendMessage(mHandler, MSG_UPDATE_POSITION);
	}

	@Override
	public void onRegisterCallBack() 
	{
		// TODO Auto-generated method stub
		String devId = getLocalData().getDeviceCode();
		Log.e("", "onRegisterCallBack");
	}

	@Override
	public void onDownloadSpaceCheck(String size) 
	{
		// TODO Auto-generated method stub
		long fSize = 0;
		if(!TextUtils.isEmpty(size))
			fSize = Long.parseLong(size);
		DBManager.INSTANCE.checkAndReleaseSpace(fSize);
	}
	
	@Override
	public void onRemoveLargeSize(String url) 
	{
		// TODO Auto-generated method stub
		//DBManager.INSTANCE.deleteSongRecordByUrl(url);
		DBManager.INSTANCE.deleteCurPlanMusicByUrl(url);
		musicPlayPresenter.removeMusic(url);
		
	}

	@Override
	public void onDownloadRecordInsert(String mediaName, String mediaUrl, String modifytime)
	{
		// TODO Auto-generated method stub
		SongInfor infor = new SongInfor();
		infor.setMediaName(mediaName);
		infor.setMediaUrl(mediaUrl);
		
		long time = 0;
		if(!TextUtils.isEmpty(modifytime))
			time = Long.parseLong(modifytime);
		DBManager.INSTANCE.insertSongRecord(infor, time);
	}

	@Override
	public void onMediaPlayStart(SongInfor songInfor) {
		// TODO Auto-generated method stub
		mZNTDownloadServiceManager.addSonginfor(songInfor);
		updateParamsForPush(true);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
			//权限请求结果，并已经处理了该回调
			return;
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public int getPermissionsRequestCode() {
		return 10000;
	}

	@Override
	public String[] getPermissions() {
		return new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.ACCESS_WIFI_STATE,
				Manifest.permission.CHANGE_WIFI_STATE,
				Manifest.permission.ACCESS_FINE_LOCATION

		};
	}
	@Override
	public void requestPermissionsSuccess()
	{
		//权限请求用户已经全部允许
		try
		{
			initData();
		}
		catch (Exception e)
		{
			if(e == null)
				showToast("初始化失败");
			else
				showToast("初始化失败："+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void requestPermissionsFail() {
		//权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
		mPermissionHelper.requestPermissions();
		//finish();
	}
}
