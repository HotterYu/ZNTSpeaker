package com.znt.speaker.http.callback;

import java.io.IOException;

import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.email.EmailSender;
import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.diange.mina.entity.CurPlanSubInfor;
import com.znt.push.email.EmailSenderManager;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.LocalDataEntity;

public abstract class CurPlanCallBack extends Callback<CurPlanInfor>
{
	private Context context = null;
	private EmailSenderManager emailManager = null;
	public CurPlanCallBack(Context context)
	{
		this.context = context;
		emailManager = new  EmailSenderManager();
	}
	
    @Override
    public CurPlanInfor parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	CurPlanInfor curPlanInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		
			try
			{
				JSONObject jsonObject = new JSONObject(string);
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = jsonObject.getString(RESULT_INFO);
					if(TextUtils.isEmpty(info))
					{
						return null;
					}
					curPlanInfor = new CurPlanInfor();
					DBManager.INSTANCE.deleteAllPlan();
					
					JSONObject json = new JSONObject(info);
					/*String total = getInforFromJason(json, "total");
					if(!TextUtils.isEmpty(total))
						httpResult.setTotal(Integer.parseInt(total));*/
					//String cycleList = getInforFromJason(json, "cycleList");
					String endDate = getInforFromJason(json, "endDate");
					String id = getInforFromJason(json, "id");
					//String memberId = getInforFromJason(json, "memberId");
					//String planFlag = getInforFromJason(json, "planFlag");
					String planName = getInforFromJason(json, "planName");
					//String planType = getInforFromJason(json, "planType");
					//String publishTime = getInforFromJason(json, "publishTime");
					String startDate = getInforFromJason(json, "startDate");
					//String status = getInforFromJason(json, "status");
					//String terminalList = getInforFromJason(json, "terminalList");
					//String tlist = getInforFromJason(json, "tlist");
					
					if(!TextUtils.isEmpty(startDate))
					{
						//long dateLong = Long.parseLong(startDate);
						curPlanInfor.setStartDate(startDate);
						//planInfor.setStartDate(DateUtils.getStringTimeHead(dateLong));
					}
					if(!TextUtils.isEmpty(endDate))
					{
						//long dateLong = Long.parseLong(endDate);
						curPlanInfor.setEndDate(endDate);
						//planInfor.setEndDate(DateUtils.getStringTimeHead(dateLong));
					}
					curPlanInfor.setPlanName(planName);
					curPlanInfor.setPlanId(id);
					
					
					String pslist = getInforFromJason(json, "pslist");
					JSONArray jsonArray = new JSONArray(pslist);
					int len = jsonArray.length();
					for(int i=0;i<len;i++)
					{
						CurPlanSubInfor curSubPlanInfor = new CurPlanSubInfor();
						
						JSONObject json1 = (JSONObject) jsonArray.get(i);
						//String cycleType = getInforFromJason(json1, "cycleType");
						String endTime = getInforFromJason(json1, "endTime");
						String id1 = getInforFromJason(json1, "id");
						//String musicCategoryList = getInforFromJason(json1, "musicCategoryList");
						//String publishId = getInforFromJason(json1, "publishId");
						String startTime = getInforFromJason(json1, "startTime");
						
						if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
						{
							//int tempS = DateUtils.timeToInt(startTime, ":");
							curSubPlanInfor.setStartTime(startTime);
						}
						if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
						{
							//int tempE = DateUtils.timeToInt(endTime, ":");
							curSubPlanInfor.setEndTime(endTime);
						}
						curSubPlanInfor.setPlanId(id1);
						curPlanInfor.addSubPlanInfor(curSubPlanInfor);
						DBManager.INSTANCE.addCurPlanSub(curSubPlanInfor);
					}
				}
			} 
			catch (Exception e)
			{
				/*httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				String devId = LocalDataEntity.newInstance(context).getDeviceCode();
				if(e != null)
					emailManager.sendEmail("获取计划异常_id" + devId , e.getMessage());
				else
					emailManager.sendEmail("获取计划异常_id" + devId , "获取播放计划失败");*/
				e.printStackTrace();
			}
    	}
        
        //List<PlanInfor> devices = new Gson().fromJson(string, List.class);
        return curPlanInfor;
    }
}