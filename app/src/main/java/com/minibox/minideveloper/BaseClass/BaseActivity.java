package com.minibox.minideveloper.BaseClass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.gson.Gson;
import com.minibox.minideveloper.R;
import com.minibox.minideveloper.Uitil.AESUtil;
import com.minibox.minideveloper.Uitil.EncryptHex;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BaseActivity extends AppCompatActivity {
    private Context context;
    private final int REQUEST_CODE = 1024;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );//设置状态栏字体颜色
        }
    }
    public void toActivity(Class is){
        Intent i = new Intent(context,is);
        startActivity(i);
    }

    public void toActivityWithBundle(Class is,Bundle bundle){
        Intent i = new Intent(context,is);
        i.putExtras(bundle);
        startActivity(i);
    }

    protected String  getShapeData(String s){
        SharedPreferences sharedPreferences = this.getSharedPreferences("QQ_User",MODE_PRIVATE);
        return sharedPreferences.getString(s,"");
    }

    protected String getCloudData(String s){
        SharedPreferences sharedPreferences = this.getSharedPreferences("Cloud",MODE_PRIVATE);
        return sharedPreferences.getString(s,"");
    }

    protected static void Api_Http(String data,String url,okhttp3.Callback callback){
        OkHttpClient client =new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();//创建网络对象
        RequestBody body = new FormBody.Builder().add("sql_data",data).build();//创建Request Body，存放重要数据的键值对
        Request request = new Request.Builder().url(url).post(body).build();//创建一个请求对象，传入URL地址和一些数据键值对的对象
        client.newCall(request).enqueue(callback);//构建一个能处理请求数据的操作类
    }

    protected static void Api_Https(String data,String like,String url,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).proxy(Proxy.NO_PROXY).build();
        RequestBody body = new FormBody.Builder().add("sql_data", data).add("like", like).build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static String getTimeAgo(String postTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(postTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            long timeInMillis = calendar.getTimeInMillis();
            long currentTimeTimeInMillis = System.currentTimeMillis();
            long diff = currentTimeTimeInMillis - timeInMillis;

            long hours = diff / (60 * 60 * 1000);
            long days = hours / 24;
            long week = days / 7;
            long month = days / 30;
            if (hours == 0) {
                return "刚刚";
            } else if (hours < 24) {
                return hours + "小时前";
            } else if (hours >= 24 && hours < 48) {
                return "昨天";
            } else if (hours > 47 && hours <= 174) {
                return days + "天前";
            } else if (days > 7 && week != 0 && week < 5) {
                return week + "周前";
            } else if (week > 5 && month != 0 && month <= 11) {
                return month + "月前";
            }else {
                return postTime;
            }
        } catch (ParseException ignored) {
        }
        return "";
    }


    //状态栏样式
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Status_Style(){
        //使状态栏完全透明，条件，需要在Theme文件样式代码中，加上<item name="android:windowTranslucentStatus">true</item>"
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体颜色
    }

    /**字符串预处理**/
    public String Preprocessing_str(String content){
        String MkContent = content;
        MkContent = MkContent.replaceAll("script","`script`");
        return MkContent;
    }

    /**AES加密**/
    public String AES(String content){
        String key = "ABCDEFGHIJKLNMOP";
        return AESUtil.encrypt(content, key);
    }
    /**静态AES加密**/
    public static String SAES(String content){
        String key = "ABCDEFGHIJKLNMOP";
        String  sm =  AESUtil.encrypt(content, key);
        Log.i("密文密文：",sm);
        return sm;
    }

    /**去除评论回复的内容**/
    public String subRangeString(String body,String str1,String str2) {
        while (true) {
            int index1 = body.indexOf(str1);
            if (index1 != -1) {
                int index2 = body.indexOf(str2, index1);
                if (index2 != -1) {
                    body = body.substring(0, index1) + body.substring(index2 + str2.length());
                }else {
                    return body;
                }
            }else {
                return body;
            }
        }
    }

    /**图片转Base64**/
    public String Base64Photo(String patch) throws UnsupportedEncodingException {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                inputStream = Files.newInputStream(Paths.get(patch));
            }
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return URLEncoder.encode(Base64.encodeToString(data,Base64.NO_WRAP),"utf-8");
    }
    /**图片转Base64,非utf-8编码**/
    public String LoadBase64(String patch) throws UnsupportedEncodingException {
        InputStream inputStream;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(patch);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return java.util.Base64.getEncoder().encodeToString(data);
        }
        return patch;
    }
    /**设置全屏**/
    public void FullScreen(View view){
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**Android 11获取储存权限方法**/
    public void getSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "储存权限获取失败", Toast.LENGTH_SHORT).show();}
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {

            } else {
                Toast.makeText(this, "储存权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**unicode编码转UTF-8**/
    public String unicodeToUtf8(String data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < data.length()) {
            if (data.charAt(i) == '\\' && data.charAt(i + 1) == 'u') {
                String hex = data.substring(i + 2, i + 6);
                int code = Integer.parseInt(hex, 16);
                sb.append((char) code);
                i += 6;
            } else {
                sb.append(data.charAt(i));
                i++;
            }
        }
        try {
            byte[] bytes = sb.toString().getBytes("UTF-8");
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**获取状态栏高度**/
    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        @SuppressLint("InternalInsetResource") int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**网易云接口数据预处理**/
    public static String eapi(Object object) {
        String url = "/api/cloudsearch/pc";
        String text = (object instanceof String) ? (String) object : new Gson().toJson(object);
        String message = "nobody" + url + "use" + text + "md5forencrypt";
        String digest = md5(message);
        String data = url + "-36cd479b6b5-" + text + "-36cd479b6b5-" + digest;
        Log.i("wyapi","text为：" + text + " message为：" + message + " digest为：" + digest + "\n data为：" + data);
        return EncryptHex.encrypt(data);
    }
    /**MD5加密**/
    private static String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(message.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    /**获取图片主要颜色，并进行渐变处理**/
    public void coverImage(ImageView img ,ImageView cardView){
        if (img.getDrawable() != null){
            Palette.from(((BitmapDrawable) img.getDrawable().getCurrent()).getBitmap())
                    .generate(new Palette.PaletteAsyncListener(){
                        @Override
                        public void onGenerated(@Nullable Palette palette) {
                            //获取图片主要颜色
                            int startColor = palette.getDominantColor(getResources().getColor(R.color.purple_200));
                            int endColor = palette.getDarkVibrantColor(palette.getDominantColor(getResources().getColor(R.color.purple_200)));
                            //创建渐变背景对象
                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    new int[]{startColor,startColor,startColor,endColor}
                            );
                            cardView.setBackground(gradientDrawable);
                        }
                    });
        }else {
            //创建渐变背景对象
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{getResources().getColor(R.color.teal_200),getResources().getColor(R.color.teal_700)}
            );
            cardView.setBackground(gradientDrawable);
        }
    }

}
