/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minibox.minideveloper.Adapter.ArticleImageAdapter;

import com.minibox.minideveloper.ArticleActivity;
import com.minibox.minideveloper.BaseClass.BaseActivity;

import com.minibox.minideveloper.BaseDialog;
import com.minibox.minideveloper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

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

public class NewArticleActivity extends BaseActivity {
    private ImageButton exit;
    private TextView articleType;
    private ImageButton send;
    private EditText title;
    private EditText content;
    private String STATUS;
    private String CODE;
    private String MS;
    private final String Type = "鱼塘";
    private String Images;
    private final List<String> icons = new ArrayList<>();
    private String MkContent;
    private RecyclerView rvImage;
    private ArticleImageAdapter adapter;
    private final List<String> list = new ArrayList<>();

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
            adapter.notifyDataSetChanged();
        }
    };

    public NewArticleActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) status();
        setContentView(R.layout.new_article);

        initView();
        initData();

    }

    private void initView() {
        articleType = findViewById(R.id.new_Title);
        rvImage = findViewById(R.id.new_article_recycler);
        content = findViewById(R.id.article_content);
        title = findViewById(R.id.article_title);
        send = findViewById(R.id.send_article);
        exit = findViewById(R.id.article_activity_exit);
    }

    private void initData() {
        //退出
        exit.setOnClickListener(v -> finish());
        articleType.setText(Type);
        //设置RecycleView布局管理器及适配器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);

        rvImage.setLayoutManager(manager);
        list.add(API_URL);
        adapter = new ArticleImageAdapter(list, this);
        rvImage.setAdapter(adapter);
        //点击添加图片
        adapter.setChangDataListener(() -> {
            if (list.size() > 6) {
                Toast.makeText(NewArticleActivity.this, "最多可选6张哦", Toast.LENGTH_SHORT).show();
            } else {
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
            }
        });
        //删除图片
        adapter.setOnRemoveListener(position -> {
            list.remove(position);
            handler.close();
        });

        //发送
        send.setOnClickListener(v -> {
            String titles = NewArticleActivity.this.title.getText().toString().trim();
            String contents = NewArticleActivity.this.content.getText().toString().trim();
            MkContent = Preprocessing_str(contents);//预处理
            String content_all = MkContent.replaceAll("</?[^>]+>", "");//去除特定字符

            if (titles.length() == 0) {
                Toasty.info(NewArticleActivity.this, "标题不能为空哦", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (content_all.length() == 0 || content_all.length() < 3) {
                Toasty.info(NewArticleActivity.this, "内容太少啦！！", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (Type == null) {
                Toasty.info(NewArticleActivity.this, "请选择发布位置！！", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (titles.length() > 0 && contents.length() > 0) {
                //获取本地时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                String id = getShapeData("id");
                String img = getShapeData("Qimg");
                String name = getShapeData("Qname");
                String NewTime = sdf.format(new Date());
                String title = NewArticleActivity.this.title.getText().toString();

                Images = icons.toString().replaceAll(getString(R.string.s), "");

                try {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle.getString("title").length() > 0 && bundle.getString("content").length() > 0) {
                        String Id = bundle.getString("postId");
                        String data = "UPDATE `dev_posts` SET `post_title` = '" + title +
                                "', `post_content` = '" + MkContent + "' WHERE `dev_posts`.`ID` = " + Id;
                        api_http(data);
                    }
                } catch (Exception ignored) {
                    String data = "INSERT INTO dev_posts (ID, post_user_id, post_user_img, post_user_name, post_time, post_title, post_content, post_type, post_new_old, post_images) VALUES" +
                            " (NULL," + "'" + id + "'," + "'" + img + "'," + "'" + name + "'," + "'" + NewTime + "'," + "'" + title + "'," + "'" + MkContent + "'," + "'" + Type + "','" + 1 + "','" + Images + "')";
                    api_http(data);
                }
            }
        });
    }


    private void api_http(String data) {
        final BaseDialog dialog = new BaseDialog(NewArticleActivity.this, R.style.base_dialog, R.layout.dialog_status);
        dialog.show();

        Api_Http(AES(data), API_URL + "dev_app/dev_login/", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toasty.error(NewArticleActivity.this, "出现错误！" + e, Toasty.LENGTH_SHORT).show();
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
                    Toasty.success(NewArticleActivity.this, "发送成功，请等待管理员审核", Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toasty.normal(NewArticleActivity.this, "发送失败,错误信息：" + MS, Toasty.LENGTH_LONG).show();
                    dialog.dismiss();
                    Log.e("发送失败,错误信息：", STATUS);
                    Looper.loop();
                }
            }
        });

    }

    public void Photo(String patch) {

        OkHttpClient client = new OkHttpClient();

        String basData = "";
        File file = new File(patch);
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
                .url(API_URL + "dev_img/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                NewArticleActivity.this.runOnUiThread(() -> {
                    Toasty.normal(NewArticleActivity.this, "失败：" + e, Toasty.LENGTH_LONG).show();
                    list.remove(list.size() - 1);
                    handler.close();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String js = response.body().string();
                NewArticleActivity.this.runOnUiThread(() -> {
                    if (js.length() > 0 && !js.equals("Invalid secret key")) {
                        icons.add(js);
                        Toasty.normal(NewArticleActivity.this, "上传成功", Toasty.LENGTH_LONG).show();
                    } else {
                        Toasty.normal(NewArticleActivity.this, "上传错误", Toasty.LENGTH_LONG).show();
                    }

                    Log.e("结果", js);
                });
            }
        });
    }

    //果果互联图床
    public void upLoadData(String iconPatch) {

        File file = new File(iconPatch);
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
                NewArticleActivity.this.runOnUiThread(() -> {
                    Toasty.normal(NewArticleActivity.this, "失败：" + e, Toasty.LENGTH_LONG).show();
                    list.remove(list.size() - 1);
                    handler.close();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String js = response.body().string();
                NewArticleActivity.this.runOnUiThread(() -> {

                    try {
                        JSONObject jsonObject = new JSONObject(js);
                        String data = jsonObject.getString("data");
                        String msg = jsonObject.getString("msg");

                        if (msg.equals("success")) {
                            JSONObject jsonObject1 = new JSONObject(data);
                            String re_data = jsonObject1.getString("url");
                            icons.add(re_data);
                            Toasty.normal(NewArticleActivity.this, "上传成功", Toasty.LENGTH_LONG).show();
                        } else {
                            Toasty.error(NewArticleActivity.this, "上传失败：" + msg, Toasty.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("结果", js);
                });
            }
        });
    }

    //打开选择文件
    private void showFileChooser() {

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("image/*");

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_TITLE, "File Chooser");
        chooser.putExtra(Intent.EXTRA_INTENT, intent1);
        startActivityForResult(chooser, 0);
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
                list.add(getImagePath(selection));
                handler.close();
                /* Photo(getImagePath(selection)); */
                upLoadData(getImagePath(selection));
                Toast.makeText(this, "正在上传...", Toast.LENGTH_SHORT).show();
            } else if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                Toast.makeText(this, "不支持此存储区", Toast.LENGTH_LONG).show();
            }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void status() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}
