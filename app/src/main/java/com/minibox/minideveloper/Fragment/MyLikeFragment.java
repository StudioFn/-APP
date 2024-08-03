/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Fragment;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.PraiseAdapter;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.Entity.LikeArticleEntity;
import com.minibox.minideveloper.R;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyLikeFragment extends BaseFragment {
    private SmartRefreshLayout refreshLayout;
    private List<LikeArticleEntity.ListArticle> data;
    private PraiseAdapter adapter;

    private final Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void flush() {
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void close() throws SecurityException {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_like, container, false);
        refreshLayout = v.findViewById(R.id.my_like_smart);
        RecyclerView recyclerView = v.findViewById(R.id.my_like_recycler);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        adapter = new PraiseAdapter(requireActivity());
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = requireActivity().getIntent().getExtras();
        String uid = bundle.getString("uid");
        //加载
        getLikePost(uid);
        //刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> getLikePost(uid));
    }

    public void getLikePost(String userId) {
        Api_Http(AES(userId), API_URL + "dev_app/dev_getlike_post/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "error" + e, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    refreshLayout.finishRefresh();
                    try {
                        LikeArticleEntity article = new Gson().fromJson(json, LikeArticleEntity.class);
                        if (!article.getCode().equals(0)) {
                            data = article.getList();
                            handler.flush();
                        } else {

                        }
                    } catch (Exception ignored) {
                    }
                    Log.e("检查信息", "onResponse: " + json);
                });
            }
        });
    }

}
