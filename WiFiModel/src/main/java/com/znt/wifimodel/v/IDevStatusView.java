package com.znt.wifimodel.v;

import com.znt.diange.mina.entity.SongInfor;

import java.util.List;

public interface IDevStatusView 
{
	public void onGetDevStatus();
	public void onCheckDelay();
	public void getLocalPlayListAndPlay(List<SongInfor> tempSongList);
}
