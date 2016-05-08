package com.timen4.imagepicker.adapter;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.model.PhotoAlbumLVItem;
import com.timen4.imagepicker.utils.SDCardImageLoader;
import com.timen4.imagepicker.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by luore on 2016/4/6.
 */
public class PhotoWallAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePathlist;
    private SDCardImageLoader loader;
    //记录是否被选择
    private SparseBooleanArray selectionMap;


    public PhotoWallAdapter(Context context, ArrayList<String> imagePathlist) {
        this.context = context;
        this.imagePathlist = imagePathlist;

        loader=new SDCardImageLoader(ScreenUtils.getScreenW(),ScreenUtils.getScreenH());
        selectionMap=new SparseBooleanArray();
    }



    @Override
    public int getCount() {
        return imagePathlist==null?0:imagePathlist.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHodler hodler;
        String filePath=imagePathlist.get(position);
        if (convertView==null){
            hodler=new ViewHodler();
            convertView= LayoutInflater.from(context).inflate(R.layout.photo_wall_item, null);

            hodler.imageView= (ImageView) convertView.findViewById(R.id.photo_wall_item_photo);
            hodler.checkBox= (CheckBox) convertView.findViewById(R.id.photo_wall_item_cb);
            convertView.setTag(hodler);
        }else{
            hodler= (ViewHodler) convertView.getTag();
        }

        //tag的key必须是id的方式，以确保唯一性，否则会出现IllegalArgumentException.
        hodler.checkBox.setTag(R.id.tag_first,position);
        hodler.checkBox.setTag(R.id.tag_second,hodler.imageView);
        hodler.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer position = (Integer) buttonView.getTag(R.id.tag_first);
                ImageView image = (ImageView) buttonView.getTag(R.id.tag_second);

                selectionMap.put(position, isChecked);
                if (isChecked) {
                    image.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
                } else {
                    image.setColorFilter(null);
                }

            }
        });
        hodler.checkBox.setChecked(selectionMap.get(position));
        hodler.imageView.setTag(filePath);
        loader.loadImage(4,filePath,hodler.imageView);
        return convertView;
    }
    private class ViewHodler{
        ImageView imageView;
        CheckBox checkBox;
    }

    public SparseBooleanArray getSelectionMap(){
        return selectionMap;
    }

    public void clearSelectionMap(){
        selectionMap.clear();
    }


}
