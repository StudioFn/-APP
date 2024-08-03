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

import com.google.android.material.imageview.ShapeableImageView;
import com.minibox.minideveloper.Activity.SectionActivity;
import com.minibox.minideveloper.R;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.mViewHolder> {
    private final Context mContext;
    private int[] data;
    private String[] text;

    public SectionAdapter(Context context){this.mContext = context;}

    public void SetData(int[] mData, String[] mString){this.data = mData;this.text = mString;}

    @NonNull
    @Override
    public SectionAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mViewHolder(LayoutInflater.from(mContext).inflate(R.layout.section_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SectionAdapter.mViewHolder holder, int position) {
        holder.img.setImageResource(data[position]);
        holder.section_name.setText(text[position]);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(mContext, SectionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("img",data[position]);
            bundle.putString("type",text[position]);
            i.putExtras(bundle);
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return data != null?data.length:0;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder{
        private final ShapeableImageView img;
        private final TextView section_name;

        public mViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.recycler_section_img);
            section_name = v.findViewById(R.id.recycler_section_content);
        }
    }

}
