package com.timen4.imagepicker.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

public class ScreenUtils {
	private static int mScreenW;
	private static int mScreenH;
	private static float mScreenDensity;
	
	public static void initScreen(Activity activity) {
		DisplayMetrics metrics=new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenH=metrics.widthPixels;
		mScreenH=metrics.heightPixels;
		mScreenDensity=metrics.density;
	}
	
	public static int getScreenW() {
		return mScreenW;
	}
	public static int getScreenH() {
		return mScreenH;
	}
	public static float getScreenDensity() {
		return mScreenDensity;
	}
	/**根据手机的分辨率从dp的单位转为px（像素）*/
	public static int px2dp( float dpValue) {
		return (int) (dpValue/getScreenDensity()+0.5f);
	}
	
	/**根据手机的分辨率从dp的单位转为px（像素）*/
	public static int dp2px( float pxValue) {
		return (int) (pxValue*getScreenDensity()+0.5f);
	}
	
}
