package com.znt.wifimodel.m;

import java.util.List;

import com.znt.diange.mina.entity.WifiInfor;
import com.znt.wifimodel.db.DBManager;
import com.znt.wifimodel.entity.WifiLocalDataEntity;
import com.znt.wifimodel.receiver.WifiReceiver;
import com.znt.wifimodel.receiver.WifiReceiver.WifiStateListener;
import com.znt.wifimodel.timer.CheckSsidTimer;
import com.znt.wifimodel.utils.NetWorkUtils;
import com.znt.wifimodel.v.INetWorkView;
import com.znt.wifimoidel.netset.WifiAdmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class NetWorkModel1 implements WifiStateListener
{
	private final String TAG = "NetWorkModel";
	
	private INetWorkView iNetWorkView = null;
	
	private Context mContext = null;
	private WifiAdmin mWifiAdmin = null;
	
	private WifiReceiver mWifiReceiver = null;
	
	private volatile String curConnectWifiName = "";
	private volatile String curConnectWifiPwd = "";
	
	private boolean isConnectRunning = false;
	private CheckSsidTimer mCheckWifiStateTimer;
	private final int CHECK_WIFI_STATE = 1;
	private volatile boolean isWifiRegistered = false;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == CHECK_WIFI_STATE)
			{
				if(mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED)
				{
					stopCheckWifiState();
					Log.e("", "********************WIFI���ش򿪳ɹ�����������WIFI");
					doConnectWifi();
				}
				else if(mCheckWifiStateTimer.isOver())
				{
					doconenctWifiFail();//��������ʧ��
					Log.e("", "********************WIFI���ش�ʧ��");
					stopCheckWifiState();
				}
			}
		};
	};
	public NetWorkModel1(Context context,INetWorkView iNetWorkView)
	{
		this.mContext = context;
		this.iNetWorkView = iNetWorkView;
		
		mWifiReceiver = new WifiReceiver(context);
		mWifiReceiver.setWifiStateListener(this);
		registerWifiReceiver();
		
		curConnectWifiName = WifiLocalDataEntity.newInstance(mContext).getWifiName();
		curConnectWifiPwd = WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
		
		mCheckWifiStateTimer = new CheckSsidTimer(context);
		mCheckWifiStateTimer.setHandler(mHandler, CHECK_WIFI_STATE);
		mCheckWifiStateTimer.setTimeInterval(1000);
		mCheckWifiStateTimer.setMaxTime(20);
		
		if(mWifiAdmin == null)
		{
			mWifiAdmin = new WifiAdmin(context)
			{
				@Override
				public void onNotifyWifiConnected()
				{
					// TODO Auto-generated method stub
					Log.e("", "!!!!!!!!!!!!!!!!!!!!!!!!WIFI���ӳɹ�-->"+curConnectWifiName);
					//doconenctWifiSuccess();
				}
				
				@Override
				public void onNotifyWifiConnectFailed()
				{
					// TODO Auto-generated method stub
					//wifi����ʧ���ˣ�������һ��WIFI
					Log.e("", "!!!!!!!!!!!!!!!!!!!!!!!!WIFI����ʧ�ܣ�׼��������һ��WIFI");
					connectNextWifi();
				}
				
				@Override
				public void myUnregisterReceiver(BroadcastReceiver receiver)
				{
					// TODO Auto-generated method stub
					//mContext.unregisterReceiver(receiver);
				}
				
				@Override
				public Intent myRegisterReceiver(BroadcastReceiver receiver,
						IntentFilter filter)
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
	}
	
	public boolean isConnectRunning()
	{
		return isConnectRunning;
	}
	
	private void registerWifiReceiver()
	{
		if(isWifiRegistered)
		{
			isWifiRegistered = false;
			mContext.unregisterReceiver(mWifiReceiver);
		}
		
		if(!isWifiRegistered)
		{
			IntentFilter filter = new IntentFilter();  
	        filter.addAction("android.net.wifi.RSSI_CHANGED");  
	        filter.addAction("android.net.wifi.STATE_CHANGE");
	        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
	        mContext.registerReceiver(mWifiReceiver, filter);  
	        isWifiRegistered = true;
		}
	}
	
	public boolean isWifiEnabled()
	{
		return mWifiAdmin.isWifiEnabled();
	}
	public void openWifi()
	{
		if(!isWifiEnabled())
			mWifiAdmin.openWifi();
	}
	
	public WifiAdmin getWifiAdmin()
	{
		return mWifiAdmin;
	}
	public void startConnectCurrentWifiAndReset()
	{
		
		isCurWifiConnectedSuccessRepoted = false;
		
		loopConnectFailCountMax = 0;
		isConnectRunning = false;
		startConnectCurrentWifi();
		showLogStep("startConnectCurrentWifi");
	}
	/**
	 * ���ӱ��ر���ĵ�ǰWIFI���������ʧ���ˣ��ͱ�������wifi����
	 */
	public void startConnectCurrentWifi()
	{
		Log.e("", "��ʼ���WIFI-->"+loopConnectFailCountMax);
		if(loopConnectFailCountMax >= 3)
		{
			loopConnectFailCountMax ++;
			if(loopConnectFailCountMax >= 20)
			{
				loopConnectFailCountMax = 0;
				Log.e("", "***************wifi�������¿�ʼ-->"+loopConnectFailCountMax);
			}
			else
				Log.e("", "***************wifiѭ�����Ӵ����������ֵ����ͣ������-->"+loopConnectFailCountMax);
		}
		else
		{
			
			String name = WifiLocalDataEntity.newInstance(mContext).getWifiName();
			String pwd = WifiLocalDataEntity.newInstance(mContext).getWifiPwd();
			
			connectWifi(name, pwd);
			
		}
	}
	
	public void connectWifi(final String name, String pwd)
	{
		
		//pwd = pwd.replace(" ", "");
		Log.e(TAG, "***********start connect wifi-->" + name + "   " + pwd);
		this.curConnectWifiName = name;
		this.curConnectWifiPwd = pwd;
		isConnectRunning = true;
		
		iNetWorkView.connectWifiSatrt(name);//��ʼ��������
		
		if(isWifiEnabled())
		{
			doConnectWifi();
		}
		else
		{
			openWifi();
			startCheckWifiState();
		}
	}
	
	private void doConnectWifi()
	{
		try 
		{
			if(TextUtils.isEmpty(curConnectWifiName) || !mWifiAdmin.ifHasWifi(curConnectWifiName))
			{
				//Log.e("", "**************����û��WIFI����ʼ������һ��WIFI");
				connectNextWifi();
			}
			else
			{
				mWifiReceiver.setConnectWifi(curConnectWifiName);
				showLogStep("addNetwork");
				isFailWhenSuccess = false;
				mWifiAdmin.addNetwork(curConnectWifiName, curConnectWifiPwd);
				//Log.e("", "**************������WIFI���������ӱ���WIFI-->"+curConnectWifiName);
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	
	private void startCheckWifiState()
	{
		mCheckWifiStateTimer.startTimer();
	}
	public void stopCheckWifiState()
	{
		if(!mCheckWifiStateTimer.isOver() && !mCheckWifiStateTimer.isStop())
			mCheckWifiStateTimer.stopTimer();
	}
	
	/**
	 * ��鱾��WIFI
	 */
	public void checkIfShouldConnectWifi()
    {
		
		Log.e(TAG, "***********checkIfShouldConnectWifi");
		
    	if(NetWorkUtils.checkEthernet(mContext))
    	{
    		//������������磬����Ҫ����WIFI
    		return;
    	}
    	
    	startConnectCurrentWifi();
    }
	
	
	/**********************WIFI����***************************
	 * ѭ�����ӱ���wifi��ÿ��wifi����30�룬��ѭ����ɺ�ֹͣѭ�����ȴ���һ�ε�wifi�ж�
	 * */
	private int wifiIndex = 0;
	private int loopConnectCount = 0;
	private int loopConnectFailCountMax = 0;
	private void connectNextWifi()
	{
		if(DBManager.INSTANCE.getWifiCount() == 0)
		{
			doconenctWifiFail();
			Log.e("", "********************wifi���ݿ���û���κο������ӵ�wifi");
			return;
		}
		
		if(wifiIndex < DBManager.INSTANCE.getWifiCount())
		{
			WifiInfor tempInfor = getWifiInforByIndex();
			wifiIndex ++;
			connectWifi(tempInfor.getWifiName(), tempInfor.getWifiPwd());
			Log.e("", "********************�������ӵ�" + wifiIndex + " �� wifi-->"+ tempInfor.getWifiName());
			
		}
		else
		{
			//ѭ������ʧ��
			loopConnectCount ++;
			Log.e("", "********************ѭ�����Ӵ���-->" + loopConnectCount);
			if(loopConnectCount >= 2)//ѭ���������ζ�ʧ���ˣ��϶�����ɨ��ʧ��
				doconenctWifiFail();
			else
			{
				//������������
				wifiIndex = 0;
				connectNextWifi();
			}
		}
	}
	
	private WifiInfor getWifiInforByIndex()
	{
		List<WifiInfor> wifiList = DBManager.INSTANCE.getWifiList();
		WifiInfor tempInfor = wifiList.get(wifiIndex);
		return tempInfor;
	}
	
	private void resetLoopConnextCount()
	{
		wifiIndex = 0;
		loopConnectCount = 0;
	}
	
	private void doconenctWifiFail()
	{
		loopConnectFailCountMax ++;
		isConnectRunning = false;
		resetLoopConnextCount();
		Log.e("", "********************WIFI����ʧ��-->"+curConnectWifiName);
		if(iNetWorkView != null)
			iNetWorkView.connectWifiFailed(curConnectWifiName, curConnectWifiPwd);
	}
	private void doconenctWifiSuccess()
	{
		isConnectRunning = false;
		resetLoopConnextCount();
		//���±������ӵ�wifi
		WifiLocalDataEntity.newInstance(mContext).updateWifi(curConnectWifiName, curConnectWifiPwd);
		//����wifi���ݿ�
		DBManager.INSTANCE.insertWifi(curConnectWifiName, curConnectWifiPwd);
		
		if(iNetWorkView != null)
			iNetWorkView.connectWifiSuccess(curConnectWifiName, curConnectWifiPwd);
	}
	

	@Override
	public void onRssiChanged() 
	{
		// TODO Auto-generated method stub
		
	}

	private volatile boolean isCurWifiConnectedSuccessRepoted = false;
	private volatile boolean isFailWhenSuccess = false;
	@Override
	public void onNetWorkStateChanged(String wifi, boolean info) 
	{
		Log.e("", "!!!!!!!!!!!!!!wifi���ӵĽ��-->" + wifi + "  result-->" + info);
		if(wifi.contains(curConnectWifiName) && !TextUtils.isEmpty(curConnectWifiName))
		{
			if(info)
			{
				if(!isCurWifiConnectedSuccessRepoted)
				{
					isCurWifiConnectedSuccessRepoted = true;
					isFailWhenSuccess = true;
			        doconenctWifiSuccess(); 
				}
			}
			else
			{
				
				mHandler.postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						if(!isFailWhenSuccess)
							connectNextWifi();
					}
				}, 10000);
				
				
			}
		}
	}
	private void showLogStep(String info)
	{
		Log.e("", "@@@@@@@@@@@@@@@@@@@-->" + info);
	}
	private boolean isCurWifiSame(String wifiSSID)
	{
		return curConnectWifiName.equals(wifiSSID) || ("\""+ curConnectWifiName +"\"").equals(wifiSSID);
	}

	@Override
	public void onWifiStateChanged(int wifistate) 
	{
		// TODO Auto-generated method stub
		/*if(wifistate == WifiManager.WIFI_STATE_DISABLED)
        {  
            System.out.println("ϵͳ�ر�wifi");  
        }  
        else if(wifistate == WifiManager.WIFI_STATE_ENABLED)
        {  
            System.out.println("ϵͳ����wifi");  
        }  */
	}
	
}
