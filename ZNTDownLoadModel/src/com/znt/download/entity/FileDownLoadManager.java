package com.znt.download.entity;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.download.IDownloadListener;

public class FileDownLoadManager
{
    private Context context = null;

    private DownloadTask downloadTask;

    private volatile boolean isDownloadRunning = false;
    private boolean isCancel = false;
    private SongInfor curDownloadSong = null;
    private LinkedList<SongInfor> downloadList = new LinkedList<SongInfor>();

    private IDownloadListener mIDownloadListener = null;

    public FileDownLoadManager(IDownloadListener mIDownloadListener)
    {
        this.mIDownloadListener = mIDownloadListener;
    }

    private DownloadListener listener = new DownloadListener()
    {
        /**
         * 构建了一个用于显示下载进度的通知
         * @param progress
         */
        @Override
        public void onProgress(int progress)
        {
            //NotificationManager的notify()可以让通知显示出来。
            //notify(),接收两个参数，第一个参数是id:每个通知所指定的id都是不同的。第二个参数是Notification对象。
        }

        /**
         * 创建了一个新的通知用于告诉用户下载成功啦
         */
        @Override
        public void onSuccess()
        {
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            isDownloadRunning = false;
            startDownload();
            //Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        /**
         *用户下载失败
         */
        @Override
        public void onFailed()
        {
            //下载失败时，将前台服务通知关闭，并创建一个下载失败的通知
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

        /**
         * 用户暂停
         */
        @Override
        public void onPaused()
        {
            isDownloadRunning = false;
            //Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_SHORT).show();
        }

        /**
         * 用户取消
         */
        @Override
        public void onCanceled()
        {
            isDownloadRunning = false;
            //取消下载，将前台服务通知关闭，并创建一个下载失败的通知
            //Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRemoveLargeSize(String url) {
            // TODO Auto-generated method stub
            isDownloadRunning = false;
            if(mIDownloadListener != null)
                mIDownloadListener.onRemoveLargeSize(url);
            startDownload();
        }
    };

    public static FileDownLoadManager INSTANCE = null;
    public static FileDownLoadManager init(IDownloadListener mIDownloadListener)
    {
        if(INSTANCE == null)
        {
            synchronized (FileDownLoadManager.class)
            {
                if(INSTANCE == null)
                    INSTANCE = new FileDownLoadManager(mIDownloadListener);
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
        if(downloadList.size() > 0)
            downloadList.clear();
        downloadList.addAll(songList);
        startDownload();
    }

    /*public void initDownloadList()
    {
    	downloadList.clear();
    	downloadList.addAll(DBManager.INSTANCE.getAllPlanMusics(false));
    	startDownload();
    }*/

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
     * 开始下载
     * @param url
     */
    private void  startDownload()
    {
        if(isDownloadRunning())
        {
            Log.e("", "file download is running");
            return;
        }

        if(downloadList.size() > 0)
        {
            try
            {
                isDownloadRunning = true;
                curDownloadSong = downloadList.remove(0);
                String url = curDownloadSong.getMediaUrl();

                if(downloadTask != null)
                {
                    downloadTask.cancelDownload();
                    downloadTask.cancel(true);
                    downloadTask = null;
                }
                downloadTask = new DownloadTask(listener, mIDownloadListener);
                downloadTask.execute(url);
            }
            catch (Exception e)
            {
                // TODO: handle exception
                Log.e("", "file download error-->"+e.getMessage());
            }
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload()
    {
        if(downloadTask!=null)
        {
            downloadTask.pauseDownload();
        }
    }

    /**
     * 取消下载
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
        }*/
    }
}
