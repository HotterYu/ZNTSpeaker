
package com.znt.speaker.factory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.diange.mina.entity.DeviceInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.R;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.player.TextureVideoPlayer;
import com.znt.speaker.prcmanager.ZNTWifiServiceManager;
import com.znt.utils.DateUtils;
import com.znt.utils.FileUtils;
import com.znt.utils.SystemUtils;

/** 
 * @ClassName: UIManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-21 涓嬪崍5:00:37  
 */
public class UIManager implements OnClickListener, OnSeekBarChangeListener
{
	
	private Activity activity = null;
	
	private View parentView = null;
	
	public TextView mTVPrepareSpeed;
	
	public TextView mTVLoadSpeed;
	public TextView tvCodeHint = null;
	
	public View mControlView;	
	public TextView mTVSongName;
	public TextView mTVArtist;
	
	public ImageButton mBtnPlay;
	public ImageButton mBtnPause;
	public SeekBar mSeekBar;
	public TextView mTVCurTime;
	public TextView mTVTotalTime;
	
	public View viewWifiSet = null;
	public Button btnCancel = null;
	public Button btnConfirm = null;
	public EditText etWifiName = null;
	public EditText etWifiPwd = null;
	public TextView tvdevInfor = null;
	public Button btnUpdate = null;
	public RadioButton rbWifi = null;
	public RadioButton rbBind = null;
	
	public TextView tvCurTime = null;
	public TextView tv_speed = null;
	
	private ImageView mRatioImageView;
	private View viewImageShow;
	private TextureVideoPlayer textureView;
	private Surface surface = null;
	private View viewMusicPlayBg = null;
	//private View viewLoading = null;
	
	public TranslateAnimation mHideDownTransformation;
	public AlphaAnimation mAlphaHideTransformation;
	
	private ZNTWifiServiceManager mZNTWifiServiceManager;
	
	public View mSongInfoView;
	public boolean lrcShow = false;
	private long curTime = 0;
	private int clickCount = 0;
	
	public UIManager(Activity activity)
	{
		this.activity = activity;
		initView();
	}

	public void initView()
	{
		
		parentView = activity.findViewById(R.id.rootframeview);
		mTVPrepareSpeed = (TextView) activity.findViewById(R.id.tv_prepare_speed);
		
		mTVLoadSpeed = (TextView) activity.findViewById(R.id.tv_speed);
		
		tvCodeHint = (TextView) activity.findViewById(R.id.tv_music_player_hint);
		
		mControlView = activity.findViewById(R.id.control_panel);	
		mTVSongName = (TextView) activity.findViewById(R.id.tv_title);
		mTVArtist = (TextView) activity.findViewById(R.id.tv_artist);
		
		mBtnPlay = (ImageButton) activity.findViewById(R.id.btn_play);
		mBtnPause = (ImageButton) activity.findViewById(R.id.btn_pause);
		mBtnPlay.setOnClickListener(this);
		mBtnPause.setOnClickListener(this);	
		mSeekBar = (SeekBar) activity.findViewById(R.id.playback_seeker);
		tv_speed = (TextView) activity.findViewById(R.id.tv_cur_net_speed);
		mTVCurTime = (TextView) activity.findViewById(R.id.tv_curTime);
		mTVTotalTime = (TextView) activity.findViewById(R.id.tv_totalTime);
		
		viewWifiSet = activity.findViewById(R.id.wifi_set);
		etWifiName = (EditText) activity.findViewById(R.id.et_wifi_name);
		etWifiPwd = (EditText) activity.findViewById(R.id.et_wifi_pwd);
		tvdevInfor = (TextView) activity.findViewById(R.id.tv_wifi_dev_infor);
		btnCancel = (Button) activity.findViewById(R.id.btn_wifi_cancel);
		btnConfirm = (Button) activity.findViewById(R.id.btn_wifi_confirm);
		btnUpdate = (Button) activity.findViewById(R.id.btn_update);
		rbBind = (RadioButton) activity.findViewById(R.id.rb_bind);
		rbWifi = (RadioButton) activity.findViewById(R.id.rb_wifi);
		
		tvCurTime = (TextView) activity.findViewById(R.id.tv_cur_time);
		showCurTime();
		
		//viewLoading = activity.findViewById(R.id.prepare_panel);
		viewMusicPlayBg = activity.findViewById(R.id.view_player_bg_default);
		
		setSeekbarListener(this);
		
    	mSongInfoView = activity.findViewById(R.id.song_info_view);
	    
		mHideDownTransformation = new TranslateAnimation(0.0f, 0.0f,0.0f,200.0f);  
    	mHideDownTransformation.setDuration(1000);
    	
    	mAlphaHideTransformation = new AlphaAnimation(1, 0);
    	mAlphaHideTransformation.setDuration(1000);
    	
    	btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hindeWifiView();
			}
		});
    	btnConfirm.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			String wifiName = etWifiName.getText().toString();
    			String wifiPwd = etWifiPwd.getText().toString();
    			
    			if(isBindDevice)
    			{
    				if(TextUtils.isEmpty(wifiPwd))
        			{
        				Toast.makeText(activity, "请输入激活码，在手机APP上登录查看", Toast.LENGTH_SHORT).show();
        				return;
        			}
        			if(onBindDeviceListener != null)
        				onBindDeviceListener.onDeviceBind(wifiName, wifiPwd);
    			}
    			else
    			{
    				if(TextUtils.isEmpty(wifiName))
        			{
        				Toast.makeText(activity, "请输入要连接的WIFI名称", Toast.LENGTH_SHORT).show();
        				return;
        			}
        			if(onSetWifiListener != null)
        			{
        				onSetWifiListener.OnWifiSetStart(wifiName, wifiPwd);
        				
        			}
    			}
    			hindeWifiView();
    		}
    	});
    	btnUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hindeWifiView();
			}
		});
    	
    	parentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickCount ++;
				if(clickCount >= 3)
				{
					clickCount = 0;
					if(!viewWifiSet.isShown())
					{
						viewWifiSet.setVisibility(View.VISIBLE);
						/*DeviceInfor devInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
						if(devInfor != null)
						{
							tvdevInfor.setText("设备名称：" + devInfor.getName() + "   编号：" + devInfor.getCode());
							
							etWifiName.setText(devInfor.getWifiName());
							etWifiPwd.setText(devInfor.getWifiPwd());
						}*/
						Constant.UPDATE_TYPE = 0;
						isBindDevice = false;
						rbWifi.setChecked(true);
						updateOperStatus();
					}
				}
				/*else
					hindeWifiView();*/
			}
		});
    	
    	rbWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					isBindDevice = false;
					updateOperStatus();
				}
			}
		} );
    	rbBind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		
    		@Override
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			// TODO Auto-generated method stub
    			if(isChecked)
    			{
    				isBindDevice = true;
    				updateOperStatus();
    			}
    		}
    	} );
    	
	}
	
	public void setZNTWifiServiceManager(ZNTWifiServiceManager mZNTWifiServiceManager)
	{
		this.mZNTWifiServiceManager = mZNTWifiServiceManager;
	}
	
	private boolean isBindDevice = false;
	private void updateOperStatus()
	{
		if(isBindDevice)
		{
			etWifiName.setHint("请输入设备名称，不输入默认为  店音");
			etWifiPwd.setHint("请输入激活码，登录手机APP账户查看");
			
			etWifiName.setText("");
			etWifiPwd.setText("");
			
		}
		else
		{
			
			etWifiName.setHint("请输入要连接的WIFI名称");
			etWifiPwd.setHint("请输入正确的WIFI密码");
			
			DeviceInfor devInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
			if(devInfor != null)
			{
				tvdevInfor.setText("设备名称：" + devInfor.getName() + "   编号：" + devInfor.getCode());
				
				if(mZNTWifiServiceManager != null)
				{
					etWifiName.setText(mZNTWifiServiceManager.getCurConnectWifiName());
					etWifiPwd.setText(mZNTWifiServiceManager.getCurConnectWifiPwd());
				}
			}
		}
	}
	
	public View getWifiView()
	{
		return viewWifiSet;
	}
	public void hindeWifiView()
	{
		if(viewWifiSet.isShown())
		{
			viewWifiSet.setVisibility(View.GONE);
			clickCount = 0;
		}
	}
	
	/*private void showWifiListDialog()
	{
		final WifiListDialog mWifiListDialog = new WifiListDialog(activity);
		mWifiListDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(mWifiListDialog.getSelectedWifi()))
					etWifiName.setText(mWifiListDialog.getSelectedWifi());
			}
		});
		mWifiListDialog.showWifiDialog();
	}*/
	/**
	 * 检测GPS是否打开
	 *
	 * @return
	 */
	private boolean checkGPSIsOpen() 
	{
	    boolean isOpen;
	    LocationManager locationManager = (LocationManager) activity
	            .getSystemService(Context.LOCATION_SERVICE);
	    isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	    return isOpen;
	}

	/**
	 * 跳转GPS设置
	 */
	private void openGPSSettings() {
	    if (checkGPSIsOpen()) 
	    {
	    	//showWifiListDialog();
	    } 
	    else
	    {
	        //没有打开则弹出对话框
	        new AlertDialog.Builder(activity)
	                .setTitle("提示")
	                .setMessage("当前系统获取wifi需要开启定位，请点击确定打开定位")
	                // 拒绝, 退出应用
	                .setNegativeButton(R.string.cancel,
	                        new DialogInterface.OnClickListener() {
	                            @Override
	                            public void onClick(DialogInterface dialog, int which) {
	                                
	                            }
	                        })

	                .setPositiveButton("确定",
	                        new DialogInterface.OnClickListener() {
	                            @Override
	                            public void onClick(DialogInterface dialog, int which) {
	                                //跳转GPS设置界面
	                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                                activity.startActivity(intent);
	                            }
	                        })

	                .setCancelable(false)
	                .show();

	    }
	}

	
	public void showCurTime()
	{
		if(curTime > 0)
		{
			curTime += 1000;
			String time = DateUtils.getDateFromLong(curTime);
			tvCurTime.setText(time);
		}
		/*curTime = System.currentTimeMillis();
        Date date = new Date(curTime);
        //SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEE");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        tvCurTime.setText(format.format(date));*/
	}
	
	public void showNetSpead(String speed)
	{
		tv_speed.setText(speed);
	}
	
	public void setCurTime(String time)
	{
		if(!TextUtils.isEmpty(time))
			curTime = Long.parseLong(time);
	}
	public void setCurTime(long time)
	{
		this.curTime = time;
	}
	public long getCurTime()
	{
		return curTime;
	}
	
	/*public void setSurfaceViewOritation()
	{
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ViewUtils.setViewParams(activity, textureView,dm.heightPixels , dm.widthPixels);
		String degree = LocalDataEntity.newInstance(activity).getVideoWhirl();
		if(textureView != null)
		{
			float curDegree = getDegree(LocalDataEntity.newInstance(activity).getVideoWhirl());
			if(curDegree > 0)
			{
				curDegree = -curDegree;
				textureView.setRotation(curDegree);
			}
			textureView.setRotation(getDegree(degree));
			setVideoSize(degree);
			textureView.adjustAspectRatio();
		}
	}*/
	private float getDegree(String degree)
	{
		if(degree.equals("0"))//
			return 0;
		else if(degree.equals("1"))//
			return 90;
		else if(degree.equals("2"))//
			return 180;
		else if(degree.equals("3"))//
			return -90;
		return 0;
	}
	
	/*private void setVideoSize(String degree)
	{
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels*2;
		
		RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) textureView.getLayoutParams();
		//获取当前控件的布局对象
		
		if(degree.equals("0") || degree.equals("2"))
		{
			params.height=height;
			params.width=width;
		}
		else
		{
			params.height=width;
			params.width=height;
		}
		
		textureView.setLayoutParams(params);//将设置好的布局参数应用到控件中
	}*/
	
	public void initTextureView()
	{
		textureView = (TextureVideoPlayer) activity.findViewById(R.id.surfaceView);
		textureView.setLayerType(TextureView.LAYER_TYPE_HARDWARE, null);
		
		//setSurfaceViewOritation();
	}

	public void releaseTextureView()
	{
		if(textureView != null)
		{
			textureView.destroyDrawingCache();
			textureView = null;
		}
	}
	public TextureVideoPlayer getTextureView()
	{
		return textureView;
	}
	public Surface getSurfcae()
	{
		return surface;
	}
	
	/*public void adjustAspectRatio(int videoWidth, int videoHeight) 
	{
		if(textureView == null)
			return;
		
	    int viewWidth = textureView.getWidth();
	    int viewHeight = textureView.getHeight();
	    double aspectRatio = (double) videoHeight / videoWidth;

	    int newWidth, newHeight;
	    if (viewHeight > (int) (viewWidth * aspectRatio)) 
	    {
	        // limited by narrow width; restrict height
	        newWidth = viewWidth;
	        newHeight = (int) (viewWidth * aspectRatio);
	    } 
	    else 
	    {
	        // limited by short height; restrict width
	        newWidth = (int) (viewHeight / aspectRatio);
	        newHeight = viewHeight;
	    }
	    int xoff = (viewWidth - newWidth) / 2;
	    int yoff = (viewHeight - newHeight) / 2;
	    

	    Matrix txform = new Matrix();
	    textureView.getTransform(txform);
	    txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
	    txform.postTranslate(xoff, yoff);
	    textureView.setTransform(txform);
	}*/
	
	public void showDefaultView(SongInfor mediaInfor)
	{
		String url = mediaInfor.getMediaUrl();
		
		if(FileUtils.isMusic(url))
		{
			showPrepareLoadView(true);
		}
		else
		{
			showPrepareLoadView(false);
		}
		/*surfaceView.setVisibility(View.GONE);
		viewMusicPlayBg.setVisibility(View.GONE);*/
	}
	
	public void unInit(){
		
	}
	
	public void showDeviceInfor()
	{
		/*if(tvHint != null && activity != null)
		{
			DeviceInfor deviceInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
			String playRes = LocalDataEntity.newInstance(activity).getPlayRes();
			if(playRes.equals(PlayRes.LOCAL))
				playRes = "本地歌曲";
			else if(playRes.equals(PlayRes.ONLINE))
				playRes = "在线歌曲";
			tvHint.setText("ssid:  " + SystemUtils.getConnectWifiSsid(activity)
					+ "\n ip:  " + SystemUtils.getIP()
					 + "\n 播放模式:  " + playRes
					 + "\n 设备id:  " + deviceInfor.getId()
					 + "\n 设备code:  " + deviceInfor.getCode()
					 + "\n 设备名称:  " + deviceInfor.getName());
		}*/
		
		String code = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(TextUtils.isEmpty(code))
			Toast.makeText(activity, "未获取到设备编号，请先联网", Toast.LENGTH_SHORT).show();
		tvCodeHint.setText("v " + SystemUtils.getVersionName(activity) + " id:  " + code);
	}

	public void initRatioImageView()
	{
		mRatioImageView = (ImageView) activity.findViewById(R.id.ratioImageView);
		viewImageShow = activity.findViewById(R.id.image_show_bg);

	}
	public void showRatioImageView(boolean show)
	{
		if(mRatioImageView == null)
			return;
		if(show)
		{
			viewImageShow.setVisibility(View.VISIBLE);
			mRatioImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			mRatioImageView.setVisibility(View.GONE);
			viewImageShow.setVisibility(View.GONE);
		}
	}
	public ImageView getRatioImageView()
	{
		return mRatioImageView;
	}
	
	public void showPrepareLoadView(boolean isShow)
	{
		if (isShow)
		{
			viewMusicPlayBg.setVisibility(View.VISIBLE);
			mSongInfoView.setVisibility(View.VISIBLE);
			if(textureView != null)
				textureView.setVisibility(View.GONE);
		}
		else
		{
			viewMusicPlayBg.setVisibility(View.GONE);
			mSongInfoView.setVisibility(View.GONE);
			if(textureView != null)
				textureView.setVisibility(View.VISIBLE);
		}
	}
	
	public void showControlView(boolean show)
	{
		/*if (show)
		{
			mControlView.setVisibility(View.VISIBLE);
		}
		else
		{
			mControlView.setVisibility(View.GONE);
		}*/
	}
	
	public void play()
	{
		if(onPlayerControllListener != null)
		{
			onPlayerControllListener.onClickPlay();
		}
		//MusicActivity.mPlayerEngineImpl.play();
	}
	
	public void pause()
	{
		if(onPlayerControllListener != null)
		{
			onPlayerControllListener.onClickPause();
		}
		//MusicActivity.mPlayerEngineImpl.pause();
	}
	
	public void stop()
	{
		//MusicActivity.mPlayerEngineImpl.stop();
	}
	
	private boolean isSeekbarTouch = false;	

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_play:
				play();
				break;
			case R.id.btn_pause:
				pause();
				break;
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		setcurTime(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) 
	{
		isSeekbarTouch = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		isSeekbarTouch = false;			
		seek(seekBar.getProgress());
	}
	
	public boolean isSeekComplete = false;
	public void seek(int pos)
	{
		if(onPlayerControllListener != null)
		{
			onPlayerControllListener.onClickSeek(pos);
		}
		//isSeekComplete = false;
		//MusicActivity.mPlayerEngineImpl.skipTo(pos);
		setSeekbarProgress(pos);
	}
	
	public void showPlay(boolean bShow)
	{
		if (bShow)
		{
			mBtnPlay.setVisibility(View.VISIBLE);
			mBtnPause.setVisibility(View.INVISIBLE);
		}
		else
		{
			mBtnPlay.setVisibility(View.INVISIBLE);
			mBtnPause.setVisibility(View.VISIBLE);
		}
	}
	
	public void togglePlayPause()
	{
		if (mBtnPlay.isShown())
		{
			play();
		}
		else
		{
			pause();
		}
	}
	
	public void setSeekbarProgress(int time)
	{
		if (!isSeekbarTouch)
		{
			mSeekBar.setProgress(time);	
		}
	}
	public int getMaxProgress()
	{
		return mSeekBar.getMax();
	}
	
	public void setSeekbarSecondProgress(int time)
	{
		mSeekBar.setSecondaryProgress(time);	
	}
	
	public void setSeekbarMax(int max){
		mSeekBar.setMax(max);
	}
	
	public void setcurTime(int curTime)
	{
		final String timeString = DateUtils.formateTime(curTime);
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mTVCurTime.setText(timeString);
			}
		});
		
	}
	
	public void setTotalTime(int totalTime){
		String timeString = DateUtils.formateTime(totalTime);
		mTVTotalTime.setText(timeString);
	}
	
	public void updateMediaInfoView(SongInfor mSongInfor, String indexStr ){
		
		if(mSongInfor == null)
			return;
		setcurTime(0);
		setTotalTime(0);
		setSeekbarMax(0);
		setSeekbarProgress(0);
		
		showDefaultView(mSongInfor);

		mTVSongName.setText(indexStr + "   " + mSongInfor.getMediaName());
		mTVArtist.setText("   " + mSongInfor.getArtist());
	}
	
	public void setLoadingHint(String hint){
		//String showString = (int)speed + "KB/" + activity.getResources().getString(R.string.second);
		mTVPrepareSpeed.setText(hint);
		//mTVLoadSpeed.setText(hint);
	}

	public void setSeekbarListener(OnSeekBarChangeListener listener)
	{
		mSeekBar.setOnSeekBarChangeListener(listener);
	}

	public boolean isControlViewShow(){
		return mControlView.getVisibility() == View.VISIBLE ? true : false;
	}
	
	public boolean isLoadViewShow(){
		/*if (mLoadView.getVisibility() == View.VISIBLE || 
				mPrepareView.getVisibility() == View.VISIBLE){
			return true;
		}*/
		
		return false;
	}
	
	public OnSetWifiListener onSetWifiListener = null;
	public void setOnSetWifiListener(OnSetWifiListener onSetWifiListener)
	{
		this.onSetWifiListener = onSetWifiListener;
	}
	public interface OnSetWifiListener
	{
		public void OnWifiSetStart(String wifiName, String wifiPwd);
	}
	
	public OnBindDeviceListener onBindDeviceListener = null;
	public void setOnBindDeviceListener(OnBindDeviceListener onBindDeviceListener)
	{
		this.onBindDeviceListener = onBindDeviceListener;
	}
	public interface OnBindDeviceListener
	{
		public void onDeviceBind(String name, String account);
	}
	
	public OnPlayerControllListener onPlayerControllListener = null;
	public void setOnPlayerControllListener(OnPlayerControllListener onPlayerControllListener)
	{
		this.onPlayerControllListener = onPlayerControllListener;
	}
	public interface OnPlayerControllListener
	{
		public void onClickPause();
		public void onClickPlay();
		public void onClickSeek(int pos);
	}
}
 
