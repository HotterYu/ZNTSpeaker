package com.znt.speaker.entity;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.utils.FileUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prize on 2019/1/3.
 */

public class PlayListFilter implements Serializable
{


    private List<SongInfor> srcList = new ArrayList<>();

    private List<String> imgUrls = new ArrayList<>();
    private List<String> imgNames = new ArrayList<>();
    private List<SongInfor> mediaList = new ArrayList<>();


    public void fileMediaList()
    {
        for(int i=0;i<srcList.size();i++)
        {
            SongInfor tempSong = srcList.get(i);
            String temp = tempSong.getMediaUrl();
            if(FileUtils.isPicture(temp))
            {
                imgUrls.add(temp);
                imgNames.add(srcList.get(i).getMediaName());
            }
            else
            {
                mediaList.add(tempSong);
            }
        }
    }

    public List<SongInfor> getSrcList() {
        return srcList;
    }

    public void setSrcList(List<SongInfor> srcList) {
        this.srcList = srcList;
        fileMediaList();
    }

    public String getCurImgName(int index)
    {
        if(imgNames.size() > 0)
        {
            if(index >= imgNames.size())
                index = 0;
            return imgNames.get(index);
        }
        return "";
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public List<String> getImgNames() {
        return imgNames;
    }

    public void setImgNames(List<String> imgNames) {
        this.imgNames = imgNames;
    }

    public List<SongInfor> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<SongInfor> mediaList) {
        this.mediaList = mediaList;
    }
}
