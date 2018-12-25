package com.znt.download.entity;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.download.IDownloadListener;
import com.znt.download.ZNTDownloadService;
import com.znt.utils.UrlUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/23.
 */

/**
 * String 在执行AsyncTask时需要传入的参数，可用于在后台任务中使用。
 * Integer 后台任务执行时，如果需要在界面上显示当前的进度，则使用这里指定的泛型作为进度单位。
 * Integer 当任务执行完毕后，如果需要对结果进行返回，则使用这里指定的泛型作为返回值类型。
 */
public class DownloadTask extends AsyncTask<String,Integer,Integer>
{
    public static final int TYPE_SUCCESS=0;

    public static final int TYPE_FAILED=1;

    public static final int TYPE_PAUSED=2;

    public static final int TYPE_CANCELED=3;

    public static final int TYPE_REMOVE=4;

    private DownloadListener listener;

    private boolean isCanceled=false;

    private boolean isPaused=false;

    private int lastProgress;

    private String downloadDir = Environment.getExternalStorageDirectory() + "";//下载文件存放的目录

    private String endTag = ".temp";

    private String removeFileUrl = "";

    private IDownloadListener mIDownloadListener = null;

    public DownloadTask(DownloadListener listener, IDownloadListener mIDownloadListener)
    {
        this.listener = listener;
        this.mIDownloadListener = mIDownloadListener;
    }

    private String getDownloadDir()
    {
        if(TextUtils.isEmpty(ZNTDownloadService.STORAGE_DIR))
            ZNTDownloadService.STORAGE_DIR = Environment.getExternalStorageDirectory() + "/";

        return ZNTDownloadService.STORAGE_DIR;
    }

    /**
     * 这个方法中的所有代码都会在子线程中运行，我们应该在这里处理所有的耗时任务。
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params)
    {
        InputStream is=null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadLength = 0;   //记录已经下载的文件长度
        //文件下载地址
        String downloadUrl = params[0];
        downloadUrl = UrlUtil.decode(downloadUrl, "UTF-8");
        //downloadUrl = UrlUtil.getASCIIEncodedUrl(downloadUrl, "UTF-8");
        //下载文件的名称
        if(downloadUrl == null)
            return TYPE_FAILED;
        int lastIndex = downloadUrl.lastIndexOf("/");
        if(lastIndex < 0)
            return TYPE_FAILED;
        String fileName = downloadUrl.substring(lastIndex);
        //创建一个文件

        file = new File(getDownloadDir() +fileName);
        if(file.exists())
        {
            //如果文件存在的话，得到文件的大小
            return TYPE_SUCCESS;
        }

        //得到下载内容的大小
        long contentLength = getContentLength(downloadUrl);
        if(contentLength <= 0)
        {
            return TYPE_FAILED;
        }
        if(contentLength > 1024*1024*350)
        {
            removeFileUrl = downloadUrl;
            Log.e("doInBackground", "remove lage size -->"+ contentLength + "   " + downloadUrl);
            return TYPE_REMOVE;
        }
        //checkAndReleaseSpace(contentLength);//释放空间
        if(mIDownloadListener != null)
            mIDownloadListener.onDownloadSpaceCheck(contentLength);

        File tempFile = new File(getDownloadDir()  + fileName + ".temp");
        if(tempFile.exists())
            downloadLength = tempFile.length();

        /*else if(contentLength == downloadLength)
        {
            //已下载字节和文件总字节相等，说明已经下载完成了
            return TYPE_SUCCESS;
        }*/
        OkHttpClient client = new OkHttpClient();
        /**
         * HTTP请求是有一个Header的，里面有个Range属性是定义下载区域的，它接收的值是一个区间范围，
         * 比如：Range:bytes=0-10000。这样我们就可以按照一定的规则，将一个大文件拆分为若干很小的部分，
         * 然后分批次的下载，每个小块下载完成之后，再合并到文件中；这样即使下载中断了，重新下载时，
         * 也可以通过文件的字节长度来判断下载的起始点，然后重启断点续传的过程，直到最后完成下载过程。
         */
        Request request = new Request.Builder()
                .addHeader("RANGE","bytes=" + downloadLength+"-")  //断点续传要用到的，指示下载的区间
                .url(downloadUrl)
                .build();
        try
        {
            Response response=client.newCall(request).execute();
            if(response!=null)
            {
                is = response.body().byteStream();

                savedFile = new RandomAccessFile(tempFile,"rw");
                savedFile.seek(downloadLength);//跳过已经下载的字节
                byte[] b = new byte[1024 * 2];
                //int total = 0;
                int len;
                while((len = is.read(b)) != -1)
                {
                    if(isCanceled)
                    {
                        return TYPE_CANCELED;
                    }
                    else if(isPaused)
                    {
                        return TYPE_PAUSED;
                    }
                    else
                    {
                        //total += len;
                        savedFile.write(b,0,len);
                        //计算已经下载的百分比
                        //int progress = (int)((total + downloadLength) * 100 / contentLength);
                        //Log.e("", "****************progress-->"+progress);
                        //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                        //可以调用publishProgress()方法完成。
                        //publishProgress(progress);
                    }
                }

                response.body().close();

                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {
                    //文件完全下载成功
                    File newFile = renameFile(tempFile);
                    SongInfor infor = new SongInfor();
                    infor.setMediaUrl(newFile.getAbsolutePath());
                    infor.setMediaName(getNameFromPath(newFile.getAbsolutePath()));
                    //infor.setMediaSize(file.length());
                    if(mIDownloadListener != null)
                        mIDownloadListener.onDownloadRecordInsert(infor, newFile.lastModified());
                }
                else
                {
                    tempFile.delete();
                    return TYPE_FAILED;
                }

                return TYPE_SUCCESS;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return TYPE_FAILED;
        }
        finally
        {
            try
            {
                if(is != null)
                {
                    is.close();
                }
                if(savedFile != null)
                {
                    savedFile.close();
                }
                if(isCanceled && file != null)
                {
                    file.delete();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    private String getNameFromPath(String path)
    {
        String name = "";
        if(path.contains("/"))
            name = path.substring(path.lastIndexOf("/"));

        return name;
    }

    /**
     * 当在后台任务中调用了publishProgress(Progress...)方法之后，onProgressUpdate()方法
     * 就会很快被调用，该方法中携带的参数就是在后台任务中传递过来的。在这个方法中可以对UI进行操作，利用参数中的数值就可以
     * 对界面进行相应的更新。
     * @param values
     */
    protected void onProgressUpdate(Integer...values){
        int progress=values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    /**
     * 当后台任务执行完毕并通过Return语句进行返回时，这个方法就很快被调用。返回的数据会作为参数
     * 传递到此方法中，可以利用返回的数据来进行一些UI操作。
     * @param status
     */
    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_REMOVE:
                listener.onRemoveLargeSize(removeFileUrl);
                break;
            default:
                break;
        }
    }

    public void  pauseDownload(){
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }

    /**
     * 得到下载内容的大小
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl)
    {
        if(downloadUrl.contains("`"))
            downloadUrl = downloadUrl.replace("`", "");
        OkHttpClient client = new OkHttpClient();
        try
        {
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if(response !=null && response.isSuccessful())
            {
                long contentLength=response.body().contentLength();
                response.body().close();
                return contentLength;

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  0;
    }

    private File getTempFile(File file)
    {
        String filePath = file.getAbsolutePath();
        if(!filePath.endsWith(endTag))
            filePath = filePath + endTag;
        return new File(filePath);
    }

    private File renameFile(File file)
    {
        String filePath = file.getAbsolutePath();
        if(filePath.endsWith(endTag))
            filePath = filePath.replace(endTag, "");
        File newFile = new File(filePath);
        file.renameTo(newFile);

        return newFile;
    }

    /*private long maxRemaiSize = 1024 * 600;

    private void checkAndReleaseSpace(long releaseSize)
    {
    	long localRemainSize = SystemUtils.getAvailableExternalMemorySize() / 1024;
		if(localRemainSize <= maxRemaiSize)// || localRemainSize <= releaseSize / 1024)
		{
    		releaseSize = (maxRemaiSize - localRemainSize) * 1024 + releaseSize;
    		//释放当前所需的空间
    		DBManager.INSTANCE.deleteLastSongRecordBySize(releaseSize);
    		
    		DBManager.INSTANCE.deleteLastSongRecordByIndex(10);
    		
		}
    }*/

}
