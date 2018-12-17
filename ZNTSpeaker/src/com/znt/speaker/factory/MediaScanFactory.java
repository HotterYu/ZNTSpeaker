package com.znt.speaker.factory;

import android.content.Context;

import com.znt.diange.dms.media.MediaScannerCenter.ILocalMusicScanListener;
import com.znt.diange.dms.media.MediaStoreCenter;
import com.znt.diange.dms.media.MediaStoreCenter.SourceType;
import com.znt.speaker.db.DBManager;
import com.znt.utils.LogFactory;

public class MediaScanFactory 
{
	private static MediaScanFactory INSTANCE = null;
	
	public static MediaScanFactory getInstance(Context activity)
	{
		if(INSTANCE == null)
			INSTANCE = new MediaScanFactory(activity);
		return INSTANCE;
	}
	
	private Context activity = null;
	private MediaScanFactory(Context activity)
	{
		this.activity = activity;
	}
	
	private MediaStoreCenter mMediaStoreCenter = null;
	public void scanLocalMedias()
	{
		if(mMediaStoreCenter == null)
		{
			mMediaStoreCenter = MediaStoreCenter.getInstance(activity, SourceType.Speaker);
			mMediaStoreCenter.setOnLocalMusicScanListener(new ILocalMusicScanListener() 
			{
				
				@Override
				public void onScanStart() 
				{
					// TODO Auto-generated method stub

				}
				
				@Override
				public void onScanFinish() 
				{
					// TODO Auto-generated method stub
					DBManager.INSTANCE.checkAndReleaseSpace(0);
				}
				
				@Override
				public void onScanDoing()
				{
					// TODO Auto-generated method stub
					
				}
			});
		}
		//if(scanMusic && !isMusicScaning)
		{
			mMediaStoreCenter.clearAllData();
			mMediaStoreCenter.clearWebFolder();
			mMediaStoreCenter.createWebFolder();
			mMediaStoreCenter.doScanMedia();
		}
	}
}
