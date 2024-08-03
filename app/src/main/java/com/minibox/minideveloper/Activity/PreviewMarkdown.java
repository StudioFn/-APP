package com.minibox.minideveloper.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.minibox.minideveloper.BaseClass.BaseActivity;
import com.minibox.minideveloper.R;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.InternalStyleSheet;
import br.tiagohm.markdownview.css.styles.Github;

public class PreviewMarkdown extends BaseActivity {
    private MarkdownView markdownView;
    private ImageButton exit;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_markdown);

        title = findViewById(R.id.preview_title);
        exit = findViewById(R.id.preview_exit);
        markdownView = findViewById(R.id.preview_markdown);
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initData(){
        Bundle bundle = getIntent().getExtras();
        String title_text = bundle.getString("title");
        String content = bundle.getString("content");
        /**Markdown配置**/
        InternalStyleSheet mStyle = new Github();
        mStyle.addRule("img", "border-radius:5px","margin:8px 0");
        mStyle.addRule("*", "margin:5px 0");
        markdownView.addStyleSheet(mStyle);
        markdownView.loadMarkdown(content);
        title.setText(title_text);
        exit.setOnClickListener(v -> finish());

        /*** 解决代码块横向滑动冲突的bug*/
        markdownView.setOnTouchListener(new View.OnTouchListener() {
            private float startx;
            private float starty;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        startx = event.getX();
                        starty = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float offsetx = Math.abs(event.getX() - startx);
                        float offsety = Math.abs(event.getY() - starty);
                        if (offsetx > offsety) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

}
