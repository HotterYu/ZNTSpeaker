
package com.znt.push.update; 

import java.io.File;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/** 
 * @ClassName: ApkInstallHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-11-5 涓婂�?10:10:51  
 */
public class ApkInstallHelper
{ 
	
	private Activity activity = null;
	
	private static ApkInstallHelper INSTANCE = null;
	
	public ApkInstallHelper(Activity activity)
	{
		this.activity = activity;
	}
	
	public static ApkInstallHelper getInstance(Activity activity)
	{
		if(INSTANCE == null)
			INSTANCE = new ApkInstallHelper(activity);
		return INSTANCE;
	}
	
    public boolean install(String apkPath)
    {
        // 鍏堝垽鏂墜鏈烘槸鍚︽湁root鏉冮�?
        if(hasRootPerssion())
        {
            // 鏈塺oot鏉冮檺锛屽埄鐢ㄩ潤榛樺畨瑁呭疄鐜�?
            return clientInstall(apkPath);
        }
        else
        {
            // 娌℃湁root鏉冮檺锛屽埄鐢ㄦ剰鍥捐繘琛屽畨瑁�?
            File file = new File(apkPath);
            if(!file.exists())
                return false; 
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
            activity.startActivity(intent);
            return true;
        }
    }
    
    public boolean uninstall(String packageName)
    {
        if(hasRootPerssion())
        {
            // 鏈塺oot鏉冮檺锛屽埄鐢ㄩ潤榛樺嵏杞藉疄鐜�?
            return clientUninstall(packageName);
        }
        else
        {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(uninstallIntent);
            return true;
        }
    }
     
    /**
     * 鍒ゆ柇鎵嬫満鏄惁鏈塺oot鏉冮�?
     */
    private boolean hasRootPerssion()
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 闈欓粯�?�夎�?
     */
    private boolean clientInstall(String apkPath)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 闈欓粯鍗歌浇
     */
    private boolean clientUninstall(String packageName)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 鍚姩app
     * com.exmaple.client/.MainActivity
     * com.exmaple.client/com.exmaple.client.MainActivity
     */
    public boolean startApp(String packageName,String activityName)
    {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " \n";
        Process process = null;
        try 
        {
           process = Runtime.getRuntime().exec(cmd);
           int value = process.waitFor();  
           return returnResult(value);
        } 
        catch (Exception e) 
        {
          e.printStackTrace();
        } 
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return isSuccess;
    }
     
     
    private boolean returnResult(int value)
    {
        // 浠ｈ〃鎴愬姛  
        if (value == 0) 
        {
            return true;
        } 
        else if (value == 1) 
        { 
        	// 澶辫�?
            return false;
        } 
        else 
        { 
        	// 鏈煡鎯呭喌
            return false;
        }  
    }
}