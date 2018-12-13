package com.znt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

public class SDCardUtils 
{
    private String getSDTotalSize(Context context) 
    {  
        File path = Environment.getExternalStorageDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(context, blockSize * totalBlocks);  
    }  
  
    private String getSDAvailableSize(Context context) 
    {  
        File path = Environment.getExternalStorageDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        return Formatter.formatFileSize(context, blockSize * availableBlocks);  
    }  
  
    private String getRomTotalSize(Context context) 
    {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(context, blockSize * totalBlocks);  
    }  
  
    private String getRomAvailableSize(Context context) 
    {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        return Formatter.formatFileSize(context, blockSize * availableBlocks);  
    } 
    
    
    /**
	* @Description: 鑾峰彇鎵�鏈夌殑瀛樺偍璁惧鍒楄�?
	* @param @return   
	* @return ArrayList<String> 
	* @throws
	 */
    public static List<String> getAllExternalSdcardPath() {
        List<String> PathList = new ArrayList<String>();

        String firstPath = Environment.getExternalStorageDirectory().getPath();

        try {
        // 运行mount命令，获取命令的输出，得到系统中挂载的所有目�?
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // 将常见的linux分区过滤�?
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }

                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))){
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路�?
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1){
                        String path = items[1].toLowerCase(Locale.getDefault());
                        // 添加�?些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条�?
                        if (path != null && !PathList.contains(path) && path.contains("sd"))
                            PathList.add(items[1]);
                    }
                }
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!PathList.contains(firstPath)) {
            PathList.add(firstPath);
        }

        return PathList;
    }
    
}
