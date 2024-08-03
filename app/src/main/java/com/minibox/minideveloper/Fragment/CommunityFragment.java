package com.minibox.minideveloper.Fragment;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Activity.PostSearch;
import com.minibox.minideveloper.Adapter.CommunityAdapter;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.Entity.ArticleResponse;
import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.R;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommunityFragment extends BaseFragment {

    private LinearLayout li_layout;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CommunityAdapter communityAdapter;
    private TextView status_text;
    private LinearLayout renovate;
    private LinearLayout post_search;
    private List<CommentEntity> oldData = new ArrayList<>();
    private int Count = 5;

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
            communityAdapter.setData(oldData);
            communityAdapter.notifyDataSetChanged();
        }
    };

    public static Fragment newInstance() {
        return new CommunityFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        //渐显动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(800);
        alphaAnimation.setFillAfter(true);
        li_layout.startAnimation(alphaAnimation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_community, container, false);
        li_layout = v.findViewById(R.id.community_layout);
        refreshLayout = v.findViewById(R.id.smart);
        recyclerView = v.findViewById(R.id.community_recycler);
        status_text = v.findViewById(R.id.status_text);
        renovate = v.findViewById(R.id.renovate);
        post_search = v.findViewById(R.id.community_search);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_data();
    }

    public void init_data() {
        //渐显动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(800);
        alphaAnimation.setFillAfter(true);
        li_layout.startAnimation(alphaAnimation);

        refreshLayout.autoRefresh();//初始加载
        //线性布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);//设置RecyclerView布局管理器为LinearLayoutManager，并且为垂直排列
        communityAdapter = new CommunityAdapter(requireActivity());
        recyclerView.setAdapter(communityAdapter);

        //点击刷新
        renovate.setOnClickListener(v -> {
            http_api();
            Toast.makeText(getActivity(), "正在刷新，请稍后.....", Toast.LENGTH_SHORT).show();
        });

        //下拉刷新监听
        refreshLayout.setOnRefreshListener(refreshLayout -> http_api());
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout1 -> {
            Api_Https(AES("SELECT * FROM `dev_posts` WHERE post_look > 5 AND post_status = '通过' ORDER BY `dev_posts`.`ID` DESC,`post_user_id` ASC LIMIT " + Count + ",5"),
                    getSharedUser("id"),
                    API_URL + "dev_app/dev_getpost/", new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            requireActivity().runOnUiThread(() -> {
                                Toasty.normal(requireActivity(), "加载失败" + e, Toasty.LENGTH_LONG).show();
                                refreshLayout.finishLoadMore(true);
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            assert response.body() != null;
                            String context = response.body().string();
                            requireActivity().runOnUiThread(() -> {//在主线程更新
                                try {
                                    ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                                    if (articleResponse != null && articleResponse.getCode() == 200) {
                                        List<CommentEntity> datas = articleResponse.getList();//得到List列表
                                        oldData.addAll(datas);
                                        Count = Count + 5;
                                        handler.close();
                                    } else {
                                        Toasty.normal(requireActivity(), "到底啦！！", Toasty.LENGTH_SHORT).show();
                                        Log.e("数据库加载状态", context);
                                    }
                                    refreshLayout.finishRefresh(true);
                                    refreshLayout.finishLoadMore(true);
                                } catch (Exception ignored) {
                                }
                            });

                        }
                    });
        });
        //搜索
        post_search.setOnClickListener(v -> {
            toActivity(PostSearch.class);
        });
    }

    private void http_api() {

        Api_Https(AES("SELECT * FROM `dev_posts` WHERE post_look > 5 AND post_status = '通过' ORDER BY `dev_posts`.`ID` DESC LIMIT 5"), getSharedUser("id"),
                API_URL + "dev_app/dev_getpost/", new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        requireActivity().runOnUiThread(() -> {
                            //隐藏控件
                            status_text.setVisibility(View.GONE);
                            renovate.setVisibility(View.VISIBLE);
                            Toasty.info(requireActivity(), "网络连接失败", Toasty.LENGTH_LONG).show();
                        });
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        String context = response.body().string();

                        requireActivity().runOnUiThread(() -> {//在主线程更新
                            try {
                                renovate.setVisibility(View.GONE);
                                ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                                if (articleResponse != null && articleResponse.getCode() == 200) {
                                    oldData = articleResponse.getList();
                                    communityAdapter.setData(oldData);
                                    Count = 5;
                                    communityAdapter.notifyDataSetChanged();
                                } else {
                                    Toasty.error(requireActivity(), "加载错误", Toasty.LENGTH_SHORT).show();
                                    status_text.setVisibility(View.GONE);//隐藏加载中TextView控件
                                }
                            } catch (Exception exception) {
                                Toast.makeText(getActivity(), "与服务器失联了", Toast.LENGTH_SHORT).show();
                                status_text.setVisibility(View.GONE);//隐藏加载中TextView控件
                            }
                            status_text.setVisibility(View.GONE);//隐藏加载中TextView控件
                            refreshLayout.finishRefresh(true);
                            refreshLayout.finishLoadMore(true);
                            Log.i("获取文章借口返回的数据", context);
                        });

                    }
                });

    }

    //返回刷新
    public void refresh() {
        Api_Https(AES("SELECT * FROM `dev_posts` WHERE post_look > 5 ORDER BY `dev_posts`.`ID` DESC LIMIT " + Count),
                getSharedUser("id"),
                API_URL + "dev_app/dev_getpost/", new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        requireActivity().runOnUiThread(() -> {
                            Toasty.normal(requireActivity(), "加载失败" + e, Toasty.LENGTH_LONG).show();
                            refreshLayout.finishLoadMore(true);
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        String context = response.body().string();
                        requireActivity().runOnUiThread(() -> {//在主线程更新
                            try {
                                ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                                if (articleResponse != null && articleResponse.getCode() == 200) {
                                    List<CommentEntity> datas = articleResponse.getList();//得到List列表
                                    oldData.addAll(datas);
                                    handler.close();
                                } else {
                                    Log.e("数据库加载状态", context);
                                }
                                status_text.setVisibility(View.GONE);//隐藏加载中TextView控件
                                refreshLayout.finishRefresh(true);
                                refreshLayout.finishLoadMore(true);
                            } catch (Exception ignored) {
                            }
                        });


                    }
                });
    }

}
