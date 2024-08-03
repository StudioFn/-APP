package com.minibox.minideveloper.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.just.agentweb.AgentWeb;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

public class NewsArticle extends BaseActivity {
    private TextView web_title;
    private AgentWeb mAgentWeb;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        web_title = findViewById(R.id.news_title);
        LinearLayout webLinear = findViewById(R.id.web_linear);
        ImageButton button = findViewById(R.id.news_exit);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webLinear,new LinearLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .setWebChromeClient(webChromeClient)
                .createAgentWeb()
                .ready()
                .go(url);


        button.setOnClickListener(v -> finish());
    }

    private final com.just.agentweb.WebChromeClient webChromeClient = new com.just.agentweb.WebChromeClient(){
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            web_title.setText(title);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
