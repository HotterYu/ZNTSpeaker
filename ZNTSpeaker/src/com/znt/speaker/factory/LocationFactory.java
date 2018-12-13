package com.znt.speaker.factory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.entity.LocationInfor;
import com.znt.speaker.p.HttpPresenter;
import com.znt.speaker.v.IHttpRequestView;
import com.znt.utils.LogFactory;
import com.znt.utils.SystemUtils;

public class LocationFactory implements 
AMapLocationListener, OnGeocodeSearchListener, OnPoiSearchListener, IHttpRequestView
{

	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 鐢ㄤ簬鍒ゆ柇瀹氫綅瓒呮椂
	private Handler handler = new Handler();
	private GeocodeSearch geocoderSearch;
	
	private PoiResult poiResult; // poi杩斿洖鐨勭粨鏋�
	private int currentPage = 0;// 褰撳墠椤甸潰锛屼粠0寮�濮嬭鏁�
	private PoiSearch.Query query;// Poi鏌ヨ鏉′欢绫�
	private PoiSearch poiSearch;// POI鎼滅储
	
	private boolean isFirst = true;
	private boolean isStop = false;
	private boolean isNearBy = true;
	
	private List<PoiItem> poiNear = new ArrayList<PoiItem>();
	private List<PoiItem> poiList = new ArrayList<PoiItem>();
	
	private LocationInfor locationInfor = null;
	
	private String poiProvince = null;
	private String poiCity = null;
	private String poiStrict = null;
	private HttpPresenter httpPresenter = null;
	private int retryTime = 0;
	
	private Activity activity = null;
	
	
	public LocationFactory(Activity activity)
	{
		this.activity = activity;
		locationInfor = new LocationInfor();
		httpPresenter = new HttpPresenter(activity, this);
	}
	
	public void startLocation()
	{
		
		if(SystemUtils.isNetConnected(activity))
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					showToast("寮�濮嬪畾浣�");
					stopLocation();
					initMapLoc();
				}
			}).start();
		}
		else
			showToast("缃戠粶寮傚父");
	}
	
	public void initMapLoc()
	{
		isStop = false;
		isFirst = true;
		aMapLocManager = LocationManagerProxy.getInstance(activity);
		aMapLocManager.setGpsEnable(true);
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				if (aMapLocation == null)
				{
					if(!isStop)
					{
						retryTime ++;
						if(retryTime > 10)
						{
							isStop = true;
							retryTime = 0;
							showToast("瀹氫綅澶辫触");
						}
						else
							startLocation();// 閲嶆柊寮�濮嬪畾浣�
						//showToast("浣嶇疆鑾峰彇澶辫触");
					}
				}
			}
		}, 12000);// 璁剧疆瓒呰繃12绉掕繕娌℃湁瀹氫綅鍒板氨鍋滄瀹氫綅
	}
	
	private void startSearch(final LatLonPoint latLonPoint)
	{
		geocoderSearch = new GeocodeSearch(activity);
		geocoderSearch.setOnGeocodeSearchListener(this);
		RegeocodeQuery regecodeQuery = new RegeocodeQuery(latLonPoint, 10000,
				GeocodeSearch.AMAP);// 绗竴涓弬鏁拌〃绀轰竴涓狶atlng锛岀浜屽弬鏁拌〃绀鸿寖鍥村灏戠背锛岀涓変釜鍙傛暟琛ㄧず鏄伀绯诲潗鏍囩郴杩樻槸GPS鍘熺敓鍧愭爣绯�
		geocoderSearch.getFromLocationAsyn(regecodeQuery);// 璁剧疆鍚屾閫嗗湴鐞嗙紪鐮佽姹�
	}
	
	/**
	 * 閿�姣佸畾浣�
	 */
	public void stopLocation()
	{
		if (aMapLocManager != null) 
		{
			try {
				isStop = true;
				aMapLocManager.removeUpdates(this);
				aMapLocManager.destory();
				aMapLocManager = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0)
		{
			if (result != null && result.getQuery() != null)
			{
				// 鎼滅储poi鐨勭粨鏋�
				if (result.getQuery().equals(query)) 
				{
					// 鏄惁鏄悓涓�鏉�
					poiResult = result;
					// 鍙栧緱鎼滅储鍒扮殑poiitems鏈夊灏戦〉
					List<PoiItem> tempList = poiResult.getPois();
					if(tempList != null)
					{
						poiList.addAll(tempList);// 鍙栧緱绗竴椤电殑poiitem鏁版嵁锛岄〉鏁颁粠鏁板瓧0寮�濮�
						/*List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();// 褰撴悳绱笉鍒皃oiitem鏁版嵁鏃讹紝浼氳繑鍥炲惈鏈夋悳绱㈠叧閿瓧鐨勫煄甯備俊鎭�
						*/
						showToast("浣嶇疆鑾峰彇鎴愬姛-->"+poiList.get(0).getTitle());
						LogFactory.createLog().e("浣嶇疆鑾峰彇鎴愬姛-->"+poiList.get(0).getTitle());
						LocalDataEntity.newInstance(activity)
						.setDeviceAddr(poiList.get(0).getTitle());
						
					}
					else
						poiList.clear();
				}
			} 
			else 
			{
				showToast("瀹氫綅鏃犵粨鏋�");
			}
		} 
		else if (rCode == 27) 
		{
			showToast("缃戠粶閿欒");
		} 
		else if (rCode == 32) 
		{
			showToast("key鏃犳晥");
		} 
		else 
		{
			//showToast(getString(R.string.error_other) + rCode);
		}
		httpPresenter.register();
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) 
	{
		// TODO Auto-generated method stub
		if (location != null) 
		{
			if(isFirst)
			{
				isFirst = false;
				isStop = true;
				this.aMapLocation = location;// 鍒ゆ柇瓒呮椂鏈哄埗
				
				locationInfor.setLat(location.getLatitude() + "");
				locationInfor.setLon(location.getLongitude() + "");
				
				if(!TextUtils.isEmpty(location.getCity()))
					poiCity = location.getCity();
				if(!TextUtils.isEmpty(location.getProvince()))
					poiProvince = location.getProvince();
				if(!TextUtils.isEmpty(location.getDistrict()))
					poiStrict = location.getDistrict();
				locationInfor.setCity(poiCity);
				locationInfor.setProvince(poiProvince);
				locationInfor.setDistrict(poiStrict);
				
				locationInfor.setPoi(location.getStreet());
				
				Bundle locBundle = location.getExtras();
				String desc = locBundle.getString("desc");
				//showToast("瀹氫綅鎴愬姛,鍑嗗鎼滅储浣嶇疆-->"+desc);
				
				LocalDataEntity.newInstance(activity)
				.setDeviceAddr(desc);
				
				//淇濆瓨鍧愭爣
				LocalDataEntity.newInstance(activity)
				.setDeviceLocation(locationInfor.getLon() + "", locationInfor.getLat() + "");
				httpPresenter.register();
				//LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
				//startSearch(latLonPoint);//鎸夌収鍧愭爣鎼滅储
			}
		}
		else
			LogFactory.createLog().e("############-->鑾峰彇鍧愭爣澶辫触浜�");
	}
	
	private void showToast(final String text)
	{
		/*activity.runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Toast.makeText(activity, text, 0).show();
			}
		});*/
	}

	@Override
	public void requestStart(int requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestError(int requestId, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestSuccess(Object obj, int requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestNetWorkError() {
		// TODO Auto-generated method stub
		
	}
	
}
