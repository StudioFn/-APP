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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.R;


public class CommunityItem extends RecyclerView.Adapter<CommunityItem.mViewHolder> {
    private final Context context;
    private final String[] imgUrl;

    public CommunityItem(Context context, String[] imgUrl) {
        this.context = context;
        this.imgUrl = imgUrl;
    }


    @NonNull
    @Override
    public CommunityItem.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(context).inflate(R.layout.community_recycler_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityItem.mViewHolder holder, int position) {
        String url = imgUrl[position];
        Glide.with(context).load(url).into(holder.img);//加载头像
    }

    @Override
    public int getItemCount() {
        return imgUrl != null ? imgUrl.length : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView img;
        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.recycler_img);
        }
    }
}
