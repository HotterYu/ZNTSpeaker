package com.znt.utils;

import android.os.Parcelable;

public class FormatUtil {  
    public static double pers = 1048576; //1024*1024  
      
    //long==> 616.19KB,3.73M   
    public static String sizeFormatNum2String(long size) { 
    	
    	
        String s = "";  
        if(size>1024*1024)  
            s=String.format("%.2f", (double)size/pers)+"M";  
        else   
            s=String.format("%.2f", (double)size/(1024))+"KB";  
        return s;  
    }  
      
    //616.19KB,3.73M ==> long  
    public static long sizeFormatString2Num(String str){  
        long size = 0;  
        if(str!=null){  
            if(str.endsWith("KB"))  
                size = (long)(Double.parseDouble(str.substring(0,str.length()-2))*1024);  
            else if(str.endsWith("M"))  
                size = (long)(Double.parseDouble(str.substring(0,str.length()-1))*pers);  
        }  
        return size;  
          
    }  
}  
