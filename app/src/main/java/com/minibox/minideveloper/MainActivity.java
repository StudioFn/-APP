package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.minibox.minideveloper.BaseClass.Api_Config;
import com.minibox.minideveloper.BaseClass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private LinearLayout jumptomain;
    private TextView mText;
    private ImageView imageView;

    private final CountDownTimer timer = new CountDownTimer(5000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            mText.setText("跳过" + "(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            ToJump();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_main);

        initview();
        chardata();
        post_api();
        CloudApp();
        String ac = getShapeData("account");
        login("select * from dev_user where account = " + "'" + ac + "'");
    }

    private void chardata() {
        jumptomain.setOnClickListener(v -> {
            ToJump();
            timer.cancel();
        });

        //计数
        if (!getShapeData("id").equals("20")) {
            update("UPDATE `dev_server_count` SET `app_tsn` = `app_tsn` + 1 WHERE `dev_server_count`.`ID` = 1");
        }
        //加载图片
        Api_Config.HTTP_API(AES("SELECT * FROM `dev_server_count`"), API_URL + "dev_app/dev_sever/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();
                MainActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String url = jsonObject.getString("app_starttup_diagram");
                        String type = jsonObject.getString("openAi_type");

                        SharedPreferences share = getSharedPreferences("Cloud", MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putString("type", type);
                        editor.apply();

                        Glide.with(MainActivity.this).load(url).into(imageView);
                    } catch (JSONException ignored) {}
                    Log.i("获取Sever数据", json);
                });

                timer.start();
            }
        });
    }

    private void initview() {
        mText = findViewById(R.id.MillisUntilFinished);
        jumptomain = findViewById(R.id.jumpToMain);
        imageView = findViewById(R.id.main_img);
    }

    public void ToJump() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    //统计启动次数
    public void update(String str) {

        Api_Http(AES(str), API_URL + "dev_app/dev_login/", new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });

    }

    //一言api
    private void post_api() {
        // 开启线程来发起网络请求
        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://v1.jinrishici.com/all.json");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                forJsonObject(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    //得到一言数据进行Json解析
    private void forJsonObject(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String ms = jsonObject.getString("content");
            SharedPreferences sharedPreferences = getSharedPreferences("text", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("content", ms);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //云台App接口检查更新
    public void CloudApp() {
        //创建网络处理的对象
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .build();

        //post请求来获得数据
        //创建一个RequestBody，存放重要数据的键值对
        RequestBody body = new FormBody.Builder()
                .add("QQUID", "key2308762185").build();
        //创建一个请求对象，传入URL地址和相关数据的键值对的对象
        Request request = new Request.Builder()
                .url(API_URL + "admin/devepolecloud.php")
                .post(body)
                .build();

        //创建一个能处理请求数据的操作类
        Call call = client.newCall(request);

        //使用异步任务的模式请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "错误信息：" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(context);
                    String version = jsonObject.getString("version");
                    String download = jsonObject.getString("downloadurl");
                    String content = jsonObject.getString("appcontent");

                    //储存服务端返回的数据
                    SharedPreferences sharedPreferences = getSharedPreferences("QQ_User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("appversion", version);
                    editor.putString("Download_url", download);
                    editor.putString("appcontent", content);
                    editor.apply();


                } catch (Exception ignored) {

                }

                Log.e("TAG", "结果  " + context);
            }
        });
    }

    public void login(String sql) {
        Api_Http(AES(sql), API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();
                MainActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String sm = jsonObject.getString("code");
                        if (sm.equals("0")) {
                            Toast.makeText(MainActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                            SharedPreferences userInfo = getSharedPreferences("QQ_User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = userInfo.edit();
                            editor.clear();
                            editor.apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });


            }
        });
    }


    //活动结束停止计时
    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.cancel();
    }

    //按返回键直接跳转到MainActivity
    @Override
    public void onBackPressed() {
        timer.cancel();
        ToJump();
    }

}