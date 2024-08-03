package com.minibox.minideveloper.Adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.minibox.minideveloper.Entity.CommentEntity;
import com.minibox.minideveloper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<CommentEntity> data;

    private  OnRecyclerItemClickListener monItemClickListener = null;

    public void setDatas(List<CommentEntity> data) {
        this.data = data;
    }

    public DynamicAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        this.monItemClickListener = listener;
    }

    public interface OnRecyclerItemClickListener{
        void onItemClick(String s,String img,String name,String title,String time,String content,int type,String images);
        void onLongItemClick(String post_id);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mViewHolder v = new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_dynamic_recycle_item,parent,false));
        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentEntity commentEntity = data.get(position);
        String Ncontent = commentEntity.getPostContent();
        String mContent = Ncontent.replaceAll("</?[^>]+>","");//去除特定字符
        String mMarkdown = mContent.replaceAll("#|\\*\\*|`|_|\\[|\\]|\\(|\\)|\\{|\\}|\\#|\\+|-|\\*|\\.|!","");//去除Markdown语法符号
        mViewHolder mViewHolder = (DynamicAdapter.mViewHolder) holder;
        mViewHolder.title.setText("【"+commentEntity.getPostTitle()+"】");
        mViewHolder.content.setText(mMarkdown);
        mViewHolder.time.setText(commentEntity.getPostTime());
        mViewHolder.like_count.setText(commentEntity.getPostSubscribe());
        if (commentEntity.getPostStatus().equals("false")){
            mViewHolder.status.setVisibility(View.VISIBLE);
        }else {
            mViewHolder.status.setVisibility(View.GONE);
        }

        //点击事件
        mViewHolder.itemView.setOnClickListener(v -> {
            String id    = commentEntity.getId();
            String img   = commentEntity.getPostUserImg();
            String name  = commentEntity.getPostUserName();
            String title = commentEntity.getPostTitle();
            int status = commentEntity.getPostNewOld();
            String time = commentEntity.getPostTime();
            String content = commentEntity.getPostContent();
            String images = commentEntity.getPostImages();
            monItemClickListener.onItemClick(id,img,name,title,time,content,status,images);
        });
        //长按事件
        mViewHolder.itemView.setOnLongClickListener(v -> {
            String post_id = commentEntity.getId();
            monItemClickListener.onLongItemClick(post_id);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView time;
        private final TextView like_count;
        private final TextView status;

        public mViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.dynamic_title);
            content = v.findViewById(R.id.dynamic_content);
            time = v.findViewById(R.id.dynamic_date_time);
            status = v.findViewById(R.id.dynamic_status);
            like_count = v.findViewById(R.id.dynamic_like_count);
        }
    }

}
