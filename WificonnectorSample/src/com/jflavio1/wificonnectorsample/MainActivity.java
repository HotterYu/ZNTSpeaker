/*
 * Created by Jose Flavio on 10/18/17 1:20 PM.
 * Copyright (c) 2017 JoseFlavio.
 * All rights reserved.
 */

package com.jflavio1.wificonnectorsample;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jflavio1.wificonnector.WifiConnector;
import com.jflavio1.wificonnector.interfaces.ConnectionResultListener;
import com.jflavio1.wificonnector.interfaces.RemoveWifiListener;
import com.jflavio1.wificonnector.interfaces.ShowWifiListener;
import com.jflavio1.wificonnector.interfaces.WifiConnectorModel;
import com.jflavio1.wificonnector.interfaces.WifiStateListener;
import com.znt.wifimoidel.netset.WifiAdmin;

public class MainActivity extends Activity implements WifiConnectorModel 
{

    private Button mWifiActiveTxtView;

    private ListView listView = null;
    
    private WifiListAdapter adapter;
    private WifiConnector wifiConnector;
    
    private List<ScanResult> mWifiScanResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_wifi_list);
        mWifiActiveTxtView = (Button) findViewById(R.id.wifiActivationTv);

        adapter = new WifiListAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        //setLocationPermission();
        createWifiConnectorObject();
        scanForWifiNetworks();
        mWifiActiveTxtView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				scanForWifiNetworks();
				/*ScanResult sr = new ScanResult();
				String pwd = "1234567890";
            	String name = "DianYinGuanJia";
            	sr.SSID = name;
    			if(TextUtils.isEmpty(pwd))
    				sr.capabilities = "[ESS]";
    			else
    				sr.capabilities = "[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS]";
				WifiAdmin mWifiAdmin = new WifiAdmin(getApplicationContext()) {
					
					@Override
					public void onNotifyWifiConnected() {
						// TODO Auto-generated method stub
						Log.e("", "onNotifyWifiConnected");
					}
					
					@Override
					public void onNotifyWifiConnectFailed() {
						// TODO Auto-generated method stub
						Log.e("", "onNotifyWifiConnectFailed");
					}
					
					@Override
					public void myUnregisterReceiver(BroadcastReceiver receiver) {
						// TODO Auto-generated method stub
						Log.e("", "myUnregisterReceiver");
					}
					
					@Override
					public Intent myRegisterReceiver(BroadcastReceiver receiver,
							IntentFilter filter) {
						// TODO Auto-generated method stub
						Log.e("", "myRegisterReceiver");
						return null;
					}
				};
				
				try 
				{
					mWifiAdmin.addNetwork(name, pwd);
	                connectToWifiAccessPoint(sr, pwd);
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					Log.e("", e.getMessage());
				}*/
			}
		});
        
        listView.setOnItemClickListener(new OnItemClickListener() 
        {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// TODO Auto-generated method stub
				
				
				if (wifiConnector.isConnectedToBSSID(mWifiScanResult.get(position).BSSID)) 
                {
                    Toast.makeText(getApplicationContext(), "Already connected!", Toast.LENGTH_SHORT).show();
                } 
                else 
                {
                	openConnectDialog(mWifiScanResult.get(position));
                }
				
				
			}
		});
        listView.setOnItemLongClickListener(new OnItemLongClickListener() 
        {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				// TODO Auto-generated method stub
				
				disconnectFromAccessPoint(mWifiScanResult.get(position));
				
				return false;
			}
		});

        
    }

    @Override
    protected void onDestroy() 
    {
        destroyWifiConnectorListeners();
        super.onDestroy();
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

    public void openConnectDialog(ScanResult scanResult)
    {
        ConnectToWifiDialog dialog = new ConnectToWifiDialog(MainActivity.this, scanResult);
        dialog.setConnectButtonListener(new ConnectToWifiDialog.DialogListener()
        {
            @Override
            public void onConnectClicked(ScanResult scanResult, String password)
            {
            	//connectToWifiAccessPoint(scanResult, password);
                ScanResult sr = new ScanResult();
            	String pwd = "12345678";
            	String name = "DianYinGuanJia";
            	sr.SSID = name;
    			if(TextUtils.isEmpty(pwd))
    				sr.capabilities = "[ESS]";
    			else
    				sr.capabilities = "[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS]";
            	
            	/*String bssid = scanResult.BSSID;
            	String ssid = scanResult.SSID;
            	String cap = scanResult.capabilities;
            	String other = scanResult.toString();*/
    			WifiAdmin mWifiAdmin = new WifiAdmin(getApplicationContext()) {
					
					@Override
					public void onNotifyWifiConnected() {
						// TODO Auto-generated method stub
						Log.e("", "onNotifyWifiConnected");
					}
					
					@Override
					public void onNotifyWifiConnectFailed() {
						// TODO Auto-generated method stub
						Log.e("", "onNotifyWifiConnectFailed");
					}
					
					@Override
					public void myUnregisterReceiver(BroadcastReceiver receiver) {
						// TODO Auto-generated method stub
						Log.e("", "myUnregisterReceiver");
					}
					
					@Override
					public Intent myRegisterReceiver(BroadcastReceiver receiver,
							IntentFilter filter) {
						// TODO Auto-generated method stub
						Log.e("", "myRegisterReceiver");
						return null;
					}
				};
				
				try 
				{
					mWifiAdmin.addNetwork(name, pwd);
	                connectToWifiAccessPoint(sr, pwd);
				} 
				catch (Exception e) 
				{
					// TODO: handle exception
					Log.e("", e.getMessage());
				}
            }
        });
        dialog.show();
    }

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
                Toast.makeText(MainActivity.this, "You are connected to " + scanResult.SSID + "!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void errorConnect(int codeReason) 
            {
                Toast.makeText(MainActivity.this, "Error on connecting to wifi: " + scanResult.SSID +"\nError code: "+ codeReason,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStateChange(SupplicantState supplicantState) 
            {
            	Log.e("", "wifi onStateChange-->"+supplicantState.toString());
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
                Toast.makeText(MainActivity.this, "You have removed this wifi!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWifiNetworkRemoveError() 
            {
                Toast.makeText(MainActivity.this, "Error on removing this network!", Toast.LENGTH_SHORT).show();
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
                adapter.setScanResultList(wifiScanResult);
            }

            @Override
            public void onNetworksFound(JSONArray wifiList) 
            {
            	Log.e("", "wifiList-->"+wifiList);
            }

            @Override
            public void errorSearchingNetworks(int errorCode) 
            {
                Toast.makeText(MainActivity.this, "Error on getting wifi list, error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
	}

    // region permission
   /* private Boolean permisionLocationOn() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    private Boolean checkLocationTurnOn() {
        boolean onLocation = true;
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionGranted) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                onLocation = false;
                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog));
                //android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
                dialog.setMessage("Please turn on your location");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        }
        return onLocation;
    }
*/
    // endregion
}
