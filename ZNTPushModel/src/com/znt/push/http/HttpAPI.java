
package com.znt.push.http; 

public class HttpAPI
{
	public static final String SERVER_ADDRESS = "https://www.zhunit.com/";
	//public static final String SERVER_ADDRESS = "http://192.168.1.110:8080/vodbox/";
	//public static final String SERVER_ADDRESS = "http://115.28.191.217:8080/vodbox/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.122:8080/";
	//protected final String SERVER_ADDRESS = "http://192.168.1.113:8080/vodbox/";
	
	public final String CHECK_UPDATE = SERVER_ADDRESS + "mobinf/softVersionAction!getLastVersion.do?";
	
	public final String REGISTER = SERVER_ADDRESS + "mobinf/terminalAction!addTerminal.do?";
	public final String UPDATE_SPEAKER_INFOR = SERVER_ADDRESS + "mobinf/terminalAction!updateTerminal.do?";
	
	public final String BIND_SPEAKER_BY_ID = SERVER_ADDRESS + "/mobinf/terminalAction!bind.do";		
	public final String COIN_REMOVE = SERVER_ADDRESS + "mobinf/memberAccountAction!goldConsumeAuthComplete.do";
	public final String COIN_FREEZE_CANCEL = SERVER_ADDRESS + "mobinf/memberAccountAction!goldConsumeAuthCancel.do";
	
	public final String GET_PLAY_LIST = SERVER_ADDRESS + "mobinf/terminalMusicAction!getTerminalMusicList.do";
	public final String GET_DEVICE_STATUS = SERVER_ADDRESS + "mobinf/terminalAction!getTerminalStatus.do";
	
	public final String UPLOAD_SONG_RECORD = SERVER_ADDRESS + "mobinf/memberVodAction!musicVoded.do";
	
	public final String BIND_SPEAKER = SERVER_ADDRESS + "mobinf/memberAction!applyForAdmin.do";
		
	public final String GET_PLAN_MUSICS = SERVER_ADDRESS + "/mobinf/terminalAction!getPlanMusicList.do";
	public final String GET_CUR_TIME = SERVER_ADDRESS + "/mobinf/terminalAction!getSystemTime.do";
	
	public final String GET_PUSH_MUSIC = SERVER_ADDRESS + "/mobinf/terminalMusicAction!getPushMusic.do";
	
	public final String GET_CUR_MUSIC_POS = SERVER_ADDRESS + "/mobinf/terminalAction!getMaxPlayingPos.do";
	public final String INIT_TERMINAL = SERVER_ADDRESS + "/mobinf/terminalAction!initTerminal.do";
	
	public final String GET_CUR_PLAN = SERVER_ADDRESS + "/mobinf/planAction!getTerminalPlan.do";
	public final String GET_SCHEDULE_MUSICS = SERVER_ADDRESS + "/mobinf/terminalAction!getPlanScheMusicList.do";
			
}
 
