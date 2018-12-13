package com.znt.diange.dms.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import com.znt.diange.dms.media.MediaStoreCenter.SourceType;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.utils.CommonLog;
import com.znt.utils.LogFactory;

public class MediaScannerCenter 
{

	private static final CommonLog log = LogFactory.createLog();

	public static final int AUDIO_TYPE = 0;
	public static final int VIDEO_TYPE = 1;
	public static final int IMAGE_TYPE = 2;

	private SourceType sourceType = SourceType.Phone;
	
	String AUDIO_PATH = MediaStore.Audio.AudioColumns.DATA;
	String AUDIO_DISPLAYHNAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
	String AUDIO_DURATION = MediaStore.Audio.AudioColumns.DURATION;
	String AUDIO_COLUMN_STRS[] = {AUDIO_PATH, AUDIO_DISPLAYHNAME, AUDIO_DURATION};
	
	private static  MediaScannerCenter mInstance;
	private Context mContext;
	
	private ScanMediaThread mediaThread;
	private ILocalMusicScanListener iLocalMusicScanListener = null;
	
	private MediaScannerCenter(Context context, SourceType sourceType) 
	{
		mContext = context;
		this.sourceType = sourceType;
		
		initData();
	}

	public static synchronized MediaScannerCenter getInstance(Context context, SourceType sourceType)
	{
		if (mInstance == null)
		{
			mInstance  = new MediaScannerCenter(context, sourceType);
		}
		return mInstance;
	}
	
	public void setOnLocalMusicScanListener(ILocalMusicScanListener iLocalMusicScanListener)
	{
		this.iLocalMusicScanListener = iLocalMusicScanListener;
	}

	private void initData(){
		
	}
	
	public synchronized boolean startScanThread(IMediaScanListener listener)
	{
		if (mediaThread == null || !mediaThread.isAlive())
		{
			mediaThread = new ScanMediaThread(listener);
			mediaThread.start();
		}
		
		return true;
	}
	
	public synchronized void stopScanThread()
	{
		if (mediaThread != null)
		{
			if (mediaThread.isAlive())
			{
				mediaThread.exit();
			}
			mediaThread = null;
		}
	}
	
	public synchronized boolean isThreadOver()
	{
		if (mediaThread != null && mediaThread.isAlive())
		{
			return false;
		}
		
		return true;
	}
	
	private  boolean scanMusic(IMediaScanListener listener, ICancelScanMedia cancelObser) throws Exception 
	{
		
		Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				AUDIO_COLUMN_STRS, 
				null, 
				null,
				AUDIO_DISPLAYHNAME);				

		if (cursor != null)
		{
			int count = cursor.getCount();
			if (count != 0)
			{
				int _name_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
	     		int _dir_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	     		//int _duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
	    		if (cursor.moveToFirst())
	    		{  
	         		do 
	         		{ 
	         			if (cancelObser.ifCancel())
	         			{
	         				return false;
	         			}
	         			String srcpath = cursor.getString(_dir_index);
	         			String name = cursor.getString(_name_index);
	         			//String duration = cursor.getString(_duration);
	         			
	         			File file = new File(srcpath);
	         			if(file.length() > 1024 * 1024)
	         			{
	         				MusicManager.getInstance().addMusic(file, sourceType);
	         				listener.mediaScan(AUDIO_TYPE, srcpath, name);
	         			}
	         		} while (cursor.moveToNext());  
	         	}  			
			}		
			cursor.close();
			return true;
		}

		return false;
	}
	
	/**
	* @Description: MP3鏂囦欢
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	private boolean isMusic(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp3") || 
			path.toLowerCase().endsWith(".wav") ||
			//path.toLowerCase().endsWith(".pcm") ||
			path.toLowerCase().endsWith(".wma") ||
			path.toLowerCase().endsWith(".flac") ||
			path.toLowerCase().endsWith(".aac") ||
			path.toLowerCase().endsWith(".ape")) 
		{
			return true;
		}
		return false;
	}
	
	private boolean isTempFile(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".temp")) 
		{
			return true;
		}
		return false;
	}
	
	public boolean isVideo(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp4") || 
				path.toLowerCase().endsWith(".264") ||
				path.toLowerCase().endsWith(".3gp") ||
				path.toLowerCase().endsWith(".avi") ||
				path.toLowerCase().endsWith(".wmv") ||
				path.toLowerCase().endsWith(".263") ||
				path.toLowerCase().endsWith(".h264") ||
				path.toLowerCase().endsWith(".rmvb") ||
				path.toLowerCase().endsWith(".mts") ||
				path.toLowerCase().endsWith(".mkv") ||
				path.toLowerCase().endsWith(".flv"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 鑾峰彇鐩綍涓嬬殑鏂囦欢(鏈湴鏂囦欢鎵弿鏂瑰紡)
	* @param @param fileList  鑾峰彇鐨勬枃浠跺垪琛�
	* @param @param path   鐩綍璺緞
	* @return void 
	* @throws
	 */
	private void getMusicFromPath(String path, final IMediaScanListener listener, 
			final ICancelScanMedia cancelObser)
	{
		if(path == null || path.length() == 0)
			return ;
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				// TODO Auto-generated method stub
				if (cancelObser.ifCancel())
				{
     				return true;
     			}
				
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanDoing();
				
				if(file.isDirectory() && !file.isHidden() && file.canRead())
				{
					getMusicFromPath(file.getAbsolutePath(), listener, cancelObser);
					return false;
				}
				
				/*if(file.length() < 1024 * 1024)//杩囨护灏忎簬1M鐨勬枃浠�
					return false;*/
				String path = file.getAbsolutePath();
				if(isMusic(path) || isVideo(path) || isTempFile(path))
				{
					MusicManager.getInstance().addMusic(file, sourceType);
					listener.mediaScan(AUDIO_TYPE, path, getNameFromPath(path));
					
					SongInfor infor = new SongInfor();
					infor.setMediaUrl(path);
					infor.setMediaName(getNameFromPath(path));
					//infor.setMediaSize(file.length());
					DBManager.INSTANCE.insertSongRecord(infor, file.lastModified());
				}
				return false;
			}
		});
	}
	private String getNameFromPath(String path)
	{
		String name = "";
		if(path.contains("/"))
			name = path.substring(path.lastIndexOf("/"));
		
		return name;
	}
	
	private void getMusicByScan(IMediaScanListener listener, ICancelScanMedia cancelObser)
	{
		DBManager.INSTANCE.filterInvalidFile();
		
		StorageList storageList = new StorageList(mContext);
	    String[] strings = storageList.getVolumePaths();
		
		//List<String> dirList = getStorageDirectoriesArrayList();
		int size = strings.length;
		for(int i=0;i<size;i++)
		{
			getMusicFromPath(strings[i], listener, cancelObser);
		}
	}
	
	public class ScanMediaThread extends Thread implements ICancelScanMedia
	{
		
		IMediaScanListener mListener;
		boolean exitFlag = false;
		
		public ScanMediaThread(IMediaScanListener listener)
		{
			mListener = listener;
		}

		public void exit()
		{
			exitFlag = true;
		}
		
		@Override
		public void run() 
		{

			try 
			{
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanStart();
				
				MusicManager.getInstance().clearMusic();
				//if(getAndroidOSVersion() >= 19)//4.4浠ヤ笂鐗堟湰锛屾墜鏈哄瓨鍌ㄤ笉鑳芥壂鎻忥紝鍙兘閫氳繃濯掍綋搴撹幏寰�
				//scanMusic(mListener, this);//鍏堜粠鏁版嵁搴撲腑璇诲彇
				getMusicByScan(mListener, this);//鎵弿鏂瑰紡锛岄槻姝㈡紡鎺夌殑
				
				if(iLocalMusicScanListener != null)
					iLocalMusicScanListener.onScanFinish();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			super.run();
		}

		@Override
		public boolean ifCancel() 
		{
			return exitFlag;
		}	
	}
	
	public static int getAndroidOSVersion()  
    {  
         int osVersion;  
         try  
         {  
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);  
         }  
         catch (NumberFormatException e)  
         {  
            osVersion = 0;  
         }  
           
         return osVersion;  
   }  
	
	public  interface ICancelScanMedia
	{
		public boolean ifCancel();
	}
	
	
	
	/**
	* @Description: 鑾峰彇鎵�鏈夌殑瀛樺偍璁惧鍒楄〃
	* @param @return   
	* @return ArrayList<String> 
	* @throws
	 */
	private ArrayList<String> getStorageDirectoriesArrayList()
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
	
	public interface ILocalMusicScanListener
	{
		public void onScanStart();
		public void onScanDoing();
		public void onScanFinish();
	}
	
}
