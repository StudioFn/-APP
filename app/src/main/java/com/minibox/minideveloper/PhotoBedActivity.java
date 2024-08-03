package com.minibox.minideveloper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.minibox.minideveloper.Activity.DevActivity;
import com.minibox.minideveloper.BaseClass.BaseActivity;

import org.json.JSONException;
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

public class PhotoBedActivity extends BaseActivity {
    private TextView key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_bed_activity);

        ImageButton exit = findViewById(R.id.cloud_exit);
        key = findViewById(R.id.photo_bed_key);
        Button enter = findViewById(R.id.photo_bed_enter);
        EditText mail = findViewById(R.id.photo_bed_mail);
        EditText password = findViewById(R.id.photo_bed_password);
        TextView register = findViewById(R.id.photo_bed_register);

        //返回
        exit.setOnClickListener(v -> finish());
        //注册
        register.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("url","http://img.9a18.cn/auth/register.html");
            toActivityWithBundle(DevActivity.class,bundle);
        });
        //获取KEY
        enter.setOnClickListener(v -> {
            String add_mail = mail.getText().toString().trim();
            String add_password = password.getText().toString().trim();
            if (add_mail.length() <= 0){
                Toast.makeText(PhotoBedActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                return;
            }
            if (add_password.length() <= 0){
                Toast.makeText(PhotoBedActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (add_mail.length() != 0 && add_password.length() != 0){
                postGgApi(add_mail,add_password);
            }
        });

        if (!getShapeData("token").equals("")){
            key.setText("KEY: "+getShapeData("token"));
            key.setTextColor(Color.parseColor("#03AE06"));
        }


    }

    public void postGgApi(String account, String password){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).build();
        RequestBody requestBody = new FormBody.Builder().add("email",account).add("password",password).build();
        Request request = new Request.Builder().url("http://img.9a18.cn/api/token").post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                assert response.body() != null;
                String json = response.body().string();
                PhotoBedActivity.this.runOnUiThread(() -> {

                    try {
                        JSONObject object = new JSONObject(json);
                        if (object.getString("msg").equals("success")){
                            JSONObject jsonObject = new JSONObject(object.getString("data"));
                            String token = jsonObject.getString("token");
                            SharedPreferences preferences = getSharedPreferences("QQ_User",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token",token);
                            editor.apply();
                            Toast.makeText(PhotoBedActivity.this, "获取成功！！"+getShapeData("token"), Toast.LENGTH_SHORT).show();
                            key.setText("KEY: "+getShapeData("token"));
                            key.setTextColor(Color.parseColor("#03AE06"));
                        }else{
                            String error = object.getString("msg");
                            Toasty.error(PhotoBedActivity.this,"获取失败！"+error,Toasty.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {e.printStackTrace();}
                    Log.i("果果图床",json);

                });
            }
        });

    }

}
