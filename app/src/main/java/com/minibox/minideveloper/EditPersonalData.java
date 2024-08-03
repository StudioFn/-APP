package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.BaseClass.BaseActivity;

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

public class EditPersonalData extends BaseActivity {

    private ImageButton exit;
    private ShapeableImageView img;
    private LinearLayout edit_sing;
    private LinearLayout edit_name;
    private TextView sign;
    private TextView name;
    private LinearLayout save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Status_Style();
        }
        setContentView(R.layout.edit_personal_data);

        initView();
        initData();

    }

    public void initView() {
        exit = findViewById(R.id.personal_exit);
        img = findViewById(R.id.personal_img);
        edit_sing = findViewById(R.id.personal_click_noe);
        edit_name = findViewById(R.id.personal_click_tow);
        sign = findViewById(R.id.personal_sign);
        name = findViewById(R.id.personal_name);
        save = findViewById(R.id.personal_save);
    }

    public void initData() {
        String sing = getShapeData("sign");
        String names = getShapeData("Qname");
        String heard = getShapeData("Qimg");
        sign.setText(sing);
        name.setText(names);

        edit_sing.setOnClickListener(v -> {
            BaseDialog dialog = new BaseDialog(EditPersonalData.this, R.style.base_dialog, R.layout.center_dialog);
            dialog.show();
            TextView text = dialog.findViewById(R.id.center_dialog_show);
            EditText editText = dialog.findViewById(R.id.center_dialog_edit);
            Button enter = dialog.findViewById(R.id.center_dialog_enter);
            text.setText("更改签名");
            editText.setText(sing);
            enter.setOnClickListener(v1 -> {
                sign.setText(editText.getText().toString().trim());
                dialog.dismiss();
            });

        });
        edit_name.setOnClickListener(v -> {
            BaseDialog dialog = new BaseDialog(EditPersonalData.this, R.style.base_dialog, R.layout.center_dialog);
            dialog.show();
            TextView text = dialog.findViewById(R.id.center_dialog_show);
            EditText editText = dialog.findViewById(R.id.center_dialog_edit);
            Button enter = dialog.findViewById(R.id.center_dialog_enter);
            text.setText("更改昵称");
            editText.setText(names);
            enter.setOnClickListener(v1 -> {
                name.setText(editText.getText().toString().trim());
                dialog.dismiss();
            });
        });
        save.setOnClickListener(v -> {
            String data = AES(" UPDATE `dev_user` SET `sign` = " + "'" + sign.getText().toString() + "'," + "`name` = " + "'" + name.getText().toString() + "'" + "WHERE `dev_user`.`id` = " + getShapeData("id"));
            getUser_api(data);
        });

        Glide.with(EditPersonalData.this).load(heard).into(img);/*加载背景图片*/

        exit.setOnClickListener(v -> finish());

    }

    private void getUser_api(String data) {
        final BaseDialog dialog = new BaseDialog(EditPersonalData.this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        RequestBody body = new FormBody.Builder().add("sql_data", data).build();
        Request request = new Request.Builder().url(API_URL + "dev_app/dev_login/").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(EditPersonalData.this, "保存失败", Toasty.LENGTH_SHORT).show();
                dialog.dismiss();
                Looper.loop();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                EditPersonalData.this.runOnUiThread(() -> {
                    dialog.dismiss();
                    //储存用户数据
                    SharedPreferences sharedPreferences = getSharedPreferences("QQ_User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sign", sign.getText().toString());
                    editor.putString("Qname", name.getText().toString());
                    editor.apply();
                    //更新文章库的名称
                    post_update("UPDATE `dev_posts` SET `post_user_name` =" + "'" + name.getText().toString() + "'" + "WHERE `dev_posts`.`post_user_id` = " + getShapeData("id"));
                });
            }
        });
    }

    private void post_update(String data) {
        final BaseDialog dialog = new BaseDialog(EditPersonalData.this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        RequestBody body = new FormBody.Builder().add("sql_data", AES(data)).build();
        Request request = new Request.Builder().url(API_URL + "dev_app/dev_getpost/").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(EditPersonalData.this, "保存失败", Toasty.LENGTH_SHORT).show();
                dialog.dismiss();
                Looper.loop();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                EditPersonalData.this.runOnUiThread(() -> {
                    dialog.dismiss();
                    Toasty.normal(EditPersonalData.this, "保存成功", Toasty.LENGTH_SHORT).show();
                });
            }
        });
    }


}

