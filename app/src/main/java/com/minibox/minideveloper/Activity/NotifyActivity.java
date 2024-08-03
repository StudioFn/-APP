package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.NotificationAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.Entity.NotifyEntity;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotifyActivity extends BaseActivity {
    private ImageButton exit;
    private RefreshLayout refreshLayout;
    private List<NotifyEntity.ListDTO> data = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private int Count = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_activity);

        initView();
        initData();

    }

    private void initView() {
        exit = findViewById(R.id.notify_exit);
        RecyclerView recyclerView = findViewById(R.id.notify_recycler);
        refreshLayout = findViewById(R.id.notify_refresh);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        notificationAdapter = new NotificationAdapter(this);
        recyclerView.setAdapter(notificationAdapter);

        String id = getShapeData("id");
        String sql = "SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "'" + "ORDER BY `dev_notification`.`notifId` DESC LIMIT 0,10";
        getNotify(sql, "Refresh");
    }

    private void initData() {
        exit.setOnClickListener(v -> finish());

        refreshLayout.setOnRefreshListener(refreshLayout -> {
            String id = getShapeData("id");
            String sql = "SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "'" + "ORDER BY `dev_notification`.`notifId` DESC LIMIT 0,10";
            getNotify(sql, "Refresh");
        });

        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            String id = getShapeData("id");
            String sql = "SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "'" + "ORDER BY `dev_notification`.`notifId` DESC LIMIT " + Count + ",10";
            getNotify(sql, "");
        });

        notificationAdapter.SetOnClickItem((post_id, notify_id, art) -> {
            if (post_id != null && art == 0) {
                String uid = getShapeData("id");
                Bundle bundle = new Bundle();
                bundle.putString("id", post_id);
                bundle.putString("uid", uid);
                bundle.putBoolean("comment", true);
                toActivityWithBundle(MdArticleDetails.class, bundle);
            } else if (post_id != null && art == 1) {
                String uid = getShapeData("id");
                Bundle bundle = new Bundle();
                bundle.putString("id", post_id);
                bundle.putString("uid", uid);
                bundle.putBoolean("comment", true);
                toActivityWithBundle(RoutineActivity.class, bundle);
            }
            String sql = "UPDATE `dev_notification` SET `notifStatus` = 'true' WHERE `dev_notification`.`notifId` = " + notify_id;
            Api_Http(AES(sql), API_URL + "dev_app/dev_login/", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });
        });

    }

    public void getNotify(String sql, String type) {
        Api_Http(AES(sql), API_URL + "dev_app/dev_getnotify/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                NotifyActivity.this.runOnUiThread(() -> {
                    refreshLayout.finishRefresh(false);
                    refreshLayout.finishLoadMore(false);
                });
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();
                try {
                    NotifyActivity.this.runOnUiThread(() -> {
                        if (json.length() == 0) return;
                        NotifyEntity notifyEntity = new Gson().fromJson(json, NotifyEntity.class);
                        if (notifyEntity.getCode() == 200 && notifyEntity.getList().size() != 0) {
                            if (type.equals("Refresh")) {
                                data = notifyEntity.getList();
                                notificationAdapter.SetData(data);
                                Count = 10;
                                notificationAdapter.notifyDataSetChanged();
                            } else {
                                data.addAll(notifyEntity.getList());
                                notificationAdapter.SetData(data);
                                Count += 10;
                                notificationAdapter.notifyDataSetChanged();
                            }
                        } else {
                            refreshLayout.finishRefresh(true);
                            refreshLayout.finishLoadMore(true);
                        }
                        refreshLayout.finishRefresh(true);
                        refreshLayout.finishLoadMore(true);
                    });
                } catch (Exception ignored) {
                }

                Log.e("消息消息", json);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String id = getShapeData("id");
        String sql = "SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "'" + "ORDER BY `dev_notification`.`notifId` DESC LIMIT 0,10";
        getNotify(sql, "Refresh");
    }
}
