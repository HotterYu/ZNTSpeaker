
package com.znt.push;

// Declare any non-default types here with import statements

import com.znt.push.ITaskCallback; 
import com.znt.diange.mina.entity.SongInfor; 
interface IPushAidlInterface
{
    void putRequestParams(String fdevId, int playingSongType, String playingSong, String netInfo, boolean updateNow);
   
    void registerCallback(ITaskCallback cb);   
    void unregisterCallback(ITaskCallback cb);
    
}