package com.example.getlocationdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

import static android.graphics.PorterDuff.Mode.DST_OUT;

public class PaintView extends View {


    private Bitmap mBitmap,oldbm1,oldbm2,mBitmap1;
    private Paint mPaint1;
    private Paint mPaint2;
    private float radius;
    private float mX1,mY1;
    private float oldW1,oldH1;
    private float newW1,newH1;
    private float oldW2,oldH2;
    private float newW2,newH2;
    private boolean ismove;
    private Canvas mCanvas;
    private int dem=1;


    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PaintView(Context context, AttributeSet attrs) {

        super(context, attrs);



    }

    public void init(DisplayMetrics metrics) {

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mBitmap);

    }


    public void setbm(Bitmap bm, float newW, float newH) {
       if(dem==1) {
           this.oldW1 = bm.getWidth();
           this.oldH1 = bm.getHeight();
           Log.d("BM", bm.toString());

           ///resize

           mBitmap1 = bm;
           oldbm1 = bm.copy(Bitmap.Config.ARGB_8888, true);

           if (newH > 0 && newH > 0) {
               int width = bm.getWidth();
               int height = bm.getHeight();
               float ratioBitmap = (float) width / (float) height;
               float ratioMax = (float) newW / (float) newH;

               int finalWidth = (int) newW;
               int finalHeight = (int) newH;
               if (ratioMax > ratioBitmap) {
                   finalWidth = (int) ((float) newH * ratioBitmap);
               } else {
                   finalHeight = (int) ((float) newW / ratioBitmap);
               }
               this.newW1 = (float) finalWidth;
               this.newH1 = (float) finalHeight;
               mBitmap1 = Bitmap.createScaledBitmap(mBitmap1, finalWidth, finalHeight, true);
           }


           mBitmap = Bitmap.createBitmap(mBitmap1);
           mCanvas.setBitmap(mBitmap);
           invalidate();
           dem++;
       }
       else {
           this.oldW2 = bm.getWidth();
           this.oldH2 = bm.getHeight();
           Log.d("BM", bm.toString());

           ///resize

           mBitmap1 = bm;
           oldbm2 = bm.copy(Bitmap.Config.ARGB_8888, true);
           mBitmap1 = Bitmap.createScaledBitmap(mBitmap1, 200, 200, true);

       }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.save();
        // ps.drawColor(backgroundColor);


        canvas.drawBitmap(mBitmap, 0, 0, null);
        Log.d("Ondraw","OnDraw");



        // canvas.restore();
    }
    public Bitmap getBMRS(){
        Canvas ccc=new Canvas();
        ccc.setBitmap(oldbm1);
        Bitmap font= Bitmap.createScaledBitmap(oldbm2,(int)(200*oldH1/newH1),(int)(200*oldH1/newH1),true);
        ccc.drawBitmap(font,(int)(mX1*oldH1/newH1),(int)(mY1*oldH1/newH1),null);


        return oldbm1;
    }

    private void touchStart(float x, float y) {

    }


    private void touchMove(float x, float y) {

    }

    private void touchUp() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d("Location",x+"| "+y);
        mX1=x;
        mY1=y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    mCanvas.drawBitmap(mBitmap1,x,y,null);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }


    public static Bitmap mergeToPin(Bitmap back, Bitmap front) {
        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas mecanvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;
        int hBack = back.getHeight();
        int hFront = front.getHeight();
        float move1 = (hBack -hFront) / 2;
        front.setDensity(back.getDensity());
        mecanvas.drawBitmap(back, 0f, 0f, null);
        mecanvas.drawBitmap(front, move, move1, null);
        return result;
    }
}
