/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.MyPagerAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.Entity.DetailsEntity;
import com.minibox.minideveloper.Fragment.MyLikeFragment;
import com.minibox.minideveloper.Fragment.MyPageFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyDetailsPage extends BaseActivity {
    private ShapeableImageView img;
    private TextView sing;
    private TextView name;
    private String ID = "0";
    private ShapeableImageView back_img;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Status_Style();
        }
        setContentView(R.layout.my_details_page);

        ImageButton exit = findViewById(R.id.details_exit);//退出
        LinearLayout editor = findViewById(R.id.details_editor);//编辑资料视图
        img = findViewById(R.id.details_head_portrait);//头像
        sing = findViewById(R.id.details_sing);//个性签名
        name = findViewById(R.id.details_name);//昵称
        TabLayout tableLayout = findViewById(R.id.tabLayout);//选项布局
        ViewPager viewPager = findViewById(R.id.viewPager);//滑动布局
        back_img = findViewById(R.id.details_background_image);//背景图片
        ImageButton img_edit = findViewById(R.id.Details_ImageButton_Editor);//编辑资料按钮

        //得到用户ID
        Bundle bundle = getIntent().getExtras();
        ID = bundle.getString("uid");
        api_http(AES("SELECT * FROM `dev_user` WHERE ID =" + "'" + ID + "'"));

        //如果是自己的主页，就显示编辑个人资料按钮
        if (ID.equals(getShapeData("id"))) {
            editor.setVisibility(View.VISIBLE);
        } else {
            editor.setVisibility(View.GONE);
        }

        exit.setOnClickListener(v -> finish());
        img_edit.setOnClickListener(v -> toActivity(EditPersonalData.class));//跳转到编辑个人资料

        String[] title = new String[]{"帖子", "赞过"};
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MyPageFragment());
        fragmentList.add(new MyLikeFragment());
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, title);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tableLayout.setupWithViewPager(viewPager);

    }

    /***查询用户信息***/
    private void api_http(String data) {

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.SECONDS).build();
        RequestBody body = new FormBody.Builder().add("sql_data", data).build();
        Request request = new Request.Builder().url(API_URL + "dev_app/dev_login/").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.info(MyDetailsPage.this, "网络跑了啦！⊙﹏⊙", Toasty.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String context = response.body().string();
                try {
                    DetailsEntity Entity = new Gson().fromJson(context, DetailsEntity.class);
                    //运行在UI线程
                    MyDetailsPage.this.runOnUiThread(() -> {

                        if (Entity != null && Entity.getCode() == 200) {

                            String Heard = Entity.getImage();
                            String Sing = Entity.getSign();
                            String Name = Entity.getName();
                            String back = Entity.getBackimage();
                            ID = Entity.getId();
                            Glide.with(MyDetailsPage.this).load(Heard).into(img);//加载头像
                            Glide.with(MyDetailsPage.this).load(back).into(back_img);//加载背景图片
                            if (Sing.length() == 0) {
                                sing.setText("Ta的签名空空如也嗷");
                            } else {
                                sing.setText(Sing);
                            }
                            name.setText(Name);

                        }
                    });
                } catch (Exception exception) {
                    Looper.prepare();
                    Toasty.normal(MyDetailsPage.this, "与服务器失联了！", Toasty.LENGTH_SHORT).show();
                    Looper.loop();
                }

                Log.e("TAG", "结果  " + context);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        api_http(AES("SELECT * FROM `dev_user` WHERE ID =" + "'" + ID + "'"));
    }


}
