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
 * String ��ִ��AsyncTaskʱ��Ҫ����Ĳ������������ں�̨������ʹ�á�
 * Integer ��̨����ִ��ʱ�������Ҫ�ڽ�������ʾ��ǰ�Ľ��ȣ���ʹ������ָ���ķ�����Ϊ���ȵ�λ��
 * Integer ������ִ����Ϻ������Ҫ�Խ�����з��أ���ʹ������ָ���ķ�����Ϊ����ֵ���͡�
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
     * ��������е����д��붼�������߳������У�����Ӧ�������ﴦ�����еĺ�ʱ����
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
        long downloadLength = 0;   //��¼�Ѿ����ص��ļ�����
        //�ļ����ص�ַ
        String downloadUrl = params[0];
        downloadUrl = UrlUtil.decode(downloadUrl, "UTF-8");
        //downloadUrl = UrlUtil.getASCIIEncodedUrl(downloadUrl, "UTF-8");
        //�����ļ�������
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
        //����һ���ļ�
        file = new File(apkFile);
        if(file.exists())
        {
            //����ļ����ڵĻ����õ��ļ��Ĵ�С
        	downLoadResult.status = TYPE_SUCCESS;
	    	downLoadResult.fileSize = file.length();
	    	downLoadResult.file = file;
        	return downLoadResult;
        }
        
        //�õ��������ݵĴ�С
        contentLength = getContentLength(downloadUrl);
        if(contentLength <= 0)
        {
        	downLoadResult.status = TYPE_FAILED;
        	downLoadResult.hint = "contentLength :" + contentLength;
            return downLoadResult;
        }
        if(listener != null)
        	listener.onSpaceCheck(contentLength);
        //DBManager.INSTANCE.checkAndReleaseSpace(contentLength);//�ͷſռ�
        
        File tempFile = new File(apkFile + ".temp");
        if(tempFile.exists())
        	downloadLength = tempFile.length();
        
        /*else if(contentLength == downloadLength)
        {
            //�������ֽں��ļ����ֽ���ȣ�˵���Ѿ����������
            return TYPE_SUCCESS;
        }*/
        OkHttpClient client = new OkHttpClient();
        /**
         * HTTP��������һ��Header�ģ������и�Range�����Ƕ�����������ģ������յ�ֵ��һ�����䷶Χ��
         * ���磺Range:bytes=0-10000���������ǾͿ��԰���һ���Ĺ��򣬽�һ�����ļ����Ϊ���ɺ�С�Ĳ��֣�
         * Ȼ������ε����أ�ÿ��С���������֮���ٺϲ����ļ��У�������ʹ�����ж��ˣ���������ʱ��
         * Ҳ����ͨ���ļ����ֽڳ������ж����ص���ʼ�㣬Ȼ�������ϵ������Ĺ��̣�ֱ�����������ع��̡�
         */
        Request request = new Request.Builder()
                .addHeader("RANGE","bytes=" + downloadLength+"-")  //�ϵ�����Ҫ�õ��ģ�ָʾ���ص�����
                .url(downloadUrl)
                .build();
        try 
        {
            Response response=client.newCall(request).execute();
            if(response!=null)
            {
                is = response.body().byteStream();
                
                savedFile = new RandomAccessFile(tempFile,"rw");
                savedFile.seek(downloadLength);//�����Ѿ����ص��ֽ�
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
                        //�����Ѿ����صİٷֱ�
                        //int progress = (int) ((downloadLength) * 100 / contentLength);
                        //Log.e("", "****************progress-->"+progress);
                        //ע�⣺��doInBackground()���ǲ����Խ���UI�����ģ������Ҫ����UI,����˵������ǰ�����ִ�н��ȣ�
                        //���Ե���publishProgress()������ɡ�
                        publishProgress(downloadLength, contentLength);
                    }
                }
                
                response.body().close();
                
                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {
                	//�ļ���ȫ���سɹ�
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
     * ���ں�̨�����е�����publishProgress(Progress...)����֮��onProgressUpdate()����
     * �ͻ�ܿ챻���ã��÷�����Я���Ĳ��������ں�̨�����д��ݹ����ġ�����������п��Զ�UI���в��������ò����е���ֵ�Ϳ���
     * �Խ��������Ӧ�ĸ��¡�
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
     * ����̨����ִ����ϲ�ͨ��Return�����з���ʱ����������ͺܿ챻���á����ص����ݻ���Ϊ����
     * ���ݵ��˷����У��������÷��ص�����������һЩUI������
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
     * �õ��������ݵĴ�С
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
    		DBManager.INSTANCE.deleteLastSongRecordBySize(releaseSize);//�ͷŵ�ǰ����Ŀռ�
		}
    }
    */
}
