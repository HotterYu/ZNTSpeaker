package com.znt.push.update;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.znt.push.download.DownLoadResult;
import com.znt.push.http.UrlUtil;

import android.os.AsyncTask;
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
public class ApkDownloadTask extends AsyncTask<String,Long,DownLoadResult>
{
    public static final int TYPE_SUCCESS=0;

    public static final int TYPE_FAILED=1;

    public static final int TYPE_PAUSED=2;

    public static final int TYPE_CANCELED=3;

    private ApkDownloadListener listener;

    private boolean isCanceled=false;

    private boolean isPaused=false;

    private long lastProgress;
    private String apkFile = "";
    private String endTag = ".temp";

    public ApkDownloadTask(ApkDownloadListener listener, String filePath) 
    {
    	apkFile = filePath + "/ZNTSpeaker.apk";
        this.listener = listener;
    }
    private long contentLength = 0;
    /**
     * 这个方法中的所有代码都会在子线程中运行，我们应该在这里处理所有的耗时任务。
     * @param params
     * @return
     */
    @Override
    protected DownLoadResult doInBackground(String... params) 
    {
    	
    	DownLoadResult downLoadResult = new DownLoadResult();
    	
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
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "url is null";
        	return downLoadResult;
        }
        	
        int lastIndex = downloadUrl.lastIndexOf("/");
        if(lastIndex < 0)
    	{
	    	downLoadResult.status = TYPE_FAILED;
	    	downLoadResult.hint = "lastIndex char / < 0";
	    	return downLoadResult;
    	}
        //创建一个文件
        file = new File(apkFile);
        if(file.exists())
        {
            //如果文件存在的话，得到文件的大小
        	downLoadResult.status = TYPE_SUCCESS;
	    	downLoadResult.fileSize = file.length();
	    	downLoadResult.file = file;
        	return downLoadResult;
        }
        
        //得到下载内容的大小
        contentLength = getContentLength(downloadUrl);
        if(contentLength <= 0)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "contentLength :" + contentLength;
            return downLoadResult;
        }
        if(listener != null)
        	listener.onSpaceCheck(contentLength);
        //DBManager.INSTANCE.checkAndReleaseSpace(contentLength);//释放空间
        
        File tempFile = new File(apkFile + ".temp");
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
                    	downLoadResult.status = TYPE_CANCELED;
                    	downLoadResult.hint = "canceled";
                        return downLoadResult;
                    }
                    else if(isPaused)
                    {
                    	downLoadResult.status = TYPE_PAUSED;
                    	downLoadResult.hint = "paused";
                        return downLoadResult;
                    }
                    else 
                    {
                    	downloadLength += len;
                        savedFile.write(b,0,len);
                        //计算已经下载的百分比
                        //int progress = (int) ((downloadLength) * 100 / contentLength);
                        //Log.e("", "****************progress-->"+progress);
                        //注意：在doInBackground()中是不可以进行UI操作的，如果需要更新UI,比如说反馈当前任务的执行进度，
                        //可以调用publishProgress()方法完成。
                        publishProgress(downloadLength, contentLength);
                    }
                }
                
                response.body().close();
                
                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {
                	//文件完全下载成功
                	downLoadResult.file = renameFile(tempFile);
                }
                else
                {
                	tempFile.delete();
                	downLoadResult.status = TYPE_FAILED;
                	downLoadResult.hint = "download success, but file length not correct or not exist";
                	return downLoadResult;
                }
                downLoadResult.status = TYPE_SUCCESS;
                return downLoadResult;
            }
        } 
        catch (Exception e)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "download Exception :" + e.getMessage();
            e.printStackTrace();
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
            	downLoadResult.status = TYPE_FAILED;
            	downLoadResult.hint = "download Exception :" + e.getMessage();
                e.printStackTrace();
            }
        }
        
        return downLoadResult;
    }

    /**
     * 当在后台任务中调用了publishProgress(Progress...)方法之后，onProgressUpdate()方法
     * 就会很快被调用，该方法中携带的参数就是在后台任务中传递过来的。在这个方法中可以对UI进行操作，利用参数中的数值就可以
     * 对界面进行相应的更新。
     * @param values
     */
    @Override
    protected void onProgressUpdate(Long...values){
    	long progress=values[0];
        if(progress>lastProgress){
            listener.onDownloadProgress(progress, values[1]);
            lastProgress=progress;
        }
    }

    /**
     * 当后台任务执行完毕并通过Return语句进行返回时，这个方法就很快被调用。返回的数据会作为参数
     * 传递到此方法中，可以利用返回的数据来进行一些UI操作。
     * @param status
     */
    @Override
    protected void onPostExecute(DownLoadResult downLoadResult) {
    	int status = downLoadResult.status;
        switch (status){
            case TYPE_SUCCESS:
                listener.onDownloadFinish(downLoadResult.file);
                break;
            case TYPE_FAILED:
                listener.onDownloadError(null, downLoadResult.hint);
                break;
            case TYPE_PAUSED:
               // listener.onPaused();
                break;
            case TYPE_CANCELED:
                //listener.onCanceled();
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
                long contentLength = response.body().contentLength();
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
    	File renameFile = new File(filePath);
    	file.renameTo(renameFile);
    	return renameFile;
    }
    
    /*private void checkAndReleaseSpace(long releaseSize)
    {
    	long localRemainSize = SystemUtils.getAvailableExternalMemorySize() / 1024;
		if(localRemainSize <= 1024 * 600)// || localRemainSize <= releaseSize / 1024)
		{
    		releaseSize = (1024 * 600 - localRemainSize) * 1024 + releaseSize;
    		DBManager.INSTANCE.deleteLastSongRecordBySize(releaseSize);//释放当前所需的空间
		}
    }
    */
}
