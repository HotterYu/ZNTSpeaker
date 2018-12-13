package com.znt.speaker.factory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.TextUtils;

import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.diange.mina.entity.CurPlanSubInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.utils.DateUtils;

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
		
		
		if(isOffline)
		{
			tempList = getCurPlanMusicsFromLocal(DBManager.FILE_TYPE_LOCAL);
			if(tempList == null)
				Constant.GET_CUR_MEDIA_FROM_LOCAL_RESULT = "offline:null";
			else
				Constant.GET_CUR_MEDIA_FROM_LOCAL_RESULT = "offline:"+tempList.size();
		}
		else
		{
			tempList = getCurPlanMusicsFromLocal(DBManager.FILE_TYPE_ALL);
			if(tempList == null)
				Constant.GET_CUR_MEDIA_FROM_LOCAL_RESULT = "online:null";
			else
				Constant.GET_CUR_MEDIA_FROM_LOCAL_RESULT = "online:"+tempList.size();
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
 		     	
 		     	/*Date dateDate = new Date(mUIManager.getCurTime());
 		     	String s = DateUtils.dateToStrLong(dateDate);*/
 		     	
 				
 				if(isTimeOverlap(sLong, eLong, curTimeShortLong))
 				{
 					songList = curPlanSubInfor.getSongList();
 					/*String curPlanTime = LocalDataEntity.newInstance(activity).getPlayTime();
 					if(!curPlanTime.equals(startTime))
 					{
 						//songList = getSongListByPlanId(tempId, false);
 						songList = curPlanSubInfor.getSongList();
 						
 						LocalDataEntity.newInstance(activity).setPlanTime(startTime);
 					}
 					else//
 					{
 						songList = null;
 						//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
 						//showToast("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
 					}*/
 					break;
 				}
 				else
 				{
 					//songList = new ArrayList<SongInfor>();
 					//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
 					//showToast("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
 				}
 			}
 			else
 			{
 				//songList = new ArrayList<SongInfor>();
 				//showToast("*&&&*&*&*&*&*&*&-->startTime" + startTime + "  endTime-->"+endTime);
 			}
     		
     	 }
     	 isCheckFromMemRunning = false;
     	 return songList;
	}
	
	private boolean isTimeSerial(String time)
	{
		
		return false;
	}
	
	/**
	 * 浠庢湰鍦拌幏鍙�
	 * @param curTime
	 * @return
	 */
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