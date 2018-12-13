/*package com.znt.speaker.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager;

import com.znt.speaker.R;

public class VideSizeActivity extends Activity implements SurfaceTextureListener,

OnLayoutChangeListener 
{
	private TextureView textureView;
	Matrix matrix;
	Camera camera;
	int mWidth = 0;
	int mHeight = 0;
	int mDisplayWidth = 0;
	int mDisplayHeight = 0;
	int mPreviewWidth = 640;
	int mPreviewHeight = 480;
	int orientation = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textureView = (TextureView) findViewById(R.id.texture);
		textureView.addOnLayoutChangeListener(this);
		textureView.setSurfaceTextureListener(this);
		camera = Camera.open();
	}
	
	
	@Override
	public void onLayoutChange(View v, int left, int top, int right,
	int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
	// TODO Auto-generated method stub
		mWidth = right - left;
		mHeight = bottom - top;
	}
	
	
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
	int height) 
	{
		// TODO Auto-generated method stub 
		RectF previewRect = new RectF(0, 0, mWidth, mHeight);
		double aspect = (double) mPreviewWidth / mPreviewHeight;
		
		        if (getResources().getConfiguration().orientation
		                == Configuration.ORIENTATION_PORTRAIT) 
		        {
		        	aspect = 1 / aspect;
		        }
		
		if(mWidth < (mHeight * aspect)) 
		{
			mDisplayWidth = mWidth;
			mDisplayHeight = (int) (mHeight * aspect + .5); 
		} 
		else 
		{  
			mDisplayWidth = (int) (mWidth / aspect + .5);
			mDisplayHeight = mHeight; 
		}
		
		RectF surfaceDimensions = new RectF(0,0,mDisplayWidth,mDisplayHeight);
		Matrix matrix = new Matrix();
		matrix.setRectToRect(previewRect, surfaceDimensions, Matrix.ScaleToFit.FILL); 
		textureView.setTransform(matrix);
		Camera.Parameters param = camera.getParameters();
		int displayRotation = 0;
		
		        WindowManager windowManager = (WindowManager) VideSizeActivity.this
		                .getSystemService(Context.WINDOW_SERVICE);
		int rotation = windowManager.getDefaultDisplay().getRotation(); 
		
		
		        switch (rotation) {
		            case Surface.ROTATION_0:
		            displayRotation =  0;
		            break;
		            case Surface.ROTATION_90:
		            displayRotation =  90;
		            break;
		            case Surface.ROTATION_180:
		            displayRotation =  180;
		            break;
		            case Surface.ROTATION_270:
		            displayRotation =  270;
		            break;
		        }        
		
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(0, info);
		
		if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){ 
		orientation = (info.orientation - displayRotation + 360)% 360;
		} else { 
		orientation = (info.orientation + displayRotation)% 360;
		orientation = (360 - orientation) % 360;
		}
		
		param.setPreviewSize(mPreviewWidth, mPreviewHeight);
		camera.setParameters(param);
		camera.setDisplayOrientation(orientation);
		try {
		camera.setPreviewTexture(surface);
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		camera.startPreview(); 
	}
	
	
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
	// TODO Auto-generated method stub
	camera.stopPreview();
	camera.release();
	camera = null;
	return true;
	}
	
	
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int width,
	int height) {
	// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
	// TODO Auto-generated method stub
	}
}*/