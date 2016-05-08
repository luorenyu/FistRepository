package com.timen4.imagepicker.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.timen4.imagepicker.R;
import com.timen4.imagepicker.adapter.PhotoAlbumLVAdapter;
import com.timen4.imagepicker.model.PhotoAlbumLVItem;
import com.timen4.imagepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 分相册查看SD卡所有的图片
 * Created by luore on 2016/4/7.
 */
public class PhotoAlbumActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_album);

        if (!Utility.isSDcardOK()){
            Utility.showToast(this,"SD卡不可用");
            return;
        }
        Intent intent=getIntent();
        if (!intent.hasExtra("latest_count")){
            return;
        }
        TextView titleTV= (TextView) findViewById(R.id.topbar_title_tv);
        titleTV.setText(R.string.select_album);

        Button cancelBtn= (Button) findViewById(R.id.topbar_left_btn);
        cancelBtn.setText(R.string.main_cancel);
        cancelBtn.setVisibility(View.VISIBLE);

        ListView listView= (ListView) findViewById(R.id.select_img_listview);

//        //第一种方式：使用file
//        File rootFile = new File(Utility.getSDcardRoot());
//        //屏蔽/mnt/sdcard/DCIM/.thumbnails目录
//        String ignorePath = rootFile + File.separator + "DCIM" + File.separator + ".thumbnails";
//        getImagePathsByFile(rootFile, ignorePath);

        //第二种方式：使用ContentProvider。（效率更高）
        final ArrayList<PhotoAlbumLVItem> list=new ArrayList<PhotoAlbumLVItem>();
        //最近照片
        list.add(new PhotoAlbumLVItem(getResources().getString(R.string.latest_image), intent.getIntExtra("latest_count", -1), intent.getStringExtra("latest_first_img")));
        //相册
        list.addAll(getImagePathsByContentProvider());

        PhotoAlbumLVAdapter adapter=new PhotoAlbumLVAdapter(this,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                //第一行为“最近照片”
                if (position == 0) {
                    intent1.putExtra("code", 200);
                } else {
                    intent1.putExtra("code", 100);
                    intent1.putExtra("folderPath", list.get(position).getPathName());
                }
                startActivity(intent1);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backAction();
            }
        });
    }

    /**
     * 点击返回时，回到相册页面
     */
    private void backAction() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //重写返回键


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            backAction();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     *使用contentprovider读取SD卡所有的图片
     * @return
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE=MediaStore.Images.Media.MIME_TYPE;
        String key_DATA=MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver=getContentResolver();

        //只查询jpg和png的图片
        Cursor cursor=mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        ArrayList<PhotoAlbumLVItem> list=null;
        if (cursor!=null){
            if (cursor.moveToLast()){
                //路径缓存，防止多次扫描同一个目录
                HashSet<String> cachePath =new HashSet<String>();
                list=new ArrayList<PhotoAlbumLVItem>();

                while (true){
                    //获取图片的路径
                    String imagePath=cursor.getString(0);
                    //文件夹的路径
                    File parentFile=new File(imagePath).getParentFile();
                    String parentPath=parentFile.getAbsolutePath();

                    //不扫描重复路径
                    if(!cachePath.contains(parentPath)){
                        list.add(new PhotoAlbumLVItem(parentPath,getImageCount(parentFile),getFirstImagePath(parentFile)));
                        cachePath.add(parentPath);
                    }
                    if(!cursor.moveToPrevious()){
                        break;
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 获取目录中图片的个数
     * @param folder
     * @return
     */
    private int getImageCount(File folder){
        int count=0;
        File[] files=folder.listFiles();
        for (File file : files){
            if (Utility.isImage(file.getName())){
                count++;
            }
        }
        return count;
    }

    private String getFirstImagePath(File folder){
        File[] files=folder.listFiles();
        for(int i=files.length-1;i>=0;i--){
            File file=files[i];
            if (Utility.isImage(file.getName())){
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //动画
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

}
