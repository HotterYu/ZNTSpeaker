package com.znt.download;

import com.znt.diange.mina.entity.SongInfor;

public interface IDownloadListener {
	public void onDownloadSpaceCheck(long size);
	public void onDownloadRecordInsert(SongInfor songInfor, long modifyTime);
	void onRemoveLargeSize(String url);
}
