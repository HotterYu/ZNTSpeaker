package com.znt.utils; 

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/** 
 * @ClassName: MyViewUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-13 上午11:28:47  
 */
public class ViewUtils
{
	/**
	* @Description: activity跳转
	* @param @param context
	* @param @param cls
	* @param @param bundle   
	* @return void 
	* @throws
	 */
	public static void startActivity(Activity context, Class<?> cls, Bundle bundle)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if(bundle != null)
			intent.putExtras(bundle);
		context.startActivity(intent);
	}
	public static void startActivity(Activity context, Class<?> cls, Bundle bundle, int requestCode)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if(bundle != null)
			intent.putExtras(bundle);
		context.startActivityForResult(intent, requestCode);
	}
	public static void startActivity(Context context, Class<?> cls, Bundle bundle)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if(bundle != null)
			intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	public static void startCall(Activity context, String telNum)
	{
		if(!TextUtils.isEmpty(telNum))
		{
			 Uri uri = Uri.parse("tel:" + telNum);   
			 Intent intent = new Intent(Intent.ACTION_DIAL, uri);     
			 context.startActivity(intent);  

		}
	}
	public static void startWebView(Activity context, String webUrl)
	{
		if(!TextUtils.isEmpty(webUrl))
		{
			Uri uri = Uri.parse(webUrl);    
			Intent it = new Intent(Intent.ACTION_VIEW, uri);    
			context.startActivity(it);  
			
		}
	}
	public static void startMessage(Activity context, String telNum)
	{
		if(!TextUtils.isEmpty(telNum))
		{
			Uri uri = Uri.parse("smsto:" + telNum);    
			Intent intent = new Intent(Intent.ACTION_SENDTO,uri);    
	        context.startActivity(intent);  
		}
	}
	public static void startNetWorkSet(Context context)
	{
		Intent intentToNetwork = new Intent(Settings.ACTION_WIFI_SETTINGS);  
		//Intent intentToNetwork = new Intent(Settings.ACTION_WIRELESS_SETTINGS);  
        /*
        intentToNetwork.setAction("android.intent.action.VIEW");  */
        context.startActivity(intentToNetwork);  
	}
	
	/**
	* @Description: 跳转到其他app指定页面
	* @param @param context
	* @param @param appPkg
	* @param @param absActivityName   
	* @return void 
	* @throws
	 */
	public static void startAppActivity(Context context, String appPkg, String absActivityName)
	{
		// 这个是另外一个应用程序的包名   这个参数是要启动的Activity  
		ComponentName componetName = new ComponentName(appPkg, absActivityName);  
		//ComponentName componetName = new ComponentName(appPkg, absActivityName);  
        Intent intent = new Intent();  
        intent.setComponent(componetName);  
        context.startActivity(intent);

        //�?要在配置文件中对activity做如下配�?
        /*<activity
        android:name="com.neldtv.activity.HelpActivity">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
        </intent-filter>*/
	}
	
	
	/**
	* @Description: activity跳转
	* @param @param context
	* @param @param bundle   
	* @return void 
	* @throws
	 */
	public static void startActivity(Activity context, Bundle bundle)
	{
		Intent intent = new Intent();  
		intent.putExtras(bundle);
        context.setResult(Activity.RESULT_OK, intent);  
        context.finish();  
	}
	
	public static void setViewParams(Activity activity, View view, int w, int h)
	{
		
		int width = StringUtils.dip2px(activity, w);
		int height = StringUtils.dip2px(activity, h);
		if(view.getParent() instanceof RelativeLayout)
			setRltViewParams(view, width, height);
		else if(view.getParent() instanceof LinearLayout)
			setLinearViewParams(view, width, height);
		else if(view.getParent() instanceof FrameLayout)
			setFrameViewParams(view, width, height);
	}
	public static void setViewParams(Context activity, View view, int w, int h)
	{
		
		int width = StringUtils.dip2px(activity, w);
		int height = StringUtils.dip2px(activity, h);
		if(view.getParent() instanceof RelativeLayout)
			setRltViewParams(view, width, height);
		else if(view.getParent() instanceof LinearLayout)
			setLinearViewParams(view, width, height);
		else if(view.getParent() instanceof FrameLayout)
			setFrameViewParams(view, width, height);
	}
	
	/**
	* @Description: 设置RelativeLayout参数
	* @param @param view
	* @param @param width
	* @param @param height   
	* @return void 
	* @throws
	 */
	private static void setRltViewParams(View view, int width, int height)
    {
    	RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
	/**
	* @Description: 设置LinearLayout参数
	* @param @param view
	* @param @param width
	* @param @param height   
	* @return void 
	* @throws
	 */
	private static void setLinearViewParams(View view, int width, int height)
    {
    	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
    /**
    * @Description: 设置FrameLayout参数
    * @param @param view
    * @param @param width
    * @param @param height   
    * @return void 
    * @throws
     */
	private static void setFrameViewParams(View view, int width, int height)
    {
    	FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    	if(width > 0)
    		params.width = width;
    	if(height > 0)
    		params.height = height;
    	view.setLayoutParams(params);
    }
	
    /**
	* @Description: 发�?�handler消息
	* @param @param handler
	* @param @param obj
	* @param @param what   
	* @return void 
	* @throws
	 */
	public static void sendMessage(Handler handler, int what, Object obj)
	{
		if(handler == null)
			return;
		Message msg = new Message();
		if(obj != null)
			msg.obj = obj;
		msg.what = what;
		if(handler != null)
			handler.sendMessage(msg);
	}
	public static void sendMessage(Handler handler, int what)
	{
		if(handler != null)
			handler.sendEmptyMessage(what);
	}
	
	/**
     * 设置子字符串为红�?
     * @param text
     * @param colorText
     * @return
     */
    public static SpannableString setColorText(String text, String colorText)
    {
        //创建�?�? SpannableString对象    
        SpannableString msp = new SpannableString(text);
        Pattern p=Pattern.compile(colorText);
        Matcher matcher=p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    
    /**
     * 设置子字符串为红色和字体大小
     * @param text
     * @param colorText
     * @param colorTextSize
     * @return
     */
    public static SpannableString setColorText(String text, String colorText, float colorTextSize)
    {
        //创建�?�? SpannableString对象    
        SpannableString msp = new SpannableString(text);
        Pattern p = Pattern.compile(colorText);
        Matcher matcher = p.matcher(text);
        while(matcher.find())
        {
            msp.setSpan(new ForegroundColorSpan(Color.RED), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
                    matcher.start(), matcher.end(), 
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return msp;
    }
    public static SpannableString setColorText(String text, String colorText, float colorTextSize, int color)
    {
    	//创建�?�? SpannableString对象    
    	SpannableString msp = new SpannableString(text);
    	Pattern p = Pattern.compile(colorText);
    	Matcher matcher = p.matcher(text);
    	while(matcher.find())
    	{
    		msp.setSpan(new ForegroundColorSpan(color), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    		
    		msp.setSpan(new RelativeSizeSpan((colorTextSize)), 
    				matcher.start(), matcher.end(), 
    				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	}
    	return msp;
    }
    
    
    public static void editTextNumberAndDot(EditText editText)
	{
    	editText.setKeyListener(new NumberKeyListener()
		{  
			@Override  
			protected char[] getAcceptedChars() 
			{  
				return new char[] { '1', '2', '3', '4', '5', '6', '7', '8','9', '0','.'};  
			}  
			@Override  
			public int getInputType() 
			{  
				// TODO Auto-generated method stub   
				return android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;  
			}  
		}); 
	}
    
	/**
	 * 获取状�?�栏高度
	 * 
	 * @return
	 */
	public static int getStatusBarHeight(Context context) 
	{
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try 
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return statusBarHeight;
	}
	
	public static int getDimens(Activity activity, int id)
    {
		int result = (int) activity.getResources().getDimension(id);
    	return StringUtils.px2dip(activity, result);
    }
    public static int getDimens(Context activity, int id)
    {
    	int result = (int) activity.getResources().getDimension(id);
    	return StringUtils.px2dip(activity, result);
    }
}
 
