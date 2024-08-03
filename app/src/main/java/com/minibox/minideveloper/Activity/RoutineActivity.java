/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.CommentAdapter;
import com.minibox.minideveloper.ArticleActivity;
import com.minibox.minideveloper.BaseClass.Api_Config;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.BaseClass.BottomDialog;
import com.minibox.minideveloper.BaseClass.ScrollLinearLayoutManager;
import com.minibox.minideveloper.BaseDialog;
import com.minibox.minideveloper.Entity.CommentResponse;
import com.minibox.minideveloper.Entity.PostCommentEntity;
import com.minibox.minideveloper.MyDetailsPage;
import com.minibox.minideveloper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RoutineActivity extends BaseActivity {
    private LinearLayout not_comment;
    private String PostId = null;
    private String UID = null;
    private String ID = null;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private TextView content;
    private CommentAdapter commentAdapter;
    private TextView name, title, time, label;
    private ShapeableImageView shapeableImageView;
    private boolean isComment = false;
    private List<PostCommentEntity> datas = new ArrayList<>();
    private String CODE = "32";
    private ViewPager viewPager;
    private final List<String> data = new ArrayList<>();
    private String IMG, NAME, TITLE, TIME, Post_Uid, CONTENT, IMG_S;
    private final ScrollLinearLayoutManager Manager = new ScrollLinearLayoutManager(this);

    private final java.util.logging.Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
        }

        @Override
        public void flush() {
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void close() throws SecurityException {
            commentAdapter.SetData(datas);
            commentAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_activity);

        name = findViewById(R.id.routine_author_name);//作者昵称
        shapeableImageView = findViewById(R.id.routine_article_img);//作者头像
        title = findViewById(R.id.routine_article_title);//标题
        ImageButton exit = findViewById(R.id.routine_article_exit);//退出
        ImageButton menu = findViewById(R.id.routine_article_menu);//菜单
        recyclerView = findViewById(R.id.routine_article_recycler);//评论列表
        TextView comment_Button = findViewById(R.id.routine_comment_button);//评论按钮
        not_comment = findViewById(R.id.routine_if_not_comment);//无评论显示TextView控件
        content = findViewById(R.id.routine_text);//内容
        LinearLayout share = findViewById(R.id.routine_share);//分享
        time = findViewById(R.id.routine_article_time);//发布时间
        label = findViewById(R.id.routine_article_label);//官方标识
        viewPager = findViewById(R.id.routine_view_pager);//多张图片
        scrollView = findViewById(R.id.routine_scrollView);

        /*编辑文章*/
        menu.setOnClickListener(v -> Menu());

        /*获得必要数据*/
        try {
            Bundle bundle = getIntent().getExtras();
            PostId = bundle.getString("id");
            UID = bundle.getString("uid");
            ID = getShapeData("id");
            TITLE = bundle.getString("title");
            TIME = bundle.getString("time");
            CONTENT = bundle.getString("content");
            NAME = bundle.getString("name");
            IMG = bundle.getString("img");
            Post_Uid = bundle.getString("uid");
            isComment = bundle.getBoolean("comment");
            IMG_S = bundle.getString("images");

            name.setText(NAME);
            title.setText(TITLE);
            //【时间】通过时间戳计算距离发帖天数
            time.setText(getTimeAgo(TIME));
            content.setText(CONTENT);

            if (Post_Uid.equals("20")) {
                label.setVisibility(View.VISIBLE);
            }
            Glide.with(RoutineActivity.this).load(IMG).into(shapeableImageView);//加载头像
            if (isComment) {
                scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, recyclerView.getTop() + content.getHeight()), 1000);
            }
            showImage_ViewPager();
        } catch (Exception ignored) {
            Bundle bundle = getIntent().getExtras();
            PostId = bundle.getString("id");
            UID = bundle.getString("uid");
            ID = getShapeData("id");
            //加载文章
            LoadArticle();
        }


        comment_api("SELECT * FROM `dev_comment` WHERE PageID = " + "'" + PostId + "'");//加载评论


        /*更新阅读数*/
        String id = getShapeData("id");
        if (!id.equals(UID)) {
            Api_Http(AES("UPDATE `dev_posts` SET `post_look` = `post_look` + 1 WHERE `dev_posts`.`ID` =" + PostId),
                    API_URL + "dev_app/dev_login/", new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                        }
                    });
        }

        Manager.setOrientation(LinearLayoutManager.VERTICAL);
        Manager.setReverseLayout(true);
        Manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(Manager);

        commentAdapter = new CommentAdapter(RoutineActivity.this);
        recyclerView.setAdapter(commentAdapter);

        //分享链接
        share.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "【" + TITLE + "】 " +
                    API_URL + "MarkdownPage/?p=" + PostId);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "分享到"));
        });
        exit.setOnClickListener(v -> finish());//退出当前程序

        shapeableImageView.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("uid", UID);
            toActivityWithBundle(MyDetailsPage.class, bundle1);
        });//跳转到作者详情页

        //RecyclerView点击事件（评论的回复）
        commentAdapter.setOnRecyclerItemClickListener(new CommentAdapter.OnItemClickListener() {
            /**删除评论**/
            @Override
            public void onLongItemClick(String uid, String post_id, String content, int position) {
                BottomDialog bottomDialog = new BottomDialog(RoutineActivity.this, R.style.base_dialog, R.layout.bottom_dialog);
                bottomDialog.show();
                LinearLayout linearLayout = bottomDialog.findViewById(R.id.bottom_dialog_delete);//删帖
                LinearLayout linearLayout1 = bottomDialog.findViewById(R.id.bottom_dialog_report);//举报
                TextView textView = bottomDialog.findViewById(R.id.bottom_dialog_exit);//取消

                textView.setOnClickListener(v -> bottomDialog.dismiss());
                //删除按钮只有是自己的时候才显示出来

                if (getShapeData("id").equals(uid)) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                //删帖操作
                linearLayout.setOnClickListener(v -> {
                    bottomDialog.dismiss();//关闭底部弹窗
                    //是否删除帖子确认弹窗
                    BaseDialog dialog = new BaseDialog(RoutineActivity.this, R.style.base_dialog, R.layout.dialog_status_tow);
                    dialog.show();
                    TextView enter = dialog.findViewById(R.id.dialog_enter);
                    TextView dismiss = dialog.findViewById(R.id.dialog_dismiss);
                    enter.setOnClickListener(v1 -> {//确定删除
                        delete_api(AES("DELETE FROM `dev_comment` WHERE `dev_comment`.`PostID` = " + post_id), position);

                        //评论数减一
                        String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`-1 WHERE `dev_posts`.`ID` =" + PostId);
                        Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                            }
                        });

                        //删除回复
                        String data1 = AES("DELETE FROM `dev_notification` WHERE `postCommentContent` = " + "'" + content + "'");
                        Api_Config.HTTP_API(data1, API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                            }
                        });

                        dialog.dismiss();//关闭确认弹窗
                    });
                    dismiss.setOnClickListener(v12 -> {//取消
                        dialog.dismiss();//关闭确认弹窗
                    });
                });
            }

            /**回复评论**/
            @Override
            public void onItemClick(String s, String content, String uid) {
                String token = getShapeData("account");
                if (token.length() > 0) {
                    BottomDialog dialog = new BottomDialog(RoutineActivity.this, R.style.base_dialog, R.layout.comment_dialog_style);
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.dialog_content);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    Editable editable = editText.getText();

                    String mas = subRangeString(content, "【", "】");
                    String mas_tow = mas.replaceAll("回复" + s + ":", "");
                    String mas_three = mas_tow.replaceAll("\\n", "");
                    editable.insert(editText.getSelectionStart(), "回复" + s + ": 【" + mas_three + "】\n\n");
                    LinearLayout button = dialog.findViewById(R.id.dialog_send);
                    button.setOnClickListener(v1 -> {
                        if (editText.getText().toString().length() > 0) {
                            //获取本地时间
                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            String Time = simpleDateFormat.format(date);
                            String img = getShapeData("Qimg");
                            String Name = getShapeData("Qname");
                            String Uid = getShapeData("id");
                            String PostContent = editText.getText().toString();
                            post_api(AES("INSERT INTO dev_comment (PostID, PageID, UID, HeadPortrait, UserName, PostContent, PostTime) VALUES" +
                                    " (NULL," + "'" + PostId + "'," + "'" + Uid + "'," + "'" + img + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + Time + "')"));

                            //评论数加一
                            String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`+1 WHERE `dev_posts`.`ID` =" + PostId);
                            Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                }
                            });
                            //提交回复内容
                            String sqlData = "INSERT INTO `dev_notification`(`notifUserId`, `postId`, `postUserName`, `postCommentContent`, `postUserImg`, `postUserId`, `postUid`,`articleType`) VALUES " +
                                    "('" + uid + "'," + "'" + PostId + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + img + "','" + ID + "','" + Post_Uid + "','" + 1 + "')";
                            Api_Config.HTTP_API(AES(sqlData), API_URL + "dev_app/dev_login/", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Looper.prepare();
                                    Toast.makeText(RoutineActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    assert response.body() != null;
                                    String s1 = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(s1);
                                        if (!jsonObject.getString("massage").equals("")) {
                                            Looper.prepare();
                                            Toast.makeText(RoutineActivity.this, "失败" + jsonObject.getString("massage"), Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("测试测试测试", s1 + "ds");
                                }
                            });

                            dialog.dismiss();
                        } else {
                            Toasty.normal(RoutineActivity.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toasty.normal(RoutineActivity.this, "请先登录", Toasty.LENGTH_SHORT).show();
                }
            }
        });


        //弹出评论输入框
        comment_Button.setOnClickListener(v -> {
            String token = getShapeData("account");
            if (token.length() > 0) {
                BottomDialog dialog = new BottomDialog(RoutineActivity.this, R.style.base_dialog, R.layout.comment_dialog_style);
                dialog.show();
                EditText editText = dialog.findViewById(R.id.dialog_content);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                LinearLayout button = dialog.findViewById(R.id.dialog_send);
                button.setOnClickListener(v1 -> {
                    if (editText.getText().toString().length() > 0) {
                        //获取本地时间
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        String Time = simpleDateFormat.format(date);
                        String img = getShapeData("Qimg");
                        String Name = getShapeData("Qname");
                        String Uid = getShapeData("id");
                        String PostContent = editText.getText().toString();
                        post_api(AES("INSERT INTO dev_comment (PostID, PageID, UID, HeadPortrait, UserName, PostContent, PostTime) VALUES" +
                                " (NULL," + "'" + PostId + "'," + "'" + Uid + "'," + "'" + img + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + Time + "')"));

                        //评论数加一
                        String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`+1 WHERE `dev_posts`.`ID` =" + PostId);
                        Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                            }
                        });

                        String sqlData = "INSERT INTO `dev_notification`(`notifUserId`, `postId`, `postUserName`, `postCommentContent`, `postUserImg`, `postUserId`, `postUid`,`articleType`) VALUES " +
                                "('" + Post_Uid + "'," + "'" + PostId + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + img + "','" + ID + "','" + Post_Uid + "','" + 1 + "')";
                        Api_Config.HTTP_API(AES(sqlData), API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                assert response.body() != null;
                                String s1 = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(s1);
                                    if (!jsonObject.getString("massage").equals("")) {
                                        Looper.prepare();
                                        Toast.makeText(RoutineActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        dialog.dismiss();
                    } else {
                        Toasty.normal(RoutineActivity.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                    }

                });
            } else {
                Toasty.normal(RoutineActivity.this, "请先登录", Toasty.LENGTH_SHORT).show();
            }
        });

    }


    /***显示评论***/
    private void comment_api(String data) {

        Api_Http(AES(data), API_URL + "dev_app/dev_comment/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();

                RoutineActivity.this.runOnUiThread(() -> {
                    try {
                        CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                        if (commentResponse != null && commentResponse.getCode() == 200) {
                            not_comment.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            datas = commentResponse.getList();
                            handler.close();//通知Handler更新recycler数据
                            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(RoutineActivity.this);
                            smoothScroller.setTargetPosition(3);
                            Manager.startSmoothScroll(smoothScroller);
                        } else {
                            not_comment.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } catch (Exception ignored) {
                    }
                    Log.e("TAG", "结果  " + context);
                });
            }
        });

    }

    /****发布评论****/
    private void post_api(String data) {
        Api_Http(data, API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(RoutineActivity.this, "出现错误！" + e, Toasty.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                RoutineActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(context);
                        String code = jsonObject.getString("code");
                        CODE = code;
                        Log.e("TAG", "code:结果  " + code);
                    } catch (Exception ignored) {
                    }
                    if (CODE.equals("0")) {
                        Toasty.success(RoutineActivity.this, "发送成功", Toasty.LENGTH_LONG).show();
                        comment_api("SELECT * FROM `dev_comment` WHERE PageID = " + "'" + PostId + "'");
                        handler.close();
                    }
                });

            }
        });

    }

    /***删除评论***/
    private void delete_api(String data, int position) {

        Api_Http(data, API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                RoutineActivity.this.runOnUiThread(() -> {
                    CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                    if (commentResponse != null && commentResponse.getCode() == 0) {
                        commentAdapter.notifyItemRemoved(position);
                        datas.remove(position);
                        if (datas.size() == 0) {
                            not_comment.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        Toasty.success(RoutineActivity.this, "删除成功", Toasty.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    /***菜单选项***/
    public void Menu() {
        BottomDialog dialog = new BottomDialog(RoutineActivity.this, R.style.base_dialog, R.layout.bottom_menu_md);
        dialog.show();
        TextView button = dialog.findViewById(R.id.bottom_menu_exit);
        LinearLayout shape = dialog.findViewById(R.id.bottom_menu_share);
        LinearLayout editor = dialog.findViewById(R.id.bottom_menu_editor);

        //关闭弹窗
        button.setOnClickListener(v -> dialog.dismiss());
        //分享
        shape.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "【" + TITLE + "】 " +
                    API_URL + "MarkdownPage/?p=" + PostId);
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "分享到"));
        });
        //编辑
        editor.setOnClickListener(v -> {
            if (getShapeData("id").equals(UID)) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", TITLE);
                bundle1.putString("content", CONTENT);
                bundle1.putString("postId", PostId);
                toActivityWithBundle(ArticleActivity.class, bundle1);
                dialog.dismiss();
            } else {
                Toast.makeText(RoutineActivity.this, "无权限", Toast.LENGTH_LONG).show();
            }
        });
    }

    /***加载文章***/
    public void LoadArticle() {
        Api_Http(PostId, API_URL + "page/article.php", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String contents = response.body().string();
                RoutineActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject object = new JSONObject(contents);
                        TITLE = object.getString("title");
                        TIME = object.getString("time");
                        content.setText(object.getString("content"));
                        CONTENT = object.getString("content");
                        NAME = object.getString("userName");
                        IMG = object.getString("userImg");
                        Post_Uid = object.getString("postUserId");
                        IMG_S = object.getString("postImages");

                        name.setText(NAME);
                        title.setText(TITLE);

                        //【时间】通过时间戳计算距离发帖天数
                        time.setText(getTimeAgo(TIME));
                        showImage_ViewPager();

                        if (Post_Uid.equals("20")) {
                            label.setVisibility(View.VISIBLE);
                        }
                        Glide.with(RoutineActivity.this).load(IMG).into(shapeableImageView);//加载头像
                        if (isComment) {
                            scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, recyclerView.getTop() + content.getHeight()), 1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    /**
     * ViewPager显示多张图片方法，取决于是否有图片存在
     **/
    public void showImage_ViewPager() {
        data.addAll(Arrays.asList(IMG_S.split(",")));
        if (data != null && IMG_S.length() > 0) {
            viewPager.setVisibility(View.VISIBLE);
        }
        viewPager.setAdapter(pagerAdapter);

    }

    private final PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return data != null ? data.size() : 0;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = LayoutInflater.from(RoutineActivity.this).inflate(R.layout.routine_pager, container, false);
            TextView count = v.findViewById(R.id.rout_pager_text);
            ShapeableImageView img = v.findViewById(R.id.rout_pager_img);
            Glide.with(RoutineActivity.this).load(data.get(position)).into(img);
            img.setOnClickListener(v1 -> {
                Bundle bundle = new Bundle();
                bundle.putString("images", IMG_S);
                bundle.putInt("position", position);
                toActivityWithBundle(PreviewImages.class, bundle);
            });
            count.setText(position + 1 + "/" + data.size());
            if (v.getParent() instanceof ViewGroup) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    };


}
