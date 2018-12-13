package com.znt.push.update;

import java.util.LinkedList;

import android.content.Context;

import com.znt.diange.mina.entity.SongInfor;

public class ApkDownLoadManager 
{
	private Context context = null;
	
	private ApkDownloadTask downloadTask;
    
    private boolean isCancel = false;
    private SongInfor curDownloadSong = null;
    private LinkedList<SongInfor> downloadList = new LinkedList<SongInfor>();
    
    public static ApkDownLoadManager INSTANCE = null;
    public static ApkDownLoadManager getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized (ApkDownLoadManager.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new ApkDownLoadManager();
			}
		}
		
		return INSTANCE;
	}
    
    public void init(Context context)
    {
    	this.context = context.getApplicationContext();
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
    
    /**
     * ��ʼ����
     * @param url
     */
    public void  startDownload(String url, String dir, ApkDownloadListener listener)
    {
    	if(downloadTask != null)
    	{
    		downloadTask.cancelDownload();
    		downloadTask.cancel(true);
    		downloadTask = null;
    	}
    	downloadTask= new ApkDownloadTask(listener, dir);
        downloadTask.execute(url);
    }

    /**
     * ��ͣ����
     */
    public void pauseDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.pauseDownload();
        }
    }

    /**
     * ȡ������
     */
    public void cancelDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.cancelDownload();
        }
        isCancel = true;
        /*else 
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
        }*/
    }
}
