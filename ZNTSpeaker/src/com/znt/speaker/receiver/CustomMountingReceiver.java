package com.znt.speaker.receiver;

import com.znt.speaker.entity.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomMountingReceiver extends BroadcastReceiver 
{
	
	private IMediaChangeListener receiver = null;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		String action = arg1.getAction();
		if(action.equals(Constant.MEDIA_ADD))//鎻掑叆
		{
			String path = arg1.getDataString();
			if(receiver != null)
			{
				receiver.onMediaChange(true, path);
			}
			/*if(getAndroidOSVersion() >= 19)
				Toast.makeText(arg0, "褰撳墠绯荤粺鐗堟湰鍙兘鏃犳硶璁块棶U鐩樺唴瀹�", 0).show();
			else
				Toast.makeText(arg0, "瀛樺偍璁惧鎻掑叆", 0).show();*/
		}
		if(action.equals(Constant.MEDIA_REMOVE))//绉婚櫎
		{
			if(receiver != null)
			{
				receiver.onMediaChange(false, "");
			}
			//Toast.makeText(arg0, "瀛樺偍璁惧绉婚櫎", 0).show();
		}
	}
	
	public static int getAndroidOSVersion()  
    {  
         int osVersion;  
         try  
         {  
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);  
         }  
         catch (Exception e)  
         {  
            osVersion = 0;  
         }  
           
         return osVersion;  
   }  
	
	public void setReceiverListener(IMediaChangeListener receiver)
	{
		this.receiver = receiver;
	}
	
	public interface IMediaChangeListener
	{
		public void onMediaChange(boolean isAdd, String path);
	}
}
