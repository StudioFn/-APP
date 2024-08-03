/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.minibox.minideveloper.BaseClass.Api_Config;
import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

public class CrashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);

        TextView text = findViewById(R.id.crash_text);
        Button but = findViewById(R.id.crash_but);

        Bundle bundle = getIntent().getExtras();
        text.setText(bundle.getString("error"));

        but.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(Api_Config.QQ_INTO));
            startActivity(i);
        });

    }
}
