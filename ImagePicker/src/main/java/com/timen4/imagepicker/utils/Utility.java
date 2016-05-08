package com.timen4.imagepicker.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Utility {
	
	/**
	 * �ж�sd���Ƿ����
	 */
	public static boolean isSDcardOK() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * ��ȡSD���ĸ�Ŀ¼��SD��������ʱ������null
	 */
	public static String getSDcardRoot(){
		if(isSDcardOK()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
	/**
	 * �Զ��嵯��˾
	 * @param context 
	 */
	public static void showToast(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	public static void showToast(Context context,int msgId){
		Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
	}
	/**
	 * ��ȡ�ַ���ĳ���ַ���ֵĴ���
	 */
	public static int countMatches(String res, String goalStr){
		if(res==null){
			return 0;
		}
		if(goalStr==null||goalStr.length()==0){
			throw new IllegalAccessError("The param goalStr cannot be null or 0 length");
		}
		return (res.length()-res.replaceAll(goalStr, "").length())/goalStr.length();
	}
	/**
	 * �жϸ��ļ��Ƿ���һ��ͼƬ
	 */
	public static boolean isImage(String fileName){
		return fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")||fileName.endsWith(".png");
	}
	
}
