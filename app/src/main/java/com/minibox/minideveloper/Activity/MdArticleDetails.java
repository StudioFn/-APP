package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.InternalStyleSheet;
import br.tiagohm.markdownview.css.styles.Github;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MdArticleDetails extends BaseActivity {
    private LinearLayout not_comment;
    private String PostId = null;
    private String UID = null;
    private String ID = null;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private MarkdownView markdownView;
    private CommentAdapter commentAdapter;
    private TextView name, title, time, label;
    private ShapeableImageView shapeableImageView;
    private boolean isComment = false;
    private List<PostCommentEntity> datas = new ArrayList<>();
    private String CODE = "32";
    private WindowManager windowManager;
    private View fullView = null;
    private String IMG, NAME, TITLE, TIME, Post_Uid, CONTENT;
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
        setContentView(R.layout.md_article_details);

        name = findViewById(R.id.md_author_name);//作者昵称
        shapeableImageView = findViewById(R.id.md_article_img);//作者头像
        title = findViewById(R.id.md_article_title);//标题
        ImageButton exit = findViewById(R.id.md_article_exit);//退出
        ImageButton menu = findViewById(R.id.md_article_menu);//菜单
        recyclerView = findViewById(R.id.md_article_recycler);//评论列表
        TextView comment_Button = findViewById(R.id.md_comment_button);//评论按钮
        not_comment = findViewById(R.id.md_if_not_comment);//无评论显示TextView控件
        markdownView = findViewById(R.id.markdown_view);//内容
        LinearLayout share = findViewById(R.id.md_share);//分享
        time = findViewById(R.id.md_article_time);//发布时间
        label = findViewById(R.id.md_article_label);//官方标识
        scrollView = findViewById(R.id.md_scrollView);
        windowManager = getWindowManager();

        /*编辑文章*/
        menu.setOnClickListener(v -> Menu());

        /*Markdown配置**/
        InternalStyleSheet mStyle = new Github();
        mStyle.addRule("img", "border-radius:5px", "margin:8px 0");
        mStyle.addRule("*", "margin:5px 0");
        mStyle.addRule("body", "padding-top: 0");
        markdownView.addStyleSheet(mStyle);

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
            ViewCompat.setTransitionName(shapeableImageView, bundle.getString("transitionName"));

            markdownView.loadMarkdown(CONTENT);
            name.setText(NAME);
            title.setText(TITLE);

            //【时间】通过时间戳计算距离发帖天数
            time.setText(getTimeAgo(TIME));

            if (Post_Uid.equals("20")) {
                label.setVisibility(View.VISIBLE);
            }
            Glide.with(MdArticleDetails.this).load(IMG).into(shapeableImageView);//加载头像
            if (isComment) {
                scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, recyclerView.getTop() + markdownView.getHeight()), 1000);
            }
        } catch (Exception ignored) {
            Bundle bundle = getIntent().getExtras();
            PostId = bundle.getString("id");
            UID = bundle.getString("uid");
            ID = getShapeData("id");
            //加载文章
            LoadArticle();
        }

        /*判断页面是否加载完成*/
        markdownView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
                return true;
            }

        });

        /* 解决代码块横向滑动冲突的bug*/
        markdownView.setOnTouchListener(new View.OnTouchListener() {
            private float startx;
            private float starty;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        startx = event.getX();
                        starty = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float offsetx = Math.abs(event.getX() - startx);
                        float offsety = Math.abs(event.getY() - starty);
                        v.getParent().requestDisallowInterceptTouchEvent(offsetx > offsety);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //设置全屏视频播放
        markdownView.setWebChromeClient(new WebChromeClient() {

            //开启全屏播放
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                windowManager.addView(view, new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION));
                FullScreen(view);
                MdArticleDetails.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullView = view;
            }

            //关闭全屏播放
            @Override
            public void onHideCustomView() {
                windowManager.removeViewImmediate(fullView);
                fullView = null;
                MdArticleDetails.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });


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

        commentAdapter = new CommentAdapter(MdArticleDetails.this);
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
                BottomDialog bottomDialog = new BottomDialog(MdArticleDetails.this, R.style.base_dialog, R.layout.bottom_dialog);
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
                    BaseDialog dialog = new BaseDialog(MdArticleDetails.this, R.style.base_dialog, R.layout.dialog_status_tow);
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
                    BottomDialog dialog = new BottomDialog(MdArticleDetails.this, R.style.base_dialog, R.layout.comment_dialog_style);
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
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
                            String sqlData = "INSERT INTO `dev_notification`(`notifUserId`, `postId`, `postUserName`, `postCommentContent`, `postUserImg`, `postUserId`, `postUid`) VALUES " +
                                    "('" + uid + "'," + "'" + PostId + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + img + "','" + ID + "','" + Post_Uid + "')";
                            Api_Config.HTTP_API(AES(sqlData), API_URL + "dev_app/dev_login/", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Looper.prepare();
                                    Toast.makeText(MdArticleDetails.this, "失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(MdArticleDetails.this, "失败", Toast.LENGTH_SHORT).show();
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
                            Toasty.normal(MdArticleDetails.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toasty.normal(MdArticleDetails.this, "请先登录", Toasty.LENGTH_SHORT).show();
                }
            }
        });


        //弹出评论输入框
        comment_Button.setOnClickListener(v -> {
            String token = getShapeData("account");
            if (token.length() > 0) {
                BottomDialog dialog = new BottomDialog(MdArticleDetails.this, R.style.base_dialog, R.layout.comment_dialog_style);
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

                        //提交回复内容
                        String sqlData = "INSERT INTO `dev_notification`(`notifUserId`, `postId`, `postUserName`, `postCommentContent`, `postUserImg`, `postUserId`, `postUid`) VALUES " +
                                "('" + Post_Uid + "'," + "'" + PostId + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + img + "','" + ID + "','" + Post_Uid + "')";
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
                                        Toast.makeText(MdArticleDetails.this, "失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        dialog.dismiss();
                    } else {
                        Toasty.normal(MdArticleDetails.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                    }

                });
            } else {
                Toasty.normal(MdArticleDetails.this, "请先登录", Toasty.LENGTH_SHORT).show();
            }
        });

    }


    /***显示评论***/
    private void comment_api(String data) {

        Api_Http(AES(data), API_URL + "/dev_app/dev_comment/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();

                MdArticleDetails.this.runOnUiThread(() -> {
                    try {
                        CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                        if (commentResponse != null && commentResponse.getCode() == 200) {
                            not_comment.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            datas = commentResponse.getList();
                            handler.close();//通知Handler更新recycler数据
                            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(MdArticleDetails.this);
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
                Toasty.error(MdArticleDetails.this, "出现错误！" + e, Toasty.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                MdArticleDetails.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(context);
                        String code = jsonObject.getString("code");
                        CODE = code;
                        Log.e("TAG", "code:结果  " + code);
                        Log.e( "onResponse: ", context);
                    } catch (Exception ignored) {
                    }
                    if (CODE.equals("0")) {
                        Toasty.success(MdArticleDetails.this, "发送成功", Toasty.LENGTH_LONG).show();
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
                MdArticleDetails.this.runOnUiThread(() -> {
                    CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                    if (commentResponse != null && commentResponse.getCode() == 0) {
                        commentAdapter.notifyItemRemoved(position);
                        datas.remove(position);
                        if (datas.size() == 0) {
                            not_comment.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        Toasty.success(MdArticleDetails.this, "删除成功", Toasty.LENGTH_SHORT).show();
                    }
                });

                Log.e("TAG", "code:结果  " + context);
            }
        });

    }

    /***菜单选项***/
    public void Menu() {
        BottomDialog dialog = new BottomDialog(MdArticleDetails.this, R.style.base_dialog, R.layout.bottom_menu_md);
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
                Toast.makeText(MdArticleDetails.this, "无权限", Toast.LENGTH_LONG).show();
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
                String content = response.body().string();
                MdArticleDetails.this.runOnUiThread(() -> {
                    try {
                        JSONObject object = new JSONObject(content);
                        TITLE = object.getString("title");
                        TIME = object.getString("time");
                        markdownView.loadMarkdown(object.getString("content"));
                        CONTENT = object.getString("content");
                        NAME = object.getString("userName");
                        IMG = object.getString("userImg");
                        Post_Uid = object.getString("postUserId");

                        name.setText(NAME);
                        title.setText(TITLE);

                        //【时间】通过时间戳计算距离发帖天数
                        time.setText(getTimeAgo(TIME));

                        if (Post_Uid.equals("20")) {
                            label.setVisibility(View.VISIBLE);
                        }
                        Glide.with(MdArticleDetails.this).load(IMG).into(shapeableImageView);//加载头像
                        if (isComment) {
                            scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, recyclerView.getTop() + markdownView.getHeight()), 1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
//        LoadArticle();
        if (fullView != null) {
            FullScreen(fullView);
        }
        super.onResume();
    }

    /**
     * 点击返回按钮退出全屏
     **/
    @Override
    public void onBackPressed() {
        if (fullView != null) {
            MdArticleDetails.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            windowManager.removeViewImmediate(fullView);
            fullView = null;
        }
        super.onBackPressed();
    }
}
