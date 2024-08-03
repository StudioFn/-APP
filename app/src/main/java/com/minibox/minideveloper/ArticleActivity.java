package com.minibox.minideveloper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.minibox.minideveloper.Activity.PreviewMarkdown;
import com.minibox.minideveloper.BaseClass.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArticleActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_FILE_CHOOSER = 0;
    private ImageButton exit;
    private ImageButton send;
    private EditText title;
    private EditText content;
    private String CODE = "32";
    private String MS = "";
    private String STATUS;
    private String MkContent;
    private TextView preview;
    private String Type;
    private TextView HTitle;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);

        exit = findViewById(R.id.article_activity_exit);//退出
        send = findViewById(R.id.send_article);//发送
        title = findViewById(R.id.article_title);//标题
        content = findViewById(R.id.article_content);//内容
        preview = findViewById(R.id.code_article);//预览
        HTitle = findViewById(R.id.Title);//顶部标题

        ImageView code = findViewById(R.id.article_code);
        ImageView photo = findViewById(R.id.article_photo);
        ImageView cite = findViewById(R.id.article_cite);
        ImageView text_bold = findViewById(R.id.article_text_bold);
        TextView text_title = findViewById(R.id.article_text_title);
        TextView mark_help = findViewById(R.id.article_markdown_help);
        /*选择板块*/
        TextView s_fish = findViewById(R.id.s_fish);
        TextView s_lua = findViewById(R.id.s_lua);
        TextView s_java = findViewById(R.id.s_java);
        TextView s_html = findViewById(R.id.s_html);
        TextView s_article = findViewById(R.id.s_article);
        mark_help.setOnClickListener(this);
        s_fish.setOnClickListener(this);
        s_article.setOnClickListener(this);
        s_html.setOnClickListener(this);
        s_java.setOnClickListener(this);
        s_lua.setOnClickListener(this);

        code.setOnClickListener(this);
        photo.setOnClickListener(this);
        cite.setOnClickListener(this);
        text_bold.setOnClickListener(this);
        text_title.setOnClickListener(this);

        init_data();
    }

    private void init_data() {

        exit.setOnClickListener(v -> finish());//退出
        preview.setOnClickListener(v -> {//预览
            String titles = ArticleActivity.this.title.getText().toString().trim();
            String contents = ArticleActivity.this.content.getText().toString().trim();
            MkContent = Preprocessing_str(contents);//预处理
            String content_all = MkContent.replaceAll("</?[^>]+>", "");//去除特定字符
            Bundle bundle = new Bundle();
            bundle.putString("title", titles);
            bundle.putString("content", content_all);
            toActivityWithBundle(PreviewMarkdown.class, bundle);
        });
        //从bundle获取字符，如果有的话
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("title").length() > 0 && bundle.getString("content").length() > 0) {
                title.setText(bundle.getString("title"));
                content.setText(bundle.getString("content"));
                Type = "upData";
            }
        } catch (Exception ignored) {
        }
        //发送
        send.setOnClickListener(v -> {
            String titles = ArticleActivity.this.title.getText().toString().trim();
            String contents = ArticleActivity.this.content.getText().toString().trim();
            MkContent = Preprocessing_str(contents);//预处理
            String content_all = MkContent.replaceAll("</?[^>]+>", "");//去除特定字符

            if (titles.length() == 0) {
                Toasty.info(ArticleActivity.this, "标题不能为空哦", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (content_all.length() == 0 || content_all.length() < 3) {
                Toasty.info(ArticleActivity.this, "内容太少啦！！", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (Type == null) {
                Toasty.info(ArticleActivity.this, "请选择发布位置！！", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (titles.length() > 0 && contents.length() > 0) {
                //获取本地时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                String id = getShapeData("id");
                String img = getShapeData("Qimg");
                String name = getShapeData("Qname");
                String NewTime = sdf.format(new Date());
                String title = ArticleActivity.this.title.getText().toString();

                try {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle.getString("title").length() > 0 && bundle.getString("content").length() > 0) {
                        String Id = bundle.getString("postId");
                        String data = "UPDATE `dev_posts` SET `post_title` = '" + title +
                                "', `post_content` = '" + MkContent + "' WHERE `dev_posts`.`ID` = " + Id;
                        api_http(data);
                    }
                } catch (Exception ignored) {
                    String data = "INSERT INTO dev_posts (ID, post_user_id, post_user_img, post_user_name, post_time, post_title, post_content, post_type) VALUES" +
                            " (NULL," + "'" + id + "'," + "'" + img + "'," + "'" + name + "'," + "'" + NewTime + "'," + "'" + title + "'," + "'" + MkContent + "'," + "'" + Type + "')";
                    api_http(data);
                }
            }
        });


    }

    private void api_http(String data) {
        final BaseDialog dialog = new BaseDialog(ArticleActivity.this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();

        Api_Http(AES(data), API_URL+"dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(ArticleActivity.this, "出现错误！" + e, Toasty.LENGTH_SHORT).show();
                dialog.dismiss();
                Looper.loop();
                Log.e("TAG", "错误信息：" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String context = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(context);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("message");
                    STATUS = jsonObject.getString("aes");
                    CODE = code;
                    MS = msg;

                } catch (Exception ignored) {
                }
                if (CODE.equals("0") && MS.equals("") && !STATUS.equals("false")) {
                    //一个子线程中没有调用Looper.prepare(),就不能在该线程中创建Toast
                    Looper.prepare();
                    Toasty.success(ArticleActivity.this, "发送成功，请等待管理员审核", Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toasty.normal(ArticleActivity.this, "发送失败,错误信息：" + MS, Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                    Looper.loop();
                }
            }
        });

    }

    //官方图床
    public void Photo() {

        OkHttpClient client = new OkHttpClient();

        String basData = "";
        try {
            basData = LoadBase64(file.getPath());
        } catch (UnsupportedEncodingException e) {
            return;
        }
        RequestBody body = new FormBody.Builder()
                .add("key", "keyloadmain0377")
                .add("image", "data:image/jpg;base64," + basData)
                .build();
        Request request = new Request.Builder()
                .url(API_URL+"dev_img/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ArticleActivity.this.runOnUiThread(() -> {
                    Toasty.info(ArticleActivity.this,"上传失败："+e,Toasty.LENGTH_LONG).show();
                    Log.e("发帖页", String.valueOf(e));
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String js = response.body().string();
                ArticleActivity.this.runOnUiThread(() -> {
                    Editable editable = content.getText();
                    editable.insert(content.getSelectionStart(), "[![](" + js + ")](" + js + ")");
                    Toasty.normal(ArticleActivity.this, "上传成功", Toasty.LENGTH_LONG).show();
                    Log.i("结果", js);
                });
            }
        });
    }

    //果果图床
    public void Photo_S() {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestFile = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName()
                        , RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        Request request = new Request.Builder()
                .url("http://img.9a18.cn/api/upload")
                .addHeader("token", getShapeData("token"))
                .post(requestFile)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ArticleActivity.this.runOnUiThread(() -> Toasty.normal(ArticleActivity.this, "失败：" + e, Toasty.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String js = response.body().string();
                ArticleActivity.this.runOnUiThread(() -> {

                    try {
                        JSONObject jsonObject = new JSONObject(js);
                        String data = jsonObject.getString("data");

                        if (jsonObject.getString("msg").equals("success")) {
                            JSONObject jsonObject1 = new JSONObject(data);
                            String re_data = jsonObject1.getString("url");
                            Editable editable = content.getText();
                            editable.insert(content.getSelectionStart(), "\n[![](" + re_data + ")](" + re_data + ")");
                            Toasty.normal(ArticleActivity.this, "上传成功", Toasty.LENGTH_LONG).show();
                        } else {
                            Toasty.error(ArticleActivity.this, "上传失败" + jsonObject.getString("msg"), Toasty.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("结果", js);
                });
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Editable editable;
        switch (v.getId()) {
            case R.id.article_code:
                editable = content.getText();
                editable.insert(content.getSelectionStart(), "\n```程序语言\n\n```");
                int index = content.getSelectionEnd();
                content.setSelection(index - 5, index - 9);
                break;
            case R.id.article_photo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        showFileChooser();
                    } else {
                        getSave();
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        showFileChooser();
                    } else {
                        getSave();
                    }
                }

                break;
            case R.id.article_cite:
                editable = content.getText();
                editable.insert(content.getSelectionStart(), "\n> ");
                break;
            case R.id.article_text_bold:
                editable = content.getText();
                editable.insert(content.getSelectionStart(), "\n****");
                int x = content.getSelectionEnd();
                content.setSelection(x - 2);
                break;
            case R.id.article_text_title:
                editable = content.getText();
                editable.insert(content.getSelectionStart(), "#");
                break;
            case R.id.article_markdown_help:
                Uri uri = Uri.parse("https://www.minidown.cn/writing_in_markdown1.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }

        switch (v.getId()) {
            case R.id.s_fish:
                Type = "鱼塘";
                HTitle.setText(Type);
                break;
            case R.id.s_article:
                Type = "文章";
                HTitle.setText(Type);
                break;
            case R.id.s_lua:
                Type = "Lua";
                HTitle.setText(Type);
                break;
            case R.id.s_java:
                Type = "Java";
                HTitle.setText(Type);
                break;
            case R.id.s_html:
                Type = "Html";
                HTitle.setText(Type);
                break;
        }

    }

    //打开选择文件
    private void showFileChooser() {

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("image/*");

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_TITLE, "File Chooser");
        chooser.putExtra(Intent.EXTRA_INTENT, intent1);
        startActivityForResult(chooser, REQUEST_CODE_FILE_CHOOSER);
    }

    //回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            String docid = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())
                    || "com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                String id = docid.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                file = new File(getImagePath(selection));
            } else if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                Toast.makeText(this, "不支持此存储区", Toast.LENGTH_LONG).show();
                return;
            }
            Photo();

        }
    }

    //content类型的uri获取图片路径的方法
    @SuppressLint("Range")
    private String getImagePath(String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
