package com.frederick.remocon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.frederick.lib.Logger;

/**
 * Created by Frederick.
 */

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mHolder;
    int drawWidth=0,drawHeight=0;
    int viewWidth,viewHeight;

    public ImageSurfaceView(Context context) {
        super(context);
    }

    public ImageSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);

        //setZOrderOnTop(true);
        //mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    private void getDrawSize(int width,int height){
        double widthRatio = (double)width/viewWidth;
        double heightRatio = (double)height/viewHeight;
        if(widthRatio<heightRatio){
            drawHeight = viewHeight;
            drawWidth = (int)(width/heightRatio);
        }else{
            drawWidth = viewWidth;
            drawHeight = (int)(height/widthRatio);
        }
        Logger.i("img:"+width+"*"+height);
        Logger.i("screen:"+viewWidth+"*"+viewHeight);
        Logger.i("draw:"+drawWidth+"*"+drawHeight);
    }

    public void drawBitmap(Bitmap bitmap){
        //if(drawWidth==0)getDrawSize(bitmap.getWidth(),bitmap.getHeight());
        Canvas mCanvas = mHolder.lockCanvas();
        if(mCanvas!=null) {
            //mCanvas.drawBitmap(bitmap,0,0,null);
            mCanvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),
                    new Rect(0,0,viewWidth,viewHeight),null);
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        viewWidth = i1;
        viewHeight = i2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

