package com.minibox.minideveloper;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.minibox.minideveloper.Adapter.CommunityAdapter;
import com.minibox.minideveloper.Adapter.RecyclerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Search_ID extends AppCompatActivity {
    private ImageView mImageDelete;
    private RecyclerView recyclerView;
    private EditText eSearch;
    private RecyclerAdapter adapter;
    private List<String> Alldatas;
    private List<String>list;
    private List<String>Listnull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_search);

        initView();
        initData();
        refreshUI();
        setListener();

        //recyclerview的点击监听
        adapter.setOnItemClickListener(new RecyclerAdapter.onItemClickListener(){

            @Override
            public void onLongClick(View view, String pos,int size) {
                Toast.makeText(Search_ID.this, "已复制: "+pos , Toast.LENGTH_LONG).show();
                //复制操作
                ClipboardManager blockid = (ClipboardManager) Search_ID.this.getSystemService(Context.CLIPBOARD_SERVICE);
                blockid.setText(pos);
            }

            @Override
            public void onItemClick(View view, String pos, int size) {

            }
        });
    }

    // 设置监听
    private void setListener() {
        //edittext的监听
        eSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            //控制删除按钮的显示隐藏
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    mImageDelete.setVisibility(View.GONE);
                } else {
                    mImageDelete.setVisibility(View.VISIBLE);
                }
                //匹配文字 变色
                doChangeColor(editable.toString().trim());
            }
        });


        //删除按钮的监听
        mImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eSearch.setText("");
            }
        });
    }

    /**
     * 字体匹配方法
     */
    private void doChangeColor(String text) {
        //clear是必须的 不然只要改变edittext数据，list会一直add数据进来
        list.clear();
        //不需要匹配 把所有数据都传进来 不需要变色
        if (text.equals("")) {
            list.addAll(Alldatas);
            //防止匹配过文字之后点击删除按钮 字体仍然变色的问题
            adapter.setText(null);
            refreshUI();
        } else {
            //如果edittext里面有数据 则根据edittext里面的数据进行匹配 用contains判断是否包含该条数据 包含的话则加入到list中
            for (String i : Alldatas) {
                if (i.contains(text)) {
                    list.add(i);
                } else {


                }
            }

            //设置要变色的关键字
            adapter.setText(text);
            refreshUI();
        }
    }

    private void initData() {

        Alldatas = new ArrayList<>();
        list = new ArrayList<>();
        Listnull = new ArrayList<>();
        readFile();
        //初次进入程序时 展示全部数据
        list.addAll(Alldatas);

    }

    /**
     * 刷新UI
     */
    private void refreshUI() {
        if (adapter == null) {
            adapter = new RecyclerAdapter(this, list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        eSearch = findViewById(R.id.edt_search);
        mImageDelete = findViewById(R.id.imgv_delete);
        recyclerView = findViewById(R.id.rc_search);
        //Recyclerview的配置
        StaggeredGridLayoutManager s =new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(s);
    }

    public  void readFile()  {

        try{
            //读取文件内容
            InputStream insm = this.getResources().openRawResource(R.raw.miniworld_block_id);
            //将文件中的字节转换成字符
            InputStreamReader isReader = new InputStreamReader(insm,"UTF-8");
            //转换成机器能读懂的语言
            BufferedReader br = new BufferedReader(isReader);
            String line = null;

            try{

                while((line = br.readLine()) != null){
                    Alldatas.add(line);
                }
                //按顺序关闭，防止内存泄露
                insm.close();
                isReader.close();
                br.close();
            }catch(IOException ioe){Toast.makeText(this, "错误:"+ioe, Toast.LENGTH_LONG).show();}

        }catch(Exception e){Toast.makeText(this, "错误:"+e, Toast.LENGTH_LONG).show();}

    }


}

