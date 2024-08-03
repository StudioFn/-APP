/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Adapter;

import static com.minibox.minideveloper.ApplicationConfig.API_URL;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.R;

import java.io.File;
import java.util.List;

public class ArticleImageAdapter extends RecyclerView.Adapter<ArticleImageAdapter.mViewHolder> {
    private final List<String> data;
    private final Context context;
    private ChangData changData = null;
    private onRemoveImg removeImg = null;

    public ArticleImageAdapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setChangDataListener(ChangData changDataListener) {
        this.changData = changDataListener;
    }

    public void setOnRemoveListener(onRemoveImg removeImg){
        this.removeImg = removeImg;
    }

    public interface ChangData {
        void OnClickListener();
    }

    public interface onRemoveImg{
        void OnClickListener(int position);
    }

    @NonNull
    @Override
    public ArticleImageAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(context).inflate(R.layout.art_img_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleImageAdapter.mViewHolder holder, int position) {
        String url = data.get(position);
        Log.e( "onBindViewHolder: ", url);
        if (url.equals(API_URL)) {
            holder.layoutImg.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(v -> changData.OnClickListener());
        } else {
            Glide.with(context).load(new File(url)).into(holder.img);
            holder.remove.setOnClickListener(v -> removeImg.OnClickListener(position));
            holder.layoutImg.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView img;
        private final LinearLayout imageView;
        private final ImageView remove;
        private final RelativeLayout layoutImg;

        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.recycler_img);
            remove = v.findViewById(R.id.image_remove);
            imageView = v.findViewById(R.id.rvImage_load);
            layoutImg = v.findViewById(R.id.art_show);
        }
    }
}
