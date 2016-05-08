package com.timen4.imagepicker.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.adapter.PhotoWallAdapter;
import com.timen4.imagepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选择照片页面
 * Created by luore on 2016/4/6.
 */
public class PhotoWallActivity extends Activity {
    private TextView titleTV;

    private ArrayList<String> list;
    private PhotoWallAdapter adapter;

    /**
     * 当前文件夹路径
     */
    private String currentFolder=null;
    /**
     * 当前展示的是否为最近照片
     */
    private boolean isLatest=true;

    @Bind(R.id.photo_wall_grid)
    GridView mphotoWall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_wall);
        ButterKnife.bind(this);

        titleTV = (TextView) findViewById(R.id.topbar_title_tv);
        titleTV.setText(R.string.latest_image);

        Button backBtn = (Button) findViewById(R.id.topbar_left_btn);
        Button confirmBtn = (Button) findViewById(R.id.topbar_right_btn);
        backBtn.setText(R.string.photo_album);
        backBtn.setVisibility(View.VISIBLE);
        confirmBtn.setText(R.string.main_confirm);
        confirmBtn.setVisibility(View.VISIBLE);

        list = getLastImagePaths(100);
        adapter=new PhotoWallAdapter(this,list);
        mphotoWall.setAdapter(adapter);

        //选择照片完成
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择图片完成，回到起始页面
                ArrayList<String> paths=getSelectImagePaths();
                Intent intent=new Intent(PhotoWallActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("code", paths != null ? 100 : 101);
                intent.putStringArrayListExtra("paths", paths);
                startActivity(intent);

            }
        });
        
        //点击返回，回到选择相册页面
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backAction();
            }
        });

    }
    //从相册页面跳转到此页
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //动画
        overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);

        int code=intent.getIntExtra("code",-1);
        if(code==100){
            String folderPath=intent.getStringExtra("folderPath");
            if(isLatest||(folderPath!=null&&folderPath.equals(currentFolder))){
                currentFolder=folderPath;
                updateView(100,currentFolder);
                isLatest=false;
            }
        }else if (code==200){
            //最近照片
            if(!isLatest){
                updateView(200,null);
                isLatest=true;
            }
        }
    }

    /**
     * 根据图片所属文件夹路径，刷新页面
     * @param code
     * @param folderPath
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code==100){//某个相册
            int lastSeparator=folderPath.lastIndexOf(File.separator);
            String foldername=folderPath.substring(lastSeparator+1);
            titleTV.setText(foldername);
            list.addAll(getAllImagePathsByFolder(folderPath));
        }else if (code==200){
            titleTV.setText(R.string.latest_image);
            list.addAll(getLastImagePaths(100));
        }

        adapter.notifyDataSetChanged();
        if (list.size()>0){
            //滚动到最顶部
            mphotoWall.smoothScrollToPosition(0);
        }
    }

    /**
     * 获取指定路径下的所有图片文件
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath){
        File folder=new File(folderPath);
        String[] allfilePaths=folder.list();
        if (allfilePaths==null||allfilePaths.length==0){
            return null;
        }
        ArrayList<String> imageFilePaths=new ArrayList<String>();
        for (int i=allfilePaths.length-1;i>=0;i--){
            if (Utility.isImage(allfilePaths[i])){
                imageFilePaths.add(folderPath+File.separator+allfilePaths[i]);
            }
        }
        return imageFilePaths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片
     *
     * @param maxCount
     * @return
     */
    private ArrayList<String> getLastImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 查询jpg和png的图片，按最新修改排序
//        Cursor cursor = mContentResolver.query(mImageUri,
//                new String[]{key_DATA},
//                key_MIME_TYPE + "=?or" + key_MIME_TYPE + "=?or" +
//                        key_MIME_TYPE + "=?", new String[]{"image/jpg",
//                        "image/jpeg", "image/png"},
//                MediaStore.Images.Media.DATE_MODIFIED);
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        ArrayList<String> latesImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取，
            //当cursor中没有数据是，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latesImagePaths=new ArrayList<String>();
                while (true) {
                    //获取图片的路径
                    String path = cursor.getString(0);
                    latesImagePaths.add(path);
                    if (latesImagePaths.size()>=maxCount||!cursor.moveToPrevious()){
                        break;
                    }
                }
            }
            cursor.close();
        }
        return latesImagePaths;
    }

    /**
     * 第一次跳转至相册页面时，传递最新照片信息
     */
    private boolean firstIn=true;

    /**
     * 点击返回，跳转至相册页面
     */
    private void backAction() {
        Intent intent=new Intent(this,PhotoAlbumActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        //传递“最近照片”分类信息
        if (firstIn){
            System.out.println("list"+list);
            if (list!=null&&list.size()>0){
                intent.putExtra("latest_count",list.size());
                intent.putExtra("latest_first_img",list.get(0));
            }
            firstIn=false;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left,R.anim.out_from_right);
    }

    private ArrayList<String> getSelectImagePaths() {
        SparseBooleanArray map=adapter.getSelectionMap();
        if (map.size()==0){
            return null;
        }
        ArrayList<String> selectedImageList=new ArrayList<String>();

        for (int i=0;i<list.size();i++){
            if (map.get(i)){
                selectedImageList.add(list.get(i));
            }
        }
        return selectedImageList;
    }
}
