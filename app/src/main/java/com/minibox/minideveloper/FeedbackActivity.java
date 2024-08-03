package com.minibox.minideveloper;

import static com.just.agentweb.AgentWeb.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.just.agentweb.AgentWeb;
import com.minibox.minideveloper.BaseClass.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseActivity {
    private AgentWeb mAgentWeb;
    private ValueCallback<Uri[]> mUploadCallBackAboveL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_help_video);
        LinearLayout linearLayout = findViewById(R.id.feed_web_linear);

        String url = "http://support.qq.com/products/497715";

        mAgentWeb = with(this)
                .setAgentWebParent(linearLayout,new LinearLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .setWebChromeClient(webChromeClient)
                .createAgentWeb()
                .ready()
                .go(url);


        if (getShapeData("id").equals("20")){
            String openid = "2308762185"; // 用户的openid
            String nickname = getShapeData("Qname"); // 用户的nickname
            String headImgUrl = getShapeData("imageqq"); // 用户的头像url

            String postData = "nickname=" + nickname + "&avatar="+ headImgUrl + "&openid=" + openid;
            mAgentWeb.getWebCreator().getWebView().postUrl(url, postData.getBytes());
        }else{
            String openid = getShapeData("openid"); // 用户的openid
            String nickname = getShapeData("Qname"); // 用户的nickname
            String headImgUrl = getShapeData("imageqq");  // 用户的头像url

            String postData = "nickname=" + nickname + "&avatar="+ headImgUrl + "&openid=" + openid;
            mAgentWeb.getWebCreator().getWebView().postUrl(url, postData.getBytes());
        }


    }

    private final com.just.agentweb.WebChromeClient webChromeClient = new com.just.agentweb.WebChromeClient() {
        // Android版本 >= 5.0的方法，上传图片
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallBackAboveL = filePathCallback;
            showFileChooser();
            return true;
        }
    };

    //打开选择文件
    private void showFileChooser() {

        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("image/*");

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.destroy();
        super.onDestroy();
    }
}
