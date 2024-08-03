package com.minibox.minideveloper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Entity.PostCommentEntity;
import com.minibox.minideveloper.MyDetailsPage;
import com.minibox.minideveloper.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.mViewHolder> {

    private List<PostCommentEntity> data;
    private final Context mContext;
    private OnItemClickListener monItemClickListener = null;

    public void setOnRecyclerItemClickListener(OnItemClickListener listener){
        this.monItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onLongItemClick(String uid,String post_id,String post_content,int position);
        void onItemClick(String s,String content,String uid);
    }

    public CommentAdapter (Context context){ this.mContext = context; }
    public void SetData(List<PostCommentEntity> data){ this.data = data; }

    @NonNull
    @Override
    public CommentAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.mViewHolder holder, int position) {

        PostCommentEntity postCommentEntity = data.get(position);
        String url = postCommentEntity.getHeadPortrait();
        Glide.with(mContext).load(url).into(holder.img);//加载头像

        holder.userName.setText(postCommentEntity.getUserName());
        holder.content.setText(postCommentEntity.getPostContent());
        holder.Time.setText(postCommentEntity.getPostTime());

        holder.itemView.setOnLongClickListener(v -> {
            String uid  = postCommentEntity.getUid();
            String post = postCommentEntity.getPostID();
            String content = postCommentEntity.getPostContent();
            monItemClickListener.onLongItemClick(uid,post,content,position);
            return false;
        });
        holder.itemView.setOnClickListener(v -> {
            String post = postCommentEntity.getUserName();
            String content = postCommentEntity.getPostContent();
            String uid  = postCommentEntity.getUid();
            monItemClickListener.onItemClick(post,content,uid);
        });
        holder.img.setOnClickListener(v -> {
            String uid = postCommentEntity.getUid();
            Intent i = new Intent(mContext, MyDetailsPage.class);
            Bundle bundle = new Bundle();
            bundle.putString("uid",uid);
            i.putExtras(bundle);
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView img;
        private final TextView userName;
        private final TextView content;
        private final TextView Time;

        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.comment_head_portrait);
            userName = v.findViewById(R.id.comment_user_name);
            content = v.findViewById(R.id.comment_content);
            Time = v.findViewById(R.id.comment_post_time);
        }
    }

}
