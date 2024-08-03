package com.minibox.minideveloper.BaseClass;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.minibox.minideveloper.Uitil.AESUtil;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    public void toActivity(Class cl){
        Intent i = new Intent(getActivity(),cl);
        startActivity(i);
    }

    public void toActivityWithBundle(Class is,Bundle bundle){
        Intent i = new Intent(getActivity(),is);
        i.putExtras(bundle);
        startActivity(i);
    }

    protected String getShared(String s){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("text", MODE_PRIVATE);
        return sharedPreferences.getString(s,"");
    }

    protected String getCloudData(String s){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cloud",MODE_PRIVATE);
        return sharedPreferences.getString(s,"");
    }

    protected String getSharedUser(String s){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("QQ_User", MODE_PRIVATE);
        return sharedPreferences.getString(s,"");
    }

    protected static void Api_Http(String data,String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();
        RequestBody body = new FormBody.Builder().add("sql_data",data).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    protected static void Api_Https(String data,String like,String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();
        RequestBody body = new FormBody.Builder().add("sql_data",data).add("like",like).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }


    /**AES加密**/
    public String AES(String content){
        String key = "ABCDEFGHIJKLNMOP";
        String  sm =  AESUtil.encrypt(content, key);
        Log.i("密文密文：",sm);
        return sm;
    }
}
