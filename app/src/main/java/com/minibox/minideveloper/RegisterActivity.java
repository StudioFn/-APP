package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.minibox.minideveloper.BaseClass.BaseActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {
    private EditText username, account, password_noe, password_two;
    private Button re_but;
    private String CODE = "32";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) StatusBarTheme();
        setContentView(R.layout.register_activity);

        username = findViewById(R.id.register_name);//昵称
        account = findViewById(R.id.register_account);//账号
        password_noe = findViewById(R.id.register_password1);//密码
        password_two = findViewById(R.id.register_password2);//确认密码
        re_but = findViewById(R.id.register_enter);//注册
        initData();

    }

    public void initData() {
        //注册
        re_but.setOnClickListener(v -> {
            //获取输入框字符串
            String us = RegisterActivity.this.username.getText().toString().trim();//获取昵称
            String ac = RegisterActivity.this.account.getText().toString().trim();//获取账号
            String p1 = RegisterActivity.this.password_noe.getText().toString().trim();//获取密码
            String p2 = RegisterActivity.this.password_two.getText().toString().trim();//获取确认密码
            //条件判断
            if (us.length() == 0) {
                Toasty.info(RegisterActivity.this, "昵称不能为空", Toasty.LENGTH_SHORT).show();
                return;
            }
            if (ac.length() == 0) {
                Toasty.info(RegisterActivity.this, "请输入账号", Toasty.LENGTH_SHORT).show();
                return;
            }
            if (p1.length() == 0) {
                Toasty.info(RegisterActivity.this, "请输入密码", Toasty.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                Toasty.info(RegisterActivity.this, "两次输入的密码不一致", Toasty.LENGTH_SHORT).show();
            } else {
                Toasty.success(RegisterActivity.this, "注册中...", Toasty.LENGTH_SHORT).show();
                Bundle bundle = getIntent().getExtras();
                String id = bundle.getString("openid");
                String img = bundle.getString("head_img");
                String img_2 = bundle.getString("head_img_tow");
                String sql = "insert into dev_user (id, openid, sign, image, name, account, password, figureurl_qq_2) VALUES " +
                        "('','" + id + "','','" + img + "','" + us + "','" + ac + "','" + p2 + "','" + img_2 + "')";
                register_http(sql);
            }

        });


    }

    private void register_http(String name) {

        //创建网络处理的对象
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        //post请求来获得数据
        RequestBody body = new FormBody.Builder()
                .add("sql_data", AES(name))//提交头像参数
                .build();
        //创建一个请求对象，传入URL地址和相关数据的键值对的对象
        Request request = new Request.Builder()
                .url(API_URL + "dev_app/dev_register/")
                .post(body)
                .build();
        //创建一个能处理请求数据的操作类
        Call call = client.newCall(request);
        //使用异步任务的模式请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(RegisterActivity.this, "出现错误！" + e.toString(), Toasty.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(context);
                    String code = jsonObject.getString("code");
                    CODE = code;

                    Log.e("TAG", "code:结果  " + code);
                } catch (Exception ignored) {
                }

                if (CODE.equals("0")) {
                    //一个线程中没有调用Looper.prepare(),就不能在该线程中创建Toast
                    Looper.prepare();
                    Toasty.error(RegisterActivity.this, "注册失败！此账号已存在！", Toasty.LENGTH_LONG).show();
                    Looper.loop();
                } else if (CODE.equals("200")) {
                    Looper.prepare();
                    Toasty.success(RegisterActivity.this, "注册成功！", Toasty.LENGTH_SHORT).show();
                    finish();
                    Looper.loop();
                }
                Log.e("TAG", "结果  " + context);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void StatusBarTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        //设置状态栏字体颜色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}
