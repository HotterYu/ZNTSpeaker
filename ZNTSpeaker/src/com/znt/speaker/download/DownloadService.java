/*package com.znt.speaker.download;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;

*//**
 * 为了保证DownloadTask可以一直在后台运行，我们还需要创建一个下载的服务。
 *//*
public class DownloadService extends Service
{

    private DownloadTask downloadTask;
    
    private volatile boolean isDownloadRunning = false;
    private boolean isCancel = false;
    private SongInfor curDownloadSong = null;
    private LinkedList<SongInfor> downloadList = new LinkedList<SongInfor>();
    private DownloadListener listener = new DownloadListener() 
    {
        *//**
         * 构建了一个用于显示下载进度的通知
         * @param progress
         *//*
        @Override
        public void onProgress(int progress) 
        {
            //NotificationManager的notify()可以让通知显示出来。
            //notify(),接收两个参数，第一个参数是id:每个通知所指定的id都是不同的。第二个参数是Notification对象。
        }

        *//**
         * 创建了一个新的通知用于告诉用户下载成功啦
         *//*
        @Override
        public void onSuccess() 
        {
            downloadTask=null;
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            isDownloadRunning = false;
            mBinder.startDownload();
            //Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        *//**
         *用户下载失败
         *//*
        @Override
        public void onFailed()
        {
            downloadTask=null;
            //下载失败时，将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            isDownloadRunning = false;
            if(curDownloadSong != null)
            {
            	curDownloadSong.increaseReloadCount();
            	if(curDownloadSong.getReloadCount() <= 2)
            		downloadList.add(0, curDownloadSong);
            }
            	
            mBinder.startDownload();
            //Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        *//**
         * 用户暂停
         *//*
        @Override
        public void onPaused() 
        {
            downloadTask=null;
            isDownloadRunning = false;
            //Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_SHORT).show();
        }

        *//**
         * 用户取消
         *//*
        @Override
        public void onCanceled()
        {
            downloadTask=null;
            isDownloadRunning = false;
            //取消下载，将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            //Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mBinder=new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }

    *//**
     * 为了要让DownloadService可以和活动进行通信，我们创建了一个DownloadBinder对象
     *//*
    public class DownloadBinder extends Binder
    {

    	public boolean isDownloadRunning()
    	{
    		return isDownloadRunning;
    	}
    	
    	public void addDownloadSong(SongInfor songInfor)
	    {
	    	downloadList.add(songInfor);
	    	startDownload();
	    }
	    public void addDownloadSongs(List<SongInfor> songList)
	    {
	    	downloadList.addAll(songList);
	    	startDownload();
	    }
	    
	    public void initDownloafList()
	    {
	    	downloadList.clear();
	    	downloadList.addAll(DBManager.INSTANCE.getAllPlanMusics(DBManager.FILE_TYPE_ONLIE));
	    	startDownload();
	    }
	    
        *//**
         * 开始下载
         * @param url
         *//*
        private void  startDownload()
        {
        	if(isDownloadRunning())
        		return;
        	
        	if(downloadList.size() > 0)
        	{
        		isDownloadRunning = true;
        		curDownloadSong = downloadList.remove(0);
        		String url = curDownloadSong.getMediaUrl();
        		
        		if(downloadTask==null)
                    downloadTask= new DownloadTask(listener);
                downloadTask.execute(url);
                
        	}
        }

        *//**
         * 暂停下载
         *//*
        public void pauseDownload()
        {
            if(downloadTask!=null)
            {
                downloadTask.pauseDownload();
            }
        }

        *//**
         * 取消下载
         *//*
        public void cancelDownload()
        {
            if(downloadTask!=null)
            {
                downloadTask.cancelDownload();
            }
            isCancel = true;
            else 
            {
                if(downloadUrl!=null)
                {
                    //取消下载时需要将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file=new File(directory+fileName);
                    if(file.exists())
                    {
                        file.delete();
                    }
                    stopForeground(true);
                    //Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
*/