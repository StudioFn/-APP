package com.minibox.minideveloper;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.minibox.minideveloper.Adapter.ViewPagerAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.Fragment.CommunityFragment;
import com.minibox.minideveloper.Fragment.HomeFragment;
import com.minibox.minideveloper.Fragment.MoreFragment;
import com.minibox.minideveloper.Fragment.MyFragment;
import com.minibox.minideveloper.View.NewViewPager;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends BaseActivity {
    private NewViewPager viewPager;
    private long exitTime = 0;
    private static final String TAG = "HomeActivity";
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    //底部选项卡监听事件
    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener listener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.item_first:
                       viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_comm:
                       viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_more:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.item_second:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusStyle();
        setContentView(R.layout.home_activity);

        viewPager = findViewById(R.id.home_viewpager);
        viewPager.setOffscreenPageLimit(3);
        fragments.add(HomeFragment.newInstance());
        fragments.add(CommunityFragment.newInstance());
        fragments.add(MoreFragment.newInstance());
        fragments.add(MyFragment.newInstance());

        if (savedInstanceState == null) {
            viewPager.setCurrentItem(0);
        }
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragments));
        viewPager.setOffscreenPageLimit(3);
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomview);
        bottomNavigationView.setOnItemSelectedListener(listener);
        bottomNavigationView.setItemIconTintList(null);

        //检查更新
        String Download_url = getShapeData("Download_url");//下载地址
        String appversion = getShapeData("appversion");//最新版本
        String appcontent = getShapeData("appcontent");//更新内容
        String this_version = getAppVersionName(getApplicationContext()); //得到Version版本号
        if (this_version.equals(appversion)) {

        } else if (!appcontent.isEmpty() && !appversion.isEmpty()) {
            //自定义弹窗
            final BaseDialog dialog = new BaseDialog(this, R.style.base_dialog, R.layout.dialog_theme);
            dialog.show();
            TextView content = dialog.findViewById(R.id.content_app);
            Button button = dialog.findViewById(R.id.yes);
            content.setText(appcontent);//显示更新内容
            button.setOnClickListener(v -> {
                downloadManager(Download_url);
                Toasty.normal(this,"已在后台下载安装包",Toasty.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            Toasty.success(this, "发现有新版本更新啦(*/ω＼*)", Toasty.LENGTH_SHORT).show();
        }



    }




    private String getInstallFilePath(){
        String filePackagePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/";
        String fileName = getShapeData("appversion")+".apk";
        String installFilePath = filePackagePath+fileName;
        Log.e(TAG, "getInstallFilePath: "+installFilePath);
        return installFilePath;
    }

    //调用系统下载器
    private void downloadManager(String url) {
        File installFile = new File(getInstallFilePath());
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.fromFile(installFile));
        request.setMimeType("application/vnd.android.package-archive");
        long mDownloadId = downloadManager.enqueue(request);

        registerDownload(mDownloadId);

    }

    private void registerDownload(long id){
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long re = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if (id == re){
                    context.unregisterReceiver(this);
                    installApp(getInstallFilePath());
                }
            }
        };
        this.registerReceiver(receiver,intentFilter);
    }

    //安装
    public void installApp(String apkPath){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = (new File(apkPath));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileProvider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取软件版本号
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo p1 = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = p1.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void StatusStyle() {
        //使状态栏完全透明，条件，需要在Theme文件样式代码中，加上<item name="android:windowTranslucentStatus">true</item>"
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体颜色
    }

    //点击两次退出
    @Override
    public void onBackPressed() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toasty.info(this, "请再操作一次退出", Toasty.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
