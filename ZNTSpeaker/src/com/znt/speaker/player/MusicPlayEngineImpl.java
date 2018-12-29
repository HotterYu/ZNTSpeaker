package com.znt.speaker.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;

import com.znt.diange.mina.entity.PlayState;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.UIManager;
import com.znt.utils.CommonLog;
import com.znt.utils.FileUtils;
import com.znt.utils.LogFactory;
import com.znt.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MusicPlayEngineImpl extends AbstractMediaPlayEngine implements SurfaceTextureListener
{

	private final CommonLog log = LogFactory.createLog();	
	
	private OnSeekCompleteListener mSeekCompleteListener;
	
	private Activity activity = null;
	private boolean isPreparing = false;
	private boolean isPlayFile = false;
	private long loadStartTime = 0;
	private Surface surface = null;
	
	public  MusicPlayEngineImpl(Activity context) 
	{
		super(context);
		this.activity = context;
	}
	
	public void setOnBuffUpdateListener(OnBufferingUpdateListener listener)
	{
		mBufferingUpdateListener = listener;
	}
	
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener)
	{
		mSeekCompleteListener = listener;
	}

	private OnImagePlayListener mOnImagePlayListener = null;
	public void setOnImagePlayListener(OnImagePlayListener mOnImagePlayListener)
	{
		this.mOnImagePlayListener = mOnImagePlayListener;
	}
	public interface OnImagePlayListener
	{
		void onImagePlay( SongInfor tempInfo);

	}
	
	public boolean isLoading()
	{
		return isPreparing;
	}
	private String playError = "";
	public String getError()
	{
		return playError;
	}
	public void setLoaded()
	{
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
		isPreparing = false;
	}
	
	public boolean isPlayFile()
	{
		return isPlayFile;
	}
	
	@Override
	public void play() 
	{
		super.play();
	}

	@Override
	public void pause() 
	{
		super.pause();
	}

	@Override
	public void stop() 
	{	
		super.stop();
	}
	
	private OnBufferingUpdateListener mBufferingUpdateListener;

	@Override
	public void exit()
	{
		super.exit();
	}

	private final int MSG_IMG_SHOW_PROCESS = 0;
	private final int MSG_IMG_HIDE_PROCESS = 1;
	private final int MSG_VIDE_SHOW_PROCESS = 2;
	private final int MSG_VIDE_HIDE_PROCESS = 3;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == MSG_IMG_SHOW_PROCESS)
			{
				if(!mUimanager.isBannerViewShow())
					mUimanager.initBannerView();
				mUimanager.updateImgList((String) msg.obj);
			}
			else if(msg.what == MSG_IMG_HIDE_PROCESS)
			{
				mUimanager.stopImagePlay();
			}
			else if(msg.what == MSG_VIDE_SHOW_PROCESS)
			{
				if(mUimanager.getTextureView() == null)
				{
					mUimanager.initTextureView();
					mUimanager.getTextureView().setSurfaceTextureListener(MusicPlayEngineImpl.this);
				}
			}
			else if(msg.what == MSG_VIDE_HIDE_PROCESS)
			{
				if(surface != null)
				{
					surface.release();
					surface = null;
				}
				mUimanager.releaseTextureView();
			}
		}
	};
	
	@Override
	protected boolean prepareSelf()
	{
		String urlPlay = getMusicPlayUrL(mMediaInfo);

		/*if(FileUtils.isPicture(urlPlay))
		{
			ViewUtils.sendMessage(mHandler,MSG_IMG_SHOW_PROCESS, urlPlay);
			if(mOnImagePlayListener != null)
				mOnImagePlayListener.onImagePlay(mMediaInfo);
			setSongInforPlay(mMediaInfo);
		}
		else*/
		{
			try
			{

				if(FileUtils.isVideo(urlPlay))//如果是视频就隐藏图片
					ViewUtils.sendMessage(mHandler,MSG_IMG_HIDE_PROCESS, null);

				loadStartTime = System.currentTimeMillis();
				isPreparing = true;

				mMediaPlayer.reset();

				mMediaPlayer.setDataSource(urlPlay);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				if (mBufferingUpdateListener != null)
				{
					mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
				}
				if(FileUtils.isVideo(urlPlay))
				{
					ViewUtils.sendMessage(mHandler,MSG_VIDE_SHOW_PROCESS, null);
				}
				else
				{
					ViewUtils.sendMessage(mHandler,MSG_VIDE_HIDE_PROCESS, null);
				}

				mMediaPlayer.prepareAsync();
				mPlayState = PlayState.MPS_PARESYNC;
				performPlayListener(mPlayState);
				playError = "";
			}
			catch (Exception e)
			{
				playError = e.getMessage();
				e.printStackTrace();
				isPreparing = false;
				mPlayState = PlayState.MPS_INVALID;
				setSongInforPlay(mMediaInfo);
				performPlayListener(mPlayState);
				log.e("prepareSelf Exception-->" + e.getMessage());

				return false;
			}
		}
		return true;
	}
	
	@Override
	protected boolean prepareComplete(MediaPlayer mp) 
	{
		
		isPreparing = false;
		
		log.e("prepareComplete");	
		//int duration = mp.getDuration();
		
		setSongInforPlay(mMediaInfo);
		seekCurPos();
		
		mPlayState = PlayState.MPS_PARECOMPLETE;
		if (mPlayerEngineListener != null)
		{
			mPlayerEngineListener.onTrackPrepareComplete(mMediaInfo);
		}
		mMediaPlayer.start();
		
		mPlayState = PlayState.MPS_PLAYING;
		performPlayListener(mPlayState);
		return true;
	}
	
	public String getMusicPlayUrL(SongInfor songInfor)
	{
		String url = songInfor.getMediaUrl();
		if(url.startsWith("http://") || url.startsWith("https://"))
		{
			String fileName = "";
			if(url.contains("."))
				fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
			File file = new File(filePath);
			if(file.exists())
			{
				isPlayFile = true;
				return filePath;
			}
			else
			{
				isPlayFile = false;
			}
		}
		else
			isPlayFile = true;
		return songInfor.getMediaUrl();
	}
	
	private UIManager mUimanager = null;
	public void setUiManager(UIManager mUimanager)
	{
		this.mUimanager = mUimanager;
	}
	private void seekCurPos()
	{
		int seekPos = LocalDataEntity.newInstance(mContext).getSeekPos();
		if(seekPos > 0)
		{
			long curUpdateTime = LocalDataEntity.newInstance(mContext).getCurLastUpdateTime();
			long curSystime = 0;
			int delayTime = 0;
			if(mUimanager != null)
				curSystime = mUimanager.getCurTime();
			if(curSystime > 0 && curUpdateTime > 0)
			{
				delayTime = (int) (curSystime - curUpdateTime);
			}
			
			int duration = mMediaPlayer.getDuration();
			if(seekPos <= duration)
			{
				int loadTime = (int) (System.currentTimeMillis() - loadStartTime);
				
				int tempSeek = seekPos + delayTime + loadTime;
				if(tempSeek > 0 && tempSeek < duration)
					mMediaPlayer.seekTo(tempSeek);
				else if(tempSeek == duration)
				{
					mMediaPlayer.seekTo(duration - 1000);
				}
				else
					mMediaPlayer.seekTo(seekPos);
			}
			
			LocalDataEntity.newInstance(mContext).setSeekPos(0);
		}
	}
	
	private boolean isUrlValiable(String url)
	{
		boolean value = false;
		HttpURLConnection conn;
		try
		{
			conn = (HttpURLConnection)new URL(url).openConnection();
			int code=conn.getResponseCode();
			if(code!=200)
			{
				value=false;
			}
			else
			{
				value=true;
			}
		} 
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		surface = new Surface(arg0);
		mMediaPlayer.setSurface(surface);
		
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureDestroyed");
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureSizeChanged");
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		Log.d("", "onSurfaceTextureUpdated");
	}
}
