package com.znt.wifimodel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.Toast;

import com.znt.diange.mina.entity.WifiInfor;

import java.util.ArrayList;
import java.util.List;


public class DBManager extends MyDbHelper
{

	public static DBManager INSTANCE = null;
	private Context context = null;
	
	private volatile boolean isGetAllPlanMusicRunning = false;
	

	public DBManager(Context c)
	{
		super(c);
		this.context = c.getApplicationContext();
		// TODO Auto-generated constructor stub
	}

	public static void init(Context c)
	{
		if(INSTANCE == null)
		{
			synchronized (DBManager.class)
			{
				if(INSTANCE == null)
					INSTANCE = new DBManager(c);	
			}
		}
	}



     public synchronized long insertWifi(String wifiName, String wifiPwd)
     {
     	if(TextUtils.isEmpty(wifiName))
     		return -1;
     	
     	if(isWifiExist(wifiName, wifiPwd ))
     	{
     		//updateWifi(wifiName, wifiPwd);
     		return -1;
     	}
     	
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(wifiName))
     		values.put("wifi_name", wifiName);
     	values.put("wifi_pwd", wifiPwd);
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_WIFI);
     }
     
     public List<WifiInfor> getWifiList()
     {
    	 List<WifiInfor> wifiList = new ArrayList<WifiInfor>();
    	 
    	 Cursor cur = query(TBL_WIFI);
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			String wifi_pwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
      			WifiInfor tempInfor = new WifiInfor();
      			tempInfor.setWifiName(wifi_name);
      			tempInfor.setWifiPwd(wifi_pwd);
      			wifiList.add(tempInfor);
      		}
      	 }
      	if(cur != null )
     		cur.close();
    	 
    	 return wifiList;
     }
     
     public String getWifiPwdByName(String wifiName)
     {
    	 String wifiPwd = "";
      	 Cursor cur = queryNormal(TBL_WIFI);
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()))
      			{
      				wifiPwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
      				break;
      			}
      		}
      	 }
      	if(cur != null )
     		cur.close();
      	 return wifiPwd;
     }
     public int getWifiCount()
     {
    	 Cursor cur = query(TBL_WIFI);
    	 return cur.getCount();
     }
     
     public synchronized boolean isWifiExist(String wifiName, String wifiPwd)
     {
     	boolean  result = false;
     	Cursor cur = query(TBL_WIFI);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
     			String wifi_pwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
     			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()) && wifi_pwd != null && wifi_pwd.trim().equals(wifiPwd.trim()))
     			{
     				result = true;
     				//updateMusic(music);
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return result;
     }
     
     public synchronized int updateWifi(String wifiName, String wifiPwd)
     {
    	 try 
    	 {
			
    		 ContentValues values = new ContentValues();
    	     	
    	     	
    		 if(!TextUtils.isEmpty(wifiName))
    			 values.put("wifi_name", wifiName);
    		 values.put("wifi_pwd", wifiPwd);
    		 values.put("modify_time", System.currentTimeMillis());
    		 return editWifi(TBL_WIFI, wifiName, values);
    		 
		} 
    	 catch (Exception e) 
    	 {
			// TODO: handle exception
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
     	return -1;
     }
     
     
     

     
}
 
