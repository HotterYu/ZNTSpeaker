
package com.znt.download;

// Declare any non-default types here with import statements

import com.znt.download.IDownloadCallback; 
import com.znt.diange.mina.entity.SongInfor; 
interface IDownloadAidlInterface
{
    
    void addSongInfor(in SongInfor infor);
    void addSongInfors(in List<SongInfor> infors);
   
    void registerCallback(IDownloadCallback cb);   
    void unregisterCallback(IDownloadCallback cb);
}