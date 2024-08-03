package com.minibox.minideveloper.Activity;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

public class DevActivity extends BaseActivity {
    private WebView webView;
    private ValueCallback<Uri[]> mUploadCallBackAboveL;
    private ProgressBar progressBar;
    private String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_activity);
        ImageButton exit = findViewById(R.id.blog_exit);
        webView = findViewById(R.id.blog_id);
        progressBar = findViewById(R.id.blog_progressbar);
        TextView titles = findViewById(R.id.title);

        try {
            Bundle bundle = getIntent().getExtras();
            String Gurl = bundle.getString("url");

            if (Gurl.length()>0){
                url = Gurl;
            }else{url = API_URL;}
        }catch (Exception ignored){}


        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN){
                if (keyCode == KeyEvent.KEYCODE_BACK&&webView.canGoBack()){
                    webView.goBack();
                    return true;
                }
            }
            return false;
        });

        webView.setWebChromeClient(new WebChromeClient(){
            // Android版本 >= 5.0的方法
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallBackAboveL = filePathCallback;
                showFileChooser();
                return true;
            }

            //获取标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titles.setText(title);
            }

            //网页加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }else{
                    if (progressBar.getVisibility() == View.GONE){
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    }
                }
                super.onProgressChanged(view, newProgress);

            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")){
                    webView.loadUrl(url);
                }else{
//                    Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
//                    startActivity(i);
                }
                return true;
            }
        });

        //返回
        exit.setOnClickListener(v -> finish());
    }


    //打开选择文件
    private void showFileChooser() {

        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("*/*");

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_TITLE, "File Chooser");
        chooser.putExtra(Intent.EXTRA_INTENT, intent1);
        int REQUEST_CODE_FILE_CHOOSER = 0;
        startActivityForResult(chooser, REQUEST_CODE_FILE_CHOOSER);
    }

    //回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Uri uri = data.getData();
            mUploadCallBackAboveL.onReceiveValue(new Uri[]{uri});
            mUploadCallBackAboveL = null;
        }
        clearUploadMessage();
    }

    //未选择文件时，传空数据，防止下次无法执行
    private void clearUploadMessage() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(null);
            mUploadCallBackAboveL = null;
        }
    }

}
