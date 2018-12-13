package com.znt.speaker.factory;

import com.znt.diange.mina.entity.UpdateInfor;

public interface IJsonParse 
{

	public UpdateInfor parseUpdateInfor(String jsonStr);
	
}
