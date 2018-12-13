package com.znt.wifimodel.v;

import java.util.List;

import com.znt.diange.mina.entity.SongInfor;

public interface IDevStatusView 
{
	public void onGetDevStatus();
	public void onCheckDelay();
	public void getLocalPlayListAndPlay(List<SongInfor> tempSongList);
}
