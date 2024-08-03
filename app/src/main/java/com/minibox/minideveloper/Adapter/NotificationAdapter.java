package com.minibox.minideveloper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Entity.NotifyEntity;
import com.minibox.minideveloper.R;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.mViewHolder> {
    private final Context mContext;
    private List<NotifyEntity.ListDTO> data;
    private onItemClickListener onItemClickListener;

    public NotificationAdapter(Context context){
        this.mContext = context;
    }

    public void SetData(List<NotifyEntity.ListDTO> data){
        this.data = data;
    }

    public void SetOnClickItem(onItemClickListener listener){
        this.onItemClickListener = listener;
    }
    public interface onItemClickListener{
        void onItemListener(String post_id,String notify_id,int art);
    }

    @NonNull
    @Override
    public NotificationAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notify_item_recycler,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.mViewHolder holder, int position) {
        NotifyEntity.ListDTO notify = data.get(position);
        holder.title.setText(String.format("%s回复了你：", notify.getPostUserName()));
        holder.content.setText(notify.getPostCommentContent());
        Glide.with(mContext).load(notify.getPostUserImg()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            String id = notify.getPostId();
            int artType = notify.getArticleType();
            String notifyID = notify.getNotifId();
            onItemClickListener.onItemListener(id,notifyID,artType);
        });

        String status = notify.getNotifyStatus();
        if (status.equals("false")){
            holder.title.setTextColor(Color.parseColor("#F16C6C"));
        }else{
            holder.title.setTextColor(Color.parseColor("#434343"));
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder{
        private final ShapeableImageView img;
        private final TextView title;
        private final TextView content;

        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.notify_recycler_img);
            title = v.findViewById(R.id.notify_recycler_name);
            content = v.findViewById(R.id.notify_recycler_content);
        }
    }

}
