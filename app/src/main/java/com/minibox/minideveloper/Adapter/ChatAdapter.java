package com.minibox.minideveloper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Entity.ChatMassager;
import com.minibox.minideveloper.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.mViewHolder> {
    private List<ChatMassager> data;
    private Context context;
    private onClickListener listener = null;

    public void Data(List<ChatMassager> data,Context context){
        this.data = data;
        this.context = context;
    }

    public void setOnItemClick(onClickListener listener){this.listener = listener;}
    public interface onClickListener{
        void clickItem(String mark);
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recycler_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
         ChatMassager response = data.get(position);
         if (response.getBySend().equals(ChatMassager.CHAT_BY_BOT)){
             holder.left.setVisibility(View.VISIBLE);
             holder.right.setVisibility(View.GONE);
             holder.leftText.setText(response.getMassager());
         }else{
             holder.left.setVisibility(View.GONE);
             holder.right.setVisibility(View.VISIBLE);
             holder.rightText.setText(response.getMassager());
             Glide.with(context).load(response.getHead()).into(holder.headImg);
         }
         holder.full.setOnClickListener(v -> {
             listener.clickItem(response.getMassager());
         });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout left;
        private final LinearLayout right;
        private final TextView leftText;
        private final TextView rightText;
        private final ShapeableImageView headImg;
        private final ImageView full;

        public mViewHolder(@NonNull View v) {
            super(v);
            left = v.findViewById(R.id.chat_recycler_left);
            full = v.findViewById(R.id.chat_recycler_full);
            right = v.findViewById(R.id.chat_recycler_right);
            headImg = v.findViewById(R.id.chat_recycler_shape);
            leftText = v.findViewById(R.id.chat_recycler_left_text);
            rightText = v.findViewById(R.id.chat_recycler_right_text);
        }
    }
}
