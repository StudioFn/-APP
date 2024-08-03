package com.minibox.minideveloper.Fragment;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.minibox.minideveloper.Activity.ChatActivity;
import com.minibox.minideveloper.Activity.NewArticleActivity;
import com.minibox.minideveloper.Activity.NotifyActivity;
import com.minibox.minideveloper.ArticleActivity;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.PhotoBedActivity;
import com.minibox.minideveloper.Entity.NotifyEntity;
import com.minibox.minideveloper.LoginActivity;
import com.minibox.minideveloper.MyDetailsPage;
import com.minibox.minideveloper.R;
import com.minibox.minideveloper.SettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyFragment extends BaseFragment {
    private LinearLayout jump_cloud, publish_article, notification, ChatGpt;
    private RelativeLayout Animation_Li;
    private ImageButton jump_setting;
    private ShapeableImageView hot_img;
    private TextView username;
    private TextView noneName;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my, container, false);
        jump_cloud = v.findViewById(R.id.Yun_App_Up);//果果图床
        jump_setting = v.findViewById(R.id.my_fragment_setting);//跳转到设置界面
        Animation_Li = v.findViewById(R.id.animation_my);//界面动画
        hot_img = v.findViewById(R.id.my_banner);//用户头像
        username = v.findViewById(R.id.name);//用户昵称
        noneName = v.findViewById(R.id.none_name);//无昵称时显示
        publish_article = v.findViewById(R.id.publish_an_article);//发帖
        notification = v.findViewById(R.id.notify);//消息
        ChatGpt = v.findViewById(R.id.chatGpt);//AI
        return v;
    }

    //返回刷新
    @Override
    public void onResume() {
        super.onResume();
        Renovate();
        String id = getSharedUser("id");
        getNotify("SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "' AND " + "notifStatus='false'");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        jump_cloud.setOnClickListener(v -> toActivity(PhotoBedActivity.class));//果果图床

        jump_setting.setOnClickListener(v -> toActivity(SettingActivity.class));//设置

        notification.setOnClickListener(v -> toActivity(NotifyActivity.class));//消息

        ChatGpt.setOnClickListener(v -> toActivity(ChatActivity.class));//机器人

        publish_article.setOnClickListener(v -> {//发帖
            if (getSharedUser("account").length() <= 0) {
                Toasty.info(requireActivity(), "请先登录", Toasty.LENGTH_SHORT).show();
                toActivity(LoginActivity.class);
            } else if (getCloudData("article_type").equals("logo")) {
                toActivity(ArticleActivity.class);
            } else {
                toActivity(NewArticleActivity.class);
            }
        });

        String id = getSharedUser("id");
        getNotify("SELECT * FROM `dev_notification` where notifUserId = " + "'" + id + "' AND " + "notifStatus='false'");

        String url = getSharedUser("Qimg");//QQ头像链接
        String qname = getSharedUser("Qname");
        if (qname.length() <= 0) {
            username.setVisibility(View.GONE);
            noneName.setVisibility(View.VISIBLE);
            //如果未登录，跳转到登录界面，否则跳转到主页
            hot_img.setOnClickListener(v -> toActivity(LoginActivity.class));
        } else {
            username.setText("欢迎 " + qname);
            username.setVisibility(View.VISIBLE);
            noneName.setVisibility(View.GONE);
            //如已经登录，跳转到详情界面
            hot_img.setOnClickListener(v -> {
                String uid = getSharedUser("id");
                Bundle bundle = new Bundle();
                bundle.putString("id", uid);
                toActivityWithBundle(MyDetailsPage.class, bundle);
            });
        }

        Glide.with(requireActivity()).load(url).into(hot_img);/*加载头像*/

        //渐显动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(700);
        alphaAnimation.setFillAfter(true);
        Animation_Li.startAnimation(alphaAnimation);

    }

    public void Renovate() {
        String url = getSharedUser("Qimg");//QQ头像链接
        String qname = getSharedUser("Qname");
        try {
            Glide.with(getContext()).load(url).into(hot_img);
        } catch (Exception ignored) {
            Glide.with(getContext())
                    .load(API_URL + "imgApi/public/uploads/20230528/1685252374.jpg")
                    .into(hot_img);
        }//加载头像

        if (qname.length() <= 0) {
            username.setVisibility(View.GONE);
            noneName.setVisibility(View.VISIBLE);
            //如果未登录，跳转到登录界面，否则跳转到主页
            hot_img.setOnClickListener(v -> toActivity(LoginActivity.class));
        } else {
            username.setText("欢迎 " + qname);
            //如已经登录，跳转到详情界面
            hot_img.setOnClickListener(v -> {
                String uid = getSharedUser("id");
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                toActivityWithBundle(MyDetailsPage.class, bundle);
            });
            username.setVisibility(View.VISIBLE);
            noneName.setVisibility(View.GONE);
        }
    }

    public void setNotification(int count, String name) {
        Intent click = new Intent(getActivity(), NotifyActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(requireActivity(), R.string.app_name, click, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(requireActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(requireActivity(), getString(R.string.app_name));
        }
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.img)
                .setContentTitle("你收到了" + count + "条通知")
                .setContentText(name + "回复了你");
        Notification notify = builder.build();
        NotificationManager notifyMgr = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(1, notify);

    }

    public void getNotify(String sql) {
        Api_Http(AES(sql), API_URL + "dev_app/dev_getnotify/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();
                try {
                    NotifyEntity notifyEntity = new Gson().fromJson(json, NotifyEntity.class);
                    if (notifyEntity.getCode().equals(200) && notifyEntity.getList().size() > 0) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String s = object.getString("postUserName");
                                setNotification(i + 1, s);
                            }
                        } catch (JSONException ignored) {
                        }

                    }
                } catch (Exception ignored) {
                }


            }
        });
    }


}
