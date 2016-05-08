package com.timen4.imagepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.utils.SDCardImageLoader;
import com.timen4.imagepicker.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * 主页面中的GridView的适配器
 * Created by luore on 2016/4/6.
 */
public class MainGVAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<String> imagePathList=null;

    private SDCardImageLoader loader;

    public MainGVAdapter(Context context,ArrayList<String> imagePathList) {
        this.context=context;
        this.imagePathList=imagePathList;
        loader=new SDCardImageLoader(ScreenUtils.getScreenW(),ScreenUtils.getScreenH());
    }

    @Override
    public int getCount() {
        return imagePathList==null ? 0 : imagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String filePath= (String) getItem(position);
        final ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.main_gridview_item,null);
            holder=new ViewHolder();

            holder.imageView= (ImageView) convertView.findViewById(R.id.main_gridView_item_photo);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.imageView.setTag(filePath);
        loader.loadImage(3,filePath,holder.imageView);
        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
    }
}
