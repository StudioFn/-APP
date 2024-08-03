package com.minibox.minideveloper.Activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.minibox.minideveloper.Adapter.MusicAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.BaseDialog;
import com.minibox.minideveloper.Entity.MusicEntity;
import com.minibox.minideveloper.Entity.WyMusicEntity;
import com.minibox.minideveloper.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusicActivity extends BaseActivity {
    private ImageButton exit;
    private EditText editText;
    private ImageView search;
    private MusicAdapter musicAdapter;
    private List<MusicEntity.DataBean.SongsBean> data;
    private List<WyMusicEntity.ResultBean.SongsBean> musicData;
    private  final MediaPlayer mediaPlayer = new MediaPlayer();

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
            musicAdapter.SetData(musicData);
            musicAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);

        initView();
        initiated();

        //跳转到播放音乐界面
        musicAdapter.setOnRecyclerItemClickListener((songId,url,name,author) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("songID",songId);
            bundle.putString("PicUrl",url);
            bundle.putString("Name",name);
            bundle.putString("Author",author);
            toActivityWithBundle(PlayMusic.class,bundle);
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.stop();
            mediaPlayer.reset();
        });
    }

    private void initView() {
        exit = findViewById(R.id.search_music_exit);
        search = findViewById(R.id.search_music_img_button);
        editText = findViewById(R.id.search_music_edit);
        RecyclerView recyclerView = findViewById(R.id.search_music);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        musicAdapter = new MusicAdapter(this);
        recyclerView.setAdapter(musicAdapter);
    }

    private void initiated() {
        //初始加载
        search_music("十年金曲");
        search.setOnClickListener(v -> {
            String str = editText.getText().toString().trim();
            if (str.length()>0){
                search_music(str);
            }else{
                Toasty.normal(this,"请输入歌曲名",Toasty.LENGTH_SHORT).show();
            }
        });
        //EditText监听软键盘搜索
       editText.setOnKeyListener((v, keyCode, event) -> {
           if (keyCode == KeyEvent.KEYCODE_ENTER){
               //隐藏软键盘
               ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                       .hideSoftInputFromWindow(MusicActivity.this.getCurrentFocus()
                       .getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
               String str = editText.getText().toString().trim();
               if (str.length()>0){
                   search_music(str);
               }
           }
           return false;
       });
        //退出当前Activity
        exit.setOnClickListener(v -> finish());

    }

    /**第一方案：查找音乐**/
    public void search_music(String music){
        BaseDialog dialog = new BaseDialog(this,R.style.base_dialog,R.layout.dialog_status);
        dialog.show();
        String json = "{'s':'"+music+"','type':1,'limit':20,'total':true,'offset':0}";

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "params="+eapi(json));
        Request request = new Request.Builder()
                .url("https://interface.music.163.com/eapi/batch")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                MusicActivity.this.runOnUiThread(() -> {
                    Toast.makeText(MusicActivity.this, "加载数据失败"+e, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String Json = response.body().string();
                MusicActivity.this.runOnUiThread(() -> {
                    try {
                        WyMusicEntity res = new Gson().fromJson(Json,WyMusicEntity.class);
                        if (res.getResult() != null ){
                            musicData = res.getResult().getSongs();
                            handler.close();
                        }else {

                        }

                    }catch (Exception ignored){
                        Toast.makeText(MusicActivity.this, "获取失败", Toast.LENGTH_LONG).show();
                        Log.e("获取音乐失败",ignored+"");
                    }
                    dialog.dismiss();
                    Log.i("获取音乐数据",Json);

                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

}
