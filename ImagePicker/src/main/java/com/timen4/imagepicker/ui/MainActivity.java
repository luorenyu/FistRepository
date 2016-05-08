package com.timen4.imagepicker.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.adapter.MainGVAdapter;
import com.timen4.imagepicker.utils.ScreenUtils;
import com.timen4.imagepicker.utils.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private ArrayList<String> imagePathList;
    private MainGVAdapter adapter;

    @Bind(R.id.topbar_title_tv)
    TextView TitleTv;
    @Bind(R.id.select_image)
    Button selectImgBtn;
    @Bind(R.id.main_gridView)
    GridView mainGV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //获取屏幕像素
        ScreenUtils.initScreen(this);

        TitleTv.setText(R.string.app_name);
        imagePathList = new ArrayList<String>();
        adapter = new MainGVAdapter(this, imagePathList);
        mainGV.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int code =intent.getIntExtra("code",-1);
        if (code!=100){
            return;
        }
        ArrayList<String> paths=intent.getStringArrayListExtra("paths");
        //添加，去重
        boolean hasUpdate=false;
        for (String path:paths){
            if (!imagePathList.contains(path)){
                //最多9张
                if (imagePathList.size()==9){
                    Utility.showToast(this,"最多可添加9张！");
                    break;
                }
                imagePathList.add(path);
                hasUpdate=true;
            }
        }
        if(hasUpdate){
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.select_image)
    public void onClick() {
        //跳转至最终的选择图片页面
        Intent intent=new Intent(MainActivity.this,PhotoWallActivity.class);
        startActivity(intent);
    }
}
