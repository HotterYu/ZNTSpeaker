package com.znt.push.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;

public class CopyOfMySharedPreference
{

	private SharedPreferences sp = null;
	private Context context = null;
	private Editor edit = null;
	
	public CopyOfMySharedPreference(Context context, String sharedPreferencesName)
	{
		this.context = context;
		if((sharedPreferencesName == null) || (sharedPreferencesName.length() <= 0))
			sharedPreferencesName = "wifi_speaker";
		sp = context.getSharedPreferences(sharedPreferencesName, context.MODE_PRIVATE);
		edit = sp.edit();
	}
	
	public static CopyOfMySharedPreference newInstance(Context context)
	{
		return new CopyOfMySharedPreference(context, null);
	}
	
	
	public void setData(String key, Object value)
	{
		if(value instanceof String)
			Settings.System.putString(context.getContentResolver(),key, (String)value);
		else if(value instanceof Long)
			Settings.System.putLong(context.getContentResolver(),key, (Long)value);
		else if(value instanceof Integer)
			Settings.System.putInt(context.getContentResolver(),key, (Integer)value);
		
		
		/*if(value instanceof String)
			edit.putString(key, (String)value);
		else if(value instanceof Boolean)
			edit.putBoolean(key, (Boolean)value);
		else if(value instanceof Long)
			edit.putLong(key, (Long)value);
		else if(value instanceof Integer)
			edit.putInt(key, (Integer)value);
		edit.commit();*/
	}
	
	public String getData(String key, String defValue)
	{
		String reValue = defValue;
		try 
		{
			reValue = Settings.System.getString(context.getContentResolver(),key);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		if(reValue == null)
			reValue = defValue;
		return reValue;
		//return sp.getString(key, defValue);
	}
	public boolean getData(String key, boolean defValue)
	{
		return sp.getBoolean(key, defValue);
	}
	public int getData(String key, int defValue)
	{
		int reValue = defValue;
		try 
		{
			reValue = Settings.System.getInt(context.getContentResolver(),key);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reValue;
		//return sp.getInt(key, 0);
	}
	public long getDataLong(String key, long defValue)
	{
		long reValue = defValue;
		try 
		{
			reValue = Settings.System.getLong(context.getContentResolver(),key);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reValue;
		//return sp.getLong(key, defValue);
	}
}
