package com.minibox.minideveloper;


import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.minibox.minideveloper.Activity.CrashActivity;
import com.minibox.minideveloper.Uitil.Notify;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushRegisterCallback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TimerTask;


public class MainApplication extends android.app.Application {
    private final Handler handler = new Handler();
    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notify.createNotify(this,getString(R.string.app_name),getString(R.string.app_name),NotificationManager.IMPORTANCE_MAX);
        }

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {

            Bundle b = new Bundle();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            Intent intent = new Intent(getApplicationContext(),CrashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            b.putString("error", "崩溃日志："+sw+"\n"+t);
            intent.putExtras(b);
            startActivity(intent);

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });

        //腾讯Bugly
        CrashReport.initCrashReport(getApplicationContext(), "fae68d01ba", false);
        //友盟+
        UMConfigure.setLogEnabled(false);//设置LOG开关
        UMConfigure.preInit(this,"645b9395ba6a5259c44e1910","和圈");
        UMConfigure.init(getApplicationContext(),"645b9395ba6a5259c44e1910","和圈",UMConfigure.DEVICE_TYPE_PHONE, "b25a80b25d19fa022e0cc7ed65b8edfc");

        //注册推送
        PushAgent.getInstance(this).register(new UPushRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                Log.i("友盟+消息推送成功",deviceToken);
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                Log.i("友盟+消息推送失败","错误代码："+errCode+" desc:"+errDesc);
            }
        });

        //检测VPN
        handler.postDelayed(timeT,2000);

    }

    private final Runnable timeT = new Runnable() {
        @Override
        public void run() {
            isVPN();
            handler.postDelayed(this,2000);
        }
    };
    
    private void isVPN(){
        try{
            Enumeration<NetworkInterface> nList = NetworkInterface.getNetworkInterfaces();
            if (nList != null){
                for (NetworkInterface ntf: Collections.list(nList)) {
                    if (!ntf.isUp() || ntf.getInterfaceAddresses().size() == 0){
                        continue;
                    }
                    if ("tun0".equals(ntf.getName()) || "ppp0".equals(ntf.getName())){
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }
            }
        }catch (Exception e){}
    }
}
