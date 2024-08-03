package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;
import static com.minibox.minideveloper.ApplicationConfig.OPEN_AI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minibox.minideveloper.Adapter.ChatAdapter;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.Entity.ChatMassager;
import com.minibox.minideveloper.HomeActivity;
import com.minibox.minideveloper.R;
import com.minibox.minideveloper.View.BottomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends BaseActivity {
    private TextView title;
    private EditText editText;
    private ImageButton send;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ImageView exit;
    private final List<ChatMassager> data = new ArrayList<>();
    private final int[] soundId = new int[2];
    private SoundPool soundPool;
    private final  List<JSONObject> objects = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//设置状态栏字体颜色
        setContentView(R.layout.chat_activity);

        initView();
        initData();

    }

    private void initView() {
        exit = findViewById(R.id.chat_exit);
        title = findViewById(R.id.chat_text);
        send = findViewById(R.id.chat_send);
        editText = findViewById(R.id.chat_editor);
        recyclerView = findViewById(R.id.chat_recycler);

        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.Data(data,this);

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setMaxStreams(2)
                .build();
        soundId[0] = soundPool.load(this,R.raw.chat_request,1);
        soundId[1] = soundPool.load(this,R.raw.chat_send,1);
    }

    private void initData() {
        data.add(new ChatMassager("欢迎使用ChatGpt，问点啥吧！！",
                ChatMassager.CHAT_BY_BOT,getShapeData("Qimg")));
        send.setOnClickListener(v -> {
            sound(1);
            String getString = editText.getText().toString().trim();
            if (getString.length()>0){
                addDataT(getString,ChatMassager.CHAT_BY_ME);
                editText.setText("");
                JsonObject("user",getString);
                //选择官方chatGpt或者ALAPI的chatGpt
                if(getCloudData("type").equals("ALAPI")){AlapiAI(getString);}
                else {ChatGpt(objects);}
            }else{
                Toast.makeText(this, "您未输入任何东西哦", Toast.LENGTH_SHORT).show();
            }
        });
        //退出
        exit.setOnClickListener(v -> finish());
        //放大机器人回复的消息
        chatAdapter.setOnItemClick(mark -> {
            BottomDialog dialog = new BottomDialog(mark);
            dialog.show(getSupportFragmentManager(),"");
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(String msg, String by){
        data.add(new ChatMassager(msg,by,getShapeData("Qimg")));
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        ChatMassager content = data.get(data.size()-1);
        String ass = content.getMassager();
        JsonObject("assistant",ass);

    }
    @SuppressLint("NotifyDataSetChanged")
    public void addDataT(String msg, String by){
        data.add(new ChatMassager(msg,by,getShapeData("Qimg")));
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
    }

    public void AlapiAI(String ms){
        title.postDelayed(new TimerTask() {
            @Override
            public void run() {
                title.setText("和圈APP\n对方正在输入....");
            }},1000);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES).build();

        RequestBody body = RequestBody.create(mediaType,"token=HRkpVppZHRHR9X90&content="+ms);
        Request request = new Request.Builder()
                .url("https://v2.alapi.cn/api/chatgpt/pro")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ChatActivity.this.runOnUiThread(() -> {
                    title.setText(R.string.app_name);
                    Toast.makeText(ChatActivity.this, "抱歉，与Chat GPT失联了！！", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();

                ChatActivity.this.runOnUiThread(() -> {
                    title.setText(R.string.app_name);
                    try {
                        JSONObject jsonObject1 = new JSONObject(json);
                        String getData = jsonObject1.getString("data");
                        JSONObject DataS = new JSONObject(getData);
                        if (jsonObject1.getString("msg").equals("success")){
                            sound(0);
                            addData(DataS.getString("content"),ChatMassager.CHAT_BY_BOT);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        title.postDelayed(new TimerTask() {
                            @Override
                            public void run() {
                                sound(0);
                                title.setText(R.string.app_name);
                                addDataT("哎呀，官方给的钞能力用完啦，请等待官方为我补充钞能力😊",ChatMassager.CHAT_BY_BOT);
                            }},3000);
                    }

                });
                Log.i("Chat GPT返回的数据",json);
            }
        });
    }

    public void ChatGpt(List<JSONObject> ms){
        title.postDelayed(new TimerTask() {
            @Override
            public void run() {
                title.setText("和圈APP\n对方正在输入....");
            }},1000);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES).build();

        RequestBody body = RequestBody.create(mediaType,"data="+ms);
        Request request = new Request.Builder()
                .url(OPEN_AI)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ChatActivity.this.runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "抱歉，与Chat GPT失联了！！", Toast.LENGTH_SHORT).show();
                    title.setText(R.string.app_name);
                    Log.e("chatGpt错误消息", String.valueOf(e));
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();

                ChatActivity.this.runOnUiThread(() -> {
                    title.setText(R.string.app_name);
                    try {
                        JSONObject getChoices = new JSONObject(json);
                        JSONArray getArray = new JSONArray(getChoices.getString("choices"));
                        String content = getArray.getJSONObject(0).getString("message");
                        JSONObject data = new JSONObject(content);
                        sound(0);
                        addData(data.getString("content"),ChatMassager.CHAT_BY_BOT);
                        Log.e("ChatGpt数据",ms.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        title.postDelayed(new TimerTask() {
                            @Override
                            public void run() {
                                sound(0);
                                title.setText(R.string.app_name);
                                addDataT("发生了一点错误！"+e+"返回数据为：\n"+json,ChatMassager.CHAT_BY_BOT);}
                        },3000);
                    }
                });
                Log.i("Chat GPT返回的数据",json);
            }
        });
    }

    public void sound(int p){
        soundPool.play(soundId[p],1,1,0,0,1);
    }

    public void JsonObject(String role,String content){
        try {
            JSONObject object = new JSONObject();
            object.put("role",role);
            object.put("content",content);
            objects.add(object);
            if (objects.size()>6){
                objects.remove(0);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

}
