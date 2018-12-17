
package com.znt.diange.mina.entity; 

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/** 
 * @ClassName: SongInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-22 涓嬪崍4:21:06  
 */
public class SongInfor implements Parcelable
{
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	public final static int MUSIC_PLAY_TYPE_NORMAL = 0;
	public final static int MUSIC_PLAY_TYPE_PUSH = 1;
	public int musicPlayType = MUSIC_PLAY_TYPE_NORMAL;//0 正常播放，1，点播
	private long modify_time = 0; 
	private int reloadCount = 0;
	
	private String mediaId = "";
	private String resId = "";
	private String terminalId = "";
	private String mediaName = "";
	private String mediaUrl = "";
	private String objectclass = "";
	private long mediaSize = 0;
	private String mediaCover = "";
	private long date = 0;
	private String res = "";
	private int mediaDuration = 0;
	private String mediaResType = "1";//1-閰锋垜 2-缃戞槗
	
	private String artist = "";
	private String albumName = "";
	private String albumUrl = "";
	private boolean isDir = false;
	private boolean isAvailable = true;
	private boolean isSelected = false;
	private boolean isPlaying = false;
	private long curPlayTime = 0;
	private int resourceType = 0;

	private String saveDir = "";

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public SongInfor()
	{
		
	}
	
	public SongInfor(Parcel s)
	{
		musicPlayType = s.readInt();
    	modify_time = s.readLong();
    	reloadCount = s.readInt();
    	mediaId = s.readString();
    	resId = s.readString();
    	terminalId = s.readString();
    	mediaName = s.readString();
    	mediaUrl = s.readString();
    	mediaSize = s.readLong();
    	mediaCover = s.readString();
    	date = s.readLong();
    	res = s.readString();
    	mediaDuration = s.readInt();
    	mediaResType = s.readString();
    	artist = s.readString();
    	albumName = s.readString();
    	albumUrl = s.readString();
    	curPlayTime = s.readLong();
    	resourceType = s.readInt();
    	
    	isAvailable = s.readByte() != 0; //isAvailable == true if byte != 0
    	isSelected = s.readByte() != 0; //isAvailable == true if byte != 0
    	isPlaying = s.readByte() != 0; //isAvailable == true if byte != 0
	}
	
	public void setModify_time(long modify_time)
	{
		this.modify_time = modify_time;
	}
	public long geModify_time()
	{
		return modify_time;
	}

	public void setMusicPlayType(int musicPlayType)
	{
		this.musicPlayType = musicPlayType;
	}
	public int getMusicPlayType()
	{
		return musicPlayType;
	}
	public boolean isPushMusic()
	{
		return musicPlayType == MUSIC_PLAY_TYPE_PUSH;
	}
	
	private String getInforFromStr(String key, String content)
	{
		String result = "";
		try
		{
			JSONObject json = new JSONObject(content);
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void setReloadCount(int reloadCount)
	{
		this.reloadCount = reloadCount;
	}
	public int getReloadCount()
	{
		return reloadCount;
	}
	public void increaseReloadCount()
	{
		reloadCount ++;
	}
	
	
	
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setAvailable(boolean isAvailable)
	{
		this.isAvailable = isAvailable;
	}
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void setDir(boolean isDir)
	{
		this.isDir = isDir;
	}
	public boolean getDir()
	{
		return isDir;
	}
	
	public void setArtist(String artist)
	{
		this.artist = artist;
	}
	public String getArtist()
	{
		if(TextUtils.isEmpty(artist))
			artist = "未知";
		return artist;
	}
	
	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}
	public String getAlbumName()
	{
		if(albumName == null)
			albumName = "";
		return albumName;
	}
	
	public void setAlbumUrl(String albumUrl)
	{
		this.albumUrl = albumUrl;
	}
	public String getAlbumUrl()
	{
		if(albumUrl == null)
			albumUrl = "";
		return albumUrl;
	}
	
	public void setMediaDuration(int mediaDuration)
	{
		this.mediaDuration = mediaDuration;
	}
	public int getMediaDuration()
	{
		return mediaDuration;
	}
	
	public void setMediaResType(String mediaResType)
	{
		this.mediaResType = mediaResType;
	}
	public String getMediaResType()
	{
		return mediaResType;
	}
	
	public void setMediaId(String mediaId)
	{
		this.mediaId = mediaId;
	}
	public String getMediaId()
	{
		return mediaId;
	}
	
	public void setResId(String resId)
	{
		this.resId = resId;
	}
	public String getResId()
	{
		return resId;
	}
	
	public void setMediaName(String mediaName)
	{
		this.mediaName = mediaName;
	}
	public String getMediaName()
	{
		if(mediaName == null)
			mediaName = "";
		return mediaName;
	}
	
	public void setMediaUrl(String mediaUrl)
	{
		this.mediaUrl = mediaUrl;
	}
	public String getMediaUrl()
	{
		if(mediaUrl == null)
			mediaUrl = "";
		return mediaUrl;
	}
	
	public void setObjectClass(String objectclass)
	{
		this.objectclass = objectclass;
	}
	public String getObjectClass()
	{
		return objectclass;
	}

	public void setMediaSize(long mediaSize)
	{
		this.mediaSize = mediaSize;
	}
	public long getMediaSize()
	{
		return mediaSize;
	}
	
	public void setMediaCover(String mediaCover)
	{
		this.mediaCover = mediaCover;
	}
	public String getMediaCover()
	{
		return mediaCover;
	}
	
	public long getDate() 
	{
		return date;
	}
	public void setDate(long date) 
	{
		this.date = date;
	}
	
	public void setRes(String res) 
	{
		this.res = (res != null ? res : "");
	}
	public String getRes() 
	{
		return res;
	}

	public void setResourceType(int resourceType)
	{
		this.resourceType = resourceType;
	}
	public int getResourceType()
	{
		return resourceType;
	}
	
	public void setTerminalId(String terminalId)
	{
		this.terminalId = terminalId;
	}
	public String getTerminalId()
	{
		return terminalId;
	}
	
	public void setIsPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	public void setCurPlayTime(long curPlayTime)
	{
		this.curPlayTime = curPlayTime;
	}
	public long getCurPlayTime()
	{
		return curPlayTime;
	}
	public String getCurPlayTimeFormat()
	{
		if(curPlayTime <= 0)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));  
        return formatter.format(curPlayTime);  
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	//注意写入变量和读取变量的顺序应该一致 不然得不到正确的结果
    @Override
    public void writeToParcel(Parcel d, int flags) 
    {
    	d.writeInt(musicPlayType);
    	d.writeLong(modify_time);
    	d.writeInt(reloadCount);
    	d.writeString(mediaId);
    	d.writeString(resId);
    	d.writeString(terminalId);
    	d.writeString(mediaName);
    	d.writeString(mediaUrl);
    	d.writeLong(mediaSize);
    	d.writeString(mediaCover);
    	d.writeLong(date);
    	d.writeString(res);
    	d.writeInt(mediaDuration);
    	d.writeString(mediaResType);
    	d.writeString(artist);
    	d.writeString(albumName);
    	d.writeString(albumUrl);
    	d.writeLong(curPlayTime);
    	d.writeInt(resourceType);
    	d.writeByte((byte) (isAvailable ? 1 : 0)); //if isAvailable == true, byte == 1 
    	d.writeByte((byte) (isSelected ? 1 : 0)); //if isSelected == true, byte == 1 
    	d.writeByte((byte) (isPlaying ? 1 : 0)); //if isPlaying == true, byte == 1 
    }
 
    //注意读取变量和写入变量的顺序应该一致 不然得不到正确的结果
    public void readFromParcel(Parcel s) 
    {
    	musicPlayType = s.readInt();
    	modify_time = s.readLong();
    	reloadCount = s.readInt();
    	mediaId = s.readString();
    	resId = s.readString();
    	terminalId = s.readString();
    	mediaName = s.readString();
    	mediaUrl = s.readString();
    	mediaSize = s.readLong();
    	mediaCover = s.readString();
    	date = s.readLong();
    	res = s.readString();
    	mediaDuration = s.readInt();
    	mediaResType = s.readString();
    	artist = s.readString();
    	albumName = s.readString();
    	albumUrl = s.readString();
    	curPlayTime = s.readLong();
    	resourceType = s.readInt();
    	
    	isAvailable = s.readByte() != 0; //isAvailable == true if byte != 0
    	isSelected = s.readByte() != 0; //isAvailable == true if byte != 0
    	isPlaying = s.readByte() != 0; //isAvailable == true if byte != 0
    
    }
	
	public static final Parcelable.Creator<SongInfor> CREATOR = new Creator<SongInfor>() 
	{
        @Override
        public SongInfor[] newArray(int size) 
        {
            return new SongInfor[size];
        }

        @Override
        public SongInfor createFromParcel(Parcel s) 
        {
        	SongInfor mSongInfor = new SongInfor(s);
            return mSongInfor;
        }
    };
}
 
