package com.timen4.imagepicker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import com.timen4.imagepicker.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SDCardImageLoader {
	//缓存
    LruCache<String, Bitmap> imageCache;
    //固定2个线程来执行任务
    private ExecutorService executorService= Executors.newFixedThreadPool(2);
    private Handler handler=new Handler();

    private int screenW,screenH;

    public SDCardImageLoader(int screenW,int screenH){
        this.screenH=screenH;
        this.screenW=screenW;

        //获取应用程序最大可用内存
        int maxMemory= (int) Runtime.getRuntime().maxMemory();
        int cacheSize=maxMemory/8;

        //设置图片缓存大小为程序最大可用内存的1/8
        imageCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }
    private Bitmap loadDrawable(final int smallRate, final String filePath, final ImageCallback callback){
        //如果缓存过就从缓存中取出数据
        if(imageCache.get(filePath)!=null){
            return imageCache.get(filePath);
        }

        //如果缓存没有则读取SD卡(开启新的线程)
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    BitmapFactory.Options opt=new BitmapFactory.Options();
                    opt.inJustDecodeBounds=true;//该值为true，表示做模拟计算,没有像素
                    BitmapFactory.decodeFile(filePath,opt);

                    //获取到这个图片的原始宽高
                    int picWidth=opt.outWidth;
                    int picHeight=opt.outHeight;

                    //读取图片失败时，直接返回
                    if (picHeight==0||picWidth==0){
                        return;
                    }

                    //initialize the scaling
                    opt.inSampleSize=smallRate;
                    //根据屏幕大小和图片大小计算出缩放比例
                    if(picWidth>picHeight){
                        if(picWidth>screenW){
                            opt.inSampleSize*=picWidth/screenW;
                        }
                    }else{
                        if (picHeight>screenH){
                            opt.inSampleSize*=picHeight/screenH;
                        }
                    }

                    //这次再真正地生成一个有像素的，经过缩放了的bitmap
                    opt.inJustDecodeBounds=false;
                    final Bitmap bmp=BitmapFactory.decodeFile(filePath, opt);
                    //加载到缓存中
                    imageCache.put(filePath,bmp);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.imageLoaded(bmp);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    public void loadImage(int smallRate,final String filePath,final ImageView imageView) {
        Bitmap bmp=loadDrawable(smallRate, filePath, new ImageCallback() {
            @Override
            public void imageLoaded(Bitmap bmp) {
                if (imageView.getTag().equals(filePath)){
                    if (bmp!=null){
                        imageView.setImageBitmap(bmp);
                    }else{
                        imageView.setImageResource(R.drawable.empty_photo);
                    }
                }
            }
        });
        if (bmp!=null){
            if (imageView.getTag().equals(filePath)){
                imageView.setImageBitmap(bmp);
            }
        }else{
            imageView.setImageResource(R.drawable.empty_photo);
        }
    }
    // 对外界开放的回调接口
    public interface ImageCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        public void imageLoaded(Bitmap imageDrawable);
    }
}
