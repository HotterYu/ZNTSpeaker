package com.znt.speaker.player;

import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.znt.speaker.entity.LocalDataEntity;

public class TextureVideoPlayer extends TextureView{

    private String url;
    private Context mContext = null;
    public VideoState mState;

    private int mVideoWidth;
    private int mVideoHeight;

    public static final int CENTER_CROP_MODE = 1;
    public static final int CENTER_MODE = 2;
    public int mVideoMode = 0;

    //����״̬
    public enum VideoState{
        init,palying,pause
    }
    public TextureVideoPlayer(Context context) {
        super(context);
        this.mContext = context;
    }

    public TextureVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public TextureVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void updateTextureViewSizeCenterCrop(int mVideoWidth, int mVideoHeight){

    	this.mVideoWidth = mVideoWidth;
    	this.mVideoHeight = mVideoHeight;
    	
        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();
        float maxScale = Math.max(sx, sy);

        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        matrix.postScale(maxScale, maxScale, getWidth() / 2, getHeight() / 2);

        setTransform(matrix);
        postInvalidate();
        
        int w = getWidth();
		int h = getHeight();
		Log.e("", "");
    }
    
    public void updateVideoSize(int mVideoWidth, int mVideoHeight )
    {
    	this.mVideoWidth = mVideoWidth;
    	this.mVideoHeight = mVideoHeight;
    }
    
    public void updateTextureViewSizeCenter()
    {

        float sx = (float) getWidth() / (float) mVideoWidth;
        float sy = (float) getHeight() / (float) mVideoHeight;

        Matrix matrix = new Matrix();

        matrix.preTranslate((getWidth() - mVideoWidth) / 2, (getHeight() - mVideoHeight) / 2);
        matrix.preScale(mVideoWidth / (float) getWidth(), mVideoHeight / (float) getHeight());

        if (sx >= sy){
            matrix.postScale(sy, sy, getWidth() / 2, getHeight() / 2);
        }else{
            matrix.postScale(sx, sx, getWidth() / 2, getHeight() / 2);
        }

        setTransform(matrix);
        postInvalidate();
    }
    
    public void setVideoShow(String type) 
	{
    	if(mVideoHeight == 0 || mVideoWidth == 0)
    		return;
		
    	if(TextUtils.isEmpty(type))
    		type = "0";
    	
    	int typeInt = Integer.parseInt(type);
    	
	    if(typeInt >=4 && typeInt <= 6)
	    {
	    	adjustAspectRatio(typeInt);
	    }
	    else if(typeInt == 7)//
	    	updateTextureViewSizeCenter();//ȫ��
	    else
	    	setLocation(type);
	    
	    
	   
	}
    
    public void adjustAspectRatio(int type) 
	{
    	if(mVideoHeight == 0 || mVideoWidth == 0)
    		return;
    	
    	setVideoSizeFull();
	    	
		
	    int viewWidth = getWidth();
	    int viewHeight = getHeight();
	    double aspectRatio = (double) mVideoHeight / mVideoWidth;

	    int newWidth, newHeight;
	    if (viewHeight > (int) (viewWidth * aspectRatio)) 
	    {
	        // limited by narrow width; restrict height
	        newWidth = viewWidth;
	        newHeight = (int) (viewWidth * aspectRatio);
	    } 
	    else 
	    {
	        // limited by short height; restrict width
	        newWidth = (int) (viewHeight / aspectRatio);
	        newHeight = viewHeight;
	    }
	    
	    int xoff = (viewWidth - newWidth) / 2;
	    int yoff = (viewHeight - newHeight) / 2;

	    Matrix txform = new Matrix();
	    getTransform(txform);
	    txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
	    if(type == 4)
	    {
	    	txform.postTranslate(xoff, yoff);
	    }
	    else if(type == 5)
	    {
	    	txform.postTranslate(0, yoff*2);
	    }
	    else if(type == 6)
	    {
	    	txform.postTranslate(xoff*2, 0);
	    }
	    setTransform(txform);
	}
    
    public void setLocation(String type)
    {
    	RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
	    if(type.equals("8"))
	    {
	    	params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    	params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    	params.width = mVideoWidth;
	    	params.height = mVideoHeight;
	    	setLayoutParams(params);
	    }
	    else if(type.equals("9"))
	    {

	    	params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	params.width = mVideoWidth;
	    	params.height = mVideoHeight;
	    	setLayoutParams(params);
	    }
	    else if(type.equals("10"))
	    {
	    	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    	params.width = mVideoWidth;
	    	params.height = mVideoHeight;
	    	setLayoutParams(params);
	    }
	    else if(type.equals("11"))
	    {
	    	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	params.width = mVideoWidth;
	    	params.height = mVideoHeight;
	    	setLayoutParams(params);
	    }
    }
    
    private void setVideoSizeFull()
	{
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		
		
		RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) getLayoutParams();
		params.height = height;
		
		params.width = width;
		setLayoutParams(params);
	}
    public void setSurfaceViewOritation()
	{
		
		String degree = LocalDataEntity.newInstance(mContext).getVideoWhirl();
		float curDegree = getDegree(LocalDataEntity.newInstance(mContext).getVideoWhirl());
		if(curDegree > 0)
		{
			curDegree = -curDegree;
			setRotation(curDegree);
		}
		if(degree.equals("2"))
			setRotation(180);
		/*else if(degree.equals("3"))
			setRotation(-180);*/
		else
			setRotation(0);
    	

	}
    
    private float getDegree(String degree)
	{
		if(degree.equals("0"))//
			return 0;
		else if(degree.equals("1"))//
			return 90;
		else if(degree.equals("2"))//
			return 180;
		else if(degree.equals("3"))//
			return -90;
		return 0;
	}
    public  double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}