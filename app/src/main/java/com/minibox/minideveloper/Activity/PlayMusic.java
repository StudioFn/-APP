/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayMusic extends BaseActivity {
    private ShapeableImageView img;
    private ImageView exit;
    private ImageView sound_but;
    private ImageView cardView;
    private ImageView Loop;
    private ImageView down;
    private String Url;
    private TextView name;
    private final boolean isFirst = true;
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status();
        setContentView(R.layout.playmusic_activity);

        exit = findViewById(R.id.play_music_exit);
        img = findViewById(R.id.imageView);
        Loop = findViewById(R.id.music_looping);
        name = findViewById(R.id.play_music_name);
        down = findViewById(R.id.music_download);
        cardView = findViewById(R.id.music_cardView);
        sound_but = findViewById(R.id.music_start);
        RelativeLayout relativeLayout = findViewById(R.id.play_music_top);

        relativeLayout.setPadding(80, getStatusBarHeight(), 5, 5);

        initData();

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        //加载歌曲名
        name.setText(bundle.getString("Name"));
        //退出界面
        exit.setOnClickListener(v -> finish());
        //加载图片
        Glide.with(this).load(bundle.getString("PicUrl")).into(img);
        //加载音乐
        getSound(bundle.getInt("songID"));
        //播放或暂停
        sound_but.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                sound_but.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                sound_but.setImageResource(R.drawable.ic_pause);
            }
        });
        //是否循环播放
        Loop.setOnClickListener(v -> {
            if (mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
                Loop.setImageResource(R.drawable.ic_looping);
            } else {
                mediaPlayer.setLooping(true);
                Loop.setImageResource(R.drawable.ic_islooping);
            }
        });
        //下载音乐
        down.setOnClickListener(v -> {
            String name = bundle.getString("Name");
            String author = bundle.getString("Author");
            DownLoad(Url, name, author);
        });
        //模糊背景
        coverImage(img,cardView);

        //播放完毕时回调
        mediaPlayer.setOnCompletionListener(mp -> sound_but.setImageResource(R.drawable.ic_play));
    }

    public void getSound(int id) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.yimian.xyz/msc/?type=single&id=" + id)
                .get()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(PlayMusic.this, "加载失败" + e, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String content = response.body().string();
                try {
                    JSONObject jo = new JSONObject(content);
                    Url = jo.getString("url");
                } catch (JSONException ignored) {
                }
                PlayMusic.this.runOnUiThread(() -> {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            /*开始播放*/
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(Url);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } else if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(Url);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            sound_but.setImageResource(R.drawable.ic_pause);
                        }
                        //模糊背景
                        if (isFirst) coverImage(img,cardView);
                    } catch (IOException | IllegalStateException ignored) {
                        Toast.makeText(PlayMusic.this, "抱歉，此歌曲受版权保护", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }

    public void DownLoad(String url, String title, String author) {

        //从系统获取下载管理器
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //根据下载地址构建一个URi对象
        Uri uri = Uri.parse(url);
        //创建一个下载对象，指定从哪里下载文件
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //设置允许下载的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //设置通知栏在下载进行时与完成后都可见
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置文件的保存路径
        request.setDestinationInExternalFilesDir(PlayMusic.this, "/音乐/", title + "-" + author + ".mp3");
        downloadManager.enqueue(request);
        Toast.makeText(PlayMusic.this, "正在下载 " + title, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }


    public void status() {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

    }

}
