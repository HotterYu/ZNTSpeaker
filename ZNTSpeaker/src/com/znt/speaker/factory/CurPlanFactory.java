package com.znt.speaker.factory;

import android.app.Activity;
import android.text.TextUtils;

import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.diange.mina.entity.CurPlanSubInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class CurPlanFactory 
{
	private Activity activity = null;
	private CurPlanInfor curPlanInfor = null;
	private UIManager mUIManager = null;
	private boolean isCheckFromMemRunning = false;
	private boolean isCheckFromLocalRunning = false;
	public CurPlanFactory(Activity activity, UIManager mUIManager)
	{
		this.activity = activity;
		this.mUIManager = mUIManager;
	}
	
	public void setCurPlanInfor(CurPlanInfor curPlanInfor)
	{
		this.curPlanInfor = curPlanInfor;
	}
	
	public List<SongInfor> getCurPlanMusics(boolean isOffline) 
	{
		
		List<SongInfor> tempList = new ArrayList<SongInfor>();
		
		
		if(isOffline)//掉线的，只获取本地的列表
		{
			tempList = getCurPlanMusicsFromMem();
			if(tempList == null || tempList.size() == 0)
				tempList = getCurPlanMusicsFromLocal(DBManager.FILE_TYPE_LOCAL);
		}
		else//在线的,获取全部的播放列表
		{
			tempList = getCurPlanMusicsFromMem();
			if(tempList == null || tempList.size() == 0)
				tempList = getCurPlanMusicsFromLocal(DBManager.FILE_TYPE_ALL);
		}
		return tempList;
	}
	
	/**
	 *
	 * @return
	 */
	private List<SongInfor> getCurPlanMusicsFromMem()
	{
		if(mUIManager.getCurTime() <= 0)
			return null;
		if(curPlanInfor == null || curPlanInfor.isPlanNone())//
			return null;
		if(isCheckFromMemRunning)
			return null;
		isCheckFromMemRunning = true;
		
		 List<SongInfor> songList = new ArrayList<SongInfor>();
     	 List<CurPlanSubInfor> subPlanList = curPlanInfor.getSubPlanList();
     	 for(int i=0;i<subPlanList.size();i++)
     	 {
     		CurPlanSubInfor curPlanSubInfor = subPlanList.get(i);
     		//String tempId = curPlanSubInfor.getPlanId();
 			String startTime = curPlanSubInfor.getStartTime();
 			String endTime = curPlanSubInfor.getEndTime();
 			
 			if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
 			{
 				long sLong = DateUtils.timeToInt(startTime, ":");
 				long eLong = DateUtils.timeToInt(endTime, ":");
 				
 		     	String curTimeShort = DateUtils.getEndDateFromLong(mUIManager.getCurTime());
 		     	long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
 		     	
 				if(isTimeOverlap(sLong, eLong, curTimeShortLong))
 				{
 					songList = curPlanSubInfor.getSongList();

 					break;
 				}
 				else
 				{
 				}
 			}
 			else
 			{

 			}
     		
     	 }
     	 isCheckFromMemRunning = false;
     	 return songList;
	}
	
	private boolean isTimeSerial(String time)
	{
		
		return false;
	}
	

	private List<SongInfor> getCurPlanMusicsFromLocal(int fileTye)
	{
		if(isCheckFromLocalRunning)
			return null;
		
		if(mUIManager.getCurTime() <= 0 )
		{
			long localTime = DBManager.INSTANCE.getFirstPlanTime();
			//long localTime = LocalDataEntity.newInstance(activity).getLastServerTime();
			if(localTime > 0)
			{
				LocalDataEntity.newInstance(activity).setPlanTime("");
				mUIManager.setCurTime((localTime + 1 * 60 * 1000) + "");
			}
		}
		
		isCheckFromLocalRunning = true;
		List<SongInfor> songList = DBManager.INSTANCE.getCurPlanMusics(mUIManager.getCurTime(), fileTye);
		isCheckFromLocalRunning = false;
		return songList;
	}
	
	
	private boolean isTimeOverlap(long start, long end, long dest)
	 {
		if(start > end)
		{
			end = end + 24 * 60 * 60;
			if(dest > start)
			{
				if(dest < end)
					return true;
			}
			else
			{
				dest = dest + 24 * 60 * 60;
				if(dest < end)
					return true;
			}
		}
		else
		{
			if(dest > start && dest < end)
				return true;
		}
		
		return false;
	}
	
}
