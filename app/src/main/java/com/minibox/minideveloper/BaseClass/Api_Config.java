package com.minibox.minideveloper.BaseClass;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api_Config extends BaseActivity{
    public static final String QQ_INTO = "mqqapi://forward/url?src_type=web&style=default&plg_auth=1&version=1&url_prefix=aHR0cHM6Ly9xdW4ucXEuY2" +
                "9tL3Fxd2ViL3F1bnByby9zaGFyZT9fd3Y9MyZfd3d2PTEyOCZhcHBDaGFubmVsPXNoYXJlJmludml0ZUNvZGU9MVpPY09zMXI5WnAmYnVzaW5lc3NUeXBlPT" +
                "kmZnJvbT0xODEwNzQmYml6PWthJm1haW5Tb3VyY2VJZD1zaGFyZSZzdWJTb3VyY2VJZD1vdGhlcnMmanVtcHNvdXJjZT1zaG9ydHVybCMvb3V0";

    public static void HTTP_API(String data, String url, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();
        RequestBody body = new FormBody.Builder().add("sql_data",data).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void HTTP_AES_API(String data, String url){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();
        RequestBody body = new FormBody.Builder().add("sql_data",SAES(data)).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                Log.i("略略略略略略略略略略略略略略",response.body().string());

            }
        });
    }

}
