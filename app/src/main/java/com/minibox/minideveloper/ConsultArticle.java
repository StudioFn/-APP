package com.minibox.minideveloper;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.CommentAdapter;
import com.minibox.minideveloper.BaseClass.Api_Config;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.BaseClass.BottomDialog;
import com.minibox.minideveloper.BaseClass.ScrollLinearLayoutManager;
import com.minibox.minideveloper.Entity.CommentResponse;
import com.minibox.minideveloper.Entity.PostCommentEntity;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConsultArticle extends BaseActivity {
    private LinearLayout not_comment, loading;
    private LottieAnimationView lottieAnimationView;
    private String PostId = null;
    private RecyclerView recyclerView;
    private WebView webView;
    private CommentAdapter commentAdapter;
    private List<PostCommentEntity> datas = new ArrayList<>();
    private String CODE = "32";
    private final java.util.logging.Handler handler = new Handler() {
        @Override
        public void publish(LogRecord record) {
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
            commentAdapter.SetData(datas);
            commentAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_article);

        TextView name = findViewById(R.id.consult_article_author_name);//作者昵称
        ShapeableImageView shapeableImageView = findViewById(R.id.consult_head_portrait);//作者头像
        ImageButton exit = findViewById(R.id.consult_activity_exit);//退出
        recyclerView = findViewById(R.id.consult_recycler);//评论列表
        TextView comment_Button = findViewById(R.id.consult_comment_button);//评论按钮
        not_comment = findViewById(R.id.if_not_comment);//无评论显示TextView控件
        webView = findViewById(R.id.consult_web);
        LinearLayout share = findViewById(R.id.consult_share);//分享
        loading = findViewById(R.id.consult_loading);//预加载界面
        lottieAnimationView = findViewById(R.id.consult_lottie);


        ScrollLinearLayoutManager Manager = new ScrollLinearLayoutManager(this);
        Manager.setOrientation(LinearLayoutManager.VERTICAL);
        Manager.setReverseLayout(true);
        Manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(Manager);

        commentAdapter = new CommentAdapter(ConsultArticle.this);
        recyclerView.setAdapter(commentAdapter);

        Bundle bundle = getIntent().getExtras();
        PostId = bundle.getString("id");
        String IMG = bundle.getString("img");
        String NAME = bundle.getString("name");
        String UID = bundle.getString("uid");
        String TITLE = bundle.getString("title");
        name.setText(NAME);
        Glide.with(ConsultArticle.this).load(IMG).into(shapeableImageView);//加载头像

        String url = API_URL + "page/index.php?p=" + PostId;
        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        webView.getSettings().setDomStorageEnabled(true);
        webView.setVerticalScrollBarEnabled(false); //不显示竖滚动条
        webView.setHorizontalScrollBarEnabled(false);//不显示横滚动条
        webView.loadUrl(url);

        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString().concat(" DEV-APP"));//在APP协议头加入特殊字段
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // Android版本 >= 5.0的方法
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                return true;
            }

            //网页加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //加载评论
                    comment_api("SELECT * FROM `dev_comment` WHERE PageID = " + "'" + PostId + "'");
                    handler.close();
                    loading.setVisibility(View.GONE);//隐藏预加载界面
                    lottieAnimationView.cancelAnimation();//取消播放动画
                }
                super.onProgressChanged(view, newProgress);

            }
        });

        /**更新阅读数**/
        Api_Http(AES("UPDATE `dev_posts` SET `post_look` = `post_look` + 1 WHERE `dev_posts`.`ID` =" + PostId),
                API_URL + "dev_app/dev_login/", new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        String content = response.body().string();

                    }
                });


        /*** 解决scrollview嵌套webview中HTML中代码块横向滑动冲突的bug*/
        webView.setOnTouchListener(new View.OnTouchListener() {
            private float startx;
            private float starty;
            private float offsetx;
            private float offsety;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        startx = event.getX();
                        starty = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        offsetx = Math.abs(event.getX() - startx);
                        offsety = Math.abs(event.getY() - starty);
                        if (offsetx > offsety) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        //分享链接
        share.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "我发现了一个好应用！叫和圈APP，这是链接，复制到浏览器打开吧！\n" + "【" + TITLE + "】 " + url);
            i.setType("text/plain");
            startActivity(i);
        });
        exit.setOnClickListener(v -> finish());//退出当前程序

        shapeableImageView.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putString("uid", UID);
            toActivityWithBundle(MyDetailsPage.class, bundle1);
        });//跳转到作者详情页

        //RecyclerView点击事件（评论的回复）
        commentAdapter.setOnRecyclerItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onLongItemClick(String uid, String post_id, String content, int position) {
                BottomDialog bottomDialog = new BottomDialog(ConsultArticle.this, R.style.base_dialog, R.layout.bottom_dialog);
                bottomDialog.show();
                LinearLayout linearLayout = bottomDialog.findViewById(R.id.bottom_dialog_delete);//删帖
                LinearLayout linearLayout1 = bottomDialog.findViewById(R.id.bottom_dialog_report);//举报
                TextView textView = bottomDialog.findViewById(R.id.bottom_dialog_exit);//取消

                textView.setOnClickListener(v -> {
                    bottomDialog.dismiss();
                });
                //删除按钮只有是自己的时候才显示出来
                if (getShapeData("id").equals(uid)) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                //删帖操作
                linearLayout.setOnClickListener(v -> {
                    bottomDialog.dismiss();//关闭底部弹窗
                    //是否删除帖子确认弹窗
                    BaseDialog dialog = new BaseDialog(ConsultArticle.this, R.style.base_dialog, R.layout.dialog_status_tow);
                    dialog.show();
                    TextView enter = dialog.findViewById(R.id.dialog_enter);
                    TextView dismiss = dialog.findViewById(R.id.dialog_dismiss);
                    enter.setOnClickListener(v1 -> {//确定删除
                        delete_api(AES("DELETE FROM `dev_comment` WHERE `dev_comment`.`PostID` = " + post_id));//AES加密处理

                        //评论数减一
                        String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`-1 WHERE `dev_posts`.`ID` =" + PostId);//AES加密处理
                        Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            }
                        });
                        dialog.dismiss();//关闭确认弹窗
                    });
                    dismiss.setOnClickListener(v12 -> {//取消
                        dialog.dismiss();//关闭确认弹窗
                    });
                });
            }

            //单击事件
            @Override
            public void onItemClick(String s, String content, String uid) {
                String token = getShapeData("account");
                if (token.length() > 0) {
                    BottomDialog dialog = new BottomDialog(ConsultArticle.this, R.style.base_dialog, R.layout.comment_dialog_style);
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.dialog_content);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    Editable editable = editText.getText();
                    editable.insert(editText.getSelectionStart(), "@" + s + "  ");
                    LinearLayout button = dialog.findViewById(R.id.dialog_send);
                    button.setOnClickListener(v1 -> {
                        if (editText.getText().toString().length() > 0) {
                            //获取本地时间
                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String Time = simpleDateFormat.format(date);
                            String img = getShapeData("Qimg");
                            String Name = getShapeData("Qname");
                            String Uid = getShapeData("id");
                            String PostContent = editText.getText().toString();
                            //AES加密处理
                            post_api(AES("INSERT INTO dev_comment (PostID, PageID, UID, HeadPortrait, UserName, PostContent, PostTime) VALUES" +
                                    " (NULL," + "'" + PostId + "'," + "'" + Uid + "'," + "'" + img + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + Time + "')"));

                            //评论数加一，AES加密处理
                            String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`+1 WHERE `dev_posts`.`ID` =" + PostId);
                            Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                }
                            });

                            dialog.dismiss();
                        } else {
                            Toasty.normal(ConsultArticle.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toasty.normal(ConsultArticle.this, "请先登录", Toasty.LENGTH_SHORT).show();
                }
            }
        });


        //弹出评论输入框
        comment_Button.setOnClickListener(v -> {
            String token = getShapeData("account");
            if (token.length() > 0) {
                BottomDialog dialog = new BottomDialog(ConsultArticle.this, R.style.base_dialog, R.layout.comment_dialog_style);
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String Time = simpleDateFormat.format(date);
                        String img = getShapeData("Qimg");
                        String Name = getShapeData("Qname");
                        String Uid = getShapeData("id");
                        String PostContent = editText.getText().toString();
                        //AES加密处理
                        post_api(AES("INSERT INTO dev_comment (PostID, PageID, UID, HeadPortrait, UserName, PostContent, PostTime) VALUES" +
                                " (NULL," + "'" + PostId + "'," + "'" + Uid + "'," + "'" + img + "'," + "'" + Name + "'," + "'" + PostContent + "'," + "'" + Time + "')"));

                        //评论数加一，AES加密处理
                        String data = AES("UPDATE `dev_posts` SET `post_subscribe` = `post_subscribe`+1 WHERE `dev_posts`.`ID` =" + PostId);
                        Log.e("密文：", data);
                        Api_Config.HTTP_API(data, API_URL + "dev_app/dev_login/", new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            }
                        });

                        dialog.dismiss();
                    } else {
                        Toasty.normal(ConsultArticle.this, "内容不能为空哦", Toasty.LENGTH_SHORT).show();
                    }

                });
            } else {
                Toasty.normal(ConsultArticle.this, "请先登录", Toasty.LENGTH_SHORT).show();
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

                ConsultArticle.this.runOnUiThread(() -> {
                    try {
                        CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                        if (commentResponse != null && commentResponse.getCode() == 200) {
                            not_comment.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            List<PostCommentEntity> list = commentResponse.getList();
                            datas = list;
                            handler.close();//通知Handler更新recycler数据
                        } else {
                            not_comment.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } catch (Exception exception) {
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
                Toasty.error(ConsultArticle.this, "出现错误！" + e, Toasty.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                ConsultArticle.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(context);
                            String code = jsonObject.getString("code");
                            CODE = code;
                        } catch (Exception ignored) {
                        }
                        if (CODE.equals("0")) {
                            Toasty.success(ConsultArticle.this, "发送成功", Toasty.LENGTH_LONG).show();
                            comment_api("SELECT * FROM `dev_comment` WHERE PageID = " + "'" + PostId + "'");
                            handler.close();
                        }
                    }
                });

                Log.e("文章详情页", "结果  " + context);
            }
        });

    }

    /***删除评论***/
    private void delete_api(String data) {

        Api_Http(data, API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                Log.e("文章详情页", "code:结果  " + context);
                ConsultArticle.this.runOnUiThread(() -> {
                    CommentResponse commentResponse = new Gson().fromJson(context, CommentResponse.class);
                    if (commentResponse != null && commentResponse.getCode() == 0) {
                        comment_api("SELECT * FROM `dev_comment` WHERE PageID = " + "'" + PostId + "'");
                        handler.close();
                        Toasty.success(ConsultArticle.this, "删除成功", Toasty.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }


}
