package com.znt.speaker.p;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.R;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.player.MusicPlayEngineImpl;
import com.znt.speaker.player.PlayErrorCheck;
import com.znt.speaker.player.PlayerEngineListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayPresenter implements OnBufferingUpdateListener, OnSeekCompleteListener,OnErrorListener,MusicPlayEngineImpl.OnImagePlayListener
{
	private Activity activity = null;
	private UIManager mUIManager = null;
	public MusicPlayEngineImpl mPlayerEngineImpl;
	private MusicPlayEngineListener mPlayEngineListener;
	
	
	private SongInfor songInforPlay = null;
	private List<SongInfor> playList = new ArrayList<SongInfor>();

	@Override
	public void onImagePlay(SongInfor tempInfo) {
		if(!isFileExsit(tempInfo))
		{
			if(mOnMediaPlayCallBack != null)
				mOnMediaPlayCallBack.onMediaPlayStart(tempInfo);
		}
	}

	public interface OnMediaPlayCallBack
	{
		void onMediaPlayStart(SongInfor songInfor);
	}
	
	private OnMediaPlayCallBack mOnMediaPlayCallBack = null;
	
	public MusicPlayPresenter(Activity activity, UIManager mUIManager, OnMediaPlayCallBack mOnMediaPlayCallBack)
	{
		this.activity = activity;
		this.mUIManager = mUIManager;
		this.mOnMediaPlayCallBack = mOnMediaPlayCallBack;
		init();
	}
	
	private void init()
	{
		mPlayerEngineImpl = new MusicPlayEngineImpl(activity);
		mPlayerEngineImpl.setOnBuffUpdateListener(this);
		mPlayerEngineImpl.setOnSeekCompleteListener(this);
		mPlayerEngineImpl.setOnImagePlayListener(this);
		mPlayEngineListener = new MusicPlayEngineListener();
		mPlayerEngineImpl.setPlayerListener(mPlayEngineListener);
		mPlayerEngineImpl.setUiManager(mUIManager);
		
	}
	
	public MusicPlayEngineImpl getMusicPlayEngineImpl()
	{
		return mPlayerEngineImpl;
	}
	
	public void closePlayer()
	{
		mPlayerEngineImpl.exit();
	}
	
	public void removeMusic(String url)
	{
		if(TextUtils.isEmpty(url))
			return;
		int size = playList.size();
		for(int i=0;i<size;i++)
		{
			SongInfor tempInfor = playList.get(i);
			String curUrl = tempInfor.getMediaUrl();
			if(!TextUtils.isEmpty(curUrl) && curUrl.equals(url))
			{
				playList.remove(i);
				break;
			}
		}
	}
	
	/*public void refreshCurPos()
	{
		if(mPlayerEngineImpl != null && mPlayerEngineImpl.isPlaying())
		{
			int pos = mPlayerEngineImpl.getCurPosition();
			mUIManager.setSeekbarProgress(pos);
			
			if(mUIManager.getMaxProgress() == 0)
			{
				int duration = mPlayerEngineImpl.getDuration();
				mUIManager.setSeekbarMax(duration);
				mUIManager.setTotalTime(duration);
			}
		}
	}*/
	
	public int getCurPosition()
	{
		if(mPlayerEngineImpl == null)
			return 0;
		return mPlayerEngineImpl.getCurPosition();
	}
	public int getDuration()
	{
		if(mPlayerEngineImpl == null)
			return 0;
		return mPlayerEngineImpl.getDuration();
	}
	public SongInfor getCurPlaySong()
	{
		if(mPlayerEngineImpl == null)
			return null;
		return mPlayerEngineImpl.getSongInfor();
	}
	public String getCurPlaySongName()
	{
		SongInfor tempInfor = getCurPlaySong();
		if(tempInfor == null)
			return "";
		if(mPlayerEngineImpl.isLoading())
			return activity.getResources().getString(R.string.media_play_loading);
		if(!TextUtils.isEmpty(mPlayerEngineImpl.getError()))
			return mPlayerEngineImpl.getError();
		if(!mPlayerEngineImpl.isPlaying() && !mUIManager.getRatioImageView().isShown())
			return "";
		return tempInfor.getMediaName();
	}
	public int getCurPlaySongType()
	{
		SongInfor tempInfor = getCurPlaySong();
		if(tempInfor == null)
			return 0;
		return tempInfor.getMusicPlayType();
	}
	
	private int checkDelayCount = 0;
	public void checkDelay()
	{
		if(checkDelayCount <= 18)
		{
			checkDelayCount ++;
			return;
		}
		else
			checkDelayCount = 0;
		
		if(mPlayerEngineImpl == null)
			return;
		
		int pos = getCurPosition();
		boolean ret = isDelay(pos);
		if (ret)
		{
			mPlayerEngineImpl.setLoaded();
			if(playList.size() > 0)
			{
				startPlaySong();
			}
		}
		setPos(pos);
	}
	
	private int lastPos = 0;
	public void setPos(int pos)
	{
		lastPos = pos;
	}
	
	public boolean isDelay(int pos)
	{
		if (pos != lastPos)
		{
			return false;
		}
/*		if (pos == 0 || pos != lastPos)
		{
			return false;
		}
*/		
		return true;
	}
	
	private void doPrepare()
	{
		//String mediaName = itemInfo.getMediaName();
		playErrorCheck.clear();
		playErrorCheck.setStart(System.currentTimeMillis(), songInforPlay);
	}
	
	public void playMusicWhenInitFinish()
	{
		if(playList.size() > 0 && mPlayerEngineImpl != null && !mPlayerEngineImpl.isPlaying())
		{
			startPlaySong();
		}
	}
	public void addPlayList(List<SongInfor> tempList)
	{
		if(tempList != null)
		{
			if(playList == null)
				playList = new ArrayList<SongInfor>();
			playList.clear();
			playList.addAll(tempList);
		}
	}
	public void clearPlayList()
	{
		if(playList != null)
			playList.clear();
	}
	public boolean isPlayListNone()
	{
		return playList == null || playList.size() == 0;
	}
	public int getPlayListSize()
	{
		if(playList == null)
			return 0;
		return playList.size();
	}
	public boolean isPlayPushMusic()
	{
		if(songInforPlay != null && songInforPlay.isPushMusic())
			return true;
		return false;
	}
	
	public void handleProcessWhenNonePlayTime()
	{
		if(!isPlayPushMusic())
		{
			if(mPlayerEngineImpl != null && mPlayerEngineImpl.isPlaying())
			{
				clearPlayList();
				mPlayerEngineImpl.stop();
				mUIManager.showPrepareLoadView(true);
			}
		}
	}
	
	public void handlerLocalPlayListDo(List<SongInfor> tempList)
	{
		if(tempList != null)
		{
			addPlayList(tempList);
			LocalDataEntity.newInstance(activity).setMusicIndex(0);
			if(playList.size() > 0 && !mPlayerEngineImpl.isPlaying())
				startPlaySong();
			if(playList.size() == 0 && mPlayerEngineImpl.isPlaying())
				mPlayerEngineImpl.stop();
		}
	}
	public synchronized void startPlaySong()
	{
		if(isPlayEnable())
		{
			Log.e("", "**************************本地-->" + DBManager.INSTANCE.getSongCount());
			List<SongInfor> songList = DBManager.INSTANCE.getSongList(0, 200);
			if(songList.size() > 0)
			{
				startPlayDiangboMusic(songList);
			}
			else if(!mPlayerEngineImpl.isPlaying()) //如果当前没有歌曲在播放，就播放计划歌曲
			{
				startPlaySpeakerMusic(playList);
			}
		}
	}
	public synchronized void startPlaySongForce()
	{
		startPlaySpeakerMusic(playList);
	}
	private boolean isPlayEnable()
	{
		if(mPlayerEngineImpl == null || mPlayerEngineImpl.isLoading())
			return false;
		
		if(mPlayerEngineImpl.isPause())//如果是暂停状态，就不接受任何播放
			return false;
		if(songInforPlay != null && mPlayerEngineImpl.isPlaying())//如果正在播放
		{
			if(songInforPlay.isPushMusic())//如果当前播放的歌曲为点播的，则要等待该歌曲播放完成
				return false;
		}
		return true;
	}
	private void startPlayDiangboMusic(List<SongInfor> playList)
	{
		songInforPlay = playList.get(0);
		DBManager.INSTANCE.deleteSongById(songInforPlay);

		playMedia();
	}
	private void startPlaySpeakerMusic(List<SongInfor> playList)
	{
		try 
		{
			if(playList != null && playList.size() > 0)
			{
				
				songInforPlay = getPlaySongInfor();
				if(songInforPlay == null)
					songInforPlay = getPlaySongInfor();
				if(songInforPlay == null)
					songInforPlay = getPlaySongInfor();
				if(songInforPlay == null)
					songInforPlay = getPlaySongInfor();
				if(songInforPlay != null)
				{
					playMedia();
				}
			}
			else
			{
				songInforPlay = null;
				mPlayerEngineImpl.resetPlayInfor();
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			Log.e("", e.getMessage());
		}
	}

	private void playMedia()
	{
		mPlayerEngineImpl.playMedia(songInforPlay);
		updatePlayView();
	}
	
	private SongInfor getPlaySongInfor()
	{
		int musicIndex = LocalDataEntity.newInstance(activity).getMusicIndex();
		String playType = LocalDataEntity.newInstance(activity).getDownloadFlag();
		if(playType.equals("0"))//顺序循环
		{
			
		}
		else //随机循环
		{
			try 
			{
				Random  random = new Random();
				musicIndex = random.nextInt(playList.size() + 1);//getLocalData().getMusicIndex();
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				musicIndex = LocalDataEntity.newInstance(activity).getMusicIndex();
			}
		}
		
		if(musicIndex >= playList.size() || musicIndex < 0)
			musicIndex = 0;
		
		songInforPlay = playList.get(musicIndex);
		
		musicIndex ++;
		LocalDataEntity.newInstance(activity).setMusicIndex(musicIndex);
		return songInforPlay;
	}
	
	private void updatePlayView()
	{
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String indexString = LocalDataEntity.newInstance(activity).getMusicIndex() +" / " + playList.size();
				
				mUIManager.updateMediaInfoView(mPlayerEngineImpl.getSongInfor(), indexString);
				mUIManager.showControlView(true);
			}
		});
	}
	
	private boolean isFileExsit(SongInfor songInfor)
	{
		String url = songInfor.getMediaUrl();
		File file1 = new File(getMusicLocalPath(url));
		File file2 = new File(getMusicLocalPath(url) + ".temp");
		return file1.exists() && file2.exists();
	}
	private String getMusicLocalPath(String url)
	{
		String fileName = "";
		if(url.contains("."))
			fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
		
		return filePath;
	}
	
	/*private boolean isUpdateCurPosRunning = false;
	private void updateCurPos(SongInfor itemInfo)
	{
		if(!isUpdateCurPosRunning)
		{
			int index = LocalDataEntity.newInstance(activity).getMusicIndex();
			if(index > 0)
				index = index - 1;
			httpPresenter.updateCurPos(index, itemInfo.getMediaId());
		}
	}*/
	
	private PlayErrorCheck playErrorCheck = new PlayErrorCheck();
	private class MusicPlayEngineListener implements PlayerEngineListener
	{
		@Override
		public void onTrackPlay(final SongInfor itemInfo) 
		{
			mUIManager.showPlay(false);
			mUIManager.showControlView(true);
			
			if(!isFileExsit(songInforPlay))
			{
				if(mOnMediaPlayCallBack != null)
					mOnMediaPlayCallBack.onMediaPlayStart(songInforPlay);
			}
		}

		@Override
		public void onTrackStop(final SongInfor itemInfo) 
		{
			mUIManager.showPlay(true);
			String indexString = LocalDataEntity.newInstance(activity).getMusicIndex() +" / " + playList.size();
			mUIManager.updateMediaInfoView(itemInfo, indexString);
			mUIManager.isSeekComplete = true;
		}

		@Override
		public void onTrackPause(SongInfor itemInfo) 
		{
			mUIManager.showPlay(true);
		}

		@Override
		public void onTrackPrepareSync(final SongInfor itemInfo) 
		{
			doPrepare();
		}

		@Override
		public void onTrackPrepareComplete(SongInfor itemInfo) 
		{
			int duration = mPlayerEngineImpl.getDuration();
			
			if(duration > 0)
			{
				mUIManager.setSeekbarMax(duration);
				mUIManager.setTotalTime(duration);
			}
		}
		
		@Override
		public void onTrackStreamError(SongInfor itemInfo) 
		{
			Log.e("", itemInfo.getMediaName());
		}

		@Override
		public void onTrackPlayComplete(SongInfor itemInfo) 
		{
			mPlayerEngineImpl.stop();
			
			String curPlayUrl = "";
			if(songInforPlay != null)
				curPlayUrl = songInforPlay.getMediaUrl();
			if(itemInfo == null || itemInfo.getMediaUrl() .equals(curPlayUrl))
			{
				startPlaySong();
			}
		}
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) 
	{
		// TODO Auto-generated method stub
		mUIManager.isSeekComplete = true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) 
	{
		// TODO Auto-generated method stub
		int tempDuration = mPlayerEngineImpl.getDuration();
		int time = tempDuration * percent / 100;
		mUIManager.setSeekbarSecondProgress(time);
	}
}
