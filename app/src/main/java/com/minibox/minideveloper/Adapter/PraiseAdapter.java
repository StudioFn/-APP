/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Entity.LikeArticleEntity;
import com.minibox.minideveloper.R;

import java.text.DecimalFormat;
import java.util.List;

public class PraiseAdapter extends RecyclerView.Adapter<PraiseAdapter.mViewHolder> {
    private final Context mContext;
    private List<LikeArticleEntity.ListArticle> data;

    public PraiseAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<LikeArticleEntity.ListArticle> data){
        this.data = data;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.praise_recycler_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        LikeArticleEntity.ListArticle listArticle = data.get(position);
        String Ncontent = listArticle.getPostContent();
        String mContent = Ncontent.replaceAll("</?[^>]+>","");//去除html标签
        String mMarkdown = mContent.replaceAll("#|\\*\\*|`|_|\\[|\\]|\\(|\\)|\\{|\\}|\\#|\\+|-|\\*|\\.|!","");//去除Markdown语法符号
        String url = listArticle.getPostUserImg();
        DecimalFormat c = new DecimalFormat("0.#");//保留p位小数
        float Com_count = Integer.parseInt(listArticle.getPostSubscribe());//评论数
        float fie_count = Integer.parseInt(listArticle.getPostLook());//阅读数
        float lik_count = Integer.parseInt(listArticle.getPostLike());//点赞数

        String comment;
        if (Com_count>=9999){
            comment = c.format(Com_count / 1000)+"w";
        }else{
            comment = String.valueOf(Integer.valueOf((int) Com_count));
        }
        //【阅读】数据处理
        String fire;
        if (fie_count>=9999){
            fire = c.format(fie_count / 1000)+"w";
        }else{
            fire = String.valueOf(Integer.valueOf((int) fie_count));
        }
        //【点赞】数据处理
        final String[] like = {""};
        if (lik_count>=9999){
            like[0] = c.format(lik_count / 1000)+"w";
        }else{
            like[0] = String.valueOf(Integer.valueOf((int) lik_count));
        }

        Glide.with(mContext).load(url).into(holder.img);//加载头像
        holder.name.setText(listArticle.getPostUserName());//获得作者昵称
        holder.title.setText(listArticle.getPostTitle());//获得文章标题
        holder.content.setText(mMarkdown);//显示处理后的文章内容
        holder.time.setText(listArticle.getPostTime());//获得发布时间
        holder.comment.setText(comment);//获得评论数
        holder.lookNum.setText(fire);//获得阅读数
        holder.likeNum.setText(like[0]);//获得点赞数

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView img;
        private final TextView name;
        private final TextView title;
        private final TextView content;
        private final TextView time;
        private final TextView likeNum;
        private final TextView lookNum;
        private final TextView comment;
        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.head_portrait);
            time = v.findViewById(R.id.date_time);
            name = v.findViewById(R.id.author_name);
            likeNum = v.findViewById(R.id.like_count);
            lookNum = v.findViewById(R.id.look_count);
            comment = v.findViewById(R.id.comment_count);
            title = v.findViewById(R.id.article_title_recycler);
            content = v.findViewById(R.id.article_content_item);
        }
    }

}
