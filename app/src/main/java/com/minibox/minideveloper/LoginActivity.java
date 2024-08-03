package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private EditText account, password;
    private Button enter;
    private String CODE = null;
    private TextView toRegister;
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private ShapeableImageView QQLogin;
    private String OPENID = "123456789";
    private String Response = " ";
    private String HEAD_IMG = "https://gitcode.net/qq_44112897/imgbed/-/raw/master/comic/43.jpg";//默认头像链接
    private String HEAD_IMG_TOW = "";
    private static final String TAG = "LoginActivity";
    private static final String APP_ID = "102018641";//官方获取的APPID 102018641

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使状态栏完全透明，条件，需要在Theme文件样式代码中，加上<item name="android:windowTranslucentStatus">true</item>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarTheme();
        }
        setContentView(R.layout.dev_login_activity);

        account = findViewById(R.id.login_account);//账号输入框
        password = findViewById(R.id.login_password);//密码输入框
        enter = findViewById(R.id.login_enter);//登录按钮
        toRegister = findViewById(R.id.register);//跳转到注册页面
        QQLogin = findViewById(R.id.qq_login);//QQ登录
        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, LoginActivity.this.getApplicationContext());
        Tencent.setIsPermissionGranted(true, Build.MODEL);
        initData();

    }

    public void initData() {
        enter.setOnClickListener(v -> {//登录
            String ac = LoginActivity.this.account.getText().toString().trim();
            String pa = LoginActivity.this.password.getText().toString().trim();
            if (ac.length() <= 0) {
                Toasty.info(LoginActivity.this, "请输入账号", Toasty.LENGTH_SHORT).show();
                return;
            }
            if (pa.length() <= 0) {
                Toasty.info(LoginActivity.this, "请输入密码", Toasty.LENGTH_SHORT).show();
                return;
            }
            if (ac != null || ac.length() > 0 && pa != null || pa.length() > 0) {
                //拼接请求参数
                String data = AES("select * from dev_user where account = " + "'" + ac + "'" + "AND password =" + "'" + pa + "'");
                login(data, 1);
            }
        });

        toRegister.setOnClickListener(v -> {//注册
            Bundle bundle = new Bundle();
            bundle.putString("openid", "");
            bundle.putString("head_img", HEAD_IMG);
            toActivityWithBundle(RegisterActivity.class, bundle);
        });

        QQLogin.setOnClickListener(v -> {//QQ一键登录
            mIUiListener = new BaseUiListener();
            if (!mTencent.isSessionValid()) {
                mTencent.loginWithOEM(LoginActivity.this, "all", mIUiListener, true, "10000144", "10000144", "xxxx");
            }
            mUserInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken()); //获取用户信息
            mUserInfo.getUserInfo(mIUiListener);
        });
    }

    //自定义监听器实现IUiListener接口后，需要实现的3个方法 onComplete完成 onError错误 onCancel取消
    private class BaseUiListener implements IUiListener {
        //自定义弹窗
        final BaseDialog dialog = new BaseDialog(LoginActivity.this, R.style.base_dialog, R.layout.dialog_status);

        @Override
        public void onComplete(Object response) {
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            dialog.show();
            try {
                String openID = obj.getString("openid");   /**提交必要参数（openid,accessToken,expires）以获取用户基本参数**/
                String expires = obj.getString("expires_in");
                String accessToken = obj.getString("access_token");

                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);

                OPENID = openID;
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        dialog.dismiss();
                        String data = AES(" SELECT * FROM `dev_user` WHERE `openid` LIKE " + "'" + OPENID + "'");
                        JSONObject object = (JSONObject) response;
                        //获取用户头像链接
                        try {
                            HEAD_IMG = object.getString("figureurl_qq_2");
                            HEAD_IMG_TOW = object.getString("figureurl_2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        login(data, 2);
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Toast.makeText(LoginActivity.this, "错误" + uiError, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onWarning(int i) {
                        Toast.makeText(LoginActivity.this, "错误" + i, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            dialog.dismiss();
            Toasty.info(LoginActivity.this, "授权失败", Toasty.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            dialog.dismiss();
            Toasty.info(LoginActivity.this, "授权取消", Toasty.LENGTH_SHORT).show();
        }

        @Override
        public void onWarning(int i) {

        }
    }

    //QQ回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void login(String data, int type) {
        final BaseDialog dialog = new BaseDialog(LoginActivity.this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();
        //创建网络处理的对象
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        //创建一个RequestBody，存放重要数据的键值对
        RequestBody body = new FormBody.Builder()
                .add("sql_data", data)
                .build();
        //创建一个请求对象，传入URL地址和相关数据的键值对的对象
        Request request = new Request.Builder()
                .url(API_URL + "dev_app/dev_login/")
                .post(body)
                .build();
        //创建一个能处理请求数据的操作类
        Call call = client.newCall(request);
        //使用异步任务的模式请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "错误信息：" + e);
                dialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                Response = context;
                try {
                    JSONObject jsonObject = new JSONObject(context);
                    String code = jsonObject.getString("code");
                    CODE = code;
                    Log.e("TAG", "code:结果  " + code + context + "openid：" + OPENID);
                } catch (Exception e) {
                }

                if (type == 1) {
                    if (CODE.equals("0")) {
                        //一个线程中没有调用Looper.prepare(),就不能在该线程中创建Toast
                        Looper.prepare();
                        Toasty.error(LoginActivity.this, "账号不存在或密码错误", Toasty.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Looper.loop();
                    } else if (CODE.equals("200")) {
                        Looper.prepare();
                        Toasty.success(LoginActivity.this, "登录成功！", Toasty.LENGTH_SHORT).show();
                        dialog.dismiss();
                        jsonObject();
                        finish();
                        Looper.loop();
                    }

                }

                if (type == 2) {
                    if (CODE.equals("0")) {
                        //一个线程中没有调用Looper.prepare(),就不能在该线程中创建Toast
                        Looper.prepare();
                        Toasty.info(LoginActivity.this, "此QQ还未绑定和圈账号", Toasty.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("openid", OPENID);
                        bundle.putString("head_img", HEAD_IMG);
                        bundle.putString("head_img_tow", HEAD_IMG_TOW);
                        toActivityWithBundle(RegisterActivity.class, bundle);
                        dialog.dismiss();
                        Looper.loop();

                    } else if (CODE.equals("200")) {
                        Looper.prepare();
                        Toasty.success(LoginActivity.this, "登录成功！", Toasty.LENGTH_SHORT).show();
                        dialog.dismiss();
                        jsonObject();
                        finish();
                        Looper.loop();
                    }
                }

                Log.e("TAG", "结果  " + context);
            }
        });

    }

    public void jsonObject() {
        try {
            JSONObject jsonObject = new JSONObject(Response);
            String id = jsonObject.getString("id");
            String openid = jsonObject.getString("openid");
            String sign = jsonObject.getString("sign");
            String image = jsonObject.getString("image");
            String name = jsonObject.getString("name");
            String account = jsonObject.getString("account");
            String password = jsonObject.getString("password");
            String imageqq = jsonObject.getString("imageqq");
            //储存用户数据
            SharedPreferences sharedPreferences = getSharedPreferences("QQ_User", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id", id);
            editor.putString("sign", sign);
            editor.putString("Qname", name);
            editor.putString("Qimg", image);
            editor.putString("openid", openid);
            editor.putString("account", account);
            editor.putString("imageqq", imageqq);
            editor.putString("password", password);
            editor.apply();

        } catch (Exception ignored) {
        }
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
