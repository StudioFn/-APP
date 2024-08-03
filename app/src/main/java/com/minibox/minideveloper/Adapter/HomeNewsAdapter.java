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
import com.minibox.minideveloper.Activity.NewsArticle;
import com.minibox.minideveloper.Entity.DayArticle;
import com.minibox.minideveloper.R;

import java.util.List;

public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.mViewHolder> {

    private List<DayArticle.DataDTO.TopStoriesDTO> data;
    private final Context mContext;

    public void setData(List<DayArticle.DataDTO.TopStoriesDTO> data){this.data = data;}

    public HomeNewsAdapter(Context context){this.mContext = context;}

    @NonNull
    @Override
    public HomeNewsAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.home_recyclerview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeNewsAdapter.mViewHolder holder, int position) {
        DayArticle.DataDTO.TopStoriesDTO dayArticle = data.get(position);
        holder.author.setText(dayArticle.getHint());
        holder.content.setText(dayArticle.getTitle());
        Glide.with(mContext).load(dayArticle.getImage()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, NewsArticle.class);
            Bundle bundle = new Bundle();
            bundle.putString("url",dayArticle.getUrl());
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        private final TextView author;
        private final TextView content;
        private final ShapeableImageView img;

        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.home_news_img);
            author = v.findViewById(R.id.home_news_author);
            content = v.findViewById(R.id.home_news_content);
        }

    }


}
