package com.znt.push.m;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.znt.push.http.HttpAPI;
import com.znt.push.http.HttpRequestID;
import com.znt.push.http.callback.CheckUpdateCallBack;
import com.znt.push.http.callback.DevStatusCallBack;
import com.znt.push.utils.SystemUtils;
import com.znt.push.v.IHttpRequestView;

@SuppressLint("UseSparseArrays")
public class HttpRequestModel extends HttpAPI
{
	
	private final String mBaseUrl = "";
	private final int MAX_PAGE_SIZE = 300;
	private Context activity = null;
	private IHttpRequestView iHttpRequestView = null;
	
	private volatile HashMap<Integer, Boolean> runStatus = new HashMap<Integer, Boolean>();
	
	public HttpRequestModel(Context activity, IHttpRequestView iHttpRequestView)
	{
		this.activity = activity;
		this.iHttpRequestView = iHttpRequestView;
	}
	
	private void requestStart(final int requestId)
	{
		iHttpRequestView.requestStart(requestId);
	}
	private void requestError(final int requestId, final String error)
	{
		iHttpRequestView.requestError(requestId, error);
	}
	private void requestSuccess(final String obj, final int requestId)
	{
		iHttpRequestView.requestSuccess(obj, requestId);
	}
	private void requestNetWorkError()
	{
		iHttpRequestView.requestNetWorkError();
	}
	
	private void setRunStatusDoing(int key)
	{
		runStatus.put(key, true);
	}
	private void setRunStatusFinish(int key)
	{
		runStatus.put(key, false);
	}
	public boolean isRuning(int key)
	{
		if(runStatus.containsKey(key))
			return runStatus.get(key);
		return false;
	}
	
	
	private int isUpdateRunningCheckCount = 0;
	/**
	 * 妫�鏌ユ洿鏂�?
	 * @param params
	 */
	public void checkUpdate()
	{
		
		int requestId = HttpRequestID.CHECK_UPDATE;
		/*if(isRuning(requestId))
		{
			if(isUpdateRunningCheckCount >= 3)
			{
				setRunStatusFinish(requestId);
				isUpdateRunningCheckCount = 0;
			}
			else
				isUpdateRunningCheckCount ++;
			return;
		}*/
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("softName", "speaker");
		
		OkHttpUtils//
		.get()//
		.url(CHECK_UPDATE)//
		.id(HttpRequestID.CHECK_UPDATE)
		.params(params)//
		.build()//
		.execute(new CheckUpdateCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId, e.getMessage());
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId, response + "");
					return;
        		}
				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	
	private int isDevRunningCheckCount = 0;
	
	/**
	 * 鑾峰彇鐘舵�佷俊鎭�
	 * @param params
	 */
	public void getDevStatus(int playSeek, String playingSong, int playingSongType, String id, String netInfo)
	{
		int requestId = HttpRequestID.GET_DEVICE_STATUS;
		if(!SystemUtils.isNetConnected(activity))
		{
			requestError(requestId, "net work error");
			return;
		}
		if(isRuning(requestId))
		{
			if(isDevRunningCheckCount >= 3)
			{
				setRunStatusFinish(requestId);
				isDevRunningCheckCount = 0;
			}
			else
				isDevRunningCheckCount ++;
			return;
		}
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("id", id);
		params.put("playSeek", playSeek + "");
		params.put("playingSong", playingSong);
		params.put("playingSongType", playingSongType + "");
		params.put("netInfo", netInfo);
		
		OkHttpUtils//
		.get()//
		.url(GET_DEVICE_STATUS)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new DevStatusCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				setRunStatusFinish(requestId);
				requestError(requestId, e.getMessage());
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				setRunStatusFinish(requestId);
				/*if(response == null)
        		{
					requestError(requestId, response + "");
					return;
        		}*/
				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}



    public void getHttpsHtml(View view)
    {
        String url = "https://kyfw.12306.cn/otn/";

//                "https://12
//        url =3.125.219.144:8443/mobileConnect/MobileConnect/authLogin.action?systemid=100009&mobile=13260284063&pipe=2&reqtime=1422986580048&ispin=2";
        OkHttpUtils
                .get()//
                .url(url)//
                .id(101)
                .build()//
                .execute(new MyStringCallback());

    }

    public void getImage(View view)
    {
       // mTv.setText("");
        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        //mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id)
                    {
                        Log.e("TAG", "onResponse閿涙瓭omplete");
                       // mImageView.setImageBitmap(bitmap);
                    }
                });
    }


    public void uploadFile(View view)
    {

        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists())
        {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "瀵�?缂氬ú锟�");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = mBaseUrl + "user!uploadFile";

        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void multiFileUpload(View view)
    {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists())
        {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "瀵�?缂氬ú锟�");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void downloadFile(View view)
    {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/okhttputils-2_4_1.jar?raw=true";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                       // mProgressBar.setProgress((int) (100 * progress));
                        //Log.e(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        //Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        //Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    public void otherRequestDemo(View view)
    {
        //also can use delete ,head , patch
        /*
        OkHttpUtils
                .put()//
                .url("http://11111.com")
                .requestBody
                        ("may be something")//
                .build()//
                .execute(new MyStringCallback());



        OkHttpUtils
                .head()//
                .url(url)
                .addParams("name", "zhy")
                .build()
                .execute();

       */


    }

    public void clearSession(View view)
    {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }

    public void cancelHttp(int requestId)
    {
    	OkHttpUtils.getInstance().cancelTag(requestId);
    	//OkHttpUtils.getInstance().cancelTag(this);
    }
    
	public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            //setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            //setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            //mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            //Log.e(TAG, "onResponse閿涙瓭omplete");
            //mTv.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    //Toast.makeText(MainActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    //Toast.makeText(MainActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            //Log.e(TAG, "inProgress:" + progress);
            //mProgressBar.setProgress((int) (100 * progress));
        }
    }
	
	protected String RESULT_INFO = "info";
	protected String RESULT_OK = "result";
	protected String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}