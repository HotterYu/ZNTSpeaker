package com.znt.speaker.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.diange.mina.entity.UpdateInfor;

public class JsonParseFactory extends BaseFactory implements IJsonParse
{
	private String RESULT_INFO = "info";
	private String RESULT_OK = "result";
	
	@Override
	public UpdateInfor parseUpdateInfor(String jsonStr)
	{
		// TODO Auto-generated method stub
		
   	    try
        {
   	    	JSONObject jsonObject = new JSONObject(jsonStr);
            int result = jsonObject.getInt(RESULT_OK);
            if(result == 0)
            {
	           	 JSONObject json = jsonObject.getJSONObject(RESULT_INFO);
	           	 UpdateInfor updateInfor = new UpdateInfor();
	           	 String versionName = getInforFromJason(json, "version");
	           	 String versionNum = getInforFromJason(json, "versionNum");
	           	 String apkUrl = getInforFromJason(json, "url");
	           	 String updateType  = getInforFromJason(json, "updateType");
	           	 updateInfor.setApkUrl(apkUrl);
	           	 updateInfor.setUpdateType(updateType);
	           	 updateInfor.setVersionNum(versionNum);
	           	 updateInfor.setVersionName(versionName);
            }
            else
            {
           	 	
            }
        } 
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		return null;
	}

}
