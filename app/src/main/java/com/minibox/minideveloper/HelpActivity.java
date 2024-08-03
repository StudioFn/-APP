package com.minibox.minideveloper;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.minibox.minideveloper.BaseClass.BaseActivity;

public class HelpActivity extends BaseActivity {
    private WebView mWebView;
    private String url = "https://www.kancloud.cn/h_studio/sshs/3047919";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        mWebView = findViewById(R.id.web_help_cloud);

        mWebView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            //忽略证书的错误
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.cancel();
                super.onReceivedSslError(view, handler, error);
            }


        });

        mWebView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl(url);//调用loadUrl方法为WebView加入链接

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    if (keyCode == KeyEvent.KEYCODE_BACK&&mWebView.canGoBack()){
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.jump_browser:
                Intent intent = new Intent("android.intent.action.VIEW",Uri.parse(mWebView.getUrl()));
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
