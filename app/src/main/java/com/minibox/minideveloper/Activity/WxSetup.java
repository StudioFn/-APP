/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WxSetup extends BaseActivity {
    private EditText account, password, sum;
    private TextView enter;
    private ImageButton exit,help;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_setp);

        initListener();
        initFunction();

    }

    private void initListener() {
        account = findViewById(R.id.setp_account);
        password = findViewById(R.id.setp_password);
        enter = findViewById(R.id.setp_enter);
        exit = findViewById(R.id.step_exit);
        help = findViewById(R.id.step_help);
        sum = findViewById(R.id.setp_num);
    }

    private void initFunction() {

        enter.setOnClickListener(v -> {
             String ac = account.getText().toString().trim();
             String ps = password.getText().toString().trim();
             String sm = sum.getText().toString().trim();

            if (ac.length() > 0 &&ps.length() > 0 &&sm.length() > 0) {
                Http(ac,ps,sm);
            }else {
                Toast.makeText(WxSetup.this, "请填入完整的数据", Toast.LENGTH_LONG).show();
            }
        });

        exit.setOnClickListener(v -> finish());

        help.setOnClickListener(v ->{
            String uid = getShapeData("id");
            Bundle bundle = new Bundle();
            bundle.putString("id", "277");
            bundle.putString("uid", uid);
            toActivityWithBundle(MdArticleDetails.class,bundle);
        });
    }

    private void Http(String ac, String ps, String setp) {
        Api_Http("",
                "https://apis.jxcxin.cn/api/mi?user=" + ac + "&password=" + ps + "&step=" + setp + "&ver=cxydzsv3",
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String json = response.body().string();
                        WxSetup.this.runOnUiThread(() -> {
                            try {
                                JSONObject object = new JSONObject(json);
                                String msg = object.getString("msg");
                                String code = object.getString("code");
                                if (code.equals("200")) {
                                    Toasty.success(WxSetup.this, "修改成功", Toasty.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(WxSetup.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ignored) {
                            }
                            Log.e("返回的数据", "onResponse: "+json+"密码： "+ac);
                        });

                    }
                });
    }
}
