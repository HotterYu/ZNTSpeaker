package com.znt.speaker.db;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Environment;

import com.znt.speaker.entity.Constant;
import com.znt.utils.FileUtils;

public class SystemData 
{

	private final static String filePath = Environment.getExternalStorageDirectory() + Constant.WORK_DIR + "/data/system.text";
	
	private static void setSystemData(Activity activity, String appId, String dbVersion)
	{
		JSONObject json = new JSONObject();
		try 
		{
			json.put("SPEAKER_PID", appId);
			json.put("DB_VERSION", dbVersion);
			String content = json.toString();
			
			File file = new File(filePath);
			if(file.exists())
				file.delete();
			FileUtils.writeDataToFile(file.getAbsolutePath(), content);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*public static void checkDbversion(Activity activity)
	{
		File file = new File(filePath);
		if(file.exists())
		{
			FileInputStream fis = FileUtils.readDataFromFile(file.getAbsolutePath());
			byte[] buffer = new byte[(int) file.length()];
			try
			{
				fis.read(buffer);
				String content = new String(buffer);
				if(fis != null)
					fis.close();
				JSONObject json = new JSONObject(content);
				
				String dbVersion = json.getString("DB_VERSION");
				if(!TextUtils.isEmpty(dbVersion))
				{
					int dbV = Integer.parseInt(dbVersion);
					if(dbV > 0 && dbV < Constant.DB_VERSION)
					{
						//鏈湴淇濆瓨鐨勬暟鎹簱鐗堟湰姣斿綋鍓嶇殑浣庯紝鍒犻櫎鏈湴鏁版嵁搴�
						DBManager.newInstance(activity).deleteDbFile();
						//DBManager.newInstance(activity).openDatabase();
					}
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		setSystemData(activity, android.os.Process.myPid() + "", Constant.DB_VERSION + "");
	}*/
	
}
