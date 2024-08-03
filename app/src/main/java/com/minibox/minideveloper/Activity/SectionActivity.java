package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.CommunityAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.Entity.ArticleResponse;
import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.R;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SectionActivity extends BaseActivity {
    private ShapeableImageView imageView;
    private TextView name;
    private RecyclerView recyclerView;
    private ImageButton exit;
    private int Count = 10;
    private RefreshLayout refreshLayout;
    private CommunityAdapter communityAdapter;
    private List<CommentEntity> oldData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusStyle();
        }
        setContentView(R.layout.secton_article);

        initView();
        initData();

    }

    private void initView() {
        name = findViewById(R.id.section_article_name);
        imageView = findViewById(R.id.section_article_img);
        recyclerView = findViewById(R.id.section_article_recycler);
        exit = findViewById(R.id.section_article_exit);
        refreshLayout = findViewById(R.id.section_article_refresh);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String i = bundle.getString("type");
        int img = bundle.getInt("img");
        if (i.equals("鱼塘") || i.equals("文章")) {
            name.setText(i);
        }
        imageView.setImageResource(img);
        exit.setOnClickListener(v -> finish());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        communityAdapter = new CommunityAdapter(this);
        recyclerView.setAdapter(communityAdapter);

        //初始加载
        refreshLayout.autoRefresh();
        //刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> API("SELECT * FROM `dev_posts` WHERE `post_type` = '" + i + "' AND post_status = '通过' ORDER BY `dev_posts`.`ID` DESC LIMIT 0,10"));
        //加载新数据
        refreshLayout.setOnLoadMoreListener(refreshLayout1 -> LodAPI("SELECT * FROM `dev_posts` WHERE `post_type` = '" + i + "' AND post_status = '通过' ORDER BY `dev_posts`.`ID` DESC LIMIT " + Count + ",10"));
    }

    public void API(String data) {
        Api_Https(AES(data), getShapeData("id"), API_URL + "dev_app/dev_getpost/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                assert response.body() != null;
                String context = response.body().string();

                SectionActivity.this.runOnUiThread(() -> {//在主线程更新
                    try {
                        ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                        if (articleResponse != null && articleResponse.getCode() == 200) {
                            oldData = articleResponse.getList();
                            communityAdapter.setData(oldData);
                            Count = 10;
                            communityAdapter.notifyDataSetChanged();
                        } else {

                        }
                    } catch (Exception exception) {
                        Toast.makeText(SectionActivity.this, "与服务器失联了", Toast.LENGTH_SHORT).show();
                    }
//                    Log.i("获取文章借口返回的数据",context);
                    refreshLayout.finishRefresh(true);
                    refreshLayout.finishLoadMore(true);
                });

            }
        });
    }

    public void LodAPI(String data) {
        Api_Https(AES(data), getShapeData("id"), API_URL + "dev_app/dev_getpost/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                assert response.body() != null;
                String context = response.body().string();

                SectionActivity.this.runOnUiThread(() -> {//在主线程更新
                    try {
                        ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                        if (articleResponse != null && articleResponse.getCode() == 200) {
                            oldData.addAll(articleResponse.getList());
                            communityAdapter.setData(oldData);
                            Count = Count + 10;
                            communityAdapter.notifyDataSetChanged();
                        } else {
                            Toasty.normal(SectionActivity.this, "到底啦！", Toasty.LENGTH_SHORT).show();
                        }
                    } catch (Exception exception) {
                        Toast.makeText(SectionActivity.this, "与服务器失联了", Toast.LENGTH_SHORT).show();
                    }
                    refreshLayout.finishRefresh(true);
                    refreshLayout.finishLoadMore(true);
                });

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void StatusStyle() {
        //使状态栏完全透明，条件，需要在Theme文件样式代码中，加上<item name="android:windowTranslucentStatus">true</item>"
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体颜色
    }

}
