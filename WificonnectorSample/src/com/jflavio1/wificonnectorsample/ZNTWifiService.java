package com.jflavio1.wificonnectorsample;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jflavio1.wificonnector.WifiConnector;
import com.jflavio1.wificonnector.interfaces.ConnectionResultListener;
import com.jflavio1.wificonnector.interfaces.RemoveWifiListener;
import com.jflavio1.wificonnector.interfaces.ShowWifiListener;
import com.jflavio1.wificonnector.interfaces.WifiConnectorModel;
import com.jflavio1.wificonnector.interfaces.WifiStateListener;
import com.znt.diange.mina.entity.WifiInfor;
import com.znt.utils.NetWorkUtils;
import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.entity.WifiModelConstant;
import com.znt.wifimodel.timer.CheckSsidTimer;
import com.znt.wifimoidel.netset.WifiAdmin;

import org.json.JSONArray;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ZNTWifiService extends Service implements WifiConnectorModel
{

	private String TAG = "ZNTWifiService";

	private ServiceBinder mBinder;

	private Context mContext = null;

	private WifiConnector wifiConnector;

	private volatile String curConnectWifiName = "";
	private volatile String curConnectWifiPwd = "";

	private CheckSsidTimer mCheckWifiListTimer = null;
	private CheckSsidTimer mCheckSsidTimer = null;

	private WifiAdmin mWifiAdmin = null;

	private volatile List<ScanResult> mWifiScanResult = new ArrayList<>();

	private final int MSG_WIFI_SCAN = 100;
	private final int MSG_WIFI_CHECK = 101;
	private boolean isFirstWifiScan = true;
	private boolean isFirstWifiCheck = true;
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what == MSG_WIFI_SCAN)
			{
				if(!isFirstWifiScan)
					scanForWifiNetworks();
				else
					isFirstWifiScan = false;
			}
			else if(msg.what == MSG_WIFI_CHECK)
			{
				if(!isFirstWifiCheck)
					connectNextWifi();
				else
					isFirstWifiCheck = false;
			}
		};
	};

	public ZNTWifiService()
	{

	}

	private final String DEFAULT_WIFI_NAME = "DianYinGuanJia";
	private final String DEFAULT_WIFI_PWD = "zhunikeji";
	private void setDefaultWifi()
	{
		DBManager.INSTANCE.insertWifi(DEFAULT_WIFI_NAME, DEFAULT_WIFI_PWD);
		DBManager.INSTANCE.insertWifi("cstorebp", "51060018");
		DBManager.INSTANCE.insertWifi("cstoreap", "51060018562");
		DBManager.INSTANCE.insertWifi("dianyin", "12345678");
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

				DBManager.init(mContext);

				setDefaultWifi();//保存默认的WIFI信息

				createWifiConnectorObject();

				scanForWifiNetworks();

				mWifiAdmin = new WifiAdmin(mContext)
				{

					@Override
					public void onNotifyWifiConnected()
					{
						// TODO Auto-generated method stub
						doconenctWifiSuccess();
						Log.e("", "onNotifyWifiConnected");
					}

					@Override
					public void onNotifyWifiConnectFailed()
					{
						// TODO Auto-generated method stub
						Log.e("", "onNotifyWifiConnectFailed");
						connectNextWifi();
					}

					@Override
					public void myUnregisterReceiver(BroadcastReceiver receiver)
					{
						// TODO Auto-generated method stub
						Log.e("", "myUnregisterReceiver");
					}

					@Override
					public Intent myRegisterReceiver(BroadcastReceiver receiver,
													 IntentFilter filter)
					{
						// TODO Auto-generated method stub
						Log.e("", "myRegisterReceiver");
						return null;
					}
				};

				startWifiScanTimer();
			}
			catch (Exception e)
			{
				// TODO: handle exception
				showErrorLog(e);
			}
		}

		return mBinder;
	}

	private boolean isWifiScanRunning = false;
	private void startWifiScanTimer()
	{
		if(!isWifiScanRunning)
		{
			isFirstWifiScan = true;
			isWifiScanRunning = true;
			mCheckWifiListTimer = new CheckSsidTimer(getApplicationContext());
			mCheckWifiListTimer.setHandler(mHandler, MSG_WIFI_SCAN);
			mCheckWifiListTimer.setTimeInterval(30*1000);
			mCheckWifiListTimer.startTimer();
		}

	}
	private void stopWifiScanTimer()
	{
		if(mCheckWifiListTimer != null)
		{
			isWifiScanRunning = false;
			mCheckWifiListTimer.stopTimer();
			mCheckWifiListTimer = null;
		}
	}

	private void startWifiCheckTimer()
	{
		stopWifiCheckTimer();
		isFirstWifiCheck = true;
		mCheckSsidTimer = new CheckSsidTimer(getApplicationContext());
		mCheckSsidTimer.setHandler(mHandler, MSG_WIFI_CHECK);
		mCheckSsidTimer.setTimeInterval(30*1000);
		mCheckSsidTimer.startTimer();
	}
	private void stopWifiCheckTimer()
	{
		if(mCheckSsidTimer != null)
		{
			mCheckSsidTimer.stopTimer();
			mCheckSsidTimer = null;
		}
	}


	@Override
	public void createWifiConnectorObject()
	{
		wifiConnector = new WifiConnector(this);
		wifiConnector.setLog(true);
		wifiConnector.registerWifiStateListener(new WifiStateListener()
		{
			@Override
			public void onStateChange(int wifiState)
			{

			}

			@Override
			public void onWifiEnabled()
			{
				//MainActivity.this.onWifiEnabled();
			}

			@Override
			public void onWifiEnabling()
			{

			}

			@Override
			public void onWifiDisabling()
			{

			}

			@Override
			public void onWifiDisabled()
			{

			}
		});}

	@Override
	public void connectToWifiAccessPoint(final ScanResult scanResult, String password)
	{
		this.wifiConnector.setScanResult(scanResult, password);
		this.wifiConnector.setLog(true);
		this.wifiConnector.connectToWifi(new ConnectionResultListener()
		{
			@Override
			public void successfulConnect(String SSID)
			{
				stopWifiCheckTimer();

				if(!TextUtils.isEmpty(SSID)&& !TextUtils.isEmpty(curConnectWifiName))
				{
					if(SSID.contains("\""))
					{
						SSID = SSID.replace("\"", "");
					}
					if(curConnectWifiName.equals(SSID))
					{
						//wifi配置成功，可以上报
						doconenctWifiSuccess();
						Log.e("", "successfulConnect-->"+SSID);
					}
				}
				Toast.makeText(getApplicationContext(), "You are connected to " + SSID + "!!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void errorConnect(int codeReason)
			{
				stopWifiCheckTimer();
				connectNextWifi();
				//Toast.makeText(MainActivity.this, "Error on connecting to wifi: " + scanResult.SSID +"\nError code: "+ codeReason,Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStateChange(SupplicantState supplicantState)
			{

			}
		});
	}

	@Override
	public void disconnectFromAccessPoint(ScanResult scanResult)
	{
		this.wifiConnector.removeWifiNetwork(scanResult, new RemoveWifiListener()
		{
			@Override
			public void onWifiNetworkRemoved()
			{
				//Toast.makeText(MainActivity.this, "You have removed this wifi!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onWifiNetworkRemoveError()
			{
				//Toast.makeText(MainActivity.this, "Error on removing this network!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void destroyWifiConnectorListeners()
	{
		wifiConnector.unregisterWifiStateListener();
	}

	@Override
	public void scanForWifiNetworks()
	{
		// TODO Auto-generated method stub
		wifiConnector.showWifiList(new ShowWifiListener()
		{
			@Override
			public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult)
			{
				mWifiScanResult = wifiScanResult;
				//adapter.setScanResultList(wifiScanResult);
			}

			@Override
			public void onNetworksFound(JSONArray wifiList)
			{
				//Log.e("", "wifiList-->"+wifiList);
			}

			@Override
			public void errorSearchingNetworks(int errorCode)
			{
				Log.e("", "errorSearchingNetworks-->"+errorCode);//Toast.makeText(getApplicationContext(), "Error on getting wifi list, error code: " + errorCode, Toast.LENGTH_SHORT).show();
			}
		});
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

	public ScanResult getScanResultByWifiName(String wifiName)
	{
		ScanResult resultScanResult = null;
		int wifiCount = mWifiScanResult.size();
		for(int i=0;i<wifiCount;i++)
		{
			ScanResult tempScanResult = mWifiScanResult.get(i);
			if(!TextUtils.isEmpty(tempScanResult.SSID) && tempScanResult.SSID.equals(wifiName))
			{
				resultScanResult = tempScanResult;
				break;
			}
		}

		return resultScanResult;
	}

	private void doConnectWifi(String wifiName, String wifiPwd)
	{
		curConnectWifiName = wifiName;
		curConnectWifiPwd = wifiPwd;
		startWifiCheckTimer();
		try
		{
			ScanResult scanResult = getScanResultByWifiName(wifiName);
			if(scanResult == null)
			{

				try {
					Constructor<ScanResult> ctor = ScanResult.class.getDeclaredConstructor(ScanResult.class);
					ctor.setAccessible(true);
					ScanResult sr = ctor.newInstance(scanResult);
					sr.SSID = wifiName;
					sr.BSSID = wifiName;
					if(TextUtils.isEmpty(wifiPwd))
						sr.capabilities = "[ESS]";
					else
						sr.capabilities = "[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS]";
					mWifiAdmin.addNetwork(wifiName, wifiPwd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				connectToWifiAccessPoint(scanResult, wifiPwd);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Log.e("", e.getMessage());
			stopWifiCheckTimer();
			connectNextWifi();
		}

		Log.e(TAG, "startConnectWifi -- >" + wifiName + "   " + wifiPwd);
	}

	/**********************WIFI处理***************************
	 * 循环连接本地wifi，每个wifi连接30秒，当循环完成后，停止循环，等待下一次的wifi判断
	 * */
	private int wifiIndex = 0;
	private int loopConnectCount = 0;
	private boolean isConnectNextWifiFinished = true;
	private void connectNextWifi()
	{
		isConnectNextWifiFinished = false;
		if(DBManager.INSTANCE.getWifiCount() == 0)
		{
			isConnectNextWifiFinished = true;
			doconenctWifiFail();
			Log.e("", "********************wifi数据库中没有任何可以连接的wifi");
			return;
		}

		if(wifiIndex < DBManager.INSTANCE.getWifiCount())
		{
			WifiInfor tempInfor = getWifiInforByIndex();
			wifiIndex ++;
			doConnectWifi(tempInfor.getWifiName(), tempInfor.getWifiPwd());
			Log.e("", "********************正在连接第" + wifiIndex + " 个 wifi-->"+ tempInfor.getWifiName());

		}
		else
		{
			//循环连接失败
			loopConnectCount ++;
			Log.e("", "********************循环连接次数-->" + loopConnectCount);
			if(loopConnectCount >= 2)//循环连接三次都失败了，认定本地扫描失败
			{
				isConnectNextWifiFinished = true;
				doconenctWifiFail();
			}
			else
			{
				//继续遍历连接
				wifiIndex = 0;
				connectNextWifi();
			}
		}
	}

	private volatile int wifiReconnectCount = 0;

	private int offLineCount = 0;
	private void checkWifiStateWhenOffLine()
	{
		if(offLineCount >= 2)
		{
			//三次检查都掉线了，认定wifi连接不上了，要重新扫描wifi
			offLineCount = 0;
			if(isConnectNextWifiFinished)
			{
				startWifiScanTimer();
				connectNextWifi();
			}
		}
		else
			offLineCount ++;
	}

	private void resetLoopConnextCount()
	{
		wifiIndex = 0;
		loopConnectCount = 0;
	}

	private void doconenctWifiFail()
	{
		stopWifiCheckTimer();
		stopWifiScanTimer();

		resetLoopConnextCount();
		Log.e("", "********************WIFI连接失败-->"+curConnectWifiName);
		callback(WifiModelConstant.CALL_BACK_WIFI_CONNECT_FAIL, curConnectWifiName, curConnectWifiPwd);
	}
	private void doconenctWifiSuccess()
	{
		stopWifiCheckTimer();
		stopWifiScanTimer();

		resetLoopConnextCount();
		//更新本地连接的wifi
		WifiLocalDataEntity.newInstance(mContext).updateWifi(curConnectWifiName, curConnectWifiPwd);

		callback(WifiModelConstant.CALL_BACK_WIFI_CONNECT_SUCCESS, curConnectWifiName, curConnectWifiPwd);
	}

	private WifiInfor getWifiInforByIndex()
	{
		List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();
		WifiInfor tempInfor = wifiList.get(wifiIndex);
		return tempInfor;
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

				//WifiLocalDataEntity.newInstance(mContext).updateWifi(wifiName, wifiPwd);
				doConnectWifi(wifiName, wifiPwd);

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
					checkWifiStateWhenOffLine();
				}
				else
				{
					if(!NetWorkUtils.checkEthernet(getApplicationContext()))
					{
						//在线状态 并且不是有线连接，停止扫描WIFI
						//因为有线连接的时候在线，可能是在配置wifi
						stopWifiCheckTimer();
						stopWifiScanTimer();
					}
				}
			}
			catch (Exception e)
			{
				showErrorLog(e);
			}
		}

		@Override
		public boolean isHasWifi(String wifiName, String wifiPwd)
				throws RemoteException
		{
			// TODO Auto-generated method stub
			return DBManager.INSTANCE.isWifiExist(wifiName, wifiPwd);
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
    
    

}