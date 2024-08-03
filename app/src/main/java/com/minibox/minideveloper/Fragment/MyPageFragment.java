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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Activity.MdArticleDetails;
import com.minibox.minideveloper.Activity.RoutineActivity;
import com.minibox.minideveloper.Adapter.DynamicAdapter;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.BaseClass.BottomDialog;
import com.minibox.minideveloper.BaseDialog;
import com.minibox.minideveloper.ConsultArticle;
import com.minibox.minideveloper.Entity.ArticleResponse;
import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPageFragment extends BaseFragment {
    private String ID;
    private LinearLayout not_dynamic;
    private RecyclerView recyclerView;
    private DynamicAdapter dynamicAdapter;
    private List<CommentEntity> data = new ArrayList<>();

    private final Handler handler = new Handler() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void flush() {
            dynamicAdapter.setDatas(data);
            dynamicAdapter.notifyDataSetChanged();
        }

        @Override
        public void publish(LogRecord record) {
        }

        @Override
        public void close() throws SecurityException {
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page, container, false);
        not_dynamic = v.findViewById(R.id.page_fragment_not_dynamic);
        recyclerView = v.findViewById(R.id.page_fragment_recycler);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*设置RecyclerView布局管理器*/
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        /*设置RecyclerView适配器*/
        dynamicAdapter = new DynamicAdapter(getActivity());
        recyclerView.setAdapter(dynamicAdapter);
        /*获取帖子*/
        Bundle bundle = requireActivity().getIntent().getExtras();//得到用户ID
        ID = bundle.getString("uid");
        getPost("SELECT * FROM `dev_posts` WHERE post_user_id =" + "'" + ID + "'");
        /*设置Recycler View点击事件与长按事件*/
        dynamicAdapter.setOnRecyclerItemClickListener(new DynamicAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(String s, String img, String name, String title, String time, String content, int type, String images) {

                Bundle bundle1 = new Bundle();
                bundle1.putString("id", s);
                bundle1.putString("uid", ID);
                bundle1.putString("img", img);
                bundle1.putString("name", name);
                bundle1.putString("title", title);
                bundle1.putString("time", time);
                bundle1.putString("content", content);
                bundle1.putString("images", images);
                if (type == 1) {
                    toActivityWithBundle(RoutineActivity.class, bundle1);
                } else {
                    toActivityWithBundle(MdArticleDetails.class, bundle1);
                }


            }

            @Override
            public void onLongItemClick(String post_id) {

                BottomDialog bottomDialog = new BottomDialog(requireActivity(), R.style.base_dialog, R.layout.bottom_dialog);
                bottomDialog.show();
                LinearLayout linearLayout = bottomDialog.findViewById(R.id.bottom_dialog_delete);//删帖
                LinearLayout linearLayout1 = bottomDialog.findViewById(R.id.bottom_dialog_report);//举报帖子
                TextView textView = bottomDialog.findViewById(R.id.bottom_dialog_exit);//取消

                textView.setOnClickListener(v -> bottomDialog.dismiss());
                //删除按钮只有是自己的时候才显示出来
                if (ID.equals(getSharedUser("id"))) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                //删帖操作
                linearLayout.setOnClickListener(v -> {
                    bottomDialog.dismiss();//关闭底部弹窗
                    //是否删除帖子确认弹窗
                    BaseDialog dialog = new BaseDialog(requireActivity(), R.style.base_dialog, R.layout.dialog_status_tow);
                    dialog.show();
                    TextView enter = dialog.findViewById(R.id.dialog_enter);
                    TextView dismiss = dialog.findViewById(R.id.dialog_dismiss);
                    enter.setOnClickListener(v1 -> {//确定删除
                        delete_api("DELETE FROM `dev_posts` WHERE `dev_posts`.`ID` = " + post_id);
                        dialog.dismiss();//关闭确认弹窗
                    });
                    dismiss.setOnClickListener(v12 -> {//取消
                        dialog.dismiss();//关闭确认弹窗
                    });
                });
            }
        });

    }

    public void getPost(String sql) {

        final BaseDialog dialog = new BaseDialog(getActivity(), R.style.base_dialog, R.layout.dialog_status);
        dialog.show();

        Api_Http(AES(sql), API_URL + "dev_app/dev_getpost/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toasty.error(requireActivity(), "连接失败", Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    try {
                        ArticleResponse articleResponse = new Gson().fromJson(json, ArticleResponse.class);
                        if (articleResponse != null && articleResponse.getCode() == 200) {
                            data = articleResponse.getList();
                            handler.flush();//通知Handler更新recycler数据
                            dialog.dismiss();
                        } else {
                            not_dynamic.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    } catch (Exception exception) {
                        not_dynamic.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "与服务器失联了", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.dismiss();
            }
        });
    }

    /***删除帖子***/
    private void delete_api(String data) {

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        RequestBody body = new FormBody.Builder().add("sql_data", AES(data)).build();
        Request request = new Request.Builder().url(API_URL + "dev_app/dev_getpost/").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    ArticleResponse articleResponse = new Gson().fromJson(context, ArticleResponse.class);
                    if (articleResponse != null && articleResponse.getCode() == 0) {
                        getPost("SELECT * FROM `dev_posts` WHERE post_user_id =" + "'" + ID + "'");
                        handler.flush();
                        Toasty.success(requireActivity(), "删除成功", Toasty.LENGTH_SHORT).show();
                    }
                });

                Log.e("TAG", "code:结果  " + context);

            }
        });
    }

}
