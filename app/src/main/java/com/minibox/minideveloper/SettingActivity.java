package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";
    private static final String APP_ID = "102018641";//官方获取的APPID 1105602574
    private RelativeLayout qq, exit_login, feedback, qq_channel;
    private ShapeableImageView img;
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private ImageButton exit;
    private Switch switchButton;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体颜色
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settin_activity);

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, SettingActivity.this.getApplicationContext());
        Tencent.setIsPermissionGranted(true, Build.MODEL);
        initView();
        initData();
    }

    public void initView() {
        qq = findViewById(R.id.QQLogin);
        img = findViewById(R.id.qq_img);
        exit = findViewById(R.id.setting_exit);
        qq_channel = findViewById(R.id.setting_channel);
        exit_login = findViewById(R.id.exit_login);
        feedback = findViewById(R.id.setting_help);
        switchButton = findViewById(R.id.setting_article);
    }

    public void initData() {
        //QQ登录
        qq.setOnClickListener(v -> {
            mIUiListener = new BaseUiListener();
            if (!mTencent.isSessionValid()) {
                mTencent.loginWithOEM(SettingActivity.this, "all", mIUiListener, true, "10000144", "10000144", "xxxx");
            }
            mUserInfo = new UserInfo(SettingActivity.this, mTencent.getQQToken()); //获取用户信息
            mUserInfo.getUserInfo(mIUiListener);

        });

        //退出登录
        exit_login.setOnClickListener(v -> {
            SharedPreferences userInfo = getSharedPreferences("QQ_User", MODE_PRIVATE);
            SharedPreferences.Editor editor = userInfo.edit();
            editor.clear();
            editor.apply();
            finish();
        });

        //退出
        exit.setOnClickListener(v -> finish());

        //反馈
        feedback.setOnClickListener(v -> toActivity(FeedbackActivity.class));

        //加入QQ频道
        qq_channel.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://forward/url?src_type=web&style=default&plg_auth=1&version=1&url_prefix=aHR0cHM6Ly9xdW4ucXEuY2" +
                    "9tL3Fxd2ViL3F1bnByby9zaGFyZT9fd3Y9MyZfd3d2PTEyOCZhcHBDaGFubmVsPXNoYXJlJmludml0ZUNvZGU9MVpPY09zMXI5WnAmYnVzaW5lc3NUeXBlPTkmZnJvbT0xODEwNzQmYml6PWthJm1ha" +
                    "W5Tb3VyY2VJZD1zaGFyZSZzdWJTb3VyY2VJZD1vdGhlcnMmanVtcHNvdXJjZT1zaG9ydHVybCMvb3V0"));
            startActivity(intent);
        });

        //加载QQ头像
        Glide.with(this).load(getShapeData("Qimg")).into(img);

        //改变发帖方式
        switchButton.setChecked(getCloudData("article_type").equals("logo"));
        SharedPreferences sharedPreferences = getSharedPreferences("Cloud", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switchButton.setOnClickListener(v -> {
            if (getCloudData("article_type").equals("logo")) {
                editor.putString("article_type", "none");
                editor.apply();
            } else {
                editor.putString("article_type", "logo");
                editor.apply();
            }
        });

    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String expires = obj.getString("expires_in");
                String accessToken = obj.getString("access_token");

                mTencent.setAccessToken(accessToken, expires);
                mTencent.setOpenId(openID);   /*提交必要参数（openid,accessToken,expires）以获取用户基本信息*/

                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        Log.e("SettingActivity", response.toString());
                        try {
                            JSONObject jo = (JSONObject) response;

                            String imgs = jo.getString("figureurl_qq_2");
                            String img2 = jo.getString("figureurl_2");
                            String id = getShapeData("id");
                            String sql_data = "UPDATE `dev_user` SET `image` = " + "'" + imgs + "'" + ",`figureurl_qq_2` = " + "'" + img2 + "'" + " WHERE `dev_user`.`id` =" + "'" + id + "'";
                            String sql_dataTow = "UPDATE `dev_user` SET `openid` = " + "'" + openID + "'" + " WHERE `dev_user`.`id` =" + "'" + id + "'";
                            http_api(sql_data);
                            http_api(sql_dataTow);

                            Toasty.normal(SettingActivity.this, "绑定成功", Toasty.LENGTH_SHORT).show();
                            //储存用户数据
                            SharedPreferences sharedPreferences = getSharedPreferences("QQ_User", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Qimg", imgs);
                            editor.apply();
                            //更新头像
                            Glide.with(SettingActivity.this).load(getShapeData("Qimg")).into(img);
                        } catch (Exception ignored) {

                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Toast.makeText(SettingActivity.this, "错误" + uiError, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onWarning(int i) {
                        Toast.makeText(SettingActivity.this, "错误" + i, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toasty.info(SettingActivity.this, "授权失败", Toasty.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toasty.normal(SettingActivity.this, "授权取消", Toasty.LENGTH_SHORT).show();
        }

        @Override
        public void onWarning(int i) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //修改头像
    public void http_api(String sql_data) {
        Api_Http(AES(sql_data), API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

            }
        });
    }

}


