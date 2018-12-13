package com.znt.wifimodel.db; 

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.diange.mina.entity.WifiInfor;

/** 
 * @ClassName: DBManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-14 娑撳﹤宕�?10:25:36  
 */
public class DBManager extends MyDbHelper
{

	public static DBManager INSTANCE = null;
	private Context context = null;
	
	private volatile boolean isGetAllPlanMusicRunning = false;
	
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
	
    
    public synchronized long insertSongRecord(SongInfor infor, long modify_time)
    {
    	if(infor == null)
    		return -1;
    	if(isRecorExist(infor))
    	{
    		/*if(modify_time <= 0)
    			*/updateSongRecord(infor, modify_time);
    		return -1;
    	}
    	
    	ContentValues values = new ContentValues();
    	
    	String musicName = infor.getMediaName();
    	if(!TextUtils.isEmpty(musicName))
    		values.put("music_name", musicName);
    	if(!TextUtils.isEmpty(infor.getMediaUrl()))
    		values.put("music_url", infor.getMediaUrl());
    	if(modify_time <= 0)
    		modify_time = System.currentTimeMillis();
    	values.put("modify_time", System.currentTimeMillis());
    	
    	return insert(values, TBL_SONG_RECORD);
    }
    /**
     * 閺囧瓨鏌婇棅鍏呯娣団剝浼�
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
    	values.put("modify_time", System.currentTimeMillis());
    	
    	return edit(TBL_SONG_RECORD,  values);
    }
    
    /**
     * @Description: 閸掋倖鏌囬棅鍏呯閺勵垰鎯佺�涙ê�?�?
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
    * @Description: 閸掋倖鏌囬棅鍏呯閺勵垰鎯佺�涙ê�?�?
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
     			//閺嶈宓�?悽銊�?煕id鐎佃鐦�?惃鍕剁礉閸氬奔绔存稉顏嗘暏閹村嘲褰ч懗鐣屽仯閹绢厺绔存＃鏍ㄧ摃閺囷�?
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
     * @Description: 閼惧嘲褰囬棅鍏呯閸掓銆�
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
      * @Description: 閼惧嘲褰囬棅鍏呯閹绢厽鏂佺拋鏉跨秿
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
    				 //String music_album = cur.getString(cur.getColumnIndex("music_album"));
    				 //String music_album_url = cur.getString(cur.getColumnIndex("music_album_url"));
    				 int play_state = cur.getInt(cur.getColumnIndex("play_state"));
    				 //String play_time = cur.getString(cur.getColumnIndex("play_time"));
    				 //int music_coin = cur.getInt(cur.getColumnIndex("music_coin"));
    				 //String trand_id = cur.getString(cur.getColumnIndex("trand_id"));
    				 //String user_id = cur.getString(cur.getColumnIndex("user_id"));
    				 //String user_name = cur.getString(cur.getColumnIndex("user_name"));
    				 //String play_message = cur.getString(cur.getColumnIndex("play_message"));
    				 //String music_lyric = cur.getString(cur.getColumnIndex("music_lyric"));
    				 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    				 if(!TextUtils.isEmpty(music_url))
    				 {
    					 File file = new File(music_url);
        				 if(file.exists())
        				 {
        					 tempInfor.setMediaName(music_name);
            				 tempInfor.setArtist(music_artist);
            				 tempInfor.setMediaUrl(music_url);
            				 
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
      * 鏉╁洦鎶ら張顒��?撮弮鐘垫暏閻ㄥ嫭鏋冩禒锟�?
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
    			 if(play_state == 1)//瑜版挸澧犲锝呮躬閹绢厽鏂侀悩鑸碉拷锟�
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
    	 /*Cursor cur = query(TBL_SONG_LIST);
    	 if(cur != null && cur.getCount() > 0)
    	 {
    		 while(cur.moveToNext())
    		 {
    			 String music_url = cur.getString(cur.getColumnIndex("music_url"));
    			 if(music_url.equals(musicUrl))
    			 {
    				 delete("music_url", music_url, TBL_SONG_RECORD);
    				 break;
    			 }
    		 }
    	 }
    	 if(cur != null )
    		 cur.close();*/
    	 delete("music_url", music_url, TBL_SONG_RECORD);
     }
     
     /**
      * 閸掔�?娅庨棅鍏呯
      * @param id
      * @return
      */
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
     
     /**************缁狅紕鎮婇崨妯绘惙娴ｏ拷**************/
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
     
     /**************WIFI閹垮秳缍�?*****************/
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
 
