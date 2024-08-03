/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PreviewImages extends BaseActivity {
    private ViewPager viewPager;
    private TextView save;
    private List<String> list;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.preview_image);

        initView();
        initData();
    }

    private void initView() {
        save = findViewById(R.id.preview_image_save);
        viewPager = findViewById(R.id.preview_image_ViewPager);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String img = bundle.getString("images");
        int position = bundle.getInt("position");
        list = Arrays.asList(img.split(","));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        save.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()){
                    DownLoad(list.get(index));
                }else{
                    getSave();
                }
            }else {
                DownLoad(list.get(index));
            }
        });
    }

    private final PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = LayoutInflater.from(PreviewImages.this).inflate(R.layout.preview_image_pager_item,container,false);
            ImageView imageView = v.findViewById(R.id.pager_item);
            Glide.with(PreviewImages.this).load(list.get(position)).into(imageView);
            if (v.getParent() instanceof ViewGroup){
                ((ViewGroup)v.getParent()).removeView(v);
            }
            index = position;
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    };

    public void DownLoad(String url) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //从系统获取下载管理器
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //根据下载地址构建一个URi对象
        Uri uri = Uri.parse(url);
        //创建一个下载对象，指定从哪里下载文件
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //设置允许下载的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //设置通知栏在下载进行时与完成后都可见
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置文件的保存路径
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, calendar.getTimeInMillis() + ".png");
        long id = downloadManager.enqueue(request);
        Toast.makeText(PreviewImages.this, "正在下载" , Toast.LENGTH_SHORT).show();

        listener(id);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void listener(final long Id) {

        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(getApplicationContext(),  "下载完成!已保存到系统相册", Toast.LENGTH_LONG).show();
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }

}
