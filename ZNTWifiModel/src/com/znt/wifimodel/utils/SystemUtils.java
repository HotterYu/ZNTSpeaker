
package com.znt.wifimodel.utils; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/** 
 * @ClassName: MyStorageUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午1:41:42  
 */
public class SystemUtils
{
	
	  
	/**
	* @Description: 获取屏幕大小
	* @param @param activity
	* @param @return   
	* @return int[] 
	* @throws
	 */
	public static int[] getScreenSize(Activity activity)
	{
		int[] size = new int[2];
		
		/*int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
		int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
		size[0] = screenWidth;
		size[1] = screenHeight;*/
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		size[0] = outMetrics.widthPixels;
		size[1] = outMetrics.heightPixels;
		
		return size;
	}
	public static int[] getScreenSize(Context activity)
	{
		int[] size = new int[2];
		
		int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
		int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
		size[0] = screenWidth;
		size[1] = screenHeight;
		return size;
	}
	
	public static String getScreenOritation(Context activity)
	{
		Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信�?
		int ori = mConfiguration.orientation ; //获取屏幕方向

		if(ori == mConfiguration.ORIENTATION_LANDSCAPE)
		{

			//横屏
			return "横屏";
		}
		else if(ori == mConfiguration.ORIENTATION_PORTRAIT)
		{

			//竖屏
			return "竖屏";
		}
		return "未知";
	}
	
	/**
	 * 进行截取屏幕  
	 * @param pActivity
	 * @return bitmap
	 */
	public static Bitmap takeScreenShot(Activity activity)
	{
		Bitmap bitmap=null;
		View view = activity.getWindow().getDecorView();
		// 设置是否可以进行绘图缓存
		view.setDrawingCacheEnabled(true);
		// 如果绘图缓存无法，强制构建绘图缓�?
		view.buildDrawingCache();
	    // 返回这个缓存视图 
		bitmap = view.getDrawingCache();
		
		// 获取状�?�栏高度
		Rect frame = new Rect();
		// 测量屏幕宽和�?
		view.getWindowVisibleDisplayFrame(frame);
		int stautsHeight = frame.top;//状�?�栏的高�?
		
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		// 根据坐标点和�?要的宽和高创建bitmap
		bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height-stautsHeight);
		return bitmap;
	}
	
	/**
	* @Description: 获取机身存储可以空间
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long getAvailableInternalMemorySize()
	{
	    File path = Environment.getDataDirectory();  //获取数据目录
	    StatFs stat = new StatFs(path.getPath());
	    long blockSize = stat.getBlockSize();
	    long availableBlocks = stat.getAvailableBlocks();
	    return availableBlocks*blockSize;
	}
	 
	/**
	* @Description: 获取机身总大�?
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long getTotalInternalMemorySize()
	{
    	File path = Environment.getDataDirectory();
    	StatFs stat = new StatFs(path.getPath());
    	long blockSize = stat.getBlockSize();
    	long totalBlocks = stat.getBlockCount();
    	return totalBlocks*blockSize;
	}
	   
	/**
	* @Description: �?测是否有外部存储设备
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean externalMemoryAvailable()
	{
    	return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	 
	/**
	* @Description: 获取外部存储设备可用空间
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long getAvailableExternalMemorySize()
	{
    	if(externalMemoryAvailable())
    	{
    		File path = Environment.getExternalStorageDirectory();
    		StatFs stat = new StatFs(path.getPath());
    		@SuppressWarnings("deprecation")
			long blockSize = stat.getBlockSize();
    		@SuppressWarnings("deprecation")
			long availableBlocks = stat.getAvailableBlocks();
    		return availableBlocks*blockSize;
    	}
    	else
    	{
    		return getAvailableInternalMemorySize();
    	}
	}
	    
	/**
	* @Description: 获取外部存储设备总空�?
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long getTotalExternalMemorySize()
	{
    	if(externalMemoryAvailable())
    	{
    		File path = Environment.getExternalStorageDirectory();
    		StatFs stat = new StatFs(path.getPath());
    		long blockSize = stat.getBlockSize();
    		long totalBlocks = stat.getBlockCount();
    		return totalBlocks*blockSize;
    	}
    	else
    	{
    		return getTotalInternalMemorySize();
    	}
	}
	
	public void getInforFromSD(String filePath)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {    
        	StatFs stat = new StatFs(filePath); 
        	long blockSize = stat.getBlockSize();
        	float totalBlocks = stat.getBlockCount();  
        	int sizeInMb = (int) (blockSize * totalBlocks) / 1024 / 1024; 
        	long availableBlocks = stat.getAvailableBlocks(); 
        	float percent = availableBlocks / totalBlocks; 
        	percent = (int) (percent * 1000); 
        }  
    }
	
	/**
	* @Description: 获取存储设备可用空间
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getAvailabeMemorySize()
	{
		return StringUtils.getFormatSize(getAvailableExternalMemorySize());
	}
	/**
	* @Description: 获取存储设备总容�?
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getTotalMemorySize()
	{
		return StringUtils.getFormatSize(getTotalExternalMemorySize());
	}
	
	/**
	* @Description: 获取�?有的存储设备列表
	* @param @return   
	* @return ArrayList<String> 
	* @throws
	 */
	public static ArrayList<String> getStorageDirectoriesArrayList()
    {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try 
        {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            list.add(Environment.getExternalStorageDirectory().getPath());
            String line;
            while((line = bufReader.readLine()) != null) 
            {
                if(line.contains("vfat") || line.contains("exfat") ||
                   line.contains("/mnt") || line.contains("/Removable")) 
                {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (list.contains(s))
                        continue;

                    if (line.contains("/dev/block/vold")) 
                    {
                        if (!line.startsWith("tmpfs") &&
                            !line.startsWith("/dev/mapper") &&
                            !s.startsWith("/mnt/secure") &&
                            !s.startsWith("/mnt/shell") &&
                            !s.startsWith("/mnt/asec") &&
                            !s.startsWith("/mnt/obb")
                            ) 
                        {
                            list.add(s);
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally 
        {
            if (bufReader != null) 
            {
                try 
                {
                    bufReader.close();
                }
                catch (IOException e) {}
            }
        }
        return list;
    }
	
	/**
	* @Description: 判断当前目录是否可用
	* @param @param file
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isStorageAvailable(File file)
	{
		if(getSDspace(file)[1] > 0)
			return true;
		return false;
	}
	
	/**
	* @Description: 获取本地缓存目录
	* @param @param uniqueName
	* @param @return   
	* @return File 
	* @throws
	 */
	public static File getAvailableDir(Context context, String uniqueName) 
	{
		/*获取外部存储设备列表*/
		List<String> sdList = getStorageDirectoriesArrayList();
		
		/*选择第一个有效的存储设备作为本地缓存*/
		for(int i=0;i<sdList.size();i++)
		{
			File file = new File(sdList.get(i));
			if(isStorageAvailable(file) && file.canWrite())
			{
				if(!TextUtils.isEmpty(uniqueName))
					return new File(sdList.get(i) + File.separator + uniqueName);
				else
					return new File(sdList.get(i) + File.separator);
			}
		}
		/*如果没有外设就使用内部存�?*/
		return context.getCacheDir();
	}
	
	/**
	* @Description: 获取本地存储设备存储空间
	* @param @param file
	* @param @return   
	* @return long[] 
	* @throws
	 */
	public static long[] getSDspace(File file)
	{
		StatFs statfs = new StatFs(file.getAbsolutePath());
		
		long[] result = new long[3];
		
		long blocSize = statfs.getBlockSize(); 
		//获取BLOCK数量 
		long totalBlocks = statfs.getBlockCount(); 
		//己使用的Block的数�? 
		long availaBlock = statfs.getAvailableBlocks(); 
		
		String total = StringUtils.getFormatSize(totalBlocks*blocSize); 
		String availale = StringUtils.getFormatSize(availaBlock*blocSize); 
		
		result[0] = blocSize;
		result[1] = totalBlocks;
		result[2] = availaBlock;
		
		return result;
	}
	
	
	/**
	 * 获取当前android系统的sdk版本�?
	 * @return
	 */
	public static int getAndroidSDKVersion() 
	{
		int version = 0;
		version = Integer.valueOf(android.os.Build.VERSION.SDK);
		return version;
	}
	
	/**
	* @Description: 获取版本信息
	* @param @param activity
	* @param @return
	* @param @throws Exception   
	* @return String 
	* @throws
	 */
	public static PackageInfo getPkgInfo(Activity activity) throws Exception
    {
       // 获取packagemanager的实�?
       PackageManager packageManager = activity.getPackageManager();
       // getPackageName()是你当前类的包名�?0代表是获取版本信�?
       PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(),0);
       return packInfo;
    }
	public static PackageInfo getPkgInfo(Context activity) throws Exception
	{
		// 获取packagemanager的实�?
		PackageManager packageManager = activity.getPackageManager();
		// getPackageName()是你当前类的包名�?0代表是获取版本信�?
		PackageInfo packInfo = packageManager.getPackageInfo(activity.getPackageName(),0);
		return packInfo;
	}
	
	public static PackageInfo getApkFileInfor(Activity activity, String filePath)
	{
		PackageManager packageManager = activity.getPackageManager();  
		PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES); 
		return packageInfo;
	}
	
	
	/**
	* @Description: 获取设备ip地址
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getIP() 
	{
	    String IP = null;
	    StringBuilder IPStringBuilder = new StringBuilder();
	    try 
	    {
	      Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
	      while (networkInterfaceEnumeration.hasMoreElements()) 
	      {
	    	  NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
	    	  Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
	    	  while (inetAddressEnumeration.hasMoreElements())
	    	  {
	    		  InetAddress inetAddress = inetAddressEnumeration.nextElement();
	    		  if (!inetAddress.isLoopbackAddress()&& 
	    				  !inetAddress.isLinkLocalAddress()&& 
	    				  inetAddress.isSiteLocalAddress()) 
	    		  {
	    			  IPStringBuilder.append(inetAddress.getHostAddress().toString()+"\n");
	    		  }
	    	  }
	      }
	    } 
	    catch (SocketException ex)
	    {

	    }
	    IP = IPStringBuilder.toString();
	    if(IP.contains("\n"))
	    	IP = IP.replace("\n", "");
	    return IP;
	}
	
	/**
	* @Description: 获取当前连接的WIFI的ssid
	* @param @param activity
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getConnectWifiSsid(Activity activity)
	{
		if(activity == null)
			return "";
		WifiManager wifiManager = (WifiManager)activity.getSystemService(activity.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		if(ssid == null)
			return "";
		if(ssid.contains("\""))
		{
			ssid = ssid.replace("\"", "");
		}
		return ssid;
	}
	public static String getConnectWifiSsid(Context activity)
	{
		if(activity == null)
			return "";
		WifiManager wifiManager = (WifiManager)activity.getSystemService(activity.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		if(ssid == null)
			return "";
		if(ssid.contains("\""))
		{
			ssid = ssid.replace("\"", "");
		}
		return ssid;
	}
	public static String getWifiBSsid(Activity activity)
	{
		if(activity == null)
			return "";
		WifiManager wifiManager = (WifiManager)activity.getSystemService(activity.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String bssid = wifiInfo.getBSSID();
		if(bssid == null)
			bssid = "";
		else if(bssid.contains(":"))
			bssid = bssid.replace(":", "");
		return bssid;
	}
	public static String getWifiBSsid(Context activity)
	{
		if(activity == null)
			return "";
		WifiManager wifiManager = (WifiManager)activity.getSystemService(activity.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String bssid = wifiInfo.getBSSID();
		if(bssid == null)
			bssid = "";
		else if(bssid.contains(":"))
			bssid = bssid.replace(":", "");
		return bssid;
	}
	
	/**
	* @Description: 获取全部应用列表
	* 
	* luncher 添加:  
	* <category android:name="android.intent.category.HOME" />
 	  <category android:name="android.intent.category.DEFAULT" />
	* 
	* @param @param context
	* @param @return   
	* @return List<ResolveInfo> 
	* @throws
	 */
	public static List<ResolveInfo> getAllApps(Context context)
	{
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        return context.getPackageManager().queryIntentActivities(mainIntent, 0);
	}
	
	/**
	* @Description: 打开应用程序
	* @param @param context
	* @param @param info   
	* @return void 
	* @throws
	 */
	public static void openApp(Context context, ResolveInfo info)
	{
        
        //该应用的包名
        String pkg = info.activityInfo.packageName;
        //应用的主activity�?
        String cls = info.activityInfo.name;
        
        ComponentName componet = new ComponentName(pkg, cls);
        
        Intent i = new Intent();
        i.setComponent(componet);
        context.startActivity(i);
	}
	
	/** 
     * �?测网络是否连�? 
     *  
     * @return 
     */ 
    public static boolean isNetConnected(Context context) 
    {  
    	if(context == null)
    		return false;
    	
    	if(TextUtils.isEmpty(getWifiName(context)))
    		return false;
    	
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (cm != null) 
        {  
            NetworkInfo[] infos = cm.getAllNetworkInfo();  
            if (infos != null) 
            {  
                for (NetworkInfo ni : infos) 
                {  
                    if (ni.isConnected())
                    {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;  
    }   
   
    public static String getWifiName(Context context)
    {
    	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }
   
    /** 
     * �?�?3G是否连接 
     *  
     * @return 
     */ 
    private boolean is3gConnected(Context context) 
    {  
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (cm != null) 
        {  
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();  
            if (networkInfo != null 
                    && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) 
            {  
                return true;  
            }  
        }  
        return false;  
    }  
   
    /** 
     * �?测GPS是否打开 
     *  
     * @return 
     */ 
    private boolean isGpsEnabled(Context context) 
    {  
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        List<String> accessibleProviders = lm.getProviders(true);  
        for (String name : accessibleProviders) 
        {  
            if ("gps".equals(name)) 
            {  
                return true;  
            }  
        }  
        return false;  
    }  
    
    /**
    * @Description: �?般用于获取apikey，如�?<meta-data android:name="api_key" android:value="fjYoOGjPsZmRHj8eub0X95Up" />
    * @param @param context
    * @param @param metaKey
    * @param @return   
    * @return String 
    * @throws
     */
    public static String getMetaValue(Context context, String metaKey) 
    {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) 
        {
            return null;
        }
        try 
        {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) 
            {
                metaData = ai.metaData;
            }
            if (null != metaData) 
            {
                apiKey = metaData.getString(metaKey);
            }
        } 
        catch (NameNotFoundException e) 
        {

        }
        return apiKey;
    }
    
    public static void hideInputView(Activity activity)
    {
    	View view = activity.getWindow().peekDecorView();
        if (view != null) 
        {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    /**
    * @Description: 获取设备唯一id
    * @param @param activity
    * @param @return   
    * @return String 
    * @throws
     */
    public static String getAndroidId(Context activity)
    {
    	return Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
    }
    
    // 得到本机Mac地址
    public String getLocalMac(Activity activity)
    {
        String mac = "";
        // 获取wifi管理�?
        WifiManager wifiMng = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        mac = wifiInfor.getMacAddress();
        return mac;
    }
    
    /**隐藏软键�?**/
    public static void closeSoftInput(Activity activity)
    {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
  //版本�?
    public static String getVersionName(Context context) {
  	    return getPackageInfo(context).versionName;
  	}
  	 
  	//版本�?
    public static int getVersionCode(Context context) {
  	    return getPackageInfo(context).versionCode;
  	}
  	 
  	private static PackageInfo getPackageInfo(Context context) {
  	    PackageInfo pi = null;
  	 
  	    try {
  	        PackageManager pm = context.getPackageManager();
  	        pi = pm.getPackageInfo(context.getPackageName(),
  	                PackageManager.GET_CONFIGURATIONS);
  	 
  	        return pi;
  	    } catch (Exception e) {
  	        e.printStackTrace();
  	    }
  	 
  	    return pi;
  	}
  	
  	public static int getCurrentVolume(Activity activity)
	{
		AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE); 
		//int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
		return mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ); 
	}
}
 
