
package com.znt.push.update; 
/** 
 * @ClassName: HttpMsg 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-5-15 下午5:36:01  
 */
public class HttpMsg
{
	public static final int HTTP_CANCEL = -2;//任务取消
	public static final int NO_NET_WORK_CONNECT = -1;//无网络连�?
	
	public static final int CHECK_UPDATE_START = 0x1001;//�?测升级开�?
	public static final int CHECK_UPDATE_SUCCESS = 0x1002;//�?测升级成�?
	public static final int CHECK_UPDATE_FAIL = 0x1003;//�?测升级失�?
	
	public static final int REGISTER_START = 0x1001;//注册�?�?
	public static final int REGISTER_SUCCESS = 0x1002;//注册成功
	public static final int REGISTER_FAIL = 0x1003;//注册失败
	
	public static final int CONIN_REMOVE_START = 0x1004;//扣除金币�?�?
	public static final int CONIN_REMOVE_SUCCESS = 0x1005;//扣除金币成功
	public static final int CONIN_REMOVE_FAIL = 0x1006;//扣除金币失败
	public static final int CONIN_FREEZE_CANCEL_START = 0x1007;//冻结金币�?�?
	public static final int CONIN_FREEZE_CANCEL_SUCCESS = 0x1008;//冻结金币成功
	public static final int CONIN_FREEZE_CANCEL_FAIL = 0x1009;//冻结金币失败
	
	public static final int GET_PLAY_LIST_START = 0x2000;//获取播放列表�?�?
	public static final int GET_PLAY_LIST_SUCCESS = 0x2001;//获取播放列表成功
	public static final int GET_PLAY_LIST_FAIL = 0x2002;//获取播放列表失败
	
	public static final int UPLOAD_SONG_RECORD_START = 0x2003;//上传点播歌曲�?�?
	public static final int UPLOAD_SONG_RECORD_SUCCESS = 0x2004;//上传点播歌曲成功
	public static final int UPLOAD_SONG_RECORD_FAIL = 0x2005;//上传点播歌曲失败
	public static final int GET_DEVICE_STATUS_START = 0x2006;//上传点播歌曲�?�?
	public static final int GET_DEVICE_STATUS_SUCCESS = 0x2007;//上传点播歌曲成功
	public static final int GET_DEVICE_STATUS_FAIL = 0x2008;//上传点播歌曲失败
	public static final int GET_CUR_TIME_SUCCESS = 0x2009;//
	public static final int GET_CUR_TIME_FAIL = 0x2010;//
	public static final int GET_PUSH_MUSICS_FAIL = 0x2011;//
	public static final int GET_PUSH_MUSICS_SUCCESS = 0x2012;//
	public static final int GET_PUSH_MUSICS_STATR = 0x2013;//
	public static final int GET_CUR_PLAN_START = 0x2014;//
	public static final int GET_CUR_PLAN_SUCCESS = 0x2015;//
	public static final int GET_CUR_PLAN_FAIL = 0x2016;//
	public static final int GET_CUR_POS_START = 0x2017;//
	public static final int GET_CUR_POS_SUCCESS = 0x2018;//
	public static final int GET_CUR_POS_FAIL = 0x2019;//
	public static final int UPDATE_CUR_POS_START = 0x2020;//
	public static final int UPDATE_CUR_POS_SUCCESS = 0x2021;//
	public static final int UPDATE_CUR_POS_FAIL = 0x2022;//
	public static final int INIT_TERMINAL_START = 0x2023;//
	public static final int INIT_TERMINAL_SUCCESS = 0x2024;//
	public static final int INIT_TERMINAL_FAIL = 0x2025;//
	
}
 
