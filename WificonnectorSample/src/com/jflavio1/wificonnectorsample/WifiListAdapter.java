package com.jflavio1.wificonnectorsample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter
{
	
	private Context mContext = null;
	
	private List<ScanResult> scanResultList = new ArrayList<ScanResult>();
	
	public WifiListAdapter(Context mContext)
	{
		this.mContext = mContext;
	}
	
	public void setScanResultList(List<ScanResult> scanResultList) 
    {
        this.scanResultList = scanResultList;
        notifyDataSetChanged();
    }

	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return scanResultList.size();
	}

	@Override
	public Object getItem(int position) 
	{
		// TODO Auto-generated method stub
		return scanResultList.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.accesspoint_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.apItem_name);
			vh.tvIntensity = (TextView)convertView.findViewById(R.id.apItem_intensity);
			convertView.setTag(vh); 
		}
		else
			vh = (ViewHolder)convertView.getTag(); 
		
		ScanResult scanResult = scanResultList.get(position); 
		vh.tvName.setText(scanResult.SSID);
		vh.tvIntensity.setText(WifiManager.calculateSignalLevel(scanResult.level, 100) + "%");
		
		return convertView;
	}
	
	public class ViewHolder
	{
		public TextView tvName = null;
		public TextView tvIntensity = null;
	}
}
