package com.znt.speaker.db; 

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.znt.diange.mina.entity.CurPlanSubInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.diange.mina.entity.WifiInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.MediaScanFactory;
import com.znt.utils.DateUtils;
import com.znt.utils.SystemUtils;

/** 
 * @ClassName: DBManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-14 涓婂崍10:25:36  
 */
public class DBManager extends MyDbHelper
{

	public static DBManager INSTANCE = null;
	private Context context = null;
	
	private volatile boolean isGetAllPlanMusicRunning = false;
	
	public static final int FILE_TYPE_ALL = 0;
	public static final int FILE_TYPE_LOCAL = 1;
	public static final int FILE_TYPE_ONLIE = 2;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param c 
	*/
	public DBManager(Context c)
	{
		super(c);
		this.context = c.getApplicationContext();
		// TODO Auto-generated constructor stub
	}

	public static void init(Context c)
	{
		if(INSTANCE == null)
		{
			synchronized (DBManager.class)
			{
				if(INSTANCE == null)
					INSTANCE = new DBManager(c);	
			}
		}
	}
	
	/**
    * @Description: 鎻掑叆闊充箰
    * @param infor   
    * @return void 
    * @throws
     */
    public synchronized long insertSong(SongInfor infor)
    {
    	if(infor == null)
    		return -1;
    	
    	//濡傛灉鐐规挱闃熷垪涓湁璇ユ瓕鏇插氨鏇存柊璇ユ瓕鏇�
    	/*if(isSongExist(infor))
    	{
    		updateSong(infor);
    		return -1;
    	}*/
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	/*if(TextUtils.isEmpty(artist) 
    			&& infor.getMediaType() == SongInfor.MEDIA_TYPE_LOCAL)//鏈湴闊充箰鍒嗙姝屾墜鍜屾瓕鏇插悕
    	{
    		if(musicName.contains("-"))
    			artist = musicName.substring(0, musicName.indexOf("-"));
    	}*/
    		
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getMediaId());
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	
    	values.put("play_time", System.currentTimeMillis() + "");
    	values.put("trand_id", infor.getMediaId());
    	values.put("modify_time", System.currentTimeMillis());
		return insert(values, TBL_SONG_LIST);
    }
    public synchronized long insertSongAdmin(SongInfor infor)
    {
    	if(infor == null)
    		return -1;
    	
    	//濡傛灉鐐规挱闃熷垪涓湁璇ユ瓕鏇插氨鏇存柊璇ユ瓕鏇�
    	if(isSongExist(infor))
    	{
    		updateSong(infor);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	/*if(TextUtils.isEmpty(artist) 
    			&& infor.getMediaType() == SongInfor.MEDIA_TYPE_LOCAL)//鏈湴闊充箰鍒嗙姝屾墜鍜屾瓕鏇插悕
    	{
    		if(musicName.contains("-"))
    			artist = musicName.substring(0, musicName.indexOf("-"));
    	}*/
    	
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	/*if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getAlbumUrl());*/
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	
    	values.put("play_time", System.currentTimeMillis() + "");
    	values.put("trand_id", infor.getMediaId());
    	
    	return insert(values, TBL_SONG_LIST_ADMIN);
    }
    
    public synchronized long insertSongRecord(SongInfor infor, long modify_time)
    {
    	if(infor == null)
    		return -1;
    	if(modify_time <= 0)
    		modify_time = System.currentTimeMillis();
    	if(isRecorExist(infor))
    	{
    		updateSongRecord(infor, modify_time);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	String time = DateUtils.getStringTime(modify_time);
    	values.put("modify_time", modify_time);
    	
    	return insert(values, TBL_SONG_RECORD);
    }
    /**
     * 鏇存柊闊充箰淇℃伅
     * @param infor
     * @return
     */
    public synchronized int updateSongRecord(SongInfor infor, long modify_time)
    {
    	if(infor == null)
   		 return -1;
    	ContentValues values = new ContentValues();
    	String musicName = infor.getMediaName();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(modify_time <= 0)
    		modify_time = System.currentTimeMillis();
    	values.put("modify_time", modify_time);
    	
    	return edit(TBL_SONG_RECORD, values);
    }
    
    /**
     * @Description: 鍒ゆ柇闊充箰鏄惁瀛樺湪
     * @param @param music
     * @param @return   
     * @return boolean 
     * @throws
      */
     public synchronized boolean isRecorExist(SongInfor music)
     {
     	if(music == null)
    		 return false;
     	boolean  result = false;
     	
     	Cursor cur = query(TBL_SONG_RECORD);
      	if(cur != null && cur.getCount() > 0)
      	{
      		while(cur.moveToNext())
      		{
      			String music_url = cur.getString(cur.getColumnIndex("music_url"));
      			if(music_url == null)
  				{
      				result = false;
      				break;
  				}
      			if(music.getMediaUrl().equals(music_url))
      			{
      				result = true;
      				break;
      			}
      		}
      	}
      	if(cur != null )
      		cur.close();
     	return result;
     }
    
    /**
     * 鏇存柊闊充箰淇℃伅
     * @param infor
     * @return
     */
    public synchronized int updateSong(SongInfor infor)
    {
    	if(infor == null)
   		 return -1;
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	String artist = infor.getArtist();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(!TextUtils.isEmpty(infor.getAlbumName()))
    		values.put("music_album", infor.getAlbumName());
    	if(!TextUtils.isEmpty(infor.getAlbumUrl()))
    		values.put("music_album_url", infor.getMediaId());
    	if(!TextUtils.isEmpty(artist))
    		values.put("music_artist", artist);
    	//values.put("play_time", System.currentTimeMillis());
    	return edit(TBL_SONG_LIST, values);
    }
    
    /**
    * @Description: 鍒ゆ柇闊充箰鏄惁瀛樺湪
    * @param @param music
    * @param @return   
    * @return boolean 
    * @throws
     */
    public synchronized boolean isSongExist(SongInfor music)
    {
    	if(music == null)
   		 return false;
    	boolean  result = false;
    	
    	Cursor cur = query(TBL_SONG_LIST);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			//鏍规嵁鐢ㄦ埛id瀵规瘮鐨勶紝鍚屼竴涓敤鎴峰彧鑳界偣鎾竴棣栨瓕鏇�
     			/*String user_id = cur.getString(cur.getColumnIndex("user_id"));
     			int play_state = cur.getInt(cur.getColumnIndex("play_state"));
     			if(user_id != null && user_id.equals(music.getUserInfor().getUserId())
     					&& play_state == 0)
     			{
     				result = true;
     				break;
     			}*/
     			String musicUrl = cur.getString(cur.getColumnIndex("music_url"));
     			if(musicUrl != null && musicUrl.equals(music.getMediaUrl()))
     			{
     				result = true;
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
    	return result;
    }
    public synchronized boolean isSongExist(SongInfor music, String key)
    {
    	if(music == null)
      		 return false;
    	boolean  result = false;
    	Cursor cur = query(TBL_SONG_LIST);
    	if(cur != null && cur.getCount() > 0)
    	{
    		while(cur.moveToNext())
    		{
    			String music_name = cur.getString(cur.getColumnIndex(key));
    			if(music_name != null && music_name.trim().equals(music.getMediaName().trim()))
    			{
    				result = true;
    				//updateMusic(music);
    				break;
    			}
    		}
    	}
    	if(cur != null )
     		cur.close();
    	return result;
    }
    
    /**
     * @Description: 鑾峰彇闊充箰鍒楄〃
     * @param @return   
     * @return List<GoodsInfor> 
     * @throws
      */
     public synchronized List<SongInfor> getSongList(int pageNum, int pageSize)
     {
     	List<SongInfor> tempList = new ArrayList<SongInfor>();
     	Cursor cur = query(TBL_SONG_LIST);
     	if(cur == null || cur.getCount() == 0)
     		 return tempList;
     	if(cur != null)
     	{
     		cur.moveToPosition(pageNum * pageSize);
     		for(int i = pageNum * pageSize;i < (pageNum + 1) * pageSize;i++)
     		{
     			if(cur.moveToPosition(i))
     			{
     				SongInfor tempInfor = new SongInfor();
     				String music_name = cur.getString(cur.getColumnIndex("music_name"));
     				String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
     				//String music_album = cur.getString(cur.getColumnIndex("music_album"));
     				String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
     				int play_state = cur.getInt(cur.getColumnIndex("play_state"));
     				String play_time = cur.getString(cur.getColumnIndex("play_time"));
     				int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
     				String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
     				String user_id = cur.getString(cur.getColumnIndex("user_id"));
     				String user_name = cur.getString(cur.getColumnIndex("user_name"));
     				String play_message = cur.getString(cur.getColumnIndex("play_message"));
     				//String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
     				String music_url = cur.getString(cur.getColumnIndex("music_url"));
     				long modify_time = cur.getLong(cur.getColumnIndex("modify_time"));
     				
     				tempInfor.setModify_time(modify_time);
     				tempInfor.setMediaName(music_name);
     				tempInfor.setArtist(music_artist);
     				//tempInfor.setAlbumName(music_album);
     				tempInfor.setMediaId(trand_id);
     				UserInfor userInfor = new UserInfor();
     				userInfor.setUserId(user_id);
     				userInfor.setUserName(user_name);
     				tempInfor.setMediaUrl(music_url);
     				tempInfor.setMusicPlayType(SongInfor.MUSIC_PLAY_TYPE_PUSH);;
         			tempList.add(tempInfor);
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return tempList;
     }
     
     /**
      * @Description: 鑾峰彇闊充箰鎾斁璁板綍
      * @param @return   
      * @return List<GoodsInfor> 
      * @throws
      */
     public synchronized List<SongInfor> getSongRecord(int pageNum, int pageSize)
     {
    	 List<SongInfor> tempList = new ArrayList<SongInfor>();
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null || cur.getCount() == 0)
    		 return tempList;
    	 if(cur != null)
    	 {
    		 cur.moveToPosition(pageNum * pageSize);
    		 for(int i = pageNum * pageSize;i < (pageNum + 1) * pageSize;i++)
    		 {
    			 if(cur.moveToPosition(i))
    			 {
    				 SongInfor tempInfor = new SongInfor();
    				 String music_name = cur.getString(cur.getColumnIndex("music_name"));
    				 String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
    				 long modifyTime = cur.getLong(cur.getColumnIndex("modify_time"));
    				 int play_state = cur.getInt(cur.getColumnIndex("play_state"));
    				 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    				 if(!TextUtils.isEmpty(music_url))
    				 {
    					 File file = new File(music_url);
        				 if(file.exists())
        				 {
        					 tempInfor.setMediaName(music_name);
            				 tempInfor.setArtist(music_artist);
            				 tempInfor.setMediaUrl(music_url);
            				 tempInfor.setModify_time(modifyTime);
            				 tempList.add(tempInfor);
        				 }
        				 else
        					 deleteSongRecordByUrl(music_url);
    				 }
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return tempList;
     }
     
     /**
      * 杩囨护鏈湴鏃犵敤鐨勬枃浠�
      */
     public synchronized void filterInvalidFile()
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null || cur.getCount() == 0)
    		 return ;
    	 if(cur != null)
    	 {
    		 while(cur.moveToNext())
			 {
				 String music_url = cur.getString(cur.getColumnIndex("music_url"));
				 if(!TextUtils.isEmpty(music_url))
				 {
					 File file = new File(music_url);
					 if(!file.exists())
					 {
						 deleteSongRecordByUrl(music_url);
					 }
				 }
			 }
    	 }
    	 if(cur != null )
    		 cur.close();
     }
     
     public synchronized SongInfor getSongByUser(String userId)
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 SongInfor tempInfor = new SongInfor();
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 int play_state = cur.getInt(cur.getColumnIndex("play_state"));
    			 if(play_state == 1)//褰撳墠姝ｅ湪鎾斁鐘舵��
    			 {
	      				String music_name = cur.getString(cur.getColumnIndex("music_name"));
	      				String music_artist = cur.getString(cur.getColumnIndex("music_artist"));
	      				String music_album = cur.getString(cur.getColumnIndex("music_album"));
	      				String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
	      				String play_time = cur.getString(cur.getColumnIndex("play_time"));
	      				int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
	      				String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
	      				String user_id = cur.getString(cur.getColumnIndex("user_id"));
	      				String user_name = cur.getString(cur.getColumnIndex("user_name"));
	      				String play_message = cur.getString(cur.getColumnIndex("play_message"));
	      				//String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
	      				String music_url = cur.getString(cur.getColumnIndex("music_url"));
	      				
	      				tempInfor.setMediaName(music_name);
	      				tempInfor.setArtist(music_artist);
	      				tempInfor.setAlbumName(music_album);
	      				tempInfor.setAlbumUrl(music_album_url);
	      				UserInfor userInfor = new UserInfor();
	      				userInfor.setUserId(user_id);
	      				userInfor.setUserName(user_name);
	      				tempInfor.setMediaUrl(music_url);
	      				break;
    			 }
    		 }
    	 }
    	 if(cur != null )
      		cur.close();
    	 return tempInfor;
     }
     
     public synchronized int getSongCount()
     {
    	 Cursor cur = query(TBL_SONG_LIST);
    	 if(cur == null)
    		 return 0;
    	 return cur.getCount();
     }
     
     public synchronized int getRecordCount()
     {
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 if(cur == null)
    		 return 0;
    	 return cur.getCount();
     }
     
     public synchronized void deleteAllSong()
     {
    	 /*Cursor cur = query(TBL_SONG_LIST);
      	if(cur != null && cur.getCount() > 0)
      	{
      		while(cur.moveToNext())
      		{
      			String music_id = cur.getString(cur.getColumnIndex("music_url"));
      			delete("music_url", music_id, TBL_SONG_LIST);
      		}
      	}
      	if(cur != null )
     		cur.close();*/
    	 deleteAll(TBL_SONG_LIST);
     }
     public synchronized void deleteSongRecordByUrl(String music_url)
     {
    	 delete("music_url", music_url, TBL_SONG_RECORD);
     }
     
     public long minRemainSize = 1024 * 1024 * 100;
     public void checkAndReleaseSpace(long desFileSize)
     {
     	long localRemainSize = SystemUtils.getAvailableExternalMemorySize();
 		if(localRemainSize <= minRemainSize || (localRemainSize - minRemainSize) < desFileSize)
 		{
 			long deleteSize = 0;
 			if(desFileSize == 0)
 				deleteSize = minRemainSize - localRemainSize;
 			else
 				deleteSize = desFileSize - (localRemainSize - minRemainSize);
			deleteLastSongRecordBySize(deleteSize);
 		}
 		//deleteLastSongRecordBySize(1024*1024*100);
     }
     
     @SuppressWarnings("resource")
 	public synchronized void deleteLastSongRecordByIndex(int size)
      {
     	 Cursor cur = query(TBL_SONG_RECORD);
     	 int deleteCount = 0;
     	 
     	 if(cur.getCount() <= 0)
     	 {
     		MediaScanFactory.getInstance(context).scanLocalMedias();
    	 }
     	 else
     	 {
     		 if(cur != null && cur.getCount() > size)
         	 {
         		 while(cur.moveToNext())
         		 {
         			 //String music_name = cur.getString(cur.getColumnIndex("music_name"));
         			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
     				 
     				 File file = new File(music_url);
     				 if(file.exists())
     				 {
     					 if(file.delete())
     						 deleteSongRecordByUrl(music_url);
     				 }
     				 else
     					 deleteSongRecordByUrl(music_url);
     				 //int lC = cur.getCount();
     				 deleteCount ++;
     				 if(deleteCount >= size)
     					 break;
     				 else
     					 cur = query(TBL_SONG_RECORD);
         		 }
         	 }
     	 }
     	 if(cur != null )
     		 cur.close();
      }
     @SuppressWarnings("resource")
     public synchronized void deleteLastSongRecordBySize(long deleteSize)
     {
    	 
    	 /*List<SongInfor> tempList = getSongRecord(0, 1000);
    	 
    	 String time1 = DateUtils.getStringTime(tempList.get(0).geModify_time());
    	 String time2 = DateUtils.getStringTime(tempList.get(1).geModify_time());
    	 String time3 = DateUtils.getStringTime(tempList.get(2).geModify_time());
    	 String time4 = DateUtils.getStringTime(tempList.get(3).geModify_time());
    	 String time5 = DateUtils.getStringTime(tempList.get(4).geModify_time());
    	 String time6 = DateUtils.getStringTime(tempList.get(5).geModify_time());
    	 String time7 = DateUtils.getStringTime(tempList.get(6).geModify_time());
    	 String time8 = DateUtils.getStringTime(tempList.get(tempList.size() - 1).geModify_time());*/
    	 
    	 
    	 Cursor cur = query(TBL_SONG_RECORD);
    	 long deletedSize = 0;
    	 if(cur.getCount() <= 0)
    	 {
    		 MediaScanFactory.getInstance(context).scanLocalMedias();
    	 }
     	 else if(cur != null && deleteSize > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 //String music_name = cur.getString(cur.getColumnIndex("music_name"));
    			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    			 long modifyTime = cur.getLong(cur.getColumnIndex("modify_time"));
    			 String time = DateUtils.getStringTime(modifyTime);
    			 //delete("music_url", music_url, TBL_SONG_RECORD);
    			 File file = new File(music_url);
    			 if(file.exists())
    			 {
    				 deletedSize += file.length();
    				 if(file.delete())
    					 deleteSongRecordByUrl(music_url);
    				 if(deletedSize >= deleteSize)
    				 {
    					 Log.e("", "release space==>" + deletedSize / 1024 / 1024);
    					 break;
    				 }
    				 /*else
    					 cur = query(TBL_SONG_RECORD);*/
    			 }
    			 else
    				 deleteSongRecordByUrl(music_url);
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
     }
     public synchronized void deleteAllRecord()
     {
    	 /*Cursor cur = query(TBL_SONG_LIST);
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String music_id = cur.getString(cur.getColumnIndex("music_url"));
    			 delete("music_url", music_id, TBL_SONG_LIST);
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();*/
    	 deleteAll(TBL_SONG_RECORD);
     }

     public synchronized int deleteSongById(SongInfor infor)
     {
    	 if(infor == null)
    		 return -1;
    	 return delete("modify_time", infor.geModify_time() + "", TBL_SONG_LIST);
     }
     public synchronized int deleteSongByUrl(SongInfor infor)
     {
    	 if(infor == null)
    		 return -1;
    	 return delete("music_url", infor.getMediaUrl(), TBL_SONG_LIST);
     }
     public synchronized int deleteSongByUserId(String userId)
     {
    	 return delete("user_id", userId, TBL_SONG_LIST);
     }
     public synchronized int deleteSongByUrl(String music_url)
     {
   	  	 return delete("music_url", music_url, TBL_SONG_LIST);
     }
     
     /**************绠＄悊鍛樻搷浣�**************/
     public synchronized long insertAdmin(UserInfor infor)
     {
     	if(infor == null)
     		return -1;
     	
     	ContentValues values = new ContentValues();
     	
     	String name = infor.getUserName();
     	String id = infor.getUserId();
     	
     	if(!TextUtils.isEmpty(name))
     		values.put("user_name", name);
     	if(!TextUtils.isEmpty(id))
     		values.put("user_id", id);
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_ADMIN);
     }
     public synchronized boolean isAdminExist(String userId)
     {
     	boolean  result = false;
     	Cursor cur = query(TBL_ADMIN);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			String user_id = cur.getString(cur.getColumnIndex("user_id"));
     			if(user_id != null && user_id.trim().equals(userId.trim()))
     			{
     				result = true;
     				//updateMusic(music);
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return result;
     }
     
     /**************WIFI鎿嶄綔*****************/
     public synchronized long insertWifi(String wifiName, String wifiPwd)
     {
     	if(TextUtils.isEmpty(wifiName))
     		return -1;
     	
     	if(isWifiExist(wifiName, wifiPwd ))
     	{
     		//updateWifi(wifiName, wifiPwd);
     		return -1;
     	}
     	
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(wifiName))
     		values.put("wifi_name", wifiName);
     	values.put("wifi_pwd", wifiPwd);
     	values.put("modify_time", System.currentTimeMillis());
     	
     	return insert(values, TBL_WIFI);
     }
     
     public List<WifiInfor> getWifiList()
     {
    	 List<WifiInfor> wifiList = new ArrayList<WifiInfor>();
    	 
    	 Cursor cur = query(TBL_WIFI);
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			String wifi_pwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
      			WifiInfor tempInfor = new WifiInfor();
      			tempInfor.setWifiName(wifi_name);
      			tempInfor.setWifiPwd(wifi_pwd);
      			wifiList.add(tempInfor);
      		}
      	 }
      	if(cur != null )
     		cur.close();
    	 
    	 return wifiList;
     }
     
     public String getWifiPwdByName(String wifiName)
     {
    	 String wifiPwd = "";
      	 Cursor cur = queryNormal(TBL_WIFI);
      	 if(cur != null && cur.getCount() > 0)
      	 {
      		while(cur.moveToNext())
      		{
      			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
      			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()))
      			{
      				wifiPwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
      				break;
      			}
      		}
      	 }
      	if(cur != null )
     		cur.close();
      	 return wifiPwd;
     }
     public int getWifiCount()
     {
    	 Cursor cur = query(TBL_WIFI);
    	 return cur.getCount();
     }
     
     public synchronized boolean isWifiExist(String wifiName, String wifiPwd)
     {
     	boolean  result = false;
     	Cursor cur = query(TBL_WIFI);
     	if(cur != null && cur.getCount() > 0)
     	{
     		while(cur.moveToNext())
     		{
     			String wifi_name = cur.getString(cur.getColumnIndex("wifi_name"));
     			String wifi_pwd = cur.getString(cur.getColumnIndex("wifi_pwd"));
     			if(wifi_name != null && wifi_name.trim().equals(wifiName.trim()) && wifi_pwd != null && wifi_pwd.trim().equals(wifiPwd.trim()))
     			{
     				result = true;
     				//updateMusic(music);
     				break;
     			}
     		}
     	}
     	if(cur != null )
     		cur.close();
     	return result;
     }
     
     public synchronized int updateWifi(String wifiName, String wifiPwd)
     {
    	 try 
    	 {
			
    		 ContentValues values = new ContentValues();
    	     	
    	     	
    		 if(!TextUtils.isEmpty(wifiName))
    			 values.put("wifi_name", wifiName);
    		 values.put("wifi_pwd", wifiPwd);
    		 values.put("modify_time", System.currentTimeMillis());
    		 return editWifi(TBL_WIFI, wifiName, values);
    		 
		} 
    	 catch (Exception e) 
    	 {
			// TODO: handle exception
			Toast.makeText(context, e.getMessage(), 0).show();
		}
     	return -1;
     }
     
     
     
     /************* 鎾斁璁″垝鐨勬搷浣� ************/
     public synchronized void deleteAllPlan()
     {
    	 deleteAll(TBL_CUR_PLAN_LIST);
    	 deleteAll(TBL_CUR_PLAN_MUSIC);
     }
     public synchronized void setCurPlanSub(List<CurPlanSubInfor> planList)
     {
    	 int len = planList.size();
    	 for(int i=0;i<len;i++)
    	 {
    		 CurPlanSubInfor tempPlanInfor = planList.get(i);
    		 addCurPlanSub(tempPlanInfor);
    		 List<SongInfor> tempSongList = tempPlanInfor.getSongList();
    		 int count = tempSongList.size();
    		 for(int j=0;j<count;j++)
    		 {
    			 SongInfor tempSongInfor = tempSongList.get(j);
    			 addCurPlanMusic(tempSongInfor, tempPlanInfor.getPlanId());
    		 }
    	 }
     }
     public synchronized long addCurPlanSub(CurPlanSubInfor curPlanSubInfor)
     {
     	ContentValues values = new ContentValues();
     	
     	if(!TextUtils.isEmpty(curPlanSubInfor.getPlanId()))
     		values.put("id", curPlanSubInfor.getPlanId());
     	if(!TextUtils.isEmpty(curPlanSubInfor.getStartTime()))
     		values.put("startTime", curPlanSubInfor.getStartTime());
     	if(!TextUtils.isEmpty(curPlanSubInfor.getEndTime()))
     		values.put("endTime", curPlanSubInfor.getEndTime());
     	
     	values.put("modify_time", System.currentTimeMillis());
     	/*insert(values, TBL_CUR_PLAN_LIST);
     	int s = queryNormal(TBL_CUR_PLAN_LIST).getCount();
     	return 1;*/
     	return insert(values, TBL_CUR_PLAN_LIST);
     }
     public synchronized long addCurPlanMusic(SongInfor songInfor, String planId)
     {
    	 ContentValues values = new ContentValues();
    	 
    	 if(!TextUtils.isEmpty(planId))
    		 values.put("planId", planId);
    	 if(!TextUtils.isEmpty(songInfor.getMediaId()))
    		 values.put("musicId", songInfor.getMediaId());
    	 if(!TextUtils.isEmpty(songInfor.getMediaName()))
    		 values.put("musicName", songInfor.getMediaName());
    	 if(!TextUtils.isEmpty(songInfor.getAlbumName()))
    		 values.put("musicAlbum", songInfor.getAlbumName());
    	 if(!TextUtils.isEmpty(songInfor.getArtist()))
    		 values.put("musicSing", songInfor.getArtist());
    	 if(!TextUtils.isEmpty(songInfor.getMediaUrl()))
    		 values.put("musicUrl", songInfor.getMediaUrl());
    	 
    	 values.put("modify_time", System.currentTimeMillis());
    	 
    	 return insert(values, TBL_CUR_PLAN_MUSIC);
     }
     
     public synchronized List<SongInfor> getSongListByPlanId(String planId, int fileType)
     {
     	List<SongInfor> tempList = new ArrayList<SongInfor>();
     	Cursor cur = queryAsc(TBL_CUR_PLAN_MUSIC);
     	if(cur == null || cur.getCount() == 0)
     		 return tempList;
     	if(cur != null && cur.getCount() > 0)
     	{
	   		 while(cur.moveToNext())
	   		 {
	   			String tempId = cur.getString(cur.getColumnIndex("planId"));
	   			if(tempId.equals(planId))
	   			{
	   				SongInfor tempInfor = new SongInfor();
	   				
	   				
	  				String musicUrl = cur.getString(cur.getColumnIndex("musicUrl"));
	  				String musicId = cur.getString(cur.getColumnIndex("musicId"));
	  				String musicName = cur.getString(cur.getColumnIndex("musicName"));
	  				String musicAlbum = cur.getString(cur.getColumnIndex("musicAlbum"));
	  				String musicSing = cur.getString(cur.getColumnIndex("musicSing"));
	  				tempInfor.setMediaId(musicId);
	  				tempInfor.setMediaName(musicName);
	  				tempInfor.setAlbumName(musicAlbum);
	  				tempInfor.setArtist(musicSing);
	  				tempInfor.setMediaUrl(musicUrl);
	  				
	  				if(fileType == FILE_TYPE_ALL)
	  				{
	  					tempList.add(tempInfor);
	  				}
	  				else if(fileType == FILE_TYPE_LOCAL)
	  				{
	  					if(isFileExsit(musicUrl))
	  					{
	  						tempList.add(tempInfor);
	  					}
	  				}
	  				else if(fileType == FILE_TYPE_ONLIE)
	  				{
	  					if(!isFileExsit(musicUrl))
	  					{
	  						tempList.add(tempInfor);
	  					}
	  				}
	   			}
	   		 }
     	}
     	if(cur != null )
     		cur.close();
     	return tempList;
     }
     
     private void deleteCurPlanMusic(String music_url)
     {
    	 delete("music_url", music_url, TBL_CUR_PLAN_MUSIC);
     }
     
     public synchronized List<SongInfor> getCurPlanMusics(long curTime, int fileType)
     {
    	 try 
    	 {
			
    		 List<SongInfor> songList = new ArrayList<SongInfor>();
        	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
          	 if(cur == null || cur.getCount() == 0 || curTime == 0)
          		 return null;
          	 String curTimeShort = DateUtils.getEndDateFromLong(curTime);
          	 long curTimeShortLong = DateUtils.timeToInt(curTimeShort, ":");
          	 
          	 if(cur != null && cur.getCount() > 0)
          	 {
          		while(cur.moveToNext())
          		{
          			String tempId = cur.getString(cur.getColumnIndex("id"));
          			String startTime = cur.getString(cur.getColumnIndex("startTime"));
          			String endTime = cur.getString(cur.getColumnIndex("endTime"));
          			if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
          			{
          				long sLong = DateUtils.timeToInt(startTime, ":");
          				long eLong = DateUtils.timeToInt(endTime, ":");
          				String localPlanTime = LocalDataEntity.newInstance(context).getPlayTime();
          				if(isTimeOverlap(sLong, eLong, curTimeShortLong))
          				{
          					if(!localPlanTime.equals(startTime))
          					{
          						songList = getSongListByPlanId(tempId, fileType);
          						LocalDataEntity.newInstance(context).setPlanTime(startTime);
          					}
          					else//褰撳墠鎾斁鍒楄〃涓庢湰鍦板垪琛ㄤ竴鑷达紝涓嶆洿鏂版暟鎹�
          					{
          						songList = null;
          						//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
          						//showToast("*&&&*&*&*&*&*&*&-->curPlanTime" + curPlanTime + "  startTime-->"+startTime);
          					}
          					break;
          				}
          				else
          				{
          					//songList = new ArrayList<SongInfor>();
          					//LogFactory.createLog().e("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
          					//showToast("*&&&*&*&*&*&*&*&-->sLong" + sLong + "  eLong-->"+eLong + "  curTimeShortLong-->"+curTimeShortLong);
          				}
          			}
          			else
          			{
          				//songList = new ArrayList<SongInfor>();
          				//showToast("*&&&*&*&*&*&*&*&-->startTime" + startTime + "  endTime-->"+endTime);
          			}
          		}
          	 }
          	if(cur != null )
         		cur.close();
        	 return songList; 
    	 } 
    	 catch (Exception e)
    	 {
			// TODO: handle exception
    		 Log.e("", e.getMessage());
    	 }
    	 return null;
     }
     public synchronized List<SongInfor> getAllPlanMusics(int fileType)
     {
    	 List<SongInfor> songList = new ArrayList<SongInfor>();
    	 if(isGetAllPlanMusicRunning)
    		 return songList;
    	 isGetAllPlanMusicRunning = true;
    	 
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    	 {
    		 isGetAllPlanMusicRunning = false;
    		 return songList;
    	 }
    	 
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String tempId = cur.getString(cur.getColumnIndex("id"));
    			 String startTime = cur.getString(cur.getColumnIndex("startTime"));
    			 String endTime = cur.getString(cur.getColumnIndex("endTime"));
    			 if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
    			 {
    				 songList.addAll(getSongListByPlanId(tempId, fileType));
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 isGetAllPlanMusicRunning = false;
    	 return songList;
     }
     
     public synchronized void deleteCurPlanMusicByUrl(String music_url)
     {
    	 delete("music_url", music_url, TBL_CUR_PLAN_LIST);
     }
     
     /**
      * 鑾峰彇鏈湴璁″垝鐨勭涓�涓紑濮嬫椂闂�
      * @return
      */
     public synchronized long getFirstPlanTime()
     {
    	 long time = 0;
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return time;
    	 
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToLast())
    		 {
    			 String startTime = cur.getString(cur.getColumnIndex("startTime"));
    			 if(!TextUtils.isEmpty(startTime))
    			 {//"yyyy-MM-dd HH:mm:ss"
    				 startTime = "2018-10-1 " + startTime;
    				 //time = DateUtils.timeToInt(startTime, ":") ;
    				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				 try {
						time = formatter.parse(startTime).getTime() + 2 * 60 * 1000;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				 break;
    			 }
    			 //String endTime = cur.getString(cur.getColumnIndex("endTime"));
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return time;
     }
     public synchronized boolean isLocalHasPlan()
     {
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return false;
    	 return true;
     }
     public synchronized int getLocalPlanCount()
     {
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur != null)
    		 return cur.getCount();
    	 return 0;
     }
     
     public synchronized boolean isCurTimeHasPlayList(long serverTime)
     {
    	 boolean result = false;
    	 long time = 0;
    	 Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
    	 if(cur == null || cur.getCount() == 0)
    		 return false;
    	 
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToLast())
    		 {
    			 String startTime = cur.getString(cur.getColumnIndex("startTime"));
    			 String endTime = cur.getString(cur.getColumnIndex("endTime"));
    			 long lSatrt = 0;
    			 long lEnd = 0;
    			 if(!TextUtils.isEmpty(startTime))
    			 {
    				 lSatrt = Long.parseLong(startTime);
    			 }
    			 if(!TextUtils.isEmpty(endTime))
    			 {
    				 lEnd = Long.parseLong(endTime);
    			 }
    			 if(isTimeOverlap(lSatrt, lEnd, serverTime))
    			 {
    				 result = true;
    				 break;
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();
    	 return result;
     }
     
     private boolean isTimeOverlap(long start, long end, long dest)
 	 {
 		if(start > end)
 		{
 			end = end + 24 * 60 * 60;
 			if(dest > start)
 			{
 				if(dest < end)
 				{
 					Log.d("", "");
 					return true;
 				}
 			}
 			else
 			{
 				dest = dest + 24 * 60 * 60;
 				if(dest < end)
 				{
 					Log.d("", "");
 					return true;
 				}
 			}
 		}
 		else
 		{
 			if(dest > start && dest < end)
 				return true;
 		}
 		
 		return false;
 	}
     
    /* private void showToast(final String text)
     {
    	 ViewUtils.sendMessage(handler, 0, text);
     }*/
     
     /*private Handler handler = new Handler()
     {
    	 public void handleMessage(android.os.Message msg) 
    	 {
    		 String infor = (String) msg.obj;
    		 Toast.makeText(context, infor, 0).show();
    	 };
     };*/
     public boolean isFileExsit(String url)
 	{
 		File file = new File(getMusicLocalPath(url));
 		return file.exists();
 	}
     public boolean isFileExsit(SongInfor songInfor)
     {
    	 String url = songInfor.getMediaUrl();
    	 File file = new File(getMusicLocalPath(url));
    	 return file.exists();
     }
 	private String getMusicLocalPath(String url)
 	{
 		String fileName = "";
 		if(url.contains("."))
 			fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
 		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
 		
 		return filePath;
 	}
     
}
 
