/*package com.znt.speaker.download;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;

public class FileDownLoadManager 
{
	private Context context = null;
	
	private DownloadTask downloadTask;
    
    private volatile boolean isDownloadRunning = false;
    private boolean isCancel = false;
    private SongInfor curDownloadSong = null;
    private LinkedList<SongInfor> downloadList = new LinkedList<SongInfor>();
    private DownloadListener listener = new DownloadListener() 
    {
        *//**
         * ������һ��������ʾ���ؽ��ȵ�֪ͨ
         * @param progress
         *//*
        @Override
        public void onProgress(int progress) 
        {
            //NotificationManager��notify()������֪ͨ��ʾ������
            //notify(),����������������һ��������id:ÿ��֪ͨ��ָ����id���ǲ�ͬ�ġ��ڶ���������Notification����
        }

        *//**
         * ������һ���µ�֪ͨ���ڸ����û����سɹ���
         *//*
        @Override
        public void onSuccess() 
        {
            downloadTask=null;
            //���سɹ�ʱ��ǰ̨����֪ͨ�رգ�������һ�����سɹ���֪ͨ
            isDownloadRunning = false;
            startDownload();
            //Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        *//**
         *�û�����ʧ��
         *//*
        @Override
        public void onFailed()
        {
            downloadTask=null;
            //����ʧ��ʱ����ǰ̨����֪ͨ�رգ�������һ������ʧ�ܵ�֪ͨ
            isDownloadRunning = false;
            if(curDownloadSong != null)
            {
            	curDownloadSong.increaseReloadCount();
            	if(curDownloadSong.getReloadCount() <= 2)
            		downloadList.add(0, curDownloadSong);
            }
            	
            startDownload();
            //Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        *//**
         * �û���ͣ
         *//*
        @Override
        public void onPaused() 
        {
            downloadTask=null;
            isDownloadRunning = false;
            //Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_SHORT).show();
        }

        *//**
         * �û�ȡ��
         *//*
        @Override
        public void onCanceled()
        {
            downloadTask=null;
            isDownloadRunning = false;
            //ȡ�����أ���ǰ̨����֪ͨ�رգ�������һ������ʧ�ܵ�֪ͨ
            //Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }
    };
    
    public static FileDownLoadManager INSTANCE = null;
    public static FileDownLoadManager getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized (FileDownLoadManager.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new FileDownLoadManager();
			}
		}
		
		return INSTANCE;
	}
    
    public void init(Context context)
    {
    	this.context = context.getApplicationContext();
    }
    
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
    
    public void cancelAll()
    {
    	isCancel = true;
    	if(downloadTask != null)
    	{
    		downloadTask.cancelDownload();
        	//downloadTask.cancel(true);
        	downloadTask = null;
    	}
    }
    
    *//**
     * ��ʼ����
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
     * ��ͣ����
     *//*
    public void pauseDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.pauseDownload();
        }
    }

    *//**
     * ȡ������
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
                //ȡ������ʱ��Ҫ���ļ�ɾ��������֪ͨ�ر�
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
*/