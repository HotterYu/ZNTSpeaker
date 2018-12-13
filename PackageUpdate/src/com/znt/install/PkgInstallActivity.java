package com.znt.install;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.znt.install.R;

public class PkgInstallActivity extends Activity
{
	/** Called when the activity is first created. */
    private final int INSTALL_COMPLETE = 1;
    final static int SUCCEEDED = 1;
    final static int FAILED = 0;
    private String pkg_name;
    private String apk_path;
    
    private String ip_set_addr;
    private String ip_set_gateway;
    private String ip_set_dns1;
    private String ip_set_dns2;
    
    private String system_time_set = "";
    
    private File apk_file = null;
    private TextView tvHint;
    private Intent mLaunchIntent;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        tvHint = (TextView)findViewById(R.id.tv_hint);
        
        Intent intent = getIntent();
        
        pkg_name = intent.getStringExtra("pkg_name");
        apk_path = intent.getStringExtra("apk_path");
        if(!TextUtils.isEmpty(pkg_name))
        {
        	doUpdate();
        }
        else
        {
        	ip_set_addr = intent.getStringExtra("ip_set_addr");
            ip_set_gateway = intent.getStringExtra("ip_set_gateway");
            ip_set_dns1 = intent.getStringExtra("ip_set_dns1");
            ip_set_dns2 = intent.getStringExtra("ip_set_dns2");
            if(!TextUtils.isEmpty(ip_set_addr))
            {
            	doIpSet();
            	
            	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						finish();
					}
				}, 3000);
            }
            
            system_time_set = intent.getStringExtra("system_time_set");
        }
        
        /*if(!TextUtils.isEmpty(system_time_set))
        {
        	doSystemTimeSet();
        	
        	finish();
        }*/
        
    }
    
    /*private void doSystemTimeSet()
    {
    	
    }*/
    
    private void doIpSet()
    {
    	tvHint.setText(R.string.ip_set_start);
    	boolean result = StaticIpUtil.setIp(getApplicationContext(), ip_set_addr, ip_set_gateway, ip_set_dns1, ip_set_dns2);
    	if(result)
    		tvHint.setText(R.string.ip_set_success);
    	else
    		tvHint.setText(R.string.ip_set_fail);
    }
    
    private void doUpdate()
    {
    	/*System.out.println("pkg_name-->"+pkg_name);
        System.out.println("apk_path-->"+apk_path);*/
        
        if(TextUtils.isEmpty(pkg_name) || TextUtils.isEmpty(apk_path))
        {
        	Log.e("PkgInstallActivity", "*********install error pkg_name-->"+pkg_name + "   apk_path-->"+apk_path);
        	finish();
        	return;
        }
        
        apk_file = new File(apk_path);
        
        if(!apk_file.exists())
        {
        	Log.e("PkgInstallActivity", "*********install error  file not exsit");
        	finish();
        	return;
        }
        
        installApk(pkg_name, apk_file);
    }
    
    private void installApk(String pkg, File apkFile)
    {
		Uri uri = Uri.fromFile(apkFile);

        int installFlags = 0;
        PackageManager pm = getPackageManager();
        try 
        {
            PackageInfo pi = pm.getPackageInfo(pkg, 
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if(pi != null) 
            {
                installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
            }
        }
        catch (NameNotFoundException e) 
        {
        	
        }

        PackageInstallObserver observer = new PackageInstallObserver();
        pm.installPackage(uri, observer, installFlags, pkg);

    }
    
    @Override
    protected void onDestroy() 
    {
    	// TODO Auto-generated method stub
    	delFile(apk_file);
    	super.onDestroy();
    }
    
    public void delFile(File file) 
    {  
    	if(apk_file != null && file.exists())
    	{
    		file.delete(); 
    	}
    }  
    
    class PackageInstallObserver extends IPackageInstallObserver.Stub 
    {
        public void packageInstalled(String packageName, int returnCode) 
        {
        	
        	Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
            msg.arg1 = returnCode;
            mHandler.sendMessage(msg);
            System.out.println("send msg success-->"+returnCode);
        }
    };
    
    private Handler mHandler = new Handler() 
    {
        public void handleMessage(Message msg) 
        {
            switch (msg.what) 
            {
                case INSTALL_COMPLETE:
                	System.out.println("PackageManager.INSTALL_SUCCEEDED-->"+PackageManager.INSTALL_SUCCEEDED);
                	System.out.println("PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE-->"+PackageManager.INSTALL_FAILED_INSUFFICIENT_STORAGE);
                	
                	if(msg.arg1 == PackageManager.INSTALL_SUCCEEDED) 
                    {
                		 tvHint.setText(R.string.updated);
                    	 mLaunchIntent = getPackageManager().getLaunchIntentForPackage(pkg_name);
                    	 if(mLaunchIntent != null) 
                    	 {
                    		 startActivity(mLaunchIntent);
                             /*List<ResolveInfo> list = getPackageManager().
                                     queryIntentActivities(mLaunchIntent, 0);
                             if (list != null && list.size() > 0)
                             {
                            	 startActivity(mLaunchIntent);
                            	 
                                 finish();
                             }*/
                         }
                    	 finish();
                    } 
                    break;
                default:
                    break;
            }
        }
    };
}