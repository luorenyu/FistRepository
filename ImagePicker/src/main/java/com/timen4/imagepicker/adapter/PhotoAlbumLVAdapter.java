package com.timen4.imagepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.model.PhotoAlbumLVItem;
import com.timen4.imagepicker.utils.SDCardImageLoader;
import com.timen4.imagepicker.utils.ScreenUtils;
import com.timen4.imagepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by luore on 2016/4/6.
 */
public class PhotoAlbumLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PhotoAlbumLVItem> list;
    private SDCardImageLoader loader;

    public PhotoAlbumLVAdapter(Context context,ArrayList<PhotoAlbumLVItem> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        loader=new SDCardImageLoader(ScreenUtils.getScreenW(),ScreenUtils.getScreenH());
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.photo_album_lv_item,null);
            viewHolder.firstImageView= (ImageView) convertView.findViewById(R.id.select_img_gridView_img);
            viewHolder.pathNameTV= (TextView) convertView.findViewById(R.id.select_img_gridView_path);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        //图片（缩略图）
        String filePath=list.get(position).getFirstImagePath();
        viewHolder.firstImageView.setTag(filePath);
        System.out.println("filePath" + filePath);
        loader.loadImage(4, filePath, viewHolder.firstImageView);
        //文字
        viewHolder.pathNameTV.setText(getPathNameToShow(list.get(position)));

        return convertView;
    }
    private class ViewHolder{
        ImageView firstImageView;
        TextView pathNameTV;
    }
    /**
     * 根据完整路径，获取最后一级路径
     */
    private String getPathNameToShow(PhotoAlbumLVItem item){
        String absolutePath=item.getPathName();
        //获取最后一个分隔符的位置
        int lastSeparator=absolutePath.lastIndexOf(File.separator);
        return absolutePath.substring(lastSeparator+1)+"("+item.getFileCount()+")";
    }
}
