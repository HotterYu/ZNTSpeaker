/*package com.znt.speaker.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.AsyncTask;
import android.os.Environment;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.utils.UrlUtil;

*//**
 * Created by Administrator on 2017/2/23.
 *//*

*//**
 * String ��ִ��AsyncTaskʱ��Ҫ����Ĳ������������ں�̨������ʹ�á�
 * Integer ��̨����ִ��ʱ�������Ҫ�ڽ�������ʾ��ǰ�Ľ��ȣ���ʹ������ָ���ķ�����Ϊ���ȵ�λ��
 * Integer ������ִ����Ϻ������Ҫ�Խ�����з��أ���ʹ������ָ���ķ�����Ϊ����ֵ���͡�
 *//*
public class DownloadTask extends AsyncTask<String,Integer,Integer>
{
    public static final int TYPE_SUCCESS=0;

    public static final int TYPE_FAILED=1;

    public static final int TYPE_PAUSED=2;

    public static final int TYPE_CANCELED=3;

    private DownloadListener listener;

    private boolean isCanceled=false;

    private boolean isPaused=false;

    private int lastProgress;
    
    private String directory = Environment.getExternalStorageDirectory() + "";//�����ļ���ŵ�Ŀ¼
    
    private String endTag = ".temp";

    public DownloadTask(DownloadListener listener) 
    {
        this.listener = listener;
    }

    *//**
     * ��������е����д��붼�������߳������У�����Ӧ�������ﴦ�����еĺ�ʱ����
     * @param params
     * @return
     *//*
    @Override
    protected Integer doInBackground(String... params) 
    {
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
        	return TYPE_FAILED;
        int lastIndex = downloadUrl.lastIndexOf("/");
        if(lastIndex < 0)
        	return TYPE_FAILED;
        String fileName = downloadUrl.substring(lastIndex);
        //����һ���ļ�
        
        file = new File(directory+fileName);
        if(file.exists())
        {
            //����ļ����ڵĻ����õ��ļ��Ĵ�С
        	return TYPE_SUCCESS;
        }
        
        //�õ��������ݵĴ�С
        long contentLength = getContentLength(downloadUrl);
        if(contentLength <= 0)
        {
            return TYPE_FAILED;
        }
        //checkAndReleaseSpace(contentLength);//�ͷſռ�
        DBManager.INSTANCE.checkAndReleaseSpace(contentLength);
        
        File tempFile = new File(directory + fileName + ".temp");
        if(tempFile.exists())
        	downloadLength = tempFile.length();
        
        else if(contentLength == downloadLength)
        {
            //�������ֽں��ļ����ֽ���ȣ�˵���Ѿ����������
            return TYPE_SUCCESS;
        }
        OkHttpClient client = new OkHttpClient();
        *//**
         * HTTP��������һ��Header�ģ������и�Range�����Ƕ�����������ģ������յ�ֵ��һ�����䷶Χ��
         * ���磺Range:bytes=0-10000���������ǾͿ��԰���һ���Ĺ��򣬽�һ�����ļ����Ϊ���ɺ�С�Ĳ��֣�
         * Ȼ������ε����أ�ÿ��С���������֮���ٺϲ����ļ��У�������ʹ�����ж��ˣ���������ʱ��
         * Ҳ����ͨ���ļ����ֽڳ������ж����ص���ʼ�㣬Ȼ�������ϵ������Ĺ��̣�ֱ�����������ع��̡�
         *//*
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
                        //�����Ѿ����صİٷֱ�
                        //int progress = (int)((total + downloadLength) * 100 / contentLength);
                        //Log.e("", "****************progress-->"+progress);
                        //ע�⣺��doInBackground()���ǲ����Խ���UI�����ģ������Ҫ����UI,����˵������ǰ�����ִ�н��ȣ�
                        //���Ե���publishProgress()������ɡ�
                        //publishProgress(progress);
                    }
                }
                
                response.body().close();
                
                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {
                	//�ļ���ȫ���سɹ�
                	renameFile(tempFile);
                	SongInfor infor = new SongInfor();
					infor.setMediaUrl(tempFile.getAbsolutePath());
					infor.setMediaName(getNameFromPath(tempFile.getAbsolutePath()));
					//infor.setMediaSize(file.length());
					DBManager.INSTANCE.insertSongRecord(infor, file.lastModified());
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

    *//**
     * ���ں�̨�����е�����publishProgress(Progress...)����֮��onProgressUpdate()����
     * �ͻ�ܿ챻���ã��÷�����Я���Ĳ��������ں�̨�����д��ݹ����ġ�����������п��Զ�UI���в��������ò����е���ֵ�Ϳ���
     * �Խ��������Ӧ�ĸ��¡�
     * @param values
     *//*
    protected void onProgressUpdate(Integer...values){
        int progress=values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    *//**
     * ����̨����ִ����ϲ�ͨ��Return�����з���ʱ����������ͺܿ챻���á����ص����ݻ���Ϊ����
     * ���ݵ��˷����У��������÷��ص�����������һЩUI������
     * @param status
     *//*
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

    *//**
     * �õ��������ݵĴ�С
     * @param downloadUrl
     * @return
     *//*
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
    
    private void renameFile(File file)
    {
    	String filePath = file.getAbsolutePath();
    	if(filePath.endsWith(endTag))
    		filePath = filePath.replace(endTag, "");
    	file.renameTo(new File(filePath));
    }
    
    private long maxRemaiSize = 1024 * 600;
    
    private void checkAndReleaseSpace(long releaseSize)
    {
    	long localRemainSize = SystemUtils.getAvailableExternalMemorySize() / 1024;
		if(localRemainSize <= maxRemaiSize)// || localRemainSize <= releaseSize / 1024)
		{
    		releaseSize = (maxRemaiSize - localRemainSize) * 1024 + releaseSize;
    		//�ͷŵ�ǰ����Ŀռ�
    		DBManager.INSTANCE.deleteLastSongRecordBySize(releaseSize);
    		
    		DBManager.INSTANCE.deleteLastSongRecordByIndex(10);
    		
		}
    }
    
}
*/