package com.znt.speaker.http.callback;

import android.content.Context;
import android.text.TextUtils;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.mina.entity.CurPlanInfor;
import com.znt.diange.mina.entity.CurPlanSubInfor;
import com.znt.push.email.EmailSenderManager;
import com.znt.speaker.db.DBManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

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
					String endDate = getInforFromJason(json, "endDate");
					String id = getInforFromJason(json, "id");
					String planName = getInforFromJason(json, "planName");
					String startDate = getInforFromJason(json, "startDate");

					if(!TextUtils.isEmpty(startDate))
					{
						curPlanInfor.setStartDate(startDate);
					}
					if(!TextUtils.isEmpty(endDate))
					{
						curPlanInfor.setEndDate(endDate);
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
						String endTime = getInforFromJason(json1, "endTime");
						String id1 = getInforFromJason(json1, "id");
						String startTime = getInforFromJason(json1, "startTime");
						
						if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
						{
							curSubPlanInfor.setStartTime(startTime);
						}
						if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
						{
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

				e.printStackTrace();
			}
    	}
        
        //List<PlanInfor> devices = new Gson().fromJson(string, List.class);
        return curPlanInfor;
    }
}