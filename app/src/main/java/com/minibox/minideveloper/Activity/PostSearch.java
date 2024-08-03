package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.CommunityAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.BaseDialog;
import com.minibox.minideveloper.Entity.ArticleResponse;
import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.R;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostSearch extends BaseActivity {
    private ImageButton exit;
    private EditText editText;
    private ImageView search;
    private RecyclerView recyclerView;
    private int Count = 10;
    private CommunityAdapter communityAdapter;
    private RefreshLayout refreshLayout;
    private List<CommentEntity> Data = new ArrayList<>();
    private final Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @Override
        public void flush() {

        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void close() throws SecurityException {
            communityAdapter.setData(Data);
            communityAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_search);

        initView();
        initData();

    }


    public void initView() {
        exit = findViewById(R.id.search_exit);//退出
        editText = findViewById(R.id.search_edit);//输入框
        search = findViewById(R.id.search_img_button);//搜索按钮
        recyclerView = findViewById(R.id.post_search);//列表
        refreshLayout = findViewById(R.id.search_smart);//加载控件
    }

    private void initData() {
        /**设置布局管理器**/
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        communityAdapter = new CommunityAdapter(this);
        recyclerView.setAdapter(communityAdapter);
        //搜索
        search.setOnClickListener(v -> {
            String search = editText.getText().toString().trim();
            if (search.length() > 0) {
                Search_Post("SELECT * from dev_posts WHERE INSTR(post_title,'" + search + "')>0 ORDER BY `dev_posts`.`ID` DESC LIMIT 0,10");
            } else {
                Toasty.normal(PostSearch.this, "请输入内容", Toasty.LENGTH_SHORT).show();
            }
        });
        //加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            String search = editText.getText().toString().trim();
            if (search.length() < 1) {
                refreshLayout.finishLoadMore(false);
            } else {
                Load_Post("SELECT * from dev_posts WHERE INSTR(post_title,'" + search + "')>0 ORDER BY `dev_posts`.`ID` DESC LIMIT " + Count + ",10");
            }
        });
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.finishRefresh(true));
        //退出
        exit.setOnClickListener(v -> finish());
    }

    private void Load_Post(String data) {
        Api_Https(AES(data), getShapeData("id"), API_URL + "dev_app/dev_getpost/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                PostSearch.this.runOnUiThread(() -> refreshLayout.finishLoadMore());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String content = response.body().string();
                PostSearch.this.runOnUiThread(() -> {
                    ArticleResponse response1 = new Gson().fromJson(content, ArticleResponse.class);
                    if (response1 != null && response1.getCode() == 200) {
                        List<CommentEntity> newData = response1.getList();
                        Count = Count + 10;
                        Data.addAll(newData);
                        handler.close();
                        refreshLayout.finishLoadMore();
                    } else {
                        Toasty.normal(PostSearch.this, "到底啦", Toasty.LENGTH_SHORT).show();
                        refreshLayout.finishLoadMore();
                    }
                });
            }
        });
    }

    public void Search_Post(String data) {
        BaseDialog dialog = new BaseDialog(this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();
        Api_Https(AES(data), getShapeData("id"), API_URL + "dev_app/dev_getpost/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                dialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String content = response.body().string();
                PostSearch.this.runOnUiThread(() -> {
                    ArticleResponse response1 = new Gson().fromJson(content, ArticleResponse.class);
                    if (response1 != null && response1.getCode() == 200) {
                        Data = response1.getList();
                        handler.close();
                        dialog.dismiss();
                    } else {
                        Toasty.normal(PostSearch.this, "无结果", Toasty.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

}
